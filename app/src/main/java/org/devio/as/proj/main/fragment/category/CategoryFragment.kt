package org.devio.`as`.proj.main.fragment.category

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseIntArray
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.fragment_category.*
import org.devio.`as`.proj.common.ui.component.HiBaseFragment
import org.devio.`as`.proj.common.ui.view.EmptyView
import org.devio.`as`.proj.common.ui.view.loadUrl
import org.devio.`as`.proj.main.R
import org.devio.`as`.proj.main.http.ApiFactory
import org.devio.`as`.proj.main.http.api.CategoryApi
import org.devio.`as`.proj.main.model.Subcategory
import org.devio.`as`.proj.main.model.TabCategory
import org.devio.`as`.proj.main.route.HiRoute
import org.devio.`as`.proj.main.route.HiRoute.Destination.*
import org.devio.hi.library.restful.HiCallback
import org.devio.hi.library.restful.HiResponse
import org.devio.hi.ui.tab.bottom.HiTabBottomLayout

class CategoryFragment : HiBaseFragment() {
    private var emptyView: EmptyView? = null
    private val SPAN_COUNT = 3
    private val subcategoryListCache = mutableMapOf<String, List<Subcategory>>()
    override fun getLayoutId(): Int {
        return R.layout.fragment_category
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HiTabBottomLayout.clipBottomPadding(root_container)

        content_loading.visibility = View.VISIBLE
        queryCategoryList()
    }

    private fun onQueryCategoryListSuccess(data: List<TabCategory>) {
        if (!isAlive) return
        emptyView?.visibility = View.GONE
        content_loading.visibility = View.GONE
        slider_view.visibility = View.VISIBLE

        slider_view.bindMenuView(itemCount = data.size,
            onBindView = { holder, position ->
                val category = data[position]
                // holder.menu_item_tilte    ??????????????????
                //  holder.itemView.menu_item_title.  findviewbyid
                holder.findViewById<TextView>(R.id.menu_item_title)?.text = category.categoryName
            }, onItemClick = { holder, position ->
                val category = data[position]
                val categoryId = category.categoryId
                if (subcategoryListCache.containsKey(categoryId)) {
                    onQuerySubcategoryListSuccess(subcategoryListCache[categoryId]!!)
                } else {
                    querySubcategoryList(categoryId)
                }
            })
    }

    private val subcategoryList = mutableListOf<Subcategory>()
    private val decoration = CategoryItemDecoration({ position ->
        subcategoryList[position].groupName
    }, SPAN_COUNT)
    private val layoutManager = GridLayoutManager(context, SPAN_COUNT)
    private val groupSpanSizeOffset = SparseIntArray()
    private val spanSizeLookUp = object : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            var spanSize = 1
            val groupName: String = subcategoryList[position].groupName
            val nextGroupName: String? =
                if (position + 1 < subcategoryList.size) subcategoryList[position + 1].groupName else null

            if (TextUtils.equals(groupName, nextGroupName)) {
                spanSize = 1
            } else {
                //??????????????? ??????????????? ?????????????????????
                //1 .?????????????????? position ?????????????????? groupSpanSizeOffset ???????????????
                //2 .?????? ????????????????????? ????????? spansizeoffset ?????????
                //3 .????????????????????????item ?????? spansize count

                val indexOfKey = groupSpanSizeOffset.indexOfKey(position)
                val size = groupSpanSizeOffset.size()
                val lastGroupOffset = if (size <= 0) 0
                else if (indexOfKey >= 0) {
                    //??????????????????????????????????????????????????? groupSpanSizeOffset ???????????????????????????????????????
                    if (indexOfKey == 0) 0 else groupSpanSizeOffset.valueAt(indexOfKey - 1)
                } else {
                    //?????????????????????????????????????????????????????? groupSpanSizeOffset ???????????????????????? ????????????????????????
                    //???????????????????????????????????????
                    groupSpanSizeOffset.valueAt(size - 1)
                }
                //          3       -     (6     +    5               % 3  )?????????=0  ???1 ???2
                spanSize = SPAN_COUNT - (position + lastGroupOffset) % SPAN_COUNT
                if (indexOfKey < 0) {
                    //??????????????? ?????????????????????spansize ???????????????
                    val groupOffset = lastGroupOffset + spanSize - 1
                    groupSpanSizeOffset.put(position, groupOffset)
                }
            }
            return spanSize
        }
    }

    private fun querySubcategoryList(categoryId: String) {
        ApiFactory.create(CategoryApi::class.java).querySubcategoryList(categoryId)
            .enqueue(object : HiCallback<List<Subcategory>> {
                override fun onSuccess(response: HiResponse<List<Subcategory>>) {
                    if (response.successful() && response.data != null) {
                        onQuerySubcategoryListSuccess(response.data!!)
                        if (!subcategoryListCache.containsKey(categoryId)) {
                            subcategoryListCache[categoryId] = response.data!!
                        }
                    }
                }

                override fun onFailed(throwable: Throwable) {
                }
            })
    }


    private fun onQuerySubcategoryListSuccess(data: List<Subcategory>) {
        if (!isAlive) return
        decoration.clear()
        groupSpanSizeOffset.clear()
        subcategoryList.clear()
        subcategoryList.addAll(data)

        if (layoutManager.spanSizeLookup != spanSizeLookUp) {
            layoutManager.spanSizeLookup = spanSizeLookUp
        }
        slider_view.bindContentView(
            itemCount = data.size,
            itemDecoration = decoration,
            layoutManager = layoutManager,
            onBindView = { holder, position ->
                val subcategory = data[position]
                holder.findViewById<ImageView>(R.id.content_item_image)
                    ?.loadUrl(subcategory.subcategoryIcon)
                holder.findViewById<TextView>(R.id.content_item_title)?.text =
                    subcategory.subcategoryName
            },
            onItemClick = { holder, position ->
                //?????????????????????????????????????????????
                val subcategory = data[position]
                val bundle = Bundle()
                bundle.putString("categoryId", subcategory.categoryId)
                bundle.putString("subcategoryId", subcategory.subcategoryId)
                bundle.putString("categoryTitle", subcategory.subcategoryName)
                HiRoute.startActivity(context!!, bundle, GOODS_LIST)
            }
        )
    }

    private fun queryCategoryList() {
        ApiFactory.create(CategoryApi::class.java).queryCategoryList()
            .enqueue(object : HiCallback<List<TabCategory>> {
                override fun onSuccess(response: HiResponse<List<TabCategory>>) {
                    if (response.successful() && response.data != null) {
                        onQueryCategoryListSuccess(response.data!!)
                    } else {
                        showEmptyView()
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    showEmptyView()
                }

            })
    }


    private fun showEmptyView() {
        if (!isAlive) return

        if (emptyView == null) {
            emptyView = EmptyView(context!!)
            emptyView?.setIcon(R.string.if_empty3)
            emptyView?.setDesc(getString(R.string.list_empty_desc))
            emptyView?.setButton(getString(R.string.list_empty_action), View.OnClickListener {
                queryCategoryList()
            })

            emptyView?.setBackgroundColor(Color.WHITE)
            emptyView?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            root_container.addView(emptyView)
        }

        content_loading.visibility = View.GONE
        slider_view.visibility = View.GONE
        emptyView?.visibility = View.VISIBLE
    }
}
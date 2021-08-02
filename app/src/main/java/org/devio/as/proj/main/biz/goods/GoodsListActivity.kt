package org.devio.`as`.proj.main.biz.goods

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.activity_goods_list.*
import org.devio.`as`.proj.common.ui.component.HiBaseActivity
import org.devio.`as`.proj.main.R
import org.devio.`as`.proj.main.route.HiRoute
import org.devio.hi.library.util.HiStatusBar

@Route(path = "/goods/list")
class GoodsListActivity : HiBaseActivity() {

    @JvmField
    @Autowired
    var categoryTitle: String = ""

    @JvmField
    @Autowired
    var categoryId: String = ""

    @JvmField
    @Autowired
    var subcategoryId: String = ""

    private val FRAGMENT_TAG = "GOODS_LIST_FRAGMENT"
    override fun onCreate(savedInstanceState: Bundle?) {
        HiStatusBar.setStatusBar(this, true, translucent = false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goods_list)
       // ARouter.getInstance().inject(this)
        HiRoute.inject(this)

        action_back.setOnClickListener { onBackPressed() }
        category_title.text = categoryTitle

        var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (fragment == null) {
            fragment = GoodsListFragment.newInstance(categoryId, subcategoryId)
        }
        val ft = supportFragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            ft.add(R.id.container, fragment, FRAGMENT_TAG)
        }
        ft.show(fragment).commitNowAllowingStateLoss()
    }
}
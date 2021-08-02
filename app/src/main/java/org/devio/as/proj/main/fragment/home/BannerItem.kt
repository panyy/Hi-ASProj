package org.devio.`as`.proj.main.fragment.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import org.devio.`as`.proj.common.ui.view.loadUrl
import org.devio.`as`.proj.main.model.HomeBanner
import org.devio.`as`.proj.main.route.HiRoute
import org.devio.hi.library.util.HiDisplayUtil
import org.devio.hi.ui.banner.HiBanner
import org.devio.hi.ui.banner.core.HiBannerMo
import org.devio.hi.ui.item.HiDataItem
import org.devio.hi.ui.item.HiViewHolder

class BannerItem(val list: List<HomeBanner>) :
    HiDataItem<List<HomeBanner>, HiViewHolder>(list) {
    override fun onBindData(holder:HiViewHolder, position: Int) {
        val context = holder.itemView.context
        val banner = holder.itemView as HiBanner

        val models = mutableListOf<HiBannerMo>()
        list.forEachIndexed { index, homeBanner ->
            val bannerMo = object : HiBannerMo() {}
            bannerMo.url = homeBanner.cover
            models.add(bannerMo)
        }
        banner.setOnBannerClickListener { viewHolder, bannerMo, position ->
            val homeBanner = list[position]
            if (TextUtils.equals(homeBanner.type, HomeBanner.TYPE_GOODS)) {
                //跳详情页....
                Toast.makeText(context, "you  touch me:$position", Toast.LENGTH_SHORT).show()
            } else {
                HiRoute.startActivity4Browser(homeBanner.url)
            }

        }
        banner.setBannerData(models)
        banner.setBindAdapter { viewHolder, mo, position ->
            ((viewHolder.rootView) as ImageView).loadUrl(mo.url)
        }
    }

    override fun getItemView(parent: ViewGroup): View? {
        val context = parent.context
        val banner = HiBanner(context)
        val params = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            HiDisplayUtil.dp2px(160f)
        )
        params.bottomMargin = HiDisplayUtil.dp2px(10f)
        banner.layoutParams = params
        banner.setBackgroundColor(Color.WHITE)
        return banner
    }
}
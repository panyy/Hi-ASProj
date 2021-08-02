package org.devio.`as`.proj.main.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.android.synthetic.main.fragment_profile_page.*
import org.devio.`as`.proj.common.ui.component.HiBaseFragment
import org.devio.`as`.proj.common.ui.view.loadCircle
import org.devio.`as`.proj.common.ui.view.loadCorner
import org.devio.`as`.proj.main.R
import org.devio.`as`.proj.main.http.ApiFactory
import org.devio.`as`.proj.main.http.api.AccountApi
import org.devio.`as`.proj.main.model.CourseNotice
import org.devio.`as`.proj.main.model.Notice
import org.devio.`as`.proj.main.model.UserProfile
import org.devio.`as`.proj.main.route.HiRoute
import org.devio.hi.library.restful.HiCallback
import org.devio.hi.library.restful.HiResponse
import org.devio.hi.library.util.HiDisplayUtil
import org.devio.hi.ui.banner.core.HiBannerAdapter
import org.devio.hi.ui.banner.core.HiBannerMo

class ProfileFragment : HiBaseFragment() {
    private val REQUEST_CODE_LOGIN_PROFILE = 1001
    override fun getLayoutId(): Int {
        return R.layout.fragment_profile_page
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        queryLoginUserData()
        queryCourseNotice()
    }

    private fun queryCourseNotice() {
        ApiFactory.create(AccountApi::class.java).notice()
            .enqueue(object : HiCallback<CourseNotice> {
                override fun onSuccess(response: HiResponse<CourseNotice>) {
                    if (response.data != null && response.data!!.total > 0) {
                        notify_count.text = response.data!!.total.toString()
                        notify_count.visibility = View.VISIBLE
                    }
                }

                override fun onFailed(throwable: Throwable) {

                }
            })
    }

    private fun queryLoginUserData() {
        ApiFactory.create(AccountApi::class.java).profile()
            .enqueue(object : HiCallback<UserProfile> {
                override fun onSuccess(response: HiResponse<UserProfile>) {
                    val userProfile = response.data
                    if (response.code == HiResponse.SUCCESS && userProfile != null) {
                        updateUI(userProfile)
                    } else {
                        showToast(response.msg);
                    }
                }

                override fun onFailed(throwable: Throwable) {
                    showToast(throwable.message);
                }
            })
    }

    private fun showToast(message: String?) {
        if (message == null) return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(userProfile: UserProfile) {
        user_name.text =
            if (userProfile.isLogin) userProfile.userName else getString(R.string.profile_not_login)
        login_desc.text =
            if (userProfile.isLogin) getString(R.string.profile_login_desc_welcome_back) else getString(
                R.string.profile_login_desc
            )

        if (userProfile.isLogin) {
            //avatar 需要判断空
            if (TextUtils.isEmpty(userProfile.avatar)) {
                user_avatar.setImageResource(R.drawable.ic_avatar_default)
            } else {
                user_avatar.loadCircle(userProfile.avatar)
            }
        } else {
            user_avatar.setImageResource(R.drawable.ic_avatar_default)
            user_name.setOnClickListener {
                HiRoute.startActivity(
                    activity,
                    destination = HiRoute.Destination.ACCOUNT_LOGIN,
                    requestCode = REQUEST_CODE_LOGIN_PROFILE
                )
//                ARouter.getInstance().build("/account/login")
//                    .navigation(activity, REQUEST_CODE_LOGIN_PROFILE)
            }
        }


        tab_item_collection.text =
            spannableTabItem(
                userProfile.favoriteCount,
                getString(R.string.profile_tab_item_collection)
            )
        tab_item_history.text =
            spannableTabItem(userProfile.browseCount, getString(R.string.profile_tab_item_history))
        tab_item_learn.text =
            spannableTabItem(userProfile.learnMinutes, getString(R.string.profile_tab_item_learn))


        updateBanner(userProfile.bannerNoticeList)
    }

    private fun updateBanner(bannerNoticeList: List<Notice>?) {
        if (bannerNoticeList == null || bannerNoticeList.isEmpty()) return
        var models = mutableListOf<HiBannerMo>()
        bannerNoticeList.forEach {
            var hiBannerMo = object : HiBannerMo() {}
            hiBannerMo.url = it.cover
            models.add(hiBannerMo)
        }
        hi_banner.setOnBannerClickListener { viewHolder, bannerMo, position ->
            HiRoute.startActivity4Browser(bannerNoticeList[position].url)
        }
        hi_banner.setBannerData(R.layout.layout_profile_banner_item, models)
        hi_banner.setBindAdapter { viewHolder: HiBannerAdapter.HiBannerViewHolder?, mo: HiBannerMo?, position: Int ->
            if (viewHolder == null || mo == null) return@setBindAdapter
            val imageView = viewHolder.findViewById<ImageView>(R.id.banner_item_imageview)
            imageView.loadCorner(mo.url, HiDisplayUtil.dp2px(10f, resources))
        }
        hi_banner.visibility = View.VISIBLE


    }

    private fun spannableTabItem(topText: Int, bottomText: String): CharSequence? {
        val spanStr = topText.toString()
        var ssb = SpannableStringBuilder()
        var ssTop = SpannableString(spanStr)

        val spanFlag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        ssTop.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.color_000)),
            0,
            ssTop.length,
            spanFlag
        )
        ssTop.setSpan(AbsoluteSizeSpan(18, true), 0, ssTop.length, spanFlag)
        ssTop.setSpan(StyleSpan(Typeface.BOLD), 0, ssTop.length, spanFlag)

        ssb.append(ssTop)
        ssb.append(bottomText)

        return ssb
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOGIN_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
            queryLoginUserData()
        }
    }
}
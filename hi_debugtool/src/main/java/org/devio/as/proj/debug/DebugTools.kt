package org.devio.`as`.proj.debug

import android.content.Intent
import android.os.Process
import org.devio.`as`.proj.common.utils.SPUtil
import org.devio.hi.library.util.AppGlobals

class DebugTools {

    fun buildVersion(): String {
        // 构建版本 ： 1.0.1
        //return "构建版本:" + BuildConfig.VERSION_CODE + "." + BuildConfig.VERSION_CODE
        return "构建版本:" + "1.0.1" + "." + "1"
    }

    fun buildTime(): String {
        //new date() 当前你在运行时拿到的时间，这个包，被打出来的时间
        return "构建时间：" + BuildConfig.BUILD_TIME
    }

    //fun buildEnvironment() {
    //测试环境,正式环境
    //return "构建环境: " + BuildConfig.DEBUG
    //}


    @HiDebug(name = "一键开启Https降级", desc = "将继承Http,可以使用抓包工具明文抓包")
    fun degrade2Http() {
        SPUtil.putBoolean("degrade_http", true)
        val context = AppGlobals.get()?.applicationContext ?: return
        val intent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        //得到了 启动页的 intent
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        //杀掉当前进程,并主动启动新的 启动页，以完成重启的动作
        Process.killProcess(Process.myPid())
    }
}
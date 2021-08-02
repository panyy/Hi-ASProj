package org.devio.as.proj.main;

import com.alibaba.android.arouter.launcher.ARouter;

import org.devio.as.proj.common.ui.component.HiBaseApplication;

public class HiApplication extends HiBaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }

        ARouter.init(this);
    }
}

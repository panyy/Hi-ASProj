package org.devio.as.proj.common.ui.component;

import android.app.Application;

import com.google.gson.Gson;

import org.devio.hi.library.log.HiConsolePrinter;
import org.devio.hi.library.log.HiFilePrinter;
import org.devio.hi.library.log.HiLogConfig;
import org.devio.hi.library.log.HiLogManager;


public class HiBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLog();
    }

    private void initLog() {
        HiLogManager.init(new HiLogConfig() {
            @Override
            public JsonParser injectJsonParser() {
                return (src) -> new Gson().toJson(src);
            }

            @Override
            public boolean includeThread() {
                return true;
            }
        }, new HiConsolePrinter(), HiFilePrinter.getInstance(getCacheDir().getAbsolutePath(), 0));
    }
}

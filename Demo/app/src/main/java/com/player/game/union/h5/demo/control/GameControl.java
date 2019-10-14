package com.player.game.union.h5.demo.control;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.player.game.union.h5.demo.model.MetaDataKey;

/**
 * Author      : PillowCase
 * Craete On   : 2019-10-11 19:23
 * Description :
 */
public class GameControl {
    private static GameControl instance;
    /**
     * 是否配置闪屏图
     */
    private boolean isSplash = false;

    public static GameControl getInstance() {
        if (instance == null) {
            synchronized (GameControl.class) {
                if (instance == null) {
                    instance = new GameControl();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        try {
            PackageManager manager = application.getPackageManager();
            if (manager != null) {
                ApplicationInfo info = manager.getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
                if (info != null) {
                    Bundle data = info.metaData;
                    if (data != null) {
                        isSplash = data.getBoolean(MetaDataKey.IS_SPLASH_CONFIG);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isSplash() {
        return isSplash;
    }
}

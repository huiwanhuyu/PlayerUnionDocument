package com.player.game.union.h5.demo;

import android.content.Context;
import android.content.res.Configuration;

import com.player.game.union.PlayerGameApplication;

/**
 * Author      : PillowCase
 * Craete On   : 2019-10-11 19:21
 * Description :
 */
public class GameApplication extends PlayerGameApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        PlayerGameApplication.onProxyCreate(this);
    }

    @Override
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        PlayerGameApplication.onProxyAttachBaseContext(this, context);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        PlayerGameApplication.onProxyConfigurationChanged(configuration);
    }
}

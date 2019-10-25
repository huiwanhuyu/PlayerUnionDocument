package com.player.game.union.h5.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.player.game.union.PlayerGameUnionSdk;
import com.player.game.union.h5.demo.control.GameControl;
import com.player.game.union.h5.demo.ui.GameWebView;
import com.player.game.union.impl.sdk.IGameUnionSdkCallback;
import com.player.game.union.model.SdkToken;
import com.player.game.union.model.code.GameUnionCode;
import com.player.game.union.model.sdk.GameUnionInitParams;

/**
 * Author      : PillowCase
 * Craete On   : 2019-10-11 18:44
 * Description :
 */
public class GameActivity extends Activity {
    private ImageView splashImg, backgroundImg;
    private Button loginBtn;
    private GameWebView mWebView;
    private RelativeLayout loadingLayout;

    /**
     * 是否初始化完成
     */
    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            log("onCreate", "设置主题Theme , Version:" + Build.VERSION.SDK_INT);
            setTheme(R.style.AppTheme);
        }
        if (GameControl.getInstance().isSplash()) {
            log("onCreate", "设置主题Theme , isSplash:" + GameControl.getInstance().isSplash());
            setTheme(R.style.AppTheme_Splash);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        PlayerGameUnionSdk.getInstance().onRestart(GameActivity.this);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.clearWebCache();

            mWebView.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        PlayerGameUnionSdk.getInstance().exit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        PlayerGameUnionSdk.getInstance().onActivityResult(GameActivity.this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        PlayerGameUnionSdk.getInstance().onNewIntent(GameActivity.this, intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PlayerGameUnionSdk.getInstance().onRequestPermissionsResult(GameActivity.this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        PlayerGameUnionSdk.getInstance().onConfigurationChanged(GameActivity.this, newConfig);
        super.onConfigurationChanged(newConfig);
    }

    private void initData() {
        splashImg.setVisibility(View.GONE);
        backgroundImg.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);

        if (GameControl.getInstance().isSplash()) {
            splashImg.setVisibility(View.VISIBLE);
            splashImg.setImageResource(R.drawable.player_game_splash);

            AlphaAnimation animation = new AlphaAnimation(1.0f, 0.1f);
            animation.setDuration(5000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    log("onAnimationEnd", "");
                    splashImg.setVisibility(View.GONE);

                    showLogin();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            splashImg.setAnimation(animation);
        } else {
            showLogin();
        }
    }

    private void initView() {
        splashImg = findViewById(R.id.player_splash_img);
        backgroundImg = findViewById(R.id.player_background_img);
        loginBtn = findViewById(R.id.player_login_btn);
        mWebView = findViewById(R.id.player_game_web);
        loadingLayout = findViewById(R.id.player_loading_layout);
    }

    private void showLogin() {
        backgroundImg.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.VISIBLE);

        splashImg.setVisibility(View.GONE);
        mWebView.setVisibility(View.GONE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInit) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PlayerGameUnionSdk.getInstance().login();
                        }
                    });
                } else {
                    Toast.makeText(GameActivity.this, "登录失败，未初始化或初始化失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!isInit) {
            GameUnionInitParams params = new GameUnionInitParams();
            //游戏主页面
            params.setGameActivity(GameActivity.this);
            //我方提供的AppId
            params.setAppId(getResources().getString(R.string.AppId));
            //我方提供的LoginKey（登录Key）
            params.setLoginKey(getResources().getString(R.string.AppKey));
            PlayerGameUnionSdk.getInstance().initSdk(params, gameUnionSdkCallback);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PlayerGameUnionSdk.getInstance().login();
                }
            });
        }
    }

    private void showLogout() {
        mWebView.clearWebCache();

        mWebView.setVisibility(View.GONE);
        splashImg.setVisibility(View.GONE);
        backgroundImg.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);
    }

    private void showGame(final String gameUrl) {
        log("showGame", "Game Url : " + gameUrl);
        mWebView.setVisibility(View.VISIBLE);

        backgroundImg.setVisibility(View.GONE);
        loginBtn.setVisibility(View.GONE);
        splashImg.setVisibility(View.GONE);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.clearWebCache();

                mWebView.loadUrl(gameUrl);
            }
        });
    }

    private IGameUnionSdkCallback gameUnionSdkCallback = new IGameUnionSdkCallback() {
        @Override
        public void initSuccess() {
            log("initSuccess", "初始化成功");
            isInit = true;
            Toast.makeText(GameActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingLayout != null && loadingLayout.getVisibility() == View.VISIBLE) {
                        loadingLayout.setVisibility(View.GONE);
                    }
                    PlayerGameUnionSdk.getInstance().login();
                }
            });
        }

        @Override
        public void loginSuccess(SdkToken token) {
            log("loginSuccess",
                    "登录成功\n用户信息：\n" + token +
                            "\nuid:" + token.getUserID() +
                            "\nuserName:" + token.getSdkUsername());
            showGame(token.getGameUrl());
        }

        @Override
        public void submitGameInfoSuccess() {
            log("submitGameInfoSuccess", "提交游戏角色信息成功");
        }

        @Override
        public void logout() {
            log("logout", "注销成功");
            showLogout();
            showLogin();
        }

        @Override
        public void payCallback(String sdkOrderId) {
            log("payCallback", "支付结果\n订单号：\n" + sdkOrderId);
        }

        @Override
        public void onExist(boolean isFinish) {
            log("onExist", "退出游戏,isFinish : " + isFinish);
            if (isFinish) {
                //如果isFinish为True,则退出游戏
                showLogout();

                GameActivity.this.finish();
                System.exit(0);
            }
        }

        @Override
        public void onErrorCallback(GameUnionCode code, String message) {
            log("onErrorCallback", "Code : " + code + " , Message : " + message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingLayout != null && loadingLayout.getVisibility() == View.VISIBLE) {
                        loadingLayout.setVisibility(View.GONE);
                    }
                }
            });
            Toast.makeText(GameActivity.this, "ErrorCode : " + code + " , Message : " + message, Toast.LENGTH_SHORT).show();
        }
    };

    public void log(final String s, final Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + o);
            }
        });
    }

    public void warn(final String s, final String s1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.w(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + s1);
            }
        });
    }

    public void error(final Throwable throwable, final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + throwable);
            }
        });
    }
}

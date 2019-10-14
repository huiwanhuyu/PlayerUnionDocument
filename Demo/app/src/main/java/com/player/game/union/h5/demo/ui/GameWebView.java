package com.player.game.union.h5.demo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.player.game.union.PlayerGameUnionSdk;
import com.player.game.union.h5.demo.GameActivity;
import com.player.game.union.h5.demo.R;
import com.player.game.union.model.GameUnionPayParams;
import com.player.game.union.model.GameUnionUserExtraData;

import org.json.JSONObject;

/**
 * Author      : PillowCase
 * Craete On   : 2019-10-11 19:24
 * Description :
 */
public class GameWebView extends WebView {
    private String[] getHeightJs = new String[]{
            "window.screen.availHeight",//屏幕可用工作区高度
            "window.screen.availWidth",// 屏幕可用工作区宽度
            "window.screen.height",//屏幕分辨率的高
            "window.screen.width",//屏幕分辨率的宽
            "document.body.offsetWidth",//网页可见区域宽(包括边线的宽)
            "document.body.offsetHeight",//网页可见区域高(包括边线的高)
            "document.body.clientWidth",//网页可见区域宽
            "document.body.clientHeight",//网页可见区域高
            "document.body.scrollWidth",//网页正文全文宽
            "document.body.scrollHeight",//网页正文全文高
            "document.body.scrollTop"
    };

    public GameWebView(Context context) {
        this(context, null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public GameWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //支持JavaScript
        getSettings().setJavaScriptEnabled(true);
        //设置允许js弹出alert对话框
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //不使用缓存，只从网络获取数据。==>改为允许使用本地缓存，优先读取本地缓存，无缓存再读取网络
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 JavaScript 来操作这些数据。）
        getSettings().setDomStorageEnabled(true);
        //设置默认编码
        getSettings().setDefaultTextEncodingName("UTF-8");
        // 设置可以访问文件
        getSettings().setAllowFileAccess(true);
        //跨域访问
        getSettings().setAllowFileAccessFromFileURLs(true);
        getSettings().setSupportZoom(true);
        getSettings().setLoadWithOverviewMode(true);
        //调试
        //WebView.setWebContentsDebuggingEnabled(true);

        setBackgroundColor(0x00000000);
        requestFocus();
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        getSettings().setDisplayZoomControls(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setAllowContentAccess(true);
        getSettings().setMediaPlaybackRequiresUserGesture(false);
        addJavascriptInterface(new JSBridge(), "game_pay");

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
                try {
                    //支持插件下载
                    Uri localUri = Uri.parse(url);
                    Intent localIntent = new Intent(Intent.ACTION_VIEW, localUri);
                    localIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    getContext().startActivity(localIntent);
                } catch (Exception e) {
                    error(e, "onDownloadStart");
                }
            }
        });

        setWebViewClient(mWebViewClient);
        setWebChromeClient(mWebChromeClient);
    }

    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onCloseWindow(WebView window) {
            log("onCloseWindow", "");
            super.onCloseWindow(window);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            log("onConsoleMessage", "consoleMessage:" + consoleMessage + "\nmessage:" + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            log("onJsAlert", "url:" + url + "\nmessage:" + message + "\nresult:" + result);
            return false;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            log("onJsConfirm", "url:" + url + "\nmessage:" + message + "\nresult:" + result);
            return false;
        }
    };

    /**
     * 清除WebView缓存
     */
    public void clearWebCache() {
        log("clearCache", "");
        try {
            clearCache(true);
            clearHistory();
            clearFormData();
            loadUrl("");
        } catch (Exception e) {
            error(e, "clearCache");
        }
    }


    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            log("shouldOverrideUrlLoading", "url:" + url);
            if (url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    error(e, "shouldOverrideUrlLoading");
                }
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            log("onPageFinished", "url:" + url);
            super.onPageFinished(view, url);
            for (int i = 0; i < getHeightJs.length; i++) {
                String js = "javascript:game_pay.onGetWebContentHeight(\"" + getHeightJs[i] + "\"," + getHeightJs[i] + ")";
                view.loadUrl(js);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            log("onPageStarted", "url:" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            log("onReceivedSslError", "");
            //支持ssl
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            log("onReceivedError", "errorCode : " + errorCode + "\ndescription : " + description + "\nfailingUrl : " + failingUrl);
            setVisibility(View.GONE);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    };

    public class JSBridge {

        @JavascriptInterface
        public void onGetWebContentHeight(String type, int height) {
            log("onGetWebContentHeight", "type:" + type + " , data:" + height);
        }

        //支付js接口
        @JavascriptInterface
        public String pay(String data) {
            log("pay", "data:" + data);
            int coinNum;
            int ratio;
            String productID;
            String productName;
            String productDesc;
            String price;
            int buyNum;
            String roleID;
            String roleName;
            int roleLevel;
            String serverID;
            String serverName;
            String vip;
            String payNotifyUrl;
            String extension;
            String orderID = "";
            try {
                GameUnionPayParams params = new GameUnionPayParams();
                JSONObject jsonObject = new JSONObject(data);
                coinNum = jsonObject.optInt("coinNum");
                ratio = jsonObject.optInt("ratio");
                productID = jsonObject.optString("productID");
                productName = jsonObject.optString("productName");
                productDesc = jsonObject.optString("productDesc");
                price = jsonObject.optString("price");
                buyNum = jsonObject.optInt("buyNum");
                roleID = jsonObject.optString("roleID");
                roleName = jsonObject.optString("roleName");
                roleLevel = jsonObject.optInt("roleLevel");
                serverID = jsonObject.optString("serverID");
                serverName = jsonObject.optString("serverName");
                vip = jsonObject.optString("vip");
                payNotifyUrl = jsonObject.optString("payNotifyUrl");
                extension = jsonObject.optString("extension");
                orderID = jsonObject.optString("orderID");

                params.setCoinNum(coinNum);                //游戏币数量 没有则传“”，不能为空
                params.setRatio(ratio);                    //兑换比例(RMB与游戏币兑换比例) （必填）一般为10
                params.setProductID(productID);            //商品id （必填）
                params.setProductName(productName);        //商品名 （必填）
                params.setProductDesc(productDesc);        //商品描述 （必填）
                params.setPrice(price);                    //商品单价（RMB:元，必填）
                params.setBuyNum(buyNum);                  //购买数量    (必填)
                params.setRoleID(roleID);                  //游戏角色id  （必填）
                params.setRoleName(roleName);              //游戏角色名 （必填）
                params.setRoleLevel(roleLevel);            //角色等级 （必填）
                params.setServerID(serverID);              //游戏服务器号 （必填）
                params.setServerName(serverName);          //游戏服务器名 （必填）
                params.setVip(vip);                        //vip等级 （必填）
                params.setPayNotifyUrl(payNotifyUrl);      //充值回调地址
                params.setExtension(extension);            //扩展字段 （必填）没有则传“”，不能为空
                params.setOrderID(orderID);                //订单号 （一定要的）
                PlayerGameUnionSdk.getInstance().pay(params);
            } catch (Exception e) {
                error(e, "Pay");
            }
            log("pay", "orderID:" + orderID);
            return orderID;
        }

        //上报游戏信息js接口
        @JavascriptInterface
        public void enterGame(String data) {
            log("enterGame", "data:" + data);
            String userID;         //进入游戏或者提升等级等操作
            String roleID;         //玩家ID
            String roleName;       //角色ID
            String roleLevel;      //角色名称
            long roleCTime;        //角色等级
            String rayLevel;      //角色创建时间(单位：秒)， 长度 10，获取服务器存储的时间，不可用手机本地时间
            String serverID;         //此值VIP等级，不是角色等级。
            String serverName;    //服务器ID
            String userName;      //服务器名称
            String extension;
            int dataType = 3;
            try {
                GameUnionUserExtraData userExtraData = new GameUnionUserExtraData();
                JSONObject jsonObject = new JSONObject(data);
                userID = jsonObject.optString("userID");
                roleID = jsonObject.optString("roleID");
                roleName = jsonObject.optString("roleName");
                roleLevel = jsonObject.optString("roleLevel");
                roleCTime = jsonObject.optLong("roleCTime");
                rayLevel = jsonObject.optString("rayLevel");
                serverID = jsonObject.optString("serverID");
                serverName = jsonObject.optString("serverName");
                userName = jsonObject.optString("userName");
                extension = jsonObject.optString("extension");
                dataType = jsonObject.optInt("dataType");

                switch (dataType) {
                    case 2:
                        dataType = GameUnionUserExtraData.TYPE_CREATE_ROLE;
                        break;
                    case 3:
                        dataType = GameUnionUserExtraData.TYPE_ENTER_GAME;
                        break;
                    case 4:
                        dataType = GameUnionUserExtraData.TYPE_LEVEL_UP;
                        break;
                }

                userExtraData.setDataType(dataType);
                userExtraData.setUserId(userID);
                userExtraData.setRoleId(roleID);
                userExtraData.setRoleName(roleName);
                userExtraData.setRoleLevel(roleLevel);
                userExtraData.setRoleCTime(roleCTime);
                userExtraData.setPayLevel(rayLevel);
                userExtraData.setServerId(serverID);
                userExtraData.setServerName(serverName);
                userExtraData.setUserName(userName);
                userExtraData.setExtension(extension);
                PlayerGameUnionSdk.getInstance().submitGameRoleInfo(userExtraData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void switchLogin() {
            log("switchLogin", "");
            PlayerGameUnionSdk.getInstance().switchLogin();
        }
    }

    public void log(final String s, final Object o) {
        Log.i(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + o);
    }

    public void warn(final String s, final String s1) {
        Log.w(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + s1);
    }

    public void error(final Throwable throwable, final String s) {
        Log.e(getResources().getString(R.string.game_name), "Tag:" + s + "\nMessage:" + throwable);
    }
}

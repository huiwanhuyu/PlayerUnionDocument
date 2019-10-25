# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers       # 指定不去忽略包可见的库类的成员
-dontpreverify      # 混淆时是否做预校验
-verbose        # 混淆时是否记录日志
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*      # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
-ignorewarnings     # 是否忽略检测，（是）
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
#-ignorewarnings -keep class * { public private *; }

#如果有引用v4包可以添加下面这行
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#表示不混淆R文件中的所有静态字段
-keep class **.R$* {
    public static <fields>;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
#---------------------------------第三方混淆------------------------------------
# OkHttp
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# OkHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

# Gson
-keepattributes Signature-keepattributes,*Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。

#腾讯广点通DMP行为数据上报
-dontwarn com.qq.gdt.action.**
-keep class com.qq.gdt.action.** {*;}

-keep class com.pillowcase.data.report.gdt.GdtDmpManager{
    public *;
}
#头条数据上报
-keep class com.bytedance.**{
    *;
}
-keep class com.ss.android.common.**{
    *;
}
#OAID
-keep class com.bun.miitmdid.** {*;}

-keep class com.pillowcase.data.report.tt.TtAppLogManager{
    public *;
}
-keep class com.pillowcase.data.report.tt.TtApplication{
    public *;
}
-keep class com.pillowcase.data.report.tt.impl.ISupportListener{
    public *;
}
#模拟器检测
-keep class com.pillowcase.normal.tools.emulator.**{*;}
#LoggerTools
-keep class com.pillowcase.normal.tools.logger.**{*;}
#PermissionUtils
-keep class com.pillowcase.normal.tools.permission.**{*;}
#---------------------------------SDK处理------------------------------------
-keep class com.player.sdk.PlayerSdk{*;}
-keep class com.player.sdk.PlayerApplication{public *;}
-keep class com.player.sdk.impl.sdk.ISdkListener{*;}
-keep class com.player.sdk.model.sdk.GameRoleParams{*;}
-keep class com.player.sdk.model.sdk.InitParams{*;}
-keep class com.player.sdk.model.sdk.PayParams{*;}
-keep class com.player.sdk.model.sdk.UserParams{*;}
-keep class com.player.sdk.model.code.Code{*;}
-keep class com.player.sdk.model.code.CodeMessage{*;}

-keep class com.player.sdk.ui.base.BaseActivity{
    public *;
}
-keep class com.player.sdk.ui.PlayerActivity{
    public *;
}

-keep class com.player.sdk.model.api.ApiRequest{*;}
-keep class com.player.sdk.model.api.DataRequest{*;}
-keep class com.player.sdk.model.local.LocalUserBean{*;}
-keep class com.player.sdk.model.pay.PlayerPayParams{*;}
-keep class com.player.sdk.model.CurrentUserBean{*;}
-keep class com.player.sdk.model.CustomerInfo{*;}

-keep class com.player.sdk.manager.local.YybLocalAccountManager{
    public *;
}

-keep class com.player.sdk.utils.AssetsUtils{*;}
-keep class com.player.sdk.utils.Md5Utils{*;}
-keep class com.player.sdk.utils.RsaUtils{*;}
-keep class com.player.sdk.utils.NetWorkUtils{*;}
-keep class com.player.sdk.utils.SharedPreferencesUtils{*;}
-keep class com.player.sdk.utils.DeviceUtils{*;}
#----------------------------------------------------------------------------

#---------------------------------内部处理------------------------------------
-keep class com.player.game.union.PlayerGameUnionSdk{*;}
-keep class com.player.game.union.PlayerGameApplication{
    public *;
}

-keep class com.player.game.union.model.sdk.GameUnionInitParams{*;}
-keep class com.player.game.union.model.GameUnionUserExtraData{*;}
-keep class com.player.game.union.model.GameUnionPayParams{*;}

-keep class com.player.game.union.model.api.GameUnionApiMethod{*;}
-keep class com.player.game.union.model.api.GameUnionApiRequest{*;}
-keep class com.player.game.union.model.code.GameUnionCode{*;}
-keep class com.player.game.union.model.GameUnionOrderParams{*;}
-keep class com.player.game.union.model.SdkToken{*;}

-keep class com.player.game.union.impl.sdk.IApplicationListener{*;}
-keep class com.player.game.union.impl.sdk.IGameUnionSdkCallback{*;}

-keep class com.player.game.union.channel.**{public *;}
#----------------------------------------------------------------------------


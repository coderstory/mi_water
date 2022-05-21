package cn.coderstory.miui.water

import android.content.pm.ApplicationInfo
import android.widget.TextView
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONStringer
import java.net.URL


class MainHook : XposedHelper(), IXposedHookLoadPackage {
    var prefs = XSharedPreferences(BuildConfig.APPLICATION_ID, "conf")

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.equals("com.android.thememanager")) {
            if (prefs.getBoolean("removeThemeAd", true)) {
                hookAllMethods(
                    "com.android.thememanager.basemodule.ad.model.AdInfoResponse",
                    lpparam.classLoader,
                    "isAdValid",
                    XC_MethodReplacement.returnConstant(false)
                )
                hookAllMethods(
                    "com.android.thememanager.basemodule.ad.model.AdInfoResponse",
                    lpparam.classLoader,
                    "checkAndGetAdInfo",
                    XC_MethodReplacement.returnConstant(null)
                )
            }

        }


        if (lpparam.packageName.equals("com.miui.packageinstaller")) {
            if (prefs.getBoolean("removeInstallerAd", true)) {
                XposedHelpers.findAndHookConstructor(
                    "com.miui.packageInstaller.model.MarketControlRules",
                    lpparam.classLoader,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            var obj = param.thisObject;
                            XposedHelpers.setBooleanField(obj, "showAdsBefore", false);
                            XposedHelpers.setBooleanField(obj, "showAdsAfter", false);
                            XposedHelpers.setBooleanField(obj, "singletonAuthShowAdsBefore", false);
                            XposedHelpers.setBooleanField(obj, "singletonAuthShowAdsAfter", true);
                            XposedHelpers.setBooleanField(obj, "useSystemAppRules", true);
                        }
                    })
            }
            if (prefs.getBoolean("removeInstallerAuth", true)) {
                findAndHookMethod(
                    "java.net.URL",
                    lpparam.classLoader,
                    "openConnection",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            var obj: URL = param.thisObject as URL
                            XposedBridge.log("current host is ${obj.host}")
                            if (obj.host.equals("api-installer.pt.xiaomi.com") || obj.host.equals("preview-api.installer.xiaomi.com")) {
                                XposedHelpers.setObjectField(obj, "host", "www.baidu.com");
                            }
                        }
                    })
            }

            if (prefs.getBoolean("removeInstallerLimit", true)) {
                findAndHookMethod(
                    "android.net.Uri",
                    lpparam.classLoader,
                    "parse", String::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (param.args[0].toString().contains("com.miui.securitycenter")) {
                                param.args[0] = "ddddd"
                            }
                        }
                    })

                // return (arg2.flags & 1) > 0 || arg2.uid < 10000;
                findAndHookMethod(
                    "com.android.packageinstaller.d",
                    lpparam.classLoader,
                    "a", ApplicationInfo::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.result = true
                        }
                    })
                 // coord: (0,35,33) | addr: Lcom/miui/smsextra/http/HttpRequest$Builder;->request()Lcom/miui/smsextra/http/RequestResult; | loc: ?
            hookAllMethods(
                "com.miui.smsextra.http.HttpRequest\$Builder",
                lpparam.classLoader,
                "request",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        var result = param.result
                        XposedBridge.log(JSONStringer().value(result).toString());
                    }
                }
               )
            }
        }

        if (lpparam.packageName.equals("com.android.mms")) {
            if (prefs.getBoolean("removeMmsAd", true)) {
                hookAllMethods(
                    "com.miui.smsextra.http.RequestResult",
                    lpparam.classLoader,
                    "data",
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            var result = param.result
                            if (result.toString().contains("modules")) {
                                param.result = "{}";
                            }
                        }
                    }
                )
                hookAllMethods(
                    "com.miui.smsextra.ui.UnderstandButton",
                    lpparam.classLoader,
                    "requestAD",
                    XC_MethodReplacement.returnConstant(false)
                )
            }
        }

        if (lpparam.packageName.equals("com.miui.systemAdSolution")) {
            if (prefs.getBoolean("removeSplashAd2", false)) {

                findAndHookMethod(
                    "com.xiaomi.ad.entity.cloudControl.cn.CNDeskFolderControlInfo",
                    lpparam.classLoader,
                    "isCloseAd",
                    XC_MethodReplacement.returnConstant(true)
                )

                findAndHookMethod(
                    "com.xiaomi.ad.common.pojo.AdType",
                    lpparam.classLoader,
                    "valueOf",
                    Int::class.java,
                    XC_MethodReplacement.returnConstant(0)
                )
            }
        }

        if (lpparam.packageName.equals("com.miui.securitycenter")) {
            if (prefs.getBoolean("disableWaiting", true)) {
                findAndHookMethod("android.widget.TextView",
                    lpparam.classLoader,
                    "setEnabled",
                    Boolean::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.args[0] = true
                        }
                    })
                findAndHookMethod("android.widget.TextView",
                    lpparam.classLoader,
                    "setText",
                    CharSequence::class.java,
                    TextView.BufferType::class.java,
                    Boolean::class.java,
                    Int::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (param.args.isNotEmpty() && param.args[0]?.toString()
                                    ?.startsWith("确定(") == true
                            ) {
                                param.args[0] = "确定"
                            }
                        }
                    })
            }
        }
    }
}

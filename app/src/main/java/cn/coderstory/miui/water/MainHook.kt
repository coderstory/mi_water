package cn.coderstory.miui.water

import android.widget.TextView
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.system.exitProcess


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

        } else if (lpparam.packageName.equals("com.miui.packageinstaller")) {
            if (prefs.getBoolean("removeInstallerAd", true)) {
                XposedHelpers.findAndHookConstructor(
                    "com.miui.packageInstaller.model.MarketControlRules",
                    lpparam.classLoader,
                    object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            var obj = param.thisObject;
                            XposedHelpers.setBooleanField(obj, "isSecurityNotAllowed", false);
                            XposedHelpers.setBooleanField(obj, "showAdsBefore", false);
                            XposedHelpers.setBooleanField(obj, "showAdsAfter", false);
                            XposedHelpers.setBooleanField(obj, "useSystemAppRules", true);
                            XposedHelpers.setBooleanField(obj, "storeListed", true);
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

                // if(arg5 == 33 || arg5 == 34) {
                // 无法直接安装系统app
                findAndHookMethod(
                    "com.miui.packageInstaller.d.m",
                    lpparam.classLoader,
                    "a", Int::class.java,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val it: Int = param.args[0] as Int;
                            if (it == 44) {
                                param.args[0] = 33
                            }
                        }
                    })
            }
        } else if (lpparam.packageName.equals("com.miui.systemAdSolution")) {
            if (prefs.getBoolean("removeSplashAd", true)) {
                listOf(
                    "com.miui.systemAdSolution.splashAd.SystemSplashAdService",
                    "com.miui.systemAdSolution.splashAd.ExternalMediaSplashAdService",
                    "com.miui.systemAdSolution.splashscreen.SplashScreenServiceV2"
                ).forEach {
                    hookAllConstructors(it,
                        lpparam.classLoader,
                        object : XC_MethodReplacement() {
                            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                                return null;
                            }
                        })
                }
            }
        } else if (lpparam.packageName.equals("com.android.traceur")) {
            listOf(
                "com.android.traceur.TraceService",
                "com.android.traceur.QsService",
                "com.android.traceur.MainActivity",
                "com.android.traceur.StorageProvider"
            ).forEach {
                hookAllConstructors(
                    it,
                    lpparam.classLoader,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            exitProcess(0)
                        }
                    })
            }
        } else if (lpparam.packageName.equals("com.miui.analytics")) {
            listOf(
                "com.miui.analytics.onetrack.OneTrackService",
                "com.miui.analytics.onetrack.TrackService",
                "com.miui.analytics.EventService",
                "com.miui.analytics.AppenderService",
                "com.miui.analytics.AnalyticsService",
                "com.miui.analytics.Analytics",
                "com.miui.analytics.AnalyticsProvider",
                "com.miui.analytics.AnalyticsReceiver"
            ).forEach {
                hookAllConstructors(
                    it,
                    lpparam.classLoader,
                    object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            exitProcess(0)
                        }
                    })
            }
        } else if (lpparam.packageName.equals("com.miui.securitycenter")) {
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
                            if (param.args[0].toString().startsWith("确定(")) {
                                param.args[0] = "确定"
                            }
                        }
                    })
            }
        }
    }
}
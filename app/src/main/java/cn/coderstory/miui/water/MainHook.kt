package cn.coderstory.miui.water

import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage


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

        }
    }
}
package cn.coderstory.miui.water

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : XposedHelper(), IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName.equals("com.android.thememanager")) {
            if (findClassWithOutLog(
                    "com.android.thememanager.basemodule.ad.model.AdInfoResponse",
                    lpparam.classLoader
                ) != null
            ) {
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
    }

}
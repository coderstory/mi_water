package cn.coderstory.miui.water

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

open class XposedHelper {
    companion object {
        fun findAndHookMethod(
            p1: String?,
            lpparam: ClassLoader?,
            p2: String?,
            vararg parameterTypesAndCallback: Any?
        ) {
            try {
                if (findClass(p1, lpparam) != null) {
                    XposedHelpers.findAndHookMethod(p1, lpparam, p2, *parameterTypesAndCallback)
                }
            } catch (e: Throwable) {
                 XposedBridge.log(e)
            }
        }

        fun hookAllMethods(
            p1: String?,
            lpparam: ClassLoader?,
            methodName: String?,
            parameterTypesAndCallback: XC_MethodHook?
        ) {
            try {
                val packageParser = findClass(p1, lpparam)
                XposedBridge.hookAllMethods(packageParser, methodName, parameterTypesAndCallback)
            } catch (e: Throwable) {
                 XposedBridge.log(e)
            }
        }

        private fun findClass(className: String?, classLoader: ClassLoader?): Class<*>? {
            try {
                return XposedHelpers.findClass(className,classLoader)
            } catch (e: Throwable) {
                 XposedBridge.log(e)
            }
            return null
        }

        fun hookAllConstructors(p1: String?, classLoader: ClassLoader?,parameterTypesAndCallback: XC_MethodHook) {
            try {
                val packageParser = findClass(p1, classLoader)
                hookAllConstructors(packageParser, parameterTypesAndCallback)
            } catch (e: Throwable) {
                 XposedBridge.log(e)
            }
        }

        private fun hookAllConstructors(
            hookClass: Class<*>?,
            callback: XC_MethodHook
        ): Set<XC_MethodHook.Unhook>? {
            return try {
                XposedBridge.hookAllConstructors(hookClass, callback)
            } catch (e: Throwable) {
                 XposedBridge.log(e)
                null
            }
        }
        fun findClassWithOutLog(className: String?, classLoader: ClassLoader?): Class<*>? {
            try {
                return className?.let { Class.forName(it, false, classLoader) }
            } catch (e: Exception) {
                // 忽略
            }
            return null
        }
    }
}
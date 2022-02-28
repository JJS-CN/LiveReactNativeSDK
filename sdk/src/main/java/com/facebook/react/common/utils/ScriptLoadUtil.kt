package com.facebook.react.common.utils

import android.content.Context
import android.util.Log
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.CatalystInstance
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.common.ReactUtil
import com.facebook.react.common.bridge.BridgeUtil
import java.lang.reflect.InvocationTargetException
import java.util.HashSet

object ScriptLoadUtil {
  internal const val TAG = "ScriptLoadUtil"
  const val REACT_DIR = "react_bundles"

  /**
   * set this value when debug,you can set BuildConfig.DEBUG if need
   */
  const val MULTI_DEBUG = false //需要debug的时候设置成true,你也可以设置成跟BuildConfig.DEBUG一致
  private val sLoadedScript: MutableSet<String>? = HashSet()
  fun recreateReactContextInBackgroundInner(manager: ReactInstanceManager?) {
    try { //recreateReactContextInBackground replace this
      val method =
        ReactInstanceManager::class.java.getDeclaredMethod("recreateReactContextInBackgroundInner")
      method.isAccessible = true
      method.invoke(manager)
    } catch(e: NoSuchMethodException) {
      e.printStackTrace()
    } catch(e: IllegalAccessException) {
      e.printStackTrace()
    } catch(e: InvocationTargetException) {
      e.printStackTrace()
    }
  }

  fun moveToResumedLifecycleState(manager: ReactInstanceManager?, force: Boolean) {
    try {
      val method = ReactInstanceManager::class.java.getDeclaredMethod(
        "moveToResumedLifecycleState",
        Boolean::class.javaPrimitiveType)
      method.isAccessible = true
      method.invoke(manager, force)
    } catch(e: NoSuchMethodException) {
      e.printStackTrace()
    } catch(e: IllegalAccessException) {
      e.printStackTrace()
    } catch(e: InvocationTargetException) {
      e.printStackTrace()
    }
  }

  fun setJsModuleName(rootView: ReactRootView?, moduleName: String?) {
    try {
      val field = ReactRootView::class.java.getDeclaredField("mJSModuleName")
      field.isAccessible = true
      field[rootView] = moduleName
    } catch(e: NoSuchFieldException) {
      e.printStackTrace()
    } catch(e: IllegalAccessException) {
      e.printStackTrace()
    }
  }

  fun getCatalystInstance(host: ReactNativeHost): CatalystInstance? {
    val manager = host.reactInstanceManager
    if(manager == null) {
      Log.e(TAG, "manager is null!!")
      return null
    }
    val context = manager.currentReactContext
    if(context == null) {
      Log.e(TAG, "context is null!!")
      return null
    }
    return context.catalystInstance
  }

  fun setJsBundleAssetPath(reactContext: ReactContext, bundleAssetPath: String?) {
    reactContext
      .getJSModule(RCTDeviceEventEmitter::class.java)
      .emit("sm-bundle-changed", bundleAssetPath)
  }

  fun getSourceUrl(instance: CatalystInstance?): String? {
    return ReactUtil.getSourceUrl(instance!!)
  }

  fun loadScriptFromAsset(context: Context?,
                          instance: CatalystInstance?,
                          assetName: String, isSync: Boolean) {
    if(sLoadedScript!!.contains(assetName)) {
      return
    }
    BridgeUtil.loadScriptFromAsset(context!!, instance!!, assetName, isSync)
    sLoadedScript.add(assetName)
  }

  fun loadScriptFromFile(fileName: String?,
                         instance: CatalystInstance?,
                         sourceUrl: String, isSync: Boolean) {
    if(sLoadedScript!!.contains(sourceUrl)) {
      return
    }
    BridgeUtil.loadScriptFromFile(fileName, instance!!, sourceUrl, isSync)
    sLoadedScript.add(sourceUrl)
  }

  fun clearLoadedRecord() {
    if(sLoadedScript != null) {
      sLoadedScript.clear()
    }
  }
}
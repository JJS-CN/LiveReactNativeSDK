package com.facebook.react.common.bridge

import android.content.Context
import com.facebook.react.bridge.CatalystInstance
import com.facebook.react.bridge.CatalystInstanceImpl

object BridgeUtil {
  fun loadScriptFromAsset(context: Context,
                          instance: CatalystInstance,
                          assetName: String, loadSynchronously: Boolean) {
    var source = assetName
    if(!assetName.startsWith("assets://")) {
      source = "assets://$assetName"
    }
    (instance as CatalystInstanceImpl).loadScriptFromAssets(
      context.assets,
      source,
      loadSynchronously)
  }

  fun loadScriptFromFile(fileName: String?,
                         instance: CatalystInstance,
                         sourceUrl: String?, loadSynchronously: Boolean) {
    (instance as CatalystInstanceImpl).loadScriptFromFile(fileName, sourceUrl, loadSynchronously)
  }
}
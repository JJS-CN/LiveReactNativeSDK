package com.live.react.demo

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.ActivityEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

/**
 *  Class: 填充后续开发可能需要的基础数据：网络请求、公参、用户信息
 *  Other: 需要与ios统一格式，这样在RN代码中才能使用同一套逻辑
 *  Create by jsji on  2021/12/20.
 */
open class BaseRnConfigModule(reactApplicationContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactApplicationContext) {
  init {
    reactApplicationContext.addActivityEventListener(object : ActivityEventListener {
      override fun onActivityResult(activity: Activity?,
                                    requestCode: Int,
                                    resultCode: Int,
                                    data: Intent?) {

      }

      override fun onNewIntent(intent: Intent?) {

      }

    })


  }

  override fun getName(): String {
    return "XARNBridgeMethod"
  }

  @ReactMethod
  open fun closeRNView() {
    Log.e("BaseRnConfigModule", "closeRNView")
    currentActivity?.finish()
  }

}
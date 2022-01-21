package com.live.react.demo

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

/**
 *  Class: 填充后续开发可能需要的基础数据：网络请求、公参、用户信息
 *  Other: 需要与ios统一格式，这样在RN代码中才能使用同一套逻辑
 *  Create by jsji on  2021/12/20.
 */
class BaseRnConfigModule(reactApplicationContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactApplicationContext) {
  override fun getName(): String {
    return "BaseRnConfigModule"
  }

}
package com.facebook.react.common

import com.facebook.react.bridge.CatalystInstance

object ReactUtil {
  fun  getSourceUrl(instance: CatalystInstance): String? {
    return instance.sourceURL
  }
}
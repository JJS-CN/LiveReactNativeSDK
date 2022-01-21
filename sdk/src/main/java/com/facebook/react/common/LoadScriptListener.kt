package com.facebook.react.common

interface LoadScriptListener {
  fun onLoadComplete(success: Boolean, scriptPath: String?)
}
package com.facebook.react.common.utils

interface UpdateProgressListener {
  fun updateProgressChange(precent: Int)
  fun complete(success: Boolean)
}
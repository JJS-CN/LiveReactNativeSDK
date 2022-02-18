package com.live.react.demo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.facebook.react.common.ScriptType
import com.facebook.react.common.utils.RnBundle
import com.facebook.soloader.SoLoader

/**
 *  Class:
 *  Other:
 *  Create by jsji on  2022/1/18.
 */
class LauncherActivity : AppCompatActivity() {
  companion object {
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var v = TextView(this)
    v.text = "跳转"
    v.setPadding(10)
    v.setOnClickListener {
      DemoReactActivity.start(this, "test")
    }
    setContentView(v)
  }
}
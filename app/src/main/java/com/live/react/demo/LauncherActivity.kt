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
      Toast.makeText(this, "ssssss", Toast.LENGTH_SHORT).show()
      var rnBundle = RnBundle("", ScriptType.NETWORK, "", "")
/*      rnBundle.scriptType = ScriptType.ASSET
      rnBundle.scriptPath = "index.android.bundle"
      rnBundle.scriptUrl = "index.android.bundle"*/
      rnBundle.scriptType = ScriptType.NETWORK
      rnBundle.moduleName = "app"
      rnBundle.scriptPath = "index.android.bundle"
      rnBundle.scriptUrl = "http://dl1.yuntuds.com/download?key=cc71084a962c84dd4b14856ff8e493fc2"
      DemoReactActivity.start(this, rnBundle)
    }
    setContentView(v)
  }
}
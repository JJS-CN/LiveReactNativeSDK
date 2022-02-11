package com.live.react.demo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.react.AsyncReactActivity
import com.facebook.react.ReactPackage
import com.facebook.react.common.RNLoadingConfig
import com.facebook.react.common.ScriptType
import com.facebook.react.common.utils.RnBundle

/**
 *  Class: 需要正式打包之后使用
 *  Other: 定义好自己的默认图，加载失败等逻辑处理
 *  Create by jsji on  2021/12/15.
 */
open class DemoReactActivity : AsyncReactActivity() {
  companion object {
    fun start(context: Context, rnBundle: RnBundle) {
      Log.e("AsyncReactActivity", "start:" + "  bundle:" + rnBundle.toString())
      val intent = Intent(context, DemoReactActivity::class.java)
      intent.putExtra(INTENT_KEY_RNBUNDLE, rnBundle)
      context.startActivity(intent)
    }
  }

  override fun getAppReactPackages(): List<ReactPackage>? {
    return null
  }

  override fun getRnLoadConfig(loadConfig: RNLoadingConfig): RNLoadingConfig {
    return loadConfig
  }

  override fun loadComplete(isSuccess: Boolean) {
  }

  override fun loadStart() {
  }

  override fun showLoading() {
  }

  override fun updateDownloadProgress(precent: Int) {
  }


  /*override fun onPause() {
    super.onPause()
    var group = window.decorView.findViewById<FrameLayout>(android.R.id.content)
    val button = Button(this)
    button.text = "加载另一个bundle"
    button.setOnClickListener {
      val rnBundle = RnBundle()
      rnBundle.scriptType = ScriptType.ASSET
      rnBundle.scriptPath = "index2.android.bundle"
      rnBundle.scriptUrl = "index2.android.bundle"
      reload("reactnative_multibundler2", rnBundle)
    }
    val lp = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.WRAP_CONTENT,
      FrameLayout.LayoutParams.WRAP_CONTENT)
    button.layoutParams = lp
    group.addView(button)
  }*/
}
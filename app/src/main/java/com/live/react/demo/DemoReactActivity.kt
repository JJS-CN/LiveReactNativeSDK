package com.live.react.demo

import android.content.Context
import android.content.Intent
import android.util.Log
import com.facebook.react.AsyncReactActivity
import com.facebook.react.common.utils.RnBundle

/**
 *  Class: 需要正式打包之后使用
 *  Other: 定义好自己的默认图，加载失败等逻辑处理
 *  Create by jsji on  2021/12/15.
 */
open class DemoReactActivity : AsyncReactActivity() {
  companion object {
    fun start(context: Context, componentName: String, rnBundle: RnBundle) {
      Log.e("AsyncReactActivity", "start:" + componentName + "  bundle:" + rnBundle.toString())
      val intent = Intent(context, DemoReactActivity::class.java)
      intent.putExtra(INTENT_KEY_COMPONENT_NAME, componentName)
      intent.putExtra(INTENT_KEY_RNBUNDLE, rnBundle)
      context.startActivity(intent)
    }
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

  override fun showLoading() {
  }

  override fun dismissLoading() {
  }

  override fun updateDownloadProgerss(precent: Int) {
  }
}
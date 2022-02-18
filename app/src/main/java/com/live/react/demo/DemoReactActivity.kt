package com.live.react.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
    var pageIdKey = "pageIdKey"
    fun start(context: Context, pageId: String) {
      Log.e("AsyncReactActivity", "start:" + "  pageIdKey:" + pageId)
      val intent = Intent(context, DemoReactActivity::class.java)
      intent.putExtra(pageIdKey, pageId)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var rnBundle = RnBundle("", "", "")
    val pageId = intent.getStringExtra(pageIdKey)
    rnBundle.md5 = "33333333333"
    rnBundle.pageId = pageId!!
    rnBundle.scriptUrl = "http://dl1.yuntuds.cn/download?key=6e114c46eb667d98066da14198d8f36f2"

    startLoadRNBundle(rnBundle)
  }

  override fun getAppReactPackages(): List<ReactPackage>? {
    return null
  }


  override fun getRnLoadConfig(loadConfig: RNLoadingConfig): RNLoadingConfig {
    ((loadConfig.errorView as ViewGroup).getChildAt(1) as ViewGroup).getChildAt(1)
      .setOnClickListener {
        var rnBundle = RnBundle("", "", "")
/*      rnBundle.scriptType = ScriptType.ASSET
      rnBundle.scriptPath = "index.android.bundle"
      rnBundle.scriptUrl = "index.android.bundle"*/
        rnBundle.md5 = "33333333333"
        rnBundle.pageId = "1"
        rnBundle.scriptUrl = "http://dl1.yuntuds.cn/download?key=6e114c46eb667d98066da14198d8f36f"
        reload(rnBundle)
      }
    return loadConfig
  }

  override fun loadComplete(isSuccess: Boolean) {
    println("loadComplete:" + isSuccess)
  }

  override fun loadStart() {
    println("loadStart:")
  }

  override fun showLoading() {
    println("showLoading:")
  }

  override fun updateDownloadProgress(precent: Int) {
    println("updateDownloadProgress:" + precent)
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
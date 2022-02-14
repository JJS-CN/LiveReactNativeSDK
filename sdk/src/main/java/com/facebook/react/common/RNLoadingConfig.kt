package com.facebook.react.common

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.react.common.utils.RnBundle
import com.live.react.R

/**
 *  Class:
 *  Other:
 *  Create by jsji on  2022/2/11.
 */
class RNLoadingConfig(context: Context) {
  //是否需要下载进度条
  var progressView: View? = null

  //加载错误时显示的view
  var errorView: View? = null

  //加载占位时显示的view
  var loadView: View? = null

  var reloadListener: View.OnClickListener? = null

  init {
    buildProgressView(context)
    buildErrorView(context)
  }


  private fun buildErrorView(context: Context) {
    val linearLayout = LinearLayout(context)
    val lp = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT,
      FrameLayout.LayoutParams.MATCH_PARENT)
    lp.gravity = Gravity.CENTER

    linearLayout.gravity = Gravity.CENTER
    linearLayout.layoutParams = lp
    linearLayout.orientation = LinearLayout.VERTICAL

    val textLp = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT)
    textLp.gravity = Gravity.CENTER
    val errText = TextView(context)
    errText.text = "加载失败"
    errText.layoutParams = textLp
    linearLayout.addView(errText)
    val h = LinearLayout(context)
    h.orientation = LinearLayout.HORIZONTAL
    h.gravity = Gravity.CENTER
    val exitBtn = Button(context)
    exitBtn.text = "退出"
    exitBtn.setOnClickListener {
      if(context is Activity) {
        context.finish()
      }
    }
    h.addView(exitBtn)
    val errBtn = Button(context)
    errBtn.text = "重试"
    errBtn.setOnClickListener {
      reloadListener?.onClick(it)
    }
    h.addView(errBtn)
    linearLayout.addView(h)
    errorView = linearLayout
  }

  private fun buildProgressView(context: Context) {
    val progress = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
    val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10)
    progress.layoutParams = lp
    progress.max = 100
    progress.progress = 0
    progressView = progress
  }


}
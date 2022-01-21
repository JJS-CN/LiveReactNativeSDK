package com.live.react.demo

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.BuildConfig
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.common.LifecycleState
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactPackage
import com.facebook.react.ReactRootView
import com.facebook.soloader.SoLoader


/**
 *  Class: 通过adb连接，开发阶段快速调试使用
 *  Other:
 *  Create by jsji on  2021/12/16.
 */
class TestReactActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {

  private var mReactRootView: ReactRootView? = null
  private var mReactInstanceManager: ReactInstanceManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    SoLoader.init(this, false)
    mReactRootView = ReactRootView(this)
    val packages: ArrayList<ReactPackage> = ArrayList()
    packages.add(BaseConfigPackage())
    // Packages that cannot be autolinked yet can be added manually here, for example:
    // packages.add(new MyReactNativePackage());
    // Remember to include them in `settings.gradle` and `app/build.gradle` too.
    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(application)
      .setCurrentActivity(this)
      .setBundleAssetName("index.android.bundle")
      .setJSMainModulePath("index")
      //.setJSBundleFile(CodePush.getJSBundleFile())
      .addPackages(packages)
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build()
    // The string here (e.g. "MyReactNativeApp") has to match
    // the string in AppRegistry.registerComponent() in index.js
    mReactRootView!!.startReactApplication(mReactInstanceManager, "app", null)
    setContentView(mReactRootView)
  }

  override fun invokeDefaultOnBackPressed() {
    super.onBackPressed()
  }

  override fun onPause() {
    super.onPause()
    if(mReactInstanceManager != null) {
      mReactInstanceManager!!.onHostPause(this)
    }
  }

  override fun onResume() {
    super.onResume()
    if(mReactInstanceManager != null) {
      mReactInstanceManager!!.onHostResume(this, this)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if(mReactInstanceManager != null) {
      mReactInstanceManager!!.onHostDestroy(this)
    }
    if(mReactRootView != null) {
      mReactRootView!!.unmountReactApplication()
    }
  }

  override fun onBackPressed() {
    if(mReactInstanceManager != null) {
      mReactInstanceManager!!.onBackPressed()
    } else {
      super.onBackPressed()
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
    if(keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
      mReactInstanceManager!!.showDevOptionsDialog()
      return true
    }
    return super.onKeyUp(keyCode, event)
  }
}
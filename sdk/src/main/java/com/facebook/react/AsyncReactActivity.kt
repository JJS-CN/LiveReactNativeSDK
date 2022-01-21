package com.facebook.react

import com.facebook.react.common.utils.ScriptLoadUtil.getCatalystInstance
import com.facebook.react.common.utils.ScriptLoadUtil.setJsBundleAssetPath
import com.facebook.react.common.utils.ScriptLoadUtil.loadScriptFromAsset
import com.facebook.react.common.utils.ScriptLoadUtil.loadScriptFromFile
import com.facebook.react.common.utils.FileUtils.downloadRNBundle
import com.facebook.react.common.utils.FileUtils.getCurrentPackageMd5
import com.facebook.react.common.utils.FileUtils.getPackageFolderPath
import com.facebook.react.common.utils.FileUtils.appendPathComponent
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.common.utils.RnBundle
import android.os.Bundle
import com.facebook.react.common.utils.ScriptLoadUtil
import com.facebook.react.ReactInstanceManager.ReactInstanceEventListener
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.utils.UpdateProgressListener
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.BV.LinearGradient.LinearGradientPackage
import com.facebook.react.modules.core.PermissionListener
import com.facebook.react.common.LoadScriptListener
import com.facebook.react.common.ScriptType
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.learnium.RNDeviceInfo.RNDeviceInfo
import com.oblador.vectoricons.VectorIconsPackage
import com.smallnew.smartassets.RNSmartassetsPackage
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.swmansion.rnscreens.RNScreensPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import java.io.File
import java.util.ArrayList

/**
 * 异步加载业务bundle的activity
 */
open abstract class AsyncReactActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler,
  PermissionAwareActivity {
  private var mDelegate: ReactActivityDelegate? = null
  protected var bundleLoaded = false
  private var bundle: RnBundle? = null

  companion object {
    const val INTENT_KEY_RNBUNDLE = "INTENT_KEY_RNBUNDLE"
    private var mReactNativeHost: ReactNativeHost? = null
  }

  /* public static void start(Context context, String componentName, RnBundle rnBundle) {
      Log.e("AsyncReactActivity", "start:" + componentName + "  bundle:" + rnBundle.toString());
      Intent intent = new Intent(context, AsyncReactActivity.class);
      intent.putExtra(INTENT_KEY_COMPONENT_NAME, componentName);
      intent.putExtra(INTENT_KEY_RNBUNDLE, rnBundle);
      context.startActivity(intent);
  }*/
  protected abstract fun showLoading()
  protected abstract fun dismissLoading()
  protected abstract fun updateDownloadProgerss(precent: Int)

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   * e.g. "MoviesApp"
   */
  private val mainComponentNameInner: String?
    private get() = if(!bundleLoaded &&
      (bundle!!.scriptType === ScriptType.NETWORK
          || bundle!!.scriptType === ScriptType.NETWORK_ASSET)
    ) {
      null
    } else bundle!!.moduleName

  protected fun reload(componentName: String?, rnBundle: RnBundle?) {
    bundle = rnBundle
    mDelegate = createReactActivityDelegate()
    loadScript(object : LoadScriptListener {
      override fun onLoadComplete(success: Boolean, scriptPath: String?) {
        bundleLoaded = success
        if(success) {
          runApp(scriptPath)
        }
      }
    })
  }

  /**
   * Called at construction time, override if you have a custom delegate implementation.
   */
  protected fun createReactActivityDelegate(): ReactActivityDelegate {
    return object : ReactActivityDelegate(this, mainComponentNameInner) {
      override fun getReactNativeHost(): ReactNativeHost {
        if(mReactNativeHost == null) {
          mReactNativeHost = object : ReactNativeHost(this@AsyncReactActivity.application) {
            override fun getUseDeveloperSupport(): Boolean {
              return ScriptLoadUtil.MULTI_DEBUG //是否是debug模式
            }

            override fun getPackages(): List<ReactPackage> {
              val packages = ArrayList<ReactPackage>()
              packages.add(MainReactPackage())
              packages.add(RNDeviceInfo())
              packages.add(RNGestureHandlerPackage())
              packages.add(ReanimatedPackage())
              packages.add(LinearGradientPackage())
              packages.add(RNSmartassetsPackage())
              packages.add(RNScreensPackage())
              packages.add(SafeAreaContextPackage())
              packages.add(SvgPackage())
              packages.add(VectorIconsPackage())
              //packages.add(BaseConfigPackage())
              return packages
            }

            override fun getBundleAssetName(): String? {
              return "platform.android.bundle"
            }

            override fun getJSMainModuleName(): String {
              return "MultiDebugEntry"
            }
          }
        }
        return mReactNativeHost!!
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val serializable = intent.getSerializableExtra(INTENT_KEY_RNBUNDLE)
    if(serializable != null) {
      bundle = serializable as RnBundle?
    }
    mDelegate = createReactActivityDelegate()
    val manager = mDelegate!!.reactNativeHost.reactInstanceManager
    if(!manager.hasStartedCreatingInitialContext()
      || getCatalystInstance(reactNativeHost) == null
    ) {
      manager.addReactInstanceEventListener(object : ReactInstanceEventListener {
        override fun onReactContextInitialized(context: ReactContext) {
          loadScript(object : LoadScriptListener {
            override fun onLoadComplete(success: Boolean, scriptPath: String?) {
              bundleLoaded = success
              if(success) {
                runApp(scriptPath)
              }
            }
          })
          manager.removeReactInstanceEventListener(this)
        }
      })
      mDelegate!!.reactNativeHost.reactInstanceManager.createReactContextInBackground()
    } else {
      loadScript(object : LoadScriptListener {
        override fun onLoadComplete(success: Boolean, scriptPath: String?) {
          bundleLoaded = success
          if(success) {
            runApp(scriptPath)
          }
        }
      })
    }
  }

  protected fun runApp(scriptPath: String?) {
    var scriptPath = scriptPath
    Log.e("eeee", "runApp")
    if(scriptPath != null) {
      scriptPath = "file://" + scriptPath.substring(0, scriptPath.lastIndexOf(File.separator) + 1)
    }
    val path = scriptPath
    val bundle = bundle
    val reactInstanceManager =
      mDelegate!!.reactNativeHost.reactInstanceManager
    if(bundle!!.scriptType === ScriptType.NETWORK || bundle!!.scriptType === ScriptType.NETWORK_ASSET) { //如果是网络加载的话，此时正在子线程
      runOnUiThread {
        setJsBundleAssetPath(
          reactInstanceManager.currentReactContext!!,
          path)
        mDelegate!!.loadApp(mainComponentNameInner)
      }
    } else { //主线程运行
      setJsBundleAssetPath(
        reactInstanceManager.currentReactContext!!,
        path)
      initView()
    }
  }

  protected fun loadScript(loadListener: LoadScriptListener) {
    Log.e("eeee", "loadScript")
    val bundle = bundle
    /** all buz module is loaded when in debug mode */
    if(ScriptLoadUtil.MULTI_DEBUG) { //当设置成debug模式时，所有需要的业务代码已经都加载好了
      loadListener.onLoadComplete(true, null)
      return
    }
    val pathType = bundle!!.scriptType
    var scriptPath = bundle.scriptUrl
    var moduleName = bundle.moduleName
    val instance = getCatalystInstance(reactNativeHost)
    if(pathType === ScriptType.ASSET) {
      loadScriptFromAsset(applicationContext, instance, scriptPath!!, false)
      loadListener.onLoadComplete(true, null)
    } else if(pathType === ScriptType.FILE) {
      val scriptFile = File(
        applicationContext.filesDir
          .toString() + File.separator +  /*ScriptLoadUtil.REACT_DIR+File.separator+*/scriptPath)
      scriptPath = scriptFile.absolutePath
      loadScriptFromFile(scriptPath, instance, scriptPath, false)
      loadListener.onLoadComplete(true, scriptPath)
    } else if(pathType === ScriptType.NETWORK || bundle.scriptType === ScriptType.NETWORK_ASSET) {
      initView()
      showLoading()
      //由于downloadRNBundle里面的md5参数由组件名代替了，实际开发中需要用到md5校验的需要自己修改
      downloadRNBundle(
        this.applicationContext,
        scriptPath,
        moduleName,
        object : UpdateProgressListener {
          override fun updateProgressChange(precent: Int) {
            runOnUiThread { updateDownloadProgerss(precent) }
          }

          override fun complete(success: Boolean) {
            var success = success
            runOnUiThread { dismissLoading() }
            if(!success) {
              if(bundle.scriptType === ScriptType.NETWORK_ASSET) {
                Log.e("AsyncReactActivity", "Network loading failed, trying to load from assets")
                //如果是这类型，再尝试用assets中加载
                bundle.scriptType = ScriptType.ASSET
                bundle.scriptUrl = bundle.scriptPath
                runOnUiThread {
                  mDelegate = createReactActivityDelegate()
                  loadScript(loadListener)
                }
              } else {
                loadListener.onLoadComplete(false, null)
              }
              return
            }
            val info = getCurrentPackageMd5(
              applicationContext)
            val bundlePath = getPackageFolderPath(
              applicationContext, info)
            val jsBundleFilePath = appendPathComponent(bundlePath, bundle.scriptPath)
            val bundleFile = File(jsBundleFilePath)
            if(bundleFile != null && bundleFile.exists()) {
              loadScriptFromFile(jsBundleFilePath, instance, jsBundleFilePath, false)
            } else {
              success = false
            }
            loadListener.onLoadComplete(success, jsBundleFilePath)
          }
        })
    }
  }

  protected fun initView() {
    mDelegate!!.onCreate(null)
  }

  override fun onPause() {
    super.onPause()
    //mDelegate.onPause();
  }

  override fun onResume() {
    super.onResume()
    //mDelegate.onResume();
  }

  override fun onDestroy() {
    super.onDestroy()
    mDelegate!!.onDestroy()
  }

  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    mDelegate!!.onActivityResult(requestCode, resultCode, data)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return mDelegate!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return mDelegate!!.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)
  }

  override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
    return mDelegate!!.onKeyLongPress(keyCode, event) || super.onKeyLongPress(keyCode, event)
  }

  override fun onBackPressed() {
    super.onBackPressed()
  }

  override fun invokeDefaultOnBackPressed() {
    super.onBackPressed()
  }

  public override fun onNewIntent(intent: Intent) {
    if(!mDelegate!!.onNewIntent(intent)) {
      super.onNewIntent(intent)
    }
  }

  override fun requestPermissions(
    permissions: Array<String>,
    requestCode: Int,
    listener: PermissionListener) {
    mDelegate!!.requestPermissions(permissions, requestCode, listener)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    mDelegate!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  protected val reactNativeHost: ReactNativeHost
    protected get() = mDelegate!!.reactNativeHost
  protected val reactInstanceManager: ReactInstanceManager
    protected get() = mDelegate!!.reactInstanceManager


}
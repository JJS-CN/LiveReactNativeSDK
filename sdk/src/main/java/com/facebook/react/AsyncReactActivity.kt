package com.facebook.react

import com.facebook.react.common.utils.ScriptLoadUtil.getCatalystInstance
import com.facebook.react.common.utils.ScriptLoadUtil.setJsBundleAssetPath
import com.facebook.react.common.utils.ScriptLoadUtil.loadScriptFromAsset
import com.facebook.react.common.utils.ScriptLoadUtil.loadScriptFromFile
import com.facebook.react.common.utils.FileUtils.downloadRNBundle
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
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.PermissionListener
import com.facebook.react.common.LoadScriptListener
import com.facebook.react.common.RNLoadingConfig
import com.facebook.react.common.ReactUtil
import com.facebook.react.common.ScriptType
import java.io.File

/**
 * 远程bundle下载数据还有点问题,未复用，需要用md5码验证
 * 异步加载业务bundle的activity
 */
open abstract class AsyncReactActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler,
  PermissionAwareActivity {
  private lateinit var mDelegate: ReactActivityDelegate
  protected var bundleLoaded = false
  private lateinit var bundle: RnBundle
  protected lateinit var loadConfig: RNLoadingConfig

  protected abstract fun getRnLoadConfig(loadConfig: RNLoadingConfig): RNLoadingConfig

  /**
   * 开始加载
   */
  protected abstract fun loadStart()

  /**
   * 加载完成。返回参数是否加载失败
   */
  protected abstract fun loadComplete(isSuccess: Boolean)

  /**
   * 需要下载步骤时需要提示loading
   */
  protected abstract fun showLoading()

  /**
   * 有下载动作时的下载进度通知
   */
  protected abstract fun updateDownloadProgress(precent: Int)

  /**
   * 添加自己的RN支持方法
   */
  protected abstract fun getAppReactPackages(): List<ReactPackage>?

  /**
   * Returns the name of the main component registered from JavaScript.
   * This is used to schedule rendering of the component.
   * e.g. "MoviesApp"
   */
  private val mainComponentNameInner: String?
    private get() = if(!bundleLoaded &&
      (bundle.scriptType === ScriptType.NETWORK
          || bundle.scriptType === ScriptType.NETWORK_ASSET)
    ) {
      null
    } else bundle.moduleName

  protected fun reload(rnBundle: RnBundle?) {
    rnBundle?.let {
      bundle = rnBundle
    }
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
        return ReactUtil.createNativeHost(
          this@AsyncReactActivity.application,
          getAppReactPackages())
      }

      override fun getLaunchOptions(): Bundle? {
        //参数不为空
        if(!TextUtils.isEmpty(bundle.params)) {
          val launchBundle = Bundle()
          //向RN页面传递初始化参数
          launchBundle.putString("launchParams", bundle.params)
          return launchBundle
        }

        return super.getLaunchOptions()
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val rnLoadingConfig = RNLoadingConfig(this)
    rnLoadingConfig.reloadListener = View.OnClickListener {
      reload(bundle)
    }
    loadConfig = getRnLoadConfig(rnLoadingConfig)
    loadStart()
  }

  /**
   * 从服务器获取数据后，包装成rnBundle调用此方法开始加载
   */
  protected fun startLoadRNBundle(rnBundle: RnBundle) {
    bundle = rnBundle
    mDelegate = createReactActivityDelegate()
    val manager = mDelegate.reactNativeHost.reactInstanceManager
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
      mDelegate.reactNativeHost.reactInstanceManager.createReactContextInBackground()
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

  /**
   * 发生各种错误时，调用此方法展示错误UI
   */
  protected fun showLoadErrorView() {
    if(loadConfig.errorView != null) {
      setContentView(loadConfig.errorView)
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
      mDelegate.reactNativeHost.reactInstanceManager
    if(bundle.scriptType === ScriptType.NETWORK || bundle.scriptType === ScriptType.NETWORK_ASSET) { //如果是网络加载的话，此时正在子线程
      runOnUiThread {
        setJsBundleAssetPath(
          reactInstanceManager.currentReactContext!!,
          path)
        mDelegate.loadApp(mainComponentNameInner)
      }
    } else { //主线程运行
      setJsBundleAssetPath(
        reactInstanceManager.currentReactContext!!,
        path)
      initView()
    }
  }

  protected fun loadScript(listener: LoadScriptListener) {
    Log.e("eeee", "loadScript")
    var loadListener = object : LoadScriptListener {
      override fun onLoadComplete(success: Boolean, scriptPath: String?) {
        listener.onLoadComplete(success, scriptPath)
        loadComplete(success)
        if(!success && loadConfig.errorView != null) {
          setContentView(loadConfig.errorView)
        }
      }
    }
    try {

      /** all buz module is loaded when in debug mode */
      if(ScriptLoadUtil.MULTI_DEBUG) { //当设置成debug模式时，所有需要的业务代码已经都加载好了
        loadListener.onLoadComplete(true, null)
        return
      }
      val pathType = bundle.scriptType
      var scriptUrl = bundle.scriptUrl
      val instance = getCatalystInstance(reactNativeHost)
      if(pathType === ScriptType.ASSET) {
        loadScriptFromAsset(applicationContext, instance, scriptUrl!!, false)
        loadListener.onLoadComplete(true, null)
      } else if(pathType === ScriptType.FILE) {
        val scriptFile = File(
          applicationContext.filesDir
            .toString() + File.separator +  /*ScriptLoadUtil.REACT_DIR+File.separator+*/scriptUrl)
        scriptUrl = scriptFile.absolutePath
        loadScriptFromFile(scriptUrl, instance, scriptUrl, false)
        loadListener.onLoadComplete(true, scriptUrl)
      } else if(pathType === ScriptType.NETWORK || bundle.scriptType === ScriptType.NETWORK_ASSET) {
        initView()
        showLoading()
        if(loadConfig.progressView != null) {
          //展示下载进度条
          setContentView(loadConfig.progressView)
        }
        //根据pageId查找解压文件，从中返回modelName和bundlerName
        downloadRNBundle(
          this.applicationContext,
          scriptUrl,
          bundle.pageId,
          bundle.md5,
          object : UpdateProgressListener {
            override fun updateProgressChange(precent: Int) {
              runOnUiThread {
                updateDownloadProgress(precent)
                if(loadConfig.progressView != null) {
                  //展示下载进度条
                  var progressView = loadConfig.progressView as ProgressBar
                  progressView.progress = precent
                }
              }
            }

            override fun complete(success: Boolean, bundleName: String, modelName: String) {
              //此时将从cof.json中获得的modelName保存到临时变量中，后续加载时使用！
              bundle.moduleName = modelName
              var success = success
              if(!success) {
                if(bundle.scriptType === ScriptType.NETWORK_ASSET) {
                  Log.e("AsyncReactActivity", "Network loading failed, trying to load from assets")
                  //如果是这类型，再尝试用assets中加载
                  bundle.scriptType = ScriptType.ASSET
                  bundle.scriptUrl = bundleName
                  runOnUiThread {
                    mDelegate = createReactActivityDelegate()
                    loadScript(listener)
                  }
                } else {
                  runOnUiThread {
                    loadListener.onLoadComplete(false, null)
                  }
                }
                return
              }
              val bundlePath = getPackageFolderPath(
                applicationContext, bundle.pageId)
              val jsBundleFilePath = appendPathComponent(bundlePath, bundleName)
              val bundleFile = File(jsBundleFilePath)
              if(bundleFile != null && bundleFile.exists()) {
                loadScriptFromFile(jsBundleFilePath, instance, jsBundleFilePath, false)
              } else {
                success = false
              }
              runOnUiThread {
                loadListener.onLoadComplete(success, jsBundleFilePath)
              }
            }
          })
      }
    } catch(e: Exception) {
      e.printStackTrace()
      loadListener.onLoadComplete(false, "throw")
    }
  }

  protected fun initView() {
    try {
      mDelegate.onCreate(null)
      mDelegate.onResume()
    } catch(e: Exception) {
    }
  }

  override fun onPause() {
    super.onPause()
    /*  try {
        mDelegate.onPause()
      } catch(e: Exception) {
      }*/
  }

  override fun onResume() {
    super.onResume()
    try {
      mDelegate.onResume()
    } catch(e: Exception) {
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    try {
      mDelegate.onDestroy()
    } catch(e: Exception) {
    }
  }

  public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    try {
      mDelegate.onActivityResult(requestCode, resultCode, data)
    } catch(e: Exception) {
    }
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return try {
      mDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    } catch(e: Exception) {
      super.onKeyDown(keyCode, event)
    }
  }

  override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
    return try {
      mDelegate.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event)
    } catch(e: Exception) {
      super.onKeyUp(keyCode, event)
    }
  }

  override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
    return try {
      mDelegate.onKeyLongPress(keyCode, event) || super.onKeyLongPress(keyCode, event)
    } catch(e: Exception) {
      super.onKeyLongPress(keyCode, event)
    }
  }

  override fun onBackPressed() {
    try {
      mDelegate.onBackPressed()
    } catch(e: Exception) {
      super.onBackPressed()
    }

  }

  override fun invokeDefaultOnBackPressed() {
    super.onBackPressed()
  }

  public override fun onNewIntent(intent: Intent) {
    try {
      if(!mDelegate.onNewIntent(intent)) {
        super.onNewIntent(intent)
      }
    } catch(e: Exception) {
      super.onNewIntent(intent)
    }
  }

  override fun requestPermissions(
    permissions: Array<String>,
    requestCode: Int,
    listener: PermissionListener) {
    try {
      mDelegate.requestPermissions(permissions, requestCode, listener)
    } catch(e: Exception) {

    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    try {
      mDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    } catch(e: Exception) {

    }
  }

  protected val reactNativeHost: ReactNativeHost
    protected get() = mDelegate.reactNativeHost
  protected val reactInstanceManager: ReactInstanceManager
    protected get() = mDelegate.reactInstanceManager


}
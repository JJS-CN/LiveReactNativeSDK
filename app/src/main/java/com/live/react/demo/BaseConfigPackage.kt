package com.live.react.demo

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager
import java.util.*
import kotlin.collections.ArrayList

/**
 *  Class:
 *  Other:
 *  Create by jsji on  2021/12/20.
 */
open class BaseConfigPackage : ReactPackage {
  var myNativeModule: BaseRnConfigModule? = null
  override fun createNativeModules(reactContext: ReactApplicationContext): MutableList<NativeModule> {
    myNativeModule = BaseRnConfigModule(reactContext)
    val modules: MutableList<NativeModule> = ArrayList()
    //将我们创建NativeModule添加进原生模块列表中
    //将我们创建NativeModule添加进原生模块列表中
    modules.add(myNativeModule!!)
    return modules
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): MutableList<ViewManager<View, ReactShadowNode<*>>> {
    return Collections.emptyList()
  }
}
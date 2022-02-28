package com.facebook.react.common

import android.app.Application
import com.BV.LinearGradient.LinearGradientPackage
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.CatalystInstance
import com.facebook.react.common.utils.ScriptLoadUtil
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.learnium.RNDeviceInfo.RNDeviceInfo
import com.smallnew.smartassets.RNSmartassetsPackage
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.swmansion.rnscreens.RNScreensPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import java.util.ArrayList

object ReactUtil {
  fun getSourceUrl(instance: CatalystInstance): String? {
    return instance.sourceURL
  }

  var mReactNativeHost: ReactNativeHost? = null
  fun createNativeHost(application: Application,
                       appReactPackages: List<ReactPackage>?): ReactNativeHost {
    if(mReactNativeHost == null) {
      mReactNativeHost = object : ReactNativeHost(application) {
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
          if(appReactPackages != null) {
            packages.addAll(appReactPackages)
          }
          return packages
        }

        override fun getBundleAssetName(): String {
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
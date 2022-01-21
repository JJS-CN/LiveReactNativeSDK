package com.facebook.react.common.utils

import com.facebook.react.common.ScriptType
import java.io.Serializable

/***
 * 加载rn bundle的信息
 */
open class RnBundle : Serializable {
  var scriptPath: String? = null
  var scriptType: ScriptType? = null
  var scriptUrl: String? = null
  var md5: String = ""
  constructor()
  constructor(scriptPath: String?, scriptType: ScriptType?, scriptUrl: String?, md5: String) {
    this.scriptPath = scriptPath
    this.scriptType = scriptType
    this.scriptUrl = scriptUrl
    this.md5 = md5
  }



  override fun toString(): String {
    return "RnBundle{" +
        "scriptPath='" + scriptPath + '\'' +
        ", scriptType=" + scriptType +
        ", scriptUrl='" + scriptUrl + '\'' +
        '}'
  }
}
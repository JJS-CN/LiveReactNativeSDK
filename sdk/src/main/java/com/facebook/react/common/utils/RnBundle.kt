package com.facebook.react.common.utils

import com.facebook.react.common.ScriptType
import java.io.Serializable

/***
 * 加载rn bundle的信息
 */
open class RnBundle : Serializable {
  //var scriptPath: String? = null
  var scriptType: ScriptType = ScriptType.NETWORK

  //当从ASSETS中加载时，这个作为存储bundleName的字段
  var scriptUrl: String? = null

  var moduleName: String = ""
  var pageId: String = ""
  var md5: String = ""
  //初始化参数
  var params: String? = ""

  constructor()
  constructor(pageId: String, scriptUrl: String?, md5: String) {
    this.pageId = pageId
    this.scriptUrl = scriptUrl
    this.md5 = md5
    this.scriptType = ScriptType.NETWORK
  }

  constructor(pageId: String,
              scriptUrl: String?,
              md5: String,
              moduleName: String) {
    this.pageId = pageId
    this.scriptUrl = scriptUrl
    this.md5 = md5
    this.moduleName = moduleName
    this.scriptType = ScriptType.ASSET
  }

  constructor(pageId: String,
              scriptUrl: String?,
              md5: String,
              moduleName: String,
              scriptType: ScriptType) {
    this.pageId = pageId
    this.scriptUrl = scriptUrl
    this.md5 = md5
    this.moduleName = moduleName
    this.scriptType = scriptType
  }


  override fun toString(): String {
    return "RnBundle{" +
        "pageId='" + pageId + '\'' +
        ", scriptType=" + scriptType +
        ", scriptUrl='" + scriptUrl + '\'' +
        ", moduleName='" + moduleName + '\'' +
        ", md5='" + md5 + '\'' +
        '}'
  }
}
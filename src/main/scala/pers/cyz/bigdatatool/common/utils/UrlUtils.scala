package pers.cyz.bigdatatool.common.utils

import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.common.pojo.ComponentMap

import java.net.URL

object UrlUtils {
  def getUrl(name: String, version: String): URL = {
    new URL(AppConfig.repository.url + ComponentMap.componentMap(name) + name + "-" + version + "/" + name + "-" +
      version + SystemConfig.compressedFormat)
    //+ "-src"
  }
}

package pers.cyz.bigdatatool.node.common.utils

import pers.cyz.bigdatatool.node.common.config.{AppConfig, SystemConfig, SystemConfigMap}
import pers.cyz.bigdatatool.node.common.pojo.ComponentMap

import java.net.URL

object UrlUtils {
  def getUrl(name: String, version: String): URL = {
    new URL(AppConfig.repository.url + ComponentMap.componentMap(name) + name + "-" + version + "/" + name + "-" +
      version + "-src" + SystemConfig.compressedFormat)
  }
}

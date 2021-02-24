package pers.cyz.bigdatatool.node.config

import pers.cyz.bigdatatool.node.config.SystemConfigMap.{CompressedFormatMap, SystemArchitectureMap}

object SystemConfig {

  var userHomePath: String = _
  var compressedFormat: String = _
  var systemArchitecture: String = _

  {
    userHomePath = System.getProperty("user.home")
    compressedFormat = CompressedFormatMap(System.getProperties.getProperty("os.name"))
    systemArchitecture = SystemArchitectureMap(System.getProperties.getProperty("os.arch"))

  }

}


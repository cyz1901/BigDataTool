package pers.cyz.bigdatatool.master.config

import pers.cyz.bigdatatool.master.config.SystemConfigMap.{CompressedFormatMap, SystemArchitectureMap}

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


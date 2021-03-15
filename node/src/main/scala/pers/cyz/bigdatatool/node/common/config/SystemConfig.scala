package pers.cyz.bigdatatool.node.common.config


import pers.cyz.bigdatatool.node.common.config.SystemConfigMap.{CompressedFormatMap, SystemArchitectureMap}
import pers.cyz.bigdatatool.node.common.utils.IpUtils

import java.util

object SystemConfig {

  var localHostIp: String = _
  var localHostName: String = _
  var userHomePath: String = _
  var compressedFormat: String = _
  var systemArchitecture: String = _

  {
    localHostIp = IpUtils.getLocalIpAddr
    localHostName = IpUtils.getLocalName
    userHomePath = System.getProperty("user.home")
    compressedFormat = CompressedFormatMap(System.getProperties.getProperty("os.name"))
    systemArchitecture = SystemArchitectureMap(System.getProperties.getProperty("os.arch"))

  }

}


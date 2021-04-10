package pers.cyz.bigdatatool.common.config

import scala.collection.immutable.HashMap

object SystemConfigMap {
  val CompressedFormatMap: HashMap[String, String] = HashMap(
    "Linux" -> ".tar.gz",
  )
  val SystemArchitectureMap: HashMap[String, String] = HashMap(
    "amd64" -> "arch64",
  )

}

package pers.cyz.bigdatatool.node.config

import scala.collection.immutable.HashMap

object SystemConfigMap {
  val CompressedFormatMap: HashMap[String, String] = HashMap(
    "Linux" -> ".tar.gz",
  )
  val SystemArchitectureMap: HashMap[String, String] = HashMap(
    "amd64" -> "arch64",
  )

}

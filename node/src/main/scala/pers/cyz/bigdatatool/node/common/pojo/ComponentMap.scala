package pers.cyz.bigdatatool.node.common.pojo

import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

object ComponentMap {
  val componentMap: HashMap[String, String] = HashMap[String, String](
    "hadoop" -> "/apache/hadoop/common/"
  )
  val componentVersionMap: HashMap[String, ArrayBuffer[String]] = HashMap[String, ArrayBuffer[String]](
    "hadoop" -> ArrayBuffer("3.3.0", "3.2.2", "3.2.1", "3.1.4", "2.10.1")
  )
}

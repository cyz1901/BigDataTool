package pers.cyz.bigdatatool.node.common.utils.loader

trait Load[T] {
  var configFilePath: String
  def init()
  def getObjMapping: T
}

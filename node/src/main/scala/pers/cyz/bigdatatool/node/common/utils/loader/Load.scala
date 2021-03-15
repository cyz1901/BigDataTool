package pers.cyz.bigdatatool.node.common.utils.loader

trait Load[T] {
  var configFilePath: String
  def init()
  def fileToObjMapping(): T
  def objMappingToFile(t: T)
  def assignmentStatic(t: T)
}

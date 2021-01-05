package pers.cyz.BigDataTool.Master.Impl

import pers.cyz.BigDataTool.Master.Model.HadoopDownloadModle
import pers.cyz.BigDataTool.Master.Reptile.HadoopReptile
import pers.cyz.BigDataTool.Master.ThirftService.HadoopDownloadService

import scala.jdk.CollectionConverters.MutableMapHasAsJava


class HadoopDownloadImpl extends HadoopDownloadService.Iface{
  override def get(name: String): HadoopDownloadModle = {
    val hs = HadoopReptile.GetDownloadAddr().asJava
    val model = new HadoopDownloadModle(name, hs)
    model
  }
}

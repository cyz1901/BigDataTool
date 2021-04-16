package pers.cyz.bigdatatool.core.distributed

import io.grpc.Server
import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.common.utils.FileUtils
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.core.flower.FlowerNode
import pers.cyz.bigdatatool.core.master.MasterNode

import java.io.File
import java.net.InetAddress
import java.util.logging.Logger

object Application {
  var node: Node = _
  var server: Server = _

  private val logger = Logger.getLogger(classOf[App].getName)

  {
    // 获取配置
    new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
//      .setConfigFilePath("etc/node.yml").build().fileToObjMapping()
      .setConfigFilePath("src/main/resources/etc/node.yml").build().fileToObjMapping()

    //检测默认文件是否存在
    val path = new File(s"${SystemConfig.userHomePath}/${AppConfig.repository.downloadFile}/")
    path.mkdirs()



    // 判断节点
    if (InetAddress.getLocalHost.getHostName == AppConfig.serve.masterName) {
      node = MasterNode
    } else {
      node = FlowerNode
    }
  }


  def start(): Unit = {
    node.run()
  }

}

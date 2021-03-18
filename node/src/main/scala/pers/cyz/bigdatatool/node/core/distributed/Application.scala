package pers.cyz.bigdatatool.node.core.distributed

import io.grpc.Server
import pers.cyz.bigdatatool.node.common.config.AppConfig
import pers.cyz.bigdatatool.node.common.utils.loader.Loader
import pers.cyz.bigdatatool.node.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.node.core.flower.FlowerNode
import pers.cyz.bigdatatool.node.core.master.MasterNode

import java.net.InetAddress
import java.util.logging.Logger

object Application {
  var node: Node = _
  var server: Server = _

  private val logger = Logger.getLogger(classOf[App].getName)

  {
    // 获取配置
    val loader = new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("node/src/main/resource/etc/node.yml").build()
    val res: AppConfig.type = loader.fileToObjMapping()


    // 判断节点
    if (InetAddress.getLocalHost.getHostName == res.serve.masterName) {
      node = MasterNode
    } else {
      node = new FlowerNode()
    }
  }


  def start(): Unit = {
    node.run()
  }

}

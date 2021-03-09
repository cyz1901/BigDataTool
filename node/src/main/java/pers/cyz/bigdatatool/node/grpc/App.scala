package pers.cyz.bigdatatool.node.grpc

import io.grpc.{Server, ServerBuilder}

import java.io.FileInputStream
import org.yaml.snakeyaml.constructor.Constructor
import pers.cyz.bigdatatool.node.common.utils.loader.Loader
import pers.cyz.bigdatatool.node.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.node.config.AppConfig
import pers.cyz.bigdatatool.node.distributed.Node
import pers.cyz.bigdatatool.node.flower.{ConnectServiceImpl, FlowerNode}
import pers.cyz.bigdatatool.node.master.MasterNode

import java.io.File
import java.net.InetAddress
import java.util.logging.Logger

object App {
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
    if (InetAddress.getLocalHost.getHostName == res.master.host) {
      node = new MasterNode()
    } else {
      node = new FlowerNode()
    }
  }


  def start(): Unit = {
    node.run()
  }

}

package pers.cyz.bigdatatool.node.grpc

import io.grpc.{Server, ServerBuilder}
import org.yaml.snakeyaml.Yaml

import java.io.FileInputStream
import org.yaml.snakeyaml.constructor.Constructor
import pers.cyz.bigdatatool.node.config.AppConfig
import pers.cyz.bigdatatool.node.distributed.{FlowerNode, MasterNode, Node}
import pers.cyz.bigdatatool.node.grpc.com.ConnectService

import java.io.File
import java.net.InetAddress
import java.util.logging.Logger

object App {
  var node: Node = _
  var server : Server = _

  private val logger = Logger.getLogger(classOf[Nothing].getName)

  {
    // 获取配置
    val yaml : Yaml = new Yaml(new Constructor(classOf[AppConfig]))
    val result: AppConfig = yaml.load(new FileInputStream(new File("node/src/main/resource/etc/node.yml")))
    println(result.master.host)
    // 判断节点
    if (InetAddress.getLocalHost.getHostName == result.master.host){
      node = new MasterNode()
    }else{
      node = new FlowerNode()
    }
  }

  def start(): Unit ={
//    // grpc服务连接
    val port = 50052
    server = ServerBuilder.forPort(port).addService(new ConnectServiceImpl()).build().start()
    logger.info("Server started, listening on " + port)
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
          // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        logger.info("*** shutting down gRPC server since JVM is shutting down")
        App.stop()
        logger.info("*** server shut down")}
    ))

  }

  def stop(): Unit = {
    if (server != null) server.shutdown
  }
}

package pers.cyz.bigdatatool.node.core.flower

import io.grpc.{Server, ServerBuilder}
import pers.cyz.bigdatatool.node.core.distributed.Node

import java.util.logging.Logger

class FlowerNode extends Node {
  var node: Node = _
  var server : Server = _
  private val logger = Logger.getLogger(classOf[FlowerNode].getName)

  override def run(): Unit = {
    start()
    blockUntilShutdown()
  }

  def start(): Unit ={
    def initMetaData(): Unit ={

    }
    // grpc服务连接
    val port = 50055
    server = ServerBuilder.forPort(port).addService(new ServeServiceImpl()).build().start()
    logger.info("Server started, listening on " + port)
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      logger.info("*** shutting down gRPC server since JVM is shutting down")
      stop()
      logger.info("*** server shut down")}
    ))
  }

  def stop(): Unit = {
    if (server != null) server.shutdown
  }

  @throws[InterruptedException]
  private def blockUntilShutdown(): Unit = {
    if (server != null) server.awaitTermination()
  }
}
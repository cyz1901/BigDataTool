package pers.cyz.bigdatatool.core.flower

import io.grpc.{Server, ServerBuilder}
import org.slf4j.{Logger, LoggerFactory}
import pers.cyz.bigdatatool.core.distributed.Node


object FlowerNode extends Node {
  var node: Node = _
  var server: Server = _
  private val logger : Logger= LoggerFactory.getLogger(classOf[FlowerNode.type ])

  override def run(): Unit = {
    val tt = new Thread(){
      override def run(): Unit = {
        new FlowerClient().invokeRegister()
      }
    }
    tt.setName("ConnectService")
    tt.start()
    start()
  }

  override def init(): Unit = {
    ???
  }

  def start(): Unit = {

    // grpc服务连接
    val port = 50056
    server = ServerBuilder.forPort(port).addService(new ServeServiceImpl()).build().start()
    logger.info("Server started, listening on " + port)
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      logger.info("*** shutting down gRPC server since JVM is shutting down")
      stop()
      logger.info("*** server shut down")
    }
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
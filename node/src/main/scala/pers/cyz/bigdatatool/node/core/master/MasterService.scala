package pers.cyz.bigdatatool.node.core.master

import io.grpc.{Server, ServerBuilder}
import org.slf4j
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.node.uiservice.UiServiceApplication

import java.util.logging.Logger

class MasterService {

  val logger: slf4j.Logger = LoggerFactory.getLogger(classOf[MasterService]) //Logger.getLogger(classOf[MasterService].getName)
  var server: Server = _

//  def start() {
//    // grpc服务连接
//    val port = 50055
//    server = ServerBuilder.forPort(port).addService(new ConnectServiceImpl()).build().start()
//    logger.info("Master Server started, listening on " + port)
//
//    //启动uiService服务
//    val uiService = new Thread(){
//      override def run(): Unit = {
//        UiServiceApplication.run()
//      }
//    }
//    uiService.setName("UiService")
//    uiService.start()
//
//    //线程关闭钩子函数
//    Runtime.getRuntime.addShutdownHook(new Thread(() => {
//      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
//      logger.error("*** shutting down gRPC server since JVM is shutting down")
//      stop()
//      logger.error("*** server shut down")
//    }
//    ))
//    blockUntilShutdown()
//  }

  def stop(): Unit = {
    if (server != null) server.shutdown
  }

  @throws[InterruptedException]
  def blockUntilShutdown(): Unit = {
    if (server != null) server.awaitTermination()
  }

}

package pers.cyz.bigdatatool.node.core.master

import io.grpc.stub.StreamObserver
import pers.cyz.bigdatatool.node.grpc.com._


import java.util.logging.Logger


class ConnectServiceImpl extends ConnectGrpc.ConnectImplBase {

  private val logger = Logger.getLogger(classOf[ConnectServiceImpl].getName)


  override def register(responseObserver: StreamObserver[RegisterResponse]): StreamObserver[RegisterRequest] = {

    new StreamObserver[RegisterRequest] {
      override def onNext(v: RegisterRequest): Unit = {

      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }
  }
}

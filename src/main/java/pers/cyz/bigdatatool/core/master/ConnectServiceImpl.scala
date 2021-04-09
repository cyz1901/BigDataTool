package pers.cyz.bigdatatool.core.master

import io.grpc.stub.StreamObserver
import org.slf4j.{Logger, LoggerFactory}
import pers.cyz.bigdatatool.common.pojo.RuntimeMeta
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, RegisterRequest, RegisterResponse}




class ConnectServiceImpl extends ConnectGrpc.ConnectImplBase {

  val logger: Logger = LoggerFactory.getLogger(classOf[ConnectServiceImpl])


  override def register(responseObserver: StreamObserver[RegisterResponse]): StreamObserver[RegisterRequest] = {

    new StreamObserver[RegisterRequest] {
      override def onNext(v: RegisterRequest): Unit = {
        RuntimeMeta.hostIpMap.addOne(v.getName,v.getIp)
        logger.info(s"Heart Host name is ${v.getName} Ip is ${v.getIp} Status is ${v.getStatus}")
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

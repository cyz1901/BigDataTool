package pers.cyz.bigdatatool.node.flower

import io.grpc.stub.StreamObserver
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, DownloadComponentRequest, DownloadComponentResponse, FlowerStatus, RegisterRequest, RegisterResponse, editFileRequest, editFileResponse}

import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.logging.Logger

class ConnectServiceImpl extends ConnectGrpc.ConnectImplBase {

  private val logger = Logger.getLogger(classOf[ConnectServiceImpl].getName)

  override def register(request: RegisterRequest, responseObserver: StreamObserver[RegisterResponse]): Unit = {
    @volatile var lock = true

    while (lock) {
      val response: RegisterResponse = RegisterResponse.newBuilder().setStatus(
        FlowerStatus.newBuilder()
          .setIp(FlowerStatusObj.ip)
          .setStatus(FlowerStatusObj.status).build()
      ).build()
      responseObserver.onNext(response)
      TimeUnit.SECONDS.sleep(10)
    }
    responseObserver.onCompleted()

    super.register(request, responseObserver)
  }

  /**
   */
  override def downloadComponent(responseObserver: StreamObserver[DownloadComponentResponse]): StreamObserver[DownloadComponentRequest] = {
    super.downloadComponent(responseObserver)
  }

  /**
   */
  override def editFile(request: editFileRequest, responseObserver: StreamObserver[editFileResponse]): Unit = {
    super.editFile(request, responseObserver)
  }
}

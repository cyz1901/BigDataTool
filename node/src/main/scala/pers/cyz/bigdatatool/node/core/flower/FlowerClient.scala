package pers.cyz.bigdatatool.node.core.flower

import io.grpc.stub.StreamObserver
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import pers.cyz.bigdatatool.node.common.config.SystemConfig
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, RegisterRequest, RegisterResponse}

import java.lang.Thread.sleep
import java.util.logging.Logger

class FlowerClient {
  private val logger = Logger.getLogger(classOf[FlowerClient].getName)
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50056).usePlaintext().build()
  val stub: ConnectGrpc.ConnectBlockingStub = ConnectGrpc.newBlockingStub(channel)
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.newStub(channel)


  def invokeRegister(): Unit = {

    @volatile var lock = true
    val aa: StreamObserver[RegisterResponse] = new StreamObserver[RegisterResponse] {
      override def onNext(v: RegisterResponse): Unit = {

      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val request = asyncStub.register(aa)

    //十秒发送心跳一次
    while (true) {
      request.onNext(RegisterRequest.newBuilder()
        .setIp(SystemConfig.localHostIp)
        .setName(SystemConfig.localHostName)
        .setStatus("ok").build())
      sleep(10000)
    }


  }
}

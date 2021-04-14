package pers.cyz.bigdatatool.core.flower


import io.grpc.stub.StreamObserver
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, RegisterRequest, RegisterResponse}

import java.lang.Thread.sleep


class FlowerClient {
  private val logger = LoggerFactory.getLogger(classOf[FlowerClient])
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress(AppConfig.serve.masterName, 50055).usePlaintext().build()
  val stub: ConnectGrpc.ConnectBlockingStub = ConnectGrpc.newBlockingStub(channel)
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.newStub(channel)


  def invokeRegister(): Unit = {

    @volatile var lock = true
    val aa: StreamObserver[RegisterResponse] = new StreamObserver[RegisterResponse] {
      override def onNext(v: RegisterResponse): Unit = {

      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error $throwable")
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

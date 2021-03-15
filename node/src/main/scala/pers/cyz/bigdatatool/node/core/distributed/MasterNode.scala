package pers.cyz.bigdatatool.node.core.distributed

import io.grpc.stub.StreamObserver
import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import pers.cyz.bigdatatool.node.common.config.AppConfig
import pers.cyz.bigdatatool.node.common.pojo.ServiceLayer
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, DownloadComponentRequest, DownloadComponentResponse, RegisterRequest, RegisterResponse, hostMapRequest}
import pers.cyz.bigdatatool.node.uiservice.UiServiceApplication

import java.io.File
import java.util
import java.util.logging.Logger

object MasterNode extends Node {
  private val logger = Logger.getLogger(classOf[MasterNode.type].getName)
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50055).usePlaintext().build()
  //  OrderManagementGrpc.OrderManagementBlockingStub stub = OrderManagementGrpc.newBlockingStub(channel);
  val stub: ConnectGrpc.ConnectBlockingStub = ConnectGrpc.newBlockingStub(channel)
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.newStub(channel)


  def invokeRegister(): Unit = {
    //    val finishLatch = new CountDownLatch(1)
    @volatile var lock = true

    var bb: StreamObserver[RegisterResponse] = new StreamObserver[RegisterResponse] {
      override def onNext(v: RegisterResponse): Unit = {
        logger.info("Now the Flower: " + v.getStatus.getIp + " status is: " + v.getStatus.getStatus)
        if (v.getStatus.getStatus == "error") {
          lock = false
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    asyncStub.register(RegisterRequest.newBuilder().setC(10).build(), bb)
    println("")

    while (lock) {

    }


  }

  def invokeHostMap(): Unit = {
    stub.hostMap(hostMapRequest.newBuilder().build())

  }

  def invokeDownloadComponent(): Unit = {

    @volatile var lock = true

    var bb: StreamObserver[DownloadComponentResponse] = new StreamObserver[DownloadComponentResponse] {
      override def onNext(v: DownloadComponentResponse): Unit = {
        logger.info("Now totalSIze: " + v.getTotalSize + " nowSize is: " + v.getAlreadyDownloadSize)
        ServiceLayer.DownloadService.totalSize = v.getTotalSize
        ServiceLayer.DownloadService.alreadyDownloadSize = v.getAlreadyDownloadSize


      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val aa: StreamObserver[DownloadComponentRequest] = asyncStub.downloadComponent(bb)

    // TODO 假数据验证
    val map: java.util.Map[String, String] = new util.HashMap[String, String]()
    map.put("hadoop", "3.3.0")
    aa.onNext(DownloadComponentRequest.newBuilder().putAllComponentMap(map).setCommandType("start").build())

    while (lock) {

    }
  }

  override def run(): Unit = {

    def initMetaData(): Unit = {
      val file = new File(AppConfig.master.metaData + "data.json")
      if (!file.getParentFile.exists()) {
        file.getParentFile.mkdirs()
      }
      file.createNewFile()
    }

    initMetaData()
    println("start")
    val ui = new Thread(){
      UiServiceApplication.run()


    }

//        invokeRegister()
//    invokeDownloadComponent()

  }
}

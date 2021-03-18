package pers.cyz.bigdatatool.node.core.master

import io.grpc.{ManagedChannelBuilder, ServerBuilder}
import pers.cyz.bigdatatool.node.common.config.AppConfig
import pers.cyz.bigdatatool.node.common.pojo.RuntimeMeta
import pers.cyz.bigdatatool.node.core.distributed.Node
import pers.cyz.bigdatatool.node.grpc.com.ConnectGrpc
import pers.cyz.bigdatatool.node.uiservice.UiServiceApplication

import java.io.File
import java.lang.Thread.sleep
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class MasterNode extends Node {
  var masterClientArray: ArrayBuffer[MasterClient] = _

  {
    //    for (i <- 0 until AppConfig.serve.nodeCount) {
    //      masterClientArray.addOne(
    //        new MasterClient()
    //      )
    //    }
  }
  /* private val logger = Logger.getLogger(classOf[MasterNode.type].getName)
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
  }*/

  override def run(): Unit = {
    val service = new MasterService()

    // 启动grpc服务连接
    val port = 50055
    service.server = ServerBuilder.forPort(port).addService(new ConnectServiceImpl()).build().start()
    service.logger.info("Master Server started, listening on " + port)

    //阻塞等待节点都注册上


    //启动uiService服务
    val uiService = new Thread() {
      override def run(): Unit = {
        UiServiceApplication.run()
      }
    }
    uiService.setName("UiService")
    uiService.start()

    //

    //线程关闭钩子函数
    Runtime.getRuntime.addShutdownHook(new Thread(() => {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      service.logger.error("*** shutting down gRPC server since JVM is shutting down")
      service.stop()
      service.logger.error("*** server shut down")
    }
    ))
    service.blockUntilShutdown()

    //    def initMetaData(): Unit = {
    //      val file = new File(AppConfig.repository.downloadFile + "data.json")
    //      if (!file.getParentFile.exists()) {
    //        file.getParentFile.mkdirs()
    //      }
    //      file.createNewFile()
    //    }
    //
    //    initMetaData()


  }

  override def init(): Unit = {


  }

  def initArray(): Unit = {
    breakable {
      while (true) {
        if (RuntimeMeta.hostIpMap.size == AppConfig.serve.nodeCount) {
          RuntimeMeta.hostIpMap.foreach(entry => {
            val channel = ManagedChannelBuilder.forAddress(entry._1, 50055).usePlaintext().build()
            this.masterClientArray.addOne(new MasterClient(
              channel,
              ConnectGrpc.newBlockingStub(channel),
              ConnectGrpc.newStub(channel)
            ))
          })
          break
        } else {
          sleep(10000)
        }
      }
    }
  }
}

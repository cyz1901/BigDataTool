package pers.cyz.bigdatatool.core.master

import com.google.protobuf.ByteString
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.node.grpc.com.{DeployRequest, DeployResponse, DistributeComponentRequest, DistributeComponentResponse, DownloadComponentRequest, DownloadComponentResponse, ServeGrpc}
import pers.cyz.bigdatatool.uiservice.bean.Clusters
import pers.cyz.bigdatatool.uiservice.controller.{DeployController, DistributeController, DownloadController}

import java.io.{File, FileInputStream, FileOutputStream, ObjectOutputStream}
import java.util.concurrent.atomic.AtomicLong


class MasterClient(
                    channel: ManagedChannel,
                    stub: ServeGrpc.ServeBlockingStub,
                    asyncStub: ServeGrpc.ServeStub
                  ) {
  private val logger = LoggerFactory.getLogger(classOf[MasterService])
  //  val layer = new LayerDownloadService()

  def invokeDownload(map: java.util.Map[String, String]): Unit = {
    //    var totalSize = new InheritableThreadLocal[Long]
    val grpcResponse: StreamObserver[DownloadComponentResponse] = new StreamObserver[DownloadComponentResponse] {
      override def onNext(v: DownloadComponentResponse): Unit = {
        //        logger.info("Now totalSIze: " + v.getTotalSize + " nowSize is: " + v.getAlreadyDownloadSize)
        //        layer.set(v.getTotalSize, v.getAlreadyDownloadSize)
        //        send(v.getTotalSize, v.getAlreadyDownloadSize, _, _, "run")
        //        localDownloadSize.set(v.getAlreadyDownloadSize)
        //        DownloadController.downloadControllerCallback(localDownloadSize.get())
        DownloadController.totalSize = v.getTotalSize
        DownloadController.downloadControllerCallback(Thread.currentThread().getId, v.getAlreadyDownloadSize)
        if (v.getTotalSize <= v.getAlreadyDownloadSize) {
          //          send(v.getTotalSize, v.getAlreadyDownloadSize, _, _, "finish")
          onCompleted()
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val grpcRequest: StreamObserver[DownloadComponentRequest] = asyncStub.downloadComponent(grpcResponse)

    grpcRequest.onNext(DownloadComponentRequest.newBuilder().putAllComponentMap(map).setCommandType("start").build())
  }

  /*
  * 每个线程都会维护一个signal 通过判断signal来看是否要把最新的下载量写入全局变量
  *
  * */
  def invokeDistribute(fileName: String, file: File, count: AtomicLong): Unit = {

    val grpcResponse: StreamObserver[DistributeComponentResponse] = new StreamObserver[DistributeComponentResponse] {

      //      if (taskAccumulationSignal.get()) {
      //        DistributeController.threadBarrier.getAndIncrement()
      //      }

      override def onNext(v: DistributeComponentResponse): Unit = {

        // 记忆未阻塞处理器获得的最新数据
        count.set(v.getAlreadyDistribute)
//        println(v.getAlreadyDistribute)
        //
        //        // 判断更新数据
        //        if (taskAccumulationSignal) {
        //          logger.info(s"${Thread.currentThread().getName} Update tAS ${taskAccumulationSignal} - tB ${DistributeController.threadBarrier}")
        //          taskAccumulationSignal = false
        //          DistributeController.threadBarrier.getAndIncrement()
        //          DistributeController.updateData(count, v.getMsg)
        //        }
        //
        //        // 判断更新signal
        //        if (DistributeController.threadBarrier.get() < (DistributeController.threadNum + Thread.activeCount() )) {
        //          logger.info(s"${Thread.currentThread().getName} UpdateLock tAS ${taskAccumulationSignal} - tB ${DistributeController.threadBarrier}")
        //          taskAccumulationSignal = true
        //        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        DistributeController.nowComponents += 1
        logger.info("Completed")
      }
    }

    val grpcRequest: StreamObserver[DistributeComponentRequest] = asyncStub.distributeComponent(grpcResponse)
    val is = new FileInputStream(file)
    val buf: Array[Byte] = new Array[Byte](1024)
    while (is.read(buf) != -1) {
      grpcRequest.onNext(DistributeComponentRequest.newBuilder().setFileName(fileName).setMsg("start").setData(ByteString.copyFrom(buf)).build())
    }

    is.close()
    grpcRequest.onCompleted()
  }


  def invokeDeploy(nodeMap: java.util.Map[String, String],
                   componentMap: java.util.Map[String, String],
                   deployType: String,
                   colonyName: String
                  ) {

    val grpcResponse: StreamObserver[DeployResponse] = new StreamObserver[DeployResponse] {
      override def onNext(v: DeployResponse): Unit = {
        DeployController.setMessage(v.getMessage, v.getStatus, v.getStep)
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val grpcRequest: DeployRequest = DeployRequest.newBuilder().putAllComponentMap(componentMap).putAllNodeMap(nodeMap)
      .setMsg("start").setType(deployType).build()

    // 序列化存储
    val clustersAddr = new File(s"${SystemConfig.userHomePath}/BDMData/Meta/cluster")
    val clusters: Clusters = new Clusters()

    clusters.setColonyName(colonyName)
    nodeMap.forEach((key, value) => {
      if (value == "nameNode") {
        clusters.setNameNodeName(key)
      }
    })

    val oo = new ObjectOutputStream(new FileOutputStream(clustersAddr))
    oo.writeObject(clusters)
    oo.close()

    asyncStub.deploy(grpcRequest, grpcResponse)
  }


}


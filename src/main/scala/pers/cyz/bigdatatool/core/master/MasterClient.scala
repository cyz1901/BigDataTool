package pers.cyz.bigdatatool.core.master

import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.node.grpc.com.{DeployRequest, DeployResponse, DistributeComponentRequest, DistributeComponentResponse, DownloadComponentRequest, DownloadComponentResponse, ServeGrpc}
import pers.cyz.bigdatatool.uiservice.bean.Clusters
import pers.cyz.bigdatatool.uiservice.controller.{DeployController, DownloadController}
import pers.cyz.bigdatatool.uiservice.controller.DeployController

import java.io.{File, FileOutputStream, ObjectOutputStream}


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

  // TODO 分发
  def invokeDistribute(map: java.util.Map[String, String]): Unit = {
    val grpcResponse: StreamObserver[DistributeComponentResponse] = new StreamObserver[DistributeComponentResponse] {
      override def onNext(v: DistributeComponentResponse): Unit = {

      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val grpcRequest: StreamObserver[DistributeComponentRequest] = asyncStub.distributeComponent(grpcResponse)

    grpcRequest.onNext(DistributeComponentRequest.newBuilder().setFileName("haha").setMsg("start").build())
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


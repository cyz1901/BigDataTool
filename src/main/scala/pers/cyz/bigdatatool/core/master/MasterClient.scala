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
import java.nio.ByteBuffer
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.atomic.AtomicLong
import scala.jdk.CollectionConverters._


class MasterClient(
                    channel: ManagedChannel,
                    stub: ServeGrpc.ServeBlockingStub,
                    asyncStub: ServeGrpc.ServeStub
                  ) {
  private val logger = LoggerFactory.getLogger(classOf[MasterService])
  //  val layer = new LayerDownloadService()

  def invokeDownload(cyclicBarrier: CyclicBarrier, map: java.util.Map[String, String]): Unit = {
    val grpcResponse: StreamObserver[DownloadComponentResponse] = new StreamObserver[DownloadComponentResponse] {
      override def onNext(v: DownloadComponentResponse): Unit = {

        // 记忆未阻塞处理器获得的最新数据
        DownloadController.alreadyDownloadSize.getAndAdd(v.getAlreadyDownloadSize)
        //
//        logger.error(s"size is ${v.getTotalSize}")
        DownloadController.totalSize.set(v.getTotalSize)
        cyclicBarrier.await()
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        DistributeController.nowComponents.getAndIncrement()
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
  def invokeDistribute(cyclicBarrier: CyclicBarrier, fileName: String, file: File): Unit = {

    val grpcResponse: StreamObserver[DistributeComponentResponse] = new StreamObserver[DistributeComponentResponse] {

      override def onNext(v: DistributeComponentResponse): Unit = {
        v.getMsg match {
          case "run" => {

            // 记忆未阻塞处理器获得的最新数据
            DistributeController.alreadyDownloadSize.getAndAdd(v.getAlreadyDistribute)
            cyclicBarrier.await()
          }
          case "finish" => {

          }
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        DistributeController.nowComponents.getAndIncrement()
        logger.info("Completed")
      }
    }

    val grpcRequest: StreamObserver[DistributeComponentRequest] = asyncStub.distributeComponent(grpcResponse)
    grpcRequest.onNext(DistributeComponentRequest.newBuilder().setFileName(fileName).setMsg("start").setFileName(fileName).build())
    val is = new FileInputStream(file)
    val buf: Array[Byte] = new Array[Byte](1024)
    var len = 0
    // 必须存在len来保证不会多写入多余比特 会造成文件校验错误
    while ( {
      len = is.read(buf)
      len
    } != -1) {
      grpcRequest.onNext(DistributeComponentRequest.newBuilder().setFileName(fileName).setMsg("run").setData(ByteString.copyFrom(buf, 0, len)).build())
    }

    is.close()
    grpcRequest.onCompleted()
  }


  def invokeDeploy(cyclicBarrier: CyclicBarrier,
                   nodeMap: java.util.Map[String, String],
                   componentMap: java.util.Map[String, String],
                   deployType: String,
                   colonyName: String,
                   nameNode: String,
                   secondaryNameNode: String
                  ) {

    val grpcResponse: StreamObserver[DeployResponse] = new StreamObserver[DeployResponse] {
      override def onNext(v: DeployResponse): Unit = {
        logger.info(s"message is ${v.getMessage},step is ${v.getStep}, status is ${v.getStatus}")
        v.getStatus match {
          case "working" => {
            DeployController.setMessage(v.getMessage, v.getStatus, v.getStep)
            cyclicBarrier.await()
          }
          case "finish" => {
            DeployController.setMessage(v.getMessage, v.getStatus, v.getStep)
            cyclicBarrier.await()
          }
          case "error" => {

          }

        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.error(s"Error ${throwable}")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val grpcRequest: DeployRequest = DeployRequest.newBuilder().putAllComponentMap(componentMap).putAllNodeMap(nodeMap)
      .setMsg("start").setType(deployType).setNameNode(nameNode).setSecondaryNameNode(secondaryNameNode).build()

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


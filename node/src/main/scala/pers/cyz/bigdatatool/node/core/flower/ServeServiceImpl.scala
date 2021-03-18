package pers.cyz.bigdatatool.node.core.flower

import io.grpc.stub.StreamObserver
import pers.cyz.bigdatatool.node.common.utils.UrlUtils
import pers.cyz.bigdatatool.node.core.download.DownloadExecutor
import pers.cyz.bigdatatool.node.grpc.com.{DownloadComponentRequest, DownloadComponentResponse, ServeGrpc, editFileRequest, editFileResponse}

import java.lang.Thread.sleep
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import scala.collection.mutable.ArrayBuffer

class ServeServiceImpl extends ServeGrpc.ServeImplBase {

  private val logger = Logger.getLogger(classOf[ServeServiceImpl].getName)

//  override def register(request: RegisterRequest, responseObserver: StreamObserver[RegisterResponse]): Unit = {
//    @volatile var lock = true
//
//    while (lock) {
//      val response: RegisterResponse = RegisterResponse.newBuilder().setStatus(
//        FlowerStatus.newBuilder()
//          .setIp(FlowerStatusObj.ip)
//          .setStatus(FlowerStatusObj.status).build()
//      ).build()
//      responseObserver.onNext(response)
//      TimeUnit.SECONDS.sleep(10)
//    }
//    responseObserver.onCompleted()
//
//  }


  override def downloadComponent(responseObserver: StreamObserver[DownloadComponentResponse]): StreamObserver[DownloadComponentRequest] = {

    new StreamObserver[DownloadComponentRequest] {
      override def onNext(v: DownloadComponentRequest): Unit = {
        v.getCommandType match {
          case "start" =>
            val arrayUrl: ArrayBuffer[URL] = new ArrayBuffer[URL]()
            v.getComponentMapMap.forEach((key, value) => {
              arrayUrl.addOne(UrlUtils.getUrl(key, value))
            })
            val downloader = new DownloadExecutor()
            downloader.downloadExecute(arrayUrl.toArray)
            val downloadThread = new Thread() {
              override def run(): Unit = {
                while (DownloadExecutor.downloadSize.get() < downloader.totalSize) {
                  sleep(1000)
                  responseObserver.onNext(DownloadComponentResponse.newBuilder()
                    .setAlreadyDownloadSize(DownloadExecutor.downloadSize.get())
                    .setTotalSize(downloader.totalSize).build())
                }
              }
            }
            downloadThread.run()
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.warning(throwable.toString)
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }
  }


  override def editFile(request: editFileRequest, responseObserver: StreamObserver[editFileResponse]): Unit = {
    super.editFile(request, responseObserver)
  }

}

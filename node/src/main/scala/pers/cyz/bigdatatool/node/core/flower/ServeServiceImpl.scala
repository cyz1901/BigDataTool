package pers.cyz.bigdatatool.node.core.flower

import io.grpc.stub.StreamObserver
import org.dom4j.{Document, Element}
import org.dom4j.io.{SAXReader, XMLWriter}
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.node.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.node.common.utils.UrlUtils
import pers.cyz.bigdatatool.node.core.download.DownloadExecutor
import pers.cyz.bigdatatool.node.grpc.com.{DownloadComponentRequest, DownloadComponentResponse, ServeGrpc, deployRequest, deployResponse}

import java.io.{BufferedWriter, File, FileWriter}
import java.lang.Thread.sleep
import java.net.URL
import sys.process._
import scala.collection.mutable.ArrayBuffer

class ServeServiceImpl extends ServeGrpc.ServeImplBase {
  private val logger = LoggerFactory.getLogger(classOf[ServeServiceImpl])

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
        logger.error(throwable.toString)
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }
  }

  /**
   */
  override def deploy(request: deployRequest, responseObserver: StreamObserver[deployResponse]): Unit = {
    import java.io.FileWriter

    def findProperty(root: Element, name: String, value: String): Unit = {
      val i = root.elementIterator("property")
      while ( {
        i.hasNext
      }) {
        val foo = i.next.asInstanceOf[Element]
        if (foo.element("name").getData == name) {
          val aa = foo.element("name").getParent.element("value")
          aa.setText(value)
        }
      }
    }

    val reader: SAXReader = new SAXReader()
    //部署逻辑 flowerNode
    request.getComponentMapMap.forEach((key, value) => {
      //解压
      val res = s"tar xvf ${key}-${value}".!

      //配置配置文件(hadoop-env.sh)
      val bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
        s"/BDMData/$key-$value/etc/hadoop/hadoop-env.sh"))
      bufferWriter.write(s"JAVA_HOME=${System.getProperty("java.home")}")
      bufferWriter.close()

      //配置文件(core.site)
      val docCore: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"));
      findProperty(docCore.getRootElement, "fs.defaultFS", "hello")
      val writer = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"))
      writer.write(docCore)
      writer.close()

      //配置文件(hdfs-site.xml)
      val docHdfs: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/hdfs-site.xml"));
      findProperty(docHdfs.getRootElement, "dfs.replication", AppConfig.serve.nodeCount.toString)

//      //配置文件(yarn-env.sh)
//      val bufferWriterYarn: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
//        s"/BDMData/$key-$value/etc/hadoop/hadoop-env.sh"))
//      bufferWriterYarn.write(s"JAVA_HOME=${System.getProperty("java.home")}")
//      bufferWriterYarn.close()

      //      var doc: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/$key-$value" +
      //        s"${System.getProperty("java.home")}"));
    })
  }
}

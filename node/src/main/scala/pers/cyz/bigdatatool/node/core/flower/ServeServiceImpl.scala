package pers.cyz.bigdatatool.node.core.flower

import io.grpc.stub.StreamObserver
import org.dom4j.{Document, Element}
import org.dom4j.io.{SAXReader, XMLWriter}
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.node.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.node.common.utils.UrlUtils
import pers.cyz.bigdatatool.node.core.download.DownloadExecutor
import pers.cyz.bigdatatool.node.grpc.com.{DeployRequest, DeployResponse, DownloadComponentRequest, DownloadComponentResponse, ServeGrpc}

import java.io.{BufferedWriter, File, FileWriter}
import java.lang.Thread.sleep
import java.net.URL
import sys.process._
import scala.collection.mutable.ArrayBuffer

class ServeServiceImpl extends ServeGrpc.ServeImplBase {
  private val logger = LoggerFactory.getLogger(classOf[ServeServiceImpl])

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
  override def deploy(request: DeployRequest, responseObserver: StreamObserver[DeployResponse]): Unit = {
    import java.io.FileWriter

    logger.info("deploy")

    def editProperty(root: Element, name: String, value: String): Unit = {

      def addProperty(root: Element, name: String, value: String): Unit = {
        val p = root.addElement("property")
        val n = p.addElement("name")
        n.setText(name)
        val v = p.addElement("value")
        v.setText(value)
      }

      val list = root.selectNodes("property").asInstanceOf[java.util.List[Element]]
      println(list)
      if (list.isEmpty) {
        addProperty(root, name, value)
      } else {
        list.forEach(x=>{
          if (x.element("name").getData == name){
            x.element("value").setText(value)
          }else{
            addProperty(root, name, value)
          }
        })
      }
    }

    val reader: SAXReader = new SAXReader()

    //部署逻辑 flowerNode
    request.getComponentMapMap.forEach((key, value) => {
      //解压
//      val streamLogger = new ProcessLogger {
//        override def out(s: => String): Unit = ???
//
//        override def err(s: => String): Unit = ???
//
//        override def buffer[T](f: => T): T = ???
//      }
      val process = Seq("tar","xvf",s"/home/cyz/BDMData/${key}-${value}.tar.gz","-C","/home/cyz/BDMData/").!!
//      val p = process run new ProcessIO()
//        p.exitValue()
//      )
//      println(process)

      //配置配置文件(hadoop-env.sh)
      val bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
        s"/BDMData/$key-$value/etc/hadoop/hadoop-env.sh",true))
      bufferWriter.write(s"JAVA_HOME=${System.getProperty("java.home")}")
      bufferWriter.close()

      //配置文件(core-site.xml)
      var nameNode = ""
      request.getNodeMapMap.forEach((key,value)=>{
        if (value == "nameNode") {
          nameNode = key
        }
      })
      val docCore: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"));
      editProperty(docCore.getRootElement, "fs.defaultFS", s"hdfs://$nameNode:9000")
      editProperty(docCore.getRootElement, "hadoop.tmp.dir", s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"data/tmp")
      val writerCore = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"))
      writerCore.write(docCore)
      writerCore.close()

      //配置文件(hdfs-site.xml)
      val docHdfs: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/hdfs-site.xml"));
      editProperty(docHdfs.getRootElement, "dfs.replication", AppConfig.serve.nodeCount.toString)
      val writerHdfs = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/hdfs-site.xml"))
      writerHdfs.write(docCore)
      writerHdfs.close()

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

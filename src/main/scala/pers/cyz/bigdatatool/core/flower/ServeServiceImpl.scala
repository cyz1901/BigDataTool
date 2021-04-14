package pers.cyz.bigdatatool.core.flower

import io.grpc.stub.StreamObserver
import org.dom4j.{Document, Element}
import org.dom4j.io.{OutputFormat, SAXReader, XMLWriter}
import org.slf4j.LoggerFactory
import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.common.utils.UrlUtils
import pers.cyz.bigdatatool.core.download.DownloadExecutor
import pers.cyz.bigdatatool.node.grpc.com.{DeployRequest, DeployResponse, DistributeComponentRequest, DistributeComponentResponse, DownloadComponentRequest, DownloadComponentResponse, ServeGrpc}

import java.io.{BufferedWriter, File, FileInputStream, FileOutputStream, FileWriter, ObjectOutputStream, RandomAccessFile}
import java.lang.Thread.sleep
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
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


  override def distributeComponent(responseObserver: StreamObserver[DistributeComponentResponse]): StreamObserver[DistributeComponentRequest] = {
    new StreamObserver[DistributeComponentRequest] {

      import java.io.FileOutputStream

      val file: File = new File(SystemConfig.userHomePath + "/BDMData/test.zip")
      val os = new FileOutputStream(file)
      val buf = ByteBuffer.allocate(1024)
      var size: Long = 0

      override def onNext(v: DistributeComponentRequest): Unit = {
        v.getMsg match {
          case "start" =>
            v.getData.copyTo(buf)
            size += buf.position()
            v.getData.writeTo(os)
            buf.clear()
            responseObserver.onNext(DistributeComponentResponse.newBuilder()
                              .setAlreadyDistribute(size).setMsg("run").build())

          //            val buf = v.getData.asReadOnlyByteBuffer()
          //            val readableByteChannel = Channels.newChannel(new FileInputStream(file))
          //            val threadAccessFile = new RandomAccessFile(file, "rwd")
          //            val fileChannel = threadAccessFile.getChannel
          //            while (readableByteChannel.read(buf) != -1) {
          //              fileChannel.write(buf)
          //              buf.clear()
          //            }
          //            threadAccessFile.close()
          //            readableByteChannel.close()
          //            v.getComponentMapMap.forEach((key, value) => {
          //              arrayUrl.addOne(UrlUtils.getUrl(key, value))
          //            })
          //            val downloader = new DownloadExecutor()
          //            downloader.downloadExecute(arrayUrl.toArray)
          //            val downloadThread = new Thread() {
          //              override def run(): Unit = {
          //                while (DownloadExecutor.downloadSize.get() < downloader.totalSize) {
          //                  sleep(1000)
          //                  responseObserver.onNext(DownloadComponentResponse.newBuilder()
          //                    .setAlreadyDownloadSize(DownloadExecutor.downloadSize.get())
          //                    .setTotalSize(downloader.totalSize).build())
          //                }
          //              }
          //            }
          //            downloadThread.run()
        }
      }

      override def onError(throwable: Throwable): Unit = {
        responseObserver.onNext(DistributeComponentResponse.newBuilder()
          .setMsg("error").build())
        os.close()
        logger.error(throwable.toString)
      }

      override def onCompleted(): Unit = {
//        responseObserver.onNext(DistributeComponentResponse.newBuilder()
//          .setMsg("finish").build())
//        os.close()
        responseObserver.onCompleted()
        logger.info("Completed")
      }
    }
  }


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
      if (list.isEmpty) {
        addProperty(root, name, value)
      } else {
        list.forEach(x => {
          if (x.element("name").getData == name) {
            x.element("value").setText(value)
          } else {
            addProperty(root, name, value)
          }
        })
      }
    }

    val reader: SAXReader = new SAXReader()


    //部署逻辑 flowerNode
    request.getComponentMapMap.forEach((key, value) => {
      //解压
      responseObserver.onNext(DeployResponse.newBuilder().setStep("extract").setStatus("working").setMessage("正在解压完毕").build())
      val process = Seq("tar", "xvf", s"${SystemConfig.userHomePath}/BDMData/${key}-${value}.tar.gz", "-C", "/home/cyz/BDMData/").!!
      responseObserver.onNext(DeployResponse.newBuilder().setStep("extract").setStatus("finish").setMessage("已经解压完毕").build())

      //设置格式
      val format: OutputFormat = new OutputFormat()
      format.setExpandEmptyElements(true)
      format.setIndentSize(8) // 行缩进
      format.setNewlines(true) // 一个结点为一行

      //配置配置文件(hadoop-env.sh)
      responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(hadoop-env.sh)").build())
      val bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
        s"/BDMData/$key-$value/etc/hadoop/hadoop-env.sh", true))
      bufferWriter.write(s"export JAVA_HOME=${System.getProperty("java.home")}")
      bufferWriter.close()

      //配置文件(core-site.xml)
      responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(core-site.xml)").build())
      var nameNode = ""
      request.getNodeMapMap.forEach((key, value) => {
        if (value == "nameNode") {
          nameNode = key
        }
      })
      println(s"nameNode is ${request.getNodeMapMap}")
      val docCore: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"));
      editProperty(docCore.getRootElement, "fs.defaultFS", s"hdfs://$nameNode:9000")
      editProperty(docCore.getRootElement, "hadoop.tmp.dir", s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"data/tmp")
      val writerCore = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/core-site.xml"), format)
      writerCore.write(docCore)
      writerCore.close()

      //配置文件(hdfs-site.xml)
      responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(hdfs-site.xml)").build())
      val docHdfs: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/hdfs-site.xml"));
      editProperty(docHdfs.getRootElement, "dfs.replication", AppConfig.serve.nodeCount.toString)
      editProperty(docHdfs.getRootElement, "dfs.permissions.enabled", "false")
      val writerHdfs = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
        s"etc/hadoop/hdfs-site.xml"), format)
      writerHdfs.write(docHdfs)
      writerHdfs.close()
      responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("finish").setMessage("配置完成").build())

      //      //配置文件(yarn-env.sh)
      //      val bufferWriterYarn: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
      //        s"/BDMData/$key-$value/etc/hadoop/hadoop-env.sh"))
      //      bufferWriterYarn.write(s"JAVA_HOME=${System.getProperty("java.home")}")
      //      bufferWriterYarn.close()

      //      var doc: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/$key-$value" +
      //        s"${System.getProperty("java.home")}"));

      // TODO 改为master
      if (SystemConfig.localHostName == "Computer") {
        // 初始化namenode
        val process_1 = s"${SystemConfig.userHomePath}/BDMData/${key}-${value}/bin/hdfs namenode -format".!!
        println(process_1)
      }
      responseObserver.onNext(DeployResponse.newBuilder().setStep("init").setStatus("finish").setMessage("初始化NameNode已完成").build())
      responseObserver.onNext(DeployResponse.newBuilder().setStep("close").setStatus("finish").setMessage("close").build())
    })
  }
}

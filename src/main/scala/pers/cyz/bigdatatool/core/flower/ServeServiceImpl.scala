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
import java.nio.channels.{Channels, FileChannel}
import sys.process._
import scala.collection.mutable.ArrayBuffer

class ServeServiceImpl extends ServeGrpc.ServeImplBase {
  private val logger = LoggerFactory.getLogger(classOf[ServeServiceImpl])

  override def downloadComponent(responseObserver: StreamObserver[DownloadComponentResponse]): StreamObserver[DownloadComponentRequest] = {
    val downloader = new DownloadExecutor()

    new StreamObserver[DownloadComponentRequest] {

      var mysize: Long = 0

      override def onNext(v: DownloadComponentRequest): Unit = {
        v.getCommandType match {
          case "start" =>
            val arrayUrl: ArrayBuffer[URL] = new ArrayBuffer[URL]()
            v.getComponentMapMap.forEach((key, value) => {
              arrayUrl.addOne(UrlUtils.getUrl(key, value))
            })
            downloader.downloadExecute(arrayUrl.toArray)
            while ( {
              mysize = DownloadExecutor.downloadSize.get()
              mysize
            } < downloader.totalSize) {
              sleep(1000)
              logger.info(s"size is ${mysize}")
              responseObserver.onNext(DownloadComponentResponse.newBuilder()
                .setAlreadyDownloadSize(mysize)
                .setTotalSize(downloader.totalSize).build())
            }
        }
        responseObserver.onCompleted()
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

      var file: File = _
      var os: FileOutputStream = _
      val buf: ByteBuffer = ByteBuffer.allocate(1024)
      var size: Long = 0

      override def onNext(v: DistributeComponentRequest): Unit = {
        v.getMsg match {
          case "start" => {
            logger.info(s"hello")
            file = new File(SystemConfig.userHomePath + "/BDMData/" + v.getFileName)
            os = new FileOutputStream(file)
          }
          case "run" =>
            v.getData.copyTo(buf)
            size += buf.position()
            v.getData.writeTo(os)
            buf.clear()
            responseObserver.onNext(DistributeComponentResponse.newBuilder()
              .setAlreadyDistribute(size).setMsg("run").build())
        }
      }

      override def onError(throwable: Throwable): Unit = {
        responseObserver.onNext(DistributeComponentResponse.newBuilder()
          .setMsg("error").build())
        os.close()
        logger.error(throwable.toString)
      }

      override def onCompleted(): Unit = {
        responseObserver.onNext(DistributeComponentResponse.newBuilder()
          .setMsg("finish").build())
        os.close()
        responseObserver.onCompleted()
        logger.info("Completed")
      }
    }
  }


  override def deploy(request: DeployRequest, responseObserver: StreamObserver[DeployResponse]): Unit = {
    import java.io.FileWriter

    val nodeMap = request.getNodeMapMap

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
    try {
      request.getComponentMapMap.forEach((key, value) => {
        //解压
        responseObserver.onNext(DeployResponse.newBuilder().setStep("extract").setStatus("working").setMessage("正在解压完毕").build())
        val process = Seq("tar", "xvf", s"${SystemConfig.userHomePath}/BDMData/${key}-${value}.tar.gz", "-C", s"${SystemConfig.userHomePath}/BDMData/").!!
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

        request.getType match {
          case "standAlone" => {

            //配置文件(core-site.xml)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(core-site.xml)").build())
            //          var nameNode = ""
            //          request.getNodeMapMap.forEach((key, value) => {
            //            if (value == "nameNode") {
            //              nameNode = key
            //            }
            //          })
            println(s"nameNode is ${request.getNodeMapMap}")
            val docCore: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
              s"etc/hadoop/core-site.xml"));
            editProperty(docCore.getRootElement, "fs.defaultFS", s"hdfs://${request.getNameNode}:9000")
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

          }
          case "distributed" => {
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

            //配置文件(yarn-env.sh)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(yarn-env.sh)").build())
            val bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
              s"/BDMData/$key-$value/etc/hadoop/yarn-env.sh", true))
            bufferWriter.write(s"export JAVA_HOME=${System.getProperty("java.home")}")
            bufferWriter.close()

            //配置文件(yarn-site.xml)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(yarn-site.xml)").build())
            val docYarn: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
              s"etc/hadoop/yarn-site.xml"));
            editProperty(docYarn.getRootElement, "yarn.nodemanager.aux-services", "mapreduce_shuffle")
            editProperty(docYarn.getRootElement, "yarn.resourcemanager.hostname", request.getSecondaryNameNode)
            val writerYarn = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
              s"etc/hadoop/yarn-site.xml"), format)
            writerYarn.write(docYarn)
            writerYarn.close()

            //配置文件(mapred-env.sh)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(mapred-env.sh)").build())
            val bufferWriterMaper: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
              s"/BDMData/$key-$value/etc/hadoop/mapred-env.sh", true))
            bufferWriterMaper.write(s"export JAVA_HOME=${System.getProperty("java.home")}")
            bufferWriterMaper.close()

            //配置文件(mapred-site.xml)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(mapred-site.xml)").build())
            val docMap: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
              s"etc/hadoop/yarn-site.xml"));
            editProperty(docMap.getRootElement, "mapreduce.framework.name", "yarn")
            val writerMaper = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
              s"etc/hadoop/yarn-site.xml"), format)
            writerMaper.write(docMap)
            writerMaper.close()

            //配置文件(workers)
            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("working").setMessage("配置文件(workers)").build())
            val bufferWorkers: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
              s"/BDMData/$key-$value/etc/hadoop/workers"))
            nodeMap.forEach((key, value) => {
              if (key != AppConfig.serve.masterName) {
                bufferWorkers.write(s"${key}")
                bufferWorkers.flush()
                bufferWorkers.newLine()
                bufferWorkers.flush()
              }
            })
            bufferWorkers.close()

            responseObserver.onNext(DeployResponse.newBuilder().setStep("configure").setStatus("finish").setMessage("配置完成").build())


          }
        }


        // TODO 改为master
        if (SystemConfig.localHostName == request.getNameNode) {
          // 初始化namenode
          val process_1 = s"${SystemConfig.userHomePath}/BDMData/${key}-${value}/bin/hdfs namenode -format".!!
          println(process_1)
        }
        responseObserver.onNext(DeployResponse.newBuilder().setStep("init").setStatus("finish").setMessage("初始化NameNode已完成").build())
        responseObserver.onNext(DeployResponse.newBuilder().setStep("close").setStatus("finish").setMessage("close").build())
        responseObserver.onCompleted()
      })
    } catch {
      case exception: Exception => {
        logger.error(exception.toString)
        responseObserver.onError(exception.getCause)
      }
    }
  }
}

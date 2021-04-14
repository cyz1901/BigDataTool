package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.core.master.MasterNode
import pers.cyz.bigdatatool.uiservice.bean.ComponentDownloadData
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo.ListData
import pers.cyz.bigdatatool.uiservice.controller.DeployController.{de, lock, logger}
import pers.cyz.bigdatatool.uiservice.controller.DistributeController.threadNum

import java.io.File
import java.lang.Thread.sleep
import java.util
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{ConcurrentHashMap, CyclicBarrier}
import java.util.concurrent.locks.ReentrantLock
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint
import scala.util.control.Breaks.{break, breakable}

@ServerEndpoint("/websocket/distribute")
@Component
class DistributeController {
  private val logger = LoggerFactory.getLogger(classOf[DistributeController])
  private var session: Session = _
  val om: ObjectMapper = new ObjectMapper()
  DistributeController.de = this

  @OnOpen def onOpen(session: Session): Unit = {
    this.session = session
    System.out.println("open")
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose def onClose(): Unit = {
  }


  @OnMessage def onMessage(message: String, session: Session): Unit = {
    val requestMsg: util.ArrayList[ComponentDownloadData] = om.readValue(
      message,
      new TypeReference[util.ArrayList[ComponentDownloadData]]() {})
    val file = new File(s"${SystemConfig.userHomePath}/BDMData/cache/test")
    @volatile var completeSignal = true

    DistributeController.threadNum = MasterNode.masterClientArray.length
    logger.info(s"ThreadNum is ${DistributeController.threadNum}")


    MasterNode.masterClientArray.foreach(client => {
      DistributeController.totalComponents += 1
      val thr: Thread = new Thread() {
        val cc = new AtomicLong(0)


        override def run(): Unit = {
          // 执行逻辑更新数据
          client.invokeDistribute(fileName = "test", file, cc)
          while (completeSignal) {
            sleep(1000)
            // 判断更新数据
            if (DistributeController.taskAccumulationSignal.get()) {
              //logger.info(s"${Thread.currentThread().getName} Update tAS ${DistributeController.taskAccumulationSignal.get()} - signal ${DistributeController.threadBarrier}")
              DistributeController.taskAccumulationSignal.set(false)
              DistributeController.threadBarrier.getAndIncrement()
              // todo 增加 需要集群测试
              DistributeController.updateData(cc.get())
            }

            // 判断更新signal
            if (DistributeController.threadBarrier.get() < DistributeController.threadNum) {
              //logger.info(s"${Thread.currentThread().getName} UpdateLock tAS ${DistributeController.taskAccumulationSignal.get()} - tB ${DistributeController.threadBarrier}")
              DistributeController.taskAccumulationSignal.set(true)
            }
          }
        }
      }
      thr.setName(client.getClass.toString)
      thr.start()
    })
    DistributeController.totalSize = DistributeController.threadNum * file.length()

    breakable {
      while (true) {
        sleep(2000)
        // 多线程下载进度同步
        if (DistributeController.threadBarrier.get() >= DistributeController.threadNum) {
          // 判断完成度
          if (DistributeController.totalSize * DistributeController.threadNum <= DistributeController.alreadyDownloadSize && DistributeController.totalSize != 0) {
            completeSignal = false
            //            logger.info(s"Finish allSize is ${DistributeController.totalSize} already is ${DistributeController.alreadyDownloadSize} - signal ${completeSignal}")
            sendMessage(DistributeController.totalSize,
              DistributeController.alreadyDownloadSize, DistributeController.totalComponents, 2, "run")
            DistributeController.threadBarrier.compareAndSet(DistributeController.threadNum, 0)
            DistributeController.clear()
            break
          }
          //          logger.info(s"Main allsize is ${DistributeController.totalSize} already is ${DistributeController.alreadyDownloadSize}")
          sendMessage(DistributeController.totalSize,
            DistributeController.alreadyDownloadSize, DistributeController.totalComponents, 2, "run")
          DistributeController.threadBarrier.compareAndSet(DistributeController.threadNum, 0)
        }
      }
    }
  }


  @OnError def onError(session: Session, error: Throwable): Unit = {
    logger.error("Error")
  }

  /**
   * 实现服务器主动推送
   */

  import java.io.IOException

  @throws[IOException]
  def sendMessage(totalSize: Long,
                  alreadyDownloadSize: Long,
                  totalComponents: Int,
                  nowComponents: Int,
                  status: String): Unit = {
    val downloadNodeList: util.ArrayList[ListData] = new util.ArrayList[DownloadVo.ListData]()
    downloadNodeList.add(new DownloadVo.ListData("node1", 7, 4))
    val msg: DownloadVo = new DownloadVo(
      new DownloadVo.AllData(
        totalComponents, nowComponents, totalSize, alreadyDownloadSize
      ),
      downloadNodeList,
      status
    )
    this.session.getBasicRemote.sendText(om.writeValueAsString(msg))
  }

}

object DistributeController {
  private val logger = LoggerFactory.getLogger(classOf[DistributeController.type])
  var totalSize: Long = 0
  var alreadyDownloadSize: Long = 0
  var totalComponents = 0
  var nowComponents = 0
  var de: DistributeController = _
  var threadNum = 0

  val taskAccumulationSignal: ThreadLocal[Boolean] = new ThreadLocal[Boolean]() {
    override def initialValue(): Boolean = {
      true
    }
  }

  val threadBarrier: AtomicInteger = new AtomicInteger(0)

  private val lock = new ReentrantLock()

  def updateData(alreadyDownloadSize: Long): Unit = {
    try {
      lock.lock()
      this.alreadyDownloadSize = alreadyDownloadSize
    } catch {
      case e: Throwable => logger.error(e.toString)
    } finally {
      lock.unlock()
    }
  }

  def clear(): Unit = {
    this.totalSize = 0
    this.alreadyDownloadSize = 0
    this.totalComponents = 0
    this.nowComponents = 0
    this.threadNum = 0
    this.threadBarrier.set(0)
  }

}
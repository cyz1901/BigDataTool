package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.core.master.{MasterClient, MasterNode}
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo.ListData

import java.io.File
import java.lang.Thread.sleep
import java.util
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{ConcurrentHashMap, CyclicBarrier}
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint
import scala.util.control.Breaks.{break, breakable}

@ServerEndpoint("/websocket/distribute")
@Component
class DistributeController {
  private val logger = LoggerFactory.getLogger(classOf[DistributeController])
  private var session: Session = _
  val om: ObjectMapper = new ObjectMapper()

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
    var nowSize: Long = 0
    var totalSize: Long = 0
    var totalComponents: Int = 0
    val file = new File(s"${SystemConfig.userHomePath}/BDMData/cache/${message.replace("\"", "")}")

    val threadNum = MasterNode.masterClientArray.length
    val cyclicBarrier: CyclicBarrier = new CyclicBarrier(threadNum, new Thread(() => {
      nowSize = DistributeController.alreadyDownloadSize.get()
      DistributeController.alreadyDownloadSize.set(0)
    }))

    MasterNode.masterClientArray.foreach(client => {
      totalComponents += 1
      val work = new DistributeThread(cyclicBarrier, client, message.replace("\"", ""), file)
      work.start()
    })

    totalSize = threadNum * file.length()
    totalComponents = threadNum

    breakable {
      while (true) {
        sleep(1000)
        // 判断完成度
        if (totalSize <= nowSize && totalSize != 0) {
          logger.info(s"Finish allSize is ${totalSize} already is ${nowSize}")
          sendMessage(totalSize, nowSize, totalComponents, DistributeController.nowComponents.get(), "finish")
          DistributeController.clear()
          // gc
          System.gc()
          break
        }
        logger.info(s"Main allsize is ${totalSize} already is ${nowSize}")
        sendMessage(totalSize, nowSize, totalComponents, DistributeController.nowComponents.get(), "run")
        // gc
        System.gc()
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

  class DistributeThread(cyclicBarrier: CyclicBarrier, client: MasterClient, filename: String, filePath: File) extends Thread {
    override def run(): Unit = {
      client.invokeDistribute(cyclicBarrier, filename, filePath)
    }
  }

}

object DistributeController {
  var alreadyDownloadSize = new AtomicLong(0)
  var nowComponents = new AtomicInteger(0)


  def clear(): Unit = {
    this.alreadyDownloadSize.set(0)
    this.nowComponents.set(0)

  }

}
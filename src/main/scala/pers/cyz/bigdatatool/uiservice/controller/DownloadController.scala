package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.core.master.{MasterClient, MasterNode}
import pers.cyz.bigdatatool.uiservice.bean.ComponentDownloadData
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


@ServerEndpoint("/websocket/download")
@Component
class DownloadController {
  private val logger = LoggerFactory.getLogger(classOf[DownloadController])
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
    var totalComponents: Int = 0
    val requestMsg: util.ArrayList[ComponentDownloadData] = om.readValue(
      message,
      new TypeReference[util.ArrayList[ComponentDownloadData]]() {})

    val threadNum = MasterNode.masterClientArray.length
    val cyclicBarrier: CyclicBarrier = new CyclicBarrier(threadNum, new Thread(() => {
      nowSize = DownloadController.alreadyDownloadSize.get()
      DownloadController.alreadyDownloadSize.set(0)
    }))

    val map: java.util.Map[String, String] = new util.HashMap[String, String]()
    requestMsg.forEach(x => {
      map.put(x.getName, x.getVersion)
    })

    MasterNode.masterClientArray.foreach(client => {
      totalComponents += 1
      val work = new DownloadThread(cyclicBarrier, client, map)
      work.start()
    })

    totalComponents = threadNum

    breakable {
      while (true) {
        sleep(1000)
        // 判断完成度
        if (DownloadController.totalSize.get() * threadNum <= nowSize && DownloadController.totalSize.get() * threadNum != 0) {
          logger.info(s"already is ${DownloadController.totalSize.get()}")
          sendMessage(DownloadController.totalSize.get() * threadNum, nowSize, totalComponents, DownloadController.nowComponents.get(), "finish")
          DownloadController.clear()
          // gc
          System.gc()
          break
        }
        logger.info(s"already is ${nowSize}")
        sendMessage(DownloadController.totalSize.get() * threadNum, nowSize, totalComponents, DownloadController.nowComponents.get(), "run")
      }
    }
  }


  @OnError def onError(session: Session, error: Throwable): Unit = {
    println(error)
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

  class DownloadThread(cyclicBarrier: CyclicBarrier, client: MasterClient, map: java.util.Map[String, String]) extends Thread {
    override def run(): Unit = {
      client.invokeDownload(cyclicBarrier, map)
    }
  }

}

object DownloadController {
  var totalSize = new AtomicLong(0)
  var alreadyDownloadSize = new AtomicLong(0)
  var nowComponents = new AtomicInteger(0)


  def clear(): Unit = {
    this.totalSize.set(0)
    this.alreadyDownloadSize.set(0)
    this.nowComponents.set(0)

  }
}
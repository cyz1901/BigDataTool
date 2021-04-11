package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.core.master.MasterNode
import pers.cyz.bigdatatool.uiservice.bean.ComponentDownloadData
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo
import pers.cyz.bigdatatool.uiservice.bean.vo.DownloadVo.ListData

import java.lang.Thread.sleep
import java.util
import java.util.concurrent.ConcurrentHashMap
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
    val requestMsg: util.ArrayList[ComponentDownloadData] = om.readValue(
      message,
      new TypeReference[util.ArrayList[ComponentDownloadData]]() {})

    val map: java.util.Map[String, String] = new util.HashMap[String, String]()
    requestMsg.forEach(x => {
      map.put(x.getName, x.getVersion)
    })

    var threadNum = 0
    MasterNode.masterClientArray.foreach(client => {
      DownloadController.totalComponents += map.size()
      threadNum += 1
      val thr = new Thread() {
        override def run(): Unit = {
          client.invokeDistribute(map)
        }
      }
      thr.setName(client.getClass.toString)
      thr.start()
    })

    breakable {
      while (true) {
        sleep(1000)
        if (DownloadController.totalSize * threadNum <= DownloadController.alreadyDownloadSize && DownloadController.totalSize != 0) {
          DownloadController.updateData()
          logger.info(s"Main allsize is ${DownloadController.totalSize} already is ${DownloadController.alreadyDownloadSize}")
          DownloadController.clear()
          break
        }
        logger.info(s"Main allsize is ${DownloadController.totalSize} already is ${DownloadController.alreadyDownloadSize}")
        DownloadController.updateData()
        sendMessage(DownloadController.totalSize,
          DownloadController.alreadyDownloadSize, DownloadController.totalComponents, 2, "run")
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


}

object DistributeController {
  var totalSize: Long = 0
  var alreadyDownloadSize: Long = 0
  var totalComponents = 0
  var ha = new ConcurrentHashMap[Long, Long]()

  def downloadControllerCallback(threadId: Long, alreadyDownloadSize: Long): Unit = {
    ha.put(threadId, alreadyDownloadSize)
  }

  def updateData(): Unit = {
    alreadyDownloadSize = 0
    ha.forEach((key, value) => {
      alreadyDownloadSize += value
    })
  }

  def clear(): Unit = {
    this.totalSize = 0
    this.alreadyDownloadSize = 0
    this.totalComponents = 0
    this.ha.clear()
  }
}
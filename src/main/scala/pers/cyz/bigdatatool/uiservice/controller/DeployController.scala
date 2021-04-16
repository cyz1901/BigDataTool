package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.core.master.{MasterClient, MasterNode}
import pers.cyz.bigdatatool.uiservice.bean.DeployData
import pers.cyz.bigdatatool.uiservice.bean.vo.DeployVo

import java.io.File
import java.lang.Thread.sleep
import java.util
import java.util.EventObject
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.locks.ReentrantLock
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint
import scala.util.control.Breaks.{break, breakable}
import scala.jdk.CollectionConverters._

@ServerEndpoint("/websocket/deploy")
@Component
class DeployController {
  private val logger = LoggerFactory.getLogger(classOf[DeployController])
  private var session: Session = _
  val om: ObjectMapper = new ObjectMapper()

  class OnMessageListener extends EventObject {

    import java.util.EventObject

    def onEvent(e: EventObject): Unit = {
      System.out.println("event come")
    }
  }

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
    val requestMsg: DeployData = om.readValue(
      message,
      classOf[DeployData])

    val threadNum = MasterNode.masterClientArray.length
    val cyclicBarrier: CyclicBarrier = new CyclicBarrier(threadNum, new Thread(() => {
      sendMessage(DeployController.message,
        DeployController.status,
        DeployController.step)
    }))

    val nodeMap: util.Map[String, String] = new util.HashMap[String, String]()
    requestMsg.getNodeData.forEach(x => {
      nodeMap.put(x.getHostname, x.getNodeType)
    })

    val componentMap: util.Map[String, String] = new util.HashMap[String, String]()
    requestMsg.getComponentData.forEach(x => {
      componentMap.put(x.getName, x.getVersion)
    })

    logger.info(nodeMap.size().toString)
    logger.info(componentMap.size().toString)

    MasterNode.masterClientArray.foreach(client => {
      val work = new DeployThread(cyclicBarrier, client, nodeMap, componentMap, requestMsg.getDeployType, requestMsg.getColonyName)
      work.start()
    })

  }


  @OnError def onError(session: Session, error: Throwable): Unit = {
    println(error)
  }

  /**
   * 实现服务器主动推送
   */

  import java.io.IOException

  @throws[IOException]
  def sendMessage(
                   message: String,
                   status: String,
                   step: String
                 ): Unit = {
    val msg: DeployVo = new DeployVo()
    status match {
      case null =>
        msg.setStatus("defeat")
      case "working" =>
        msg.setStatus("defeat")
      case "finish" =>
        msg.setStatus("success")
    }
    msg.setSubtitle(message)
    msg.setTitle(step)
    this.session.getBasicRemote.sendText(om.writeValueAsString(msg))
  }

  class DeployThread(cyclicBarrier: CyclicBarrier,
                     client: MasterClient,
                     nodeMap: util.Map[String, String],
                     componentMap: util.Map[String, String],
                     DeployType: String,
                     ColonyName: String) extends Thread {
    override def run(): Unit = {
      client.invokeDeploy(cyclicBarrier, nodeMap, componentMap, DeployType, ColonyName)
    }
  }
}

// 没有实现分布式信息传送的竞争
object DeployController {
  private val logger = LoggerFactory.getLogger(classOf[DeployController.type])
  var message: String = _
  var status: String = _
  var step: String = _

  private val lock = new ReentrantLock()

  def setMessage(message: String, status: String, step: String): Unit = {
    try {
      lock.lock()
      this.message = message
      this.status = status
      this.step = step
    } catch {
      case e: Throwable => logger.error(e.toString)
    } finally {
      lock.unlock()
    }
  }

}
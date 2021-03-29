package pers.cyz.bigdatatool.node.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.node.core.master.MasterNode
import pers.cyz.bigdatatool.node.uiservice.pojo.{ComponentDownloadData, DeployData, DownloadMsgData}

import java.lang.Thread.sleep
import java.util
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint
import scala.util.control.Breaks.{break, breakable}

@ServerEndpoint("/websocket/deploy")
@Component
class DeployController {
  private val logger = LoggerFactory.getLogger(classOf[DeployController])
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
    val requestMsg: DeployData = om.readValue(
      message,
      classOf[DeployData])
    //    println(requestMsg.getNodeData)
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
      val thr = new Thread() {
        override def run(): Unit = {
          client.invokeDeploy(nodeMap, componentMap, requestMsg.getDeployType)
        }
      }
      thr.setName(client.getClass.toString)
      thr.start()
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
  def sendMessage(): Unit = {
    //    this.session.getBasicRemote.sendText(om.writeValueAsString(msg))
  }


}
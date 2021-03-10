package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.{Component, Controller}
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import pers.cyz.bigdatatool.uiservice.common.untils.Result
import pers.cyz.bigdatatool.uiservice.pojo.{ColonyObj, DownloadMsgData}
import pers.cyz.bigdatatool.uiservice.pojo.ColonyObj.{ComponentMsgData, NodesMsgData}

import java.util
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint


@Controller
class ColonyController {


  @GetMapping
  @ResponseBody
  @RequestMapping(value = Array("v1/colony/nodes"))
  def responseNodes: Result = {
    val res = new Result
    val nodesMsgList: util.ArrayList[ColonyObj.NodesMsgData] = new util.ArrayList[ColonyObj.NodesMsgData]()
    val componentMsgList: util.ArrayList[ColonyObj.ComponentMsgData] = new util.ArrayList[ColonyObj.ComponentMsgData]()
    val list: util.ArrayList[java.lang.String] = new util.ArrayList[java.lang.String]()
    list.add("1111111")
    nodesMsgList.add(new NodesMsgData("localhost", "192.168.1.1", false))
    nodesMsgList.add(new NodesMsgData("node1", "192.168.1.2", false))
    componentMsgList.add(new ComponentMsgData("hadoop", "hadoor is a big data", false, list))
    val oo: ColonyObj = new ColonyObj(
      nodesMsgList, componentMsgList
    )
    res.setData(oo)
    res.setCode(200)
    res
  }

}

@ServerEndpoint("/websocket/download")
@Component
class downloadController {

  private var session: Session = _
  val om: ObjectMapper  = new ObjectMapper();

  @OnOpen def onOpen(session: Session): Unit = {
    this.session = session
    System.out.println("open")
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose def onClose(): Unit = {
    System.out.println("close")
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息 */
  @OnMessage def onMessage(message: String, session: Session): Unit = {
    println(message)
    sendMessage("lll")
  }


  @OnError def onError(session: Session, error: Throwable): Unit = {
    println(error)
  }

  /**
   * 实现服务器主动推送
   */

  import java.io.IOException

  @throws[IOException]
  def sendMessage(message: String): Unit = {
    //    val list: util.ArrayList[java.lang.String] = new util.ArrayList[java.lang.String]()
    //    list.add("aaa")
    //    list.add("bbb")
    var downloadNodeList: util.ArrayList[DownloadMsgData.ListData] = new util.ArrayList[DownloadMsgData.ListData]()
    downloadNodeList.add(new DownloadMsgData.ListData("node1", 7, 4))
    downloadNodeList
    val msg: DownloadMsgData = new DownloadMsgData(
      new DownloadMsgData.AllData(
        100, 10, 1000, 50
      ),
      downloadNodeList
    )
    this.session.getBasicRemote.sendText(om.writeValueAsString(msg))
  }
}

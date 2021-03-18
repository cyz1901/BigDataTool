package pers.cyz.bigdatatool.node.uiservice.controller

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.stub.StreamObserver
import org.springframework.stereotype.Component
import pers.cyz.bigdatatool.node.core.master.MasterNode.asyncStub
import pers.cyz.bigdatatool.node.grpc.com.{DownloadComponentRequest, DownloadComponentResponse}
import pers.cyz.bigdatatool.node.uiservice.pojo.{ComponentDownloadData, DownloadMsgData}

import java.util
import java.util.logging.Logger
import javax.websocket.{OnClose, OnError, OnMessage, OnOpen, Session}
import javax.websocket.server.ServerEndpoint
import scala.jdk.CollectionConverters._


@ServerEndpoint("/websocket/download")
@Component
class DownloadController {

  private val logger = Logger.getLogger(classOf[DownloadController].getName)
  private var session: Session = _
  val om: ObjectMapper = new ObjectMapper();

  @OnOpen def onOpen(session: Session): Unit = {
    this.session = session
    System.out.println("open")
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose def onClose(): Unit = {
  }

  /**
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息 */
  @OnMessage def onMessage(message: String, session: Session): Unit = {
    val requestMsg: util.ArrayList[ComponentDownloadData] = om.readValue(
      message,
      new TypeReference[util.ArrayList[ComponentDownloadData]]() {})

    val map: java.util.Map[String, String] = new util.HashMap[String, String]()
    requestMsg.forEach(x => {
      map.put(x.getName, x.getVersion)
    })


    val grpcResponse: StreamObserver[DownloadComponentResponse] = new StreamObserver[DownloadComponentResponse] {
      override def onNext(v: DownloadComponentResponse): Unit = {
        logger.info("Now totalSIze: " + v.getTotalSize + " nowSize is: " + v.getAlreadyDownloadSize)
        sendMessage(v.getTotalSize, v.getAlreadyDownloadSize, "run")
        if (v.getTotalSize <= v.getAlreadyDownloadSize) {
          sendMessage(v.getTotalSize, v.getAlreadyDownloadSize, "finish")
          onClose()
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    val grpcRequest: StreamObserver[DownloadComponentRequest] = asyncStub.downloadComponent(grpcResponse)
    // TODO 假数据验证
    grpcRequest.onNext(DownloadComponentRequest.newBuilder().putAllComponentMap(map).setCommandType("start").build())


  }


  @OnError def onError(session: Session, error: Throwable): Unit = {
    println(error)
  }

  /**
   * 实现服务器主动推送
   */

  import java.io.IOException

  @throws[IOException]
  def sendMessage(totalSize: Long, alreadyDownloadSize: Long, status: String): Unit = {
    var downloadNodeList: util.ArrayList[DownloadMsgData.ListData] = new util.ArrayList[DownloadMsgData.ListData]()
    downloadNodeList.add(new DownloadMsgData.ListData("node1", 7, 4))
    val msg: DownloadMsgData = new DownloadMsgData(
      new DownloadMsgData.AllData(
        100, 10, totalSize, alreadyDownloadSize
      ),
      downloadNodeList,
      status
    )
    this.session.getBasicRemote.sendText(om.writeValueAsString(msg))
  }
}
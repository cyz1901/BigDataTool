package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.stub.StreamObserver
import org.springframework.stereotype.{Component, Controller}
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import pers.cyz.bigdatatool.common.pojo.ComponentMap
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo.{ComponentMsgData, NodesMsgData}
import pers.cyz.bigdatatool.uiservice.untils.Result

import java.util
import scala.jdk.CollectionConverters._


@Controller
class ColonyController {
  @GetMapping
  @ResponseBody
  @RequestMapping(value = Array("v1/colony"))
  def responseNodes: Result = {
    val res = new Result()
    //初始化数据
    val nodesMsgList: util.ArrayList[ColonyVo.NodesMsgData] = new util.ArrayList[ColonyVo.NodesMsgData]()
    val componentMsgList: util.ArrayList[ColonyVo.ComponentMsgData] = new util.ArrayList[ColonyVo.ComponentMsgData]()
    nodesMsgList.add(new NodesMsgData("localhost", "192.168.1.1", false))
    nodesMsgList.add(new NodesMsgData("node1", "192.168.1.2", false))

    ComponentMap.componentVersionMap.foreach(t => {
      componentMsgList.add(new ComponentMsgData(t._1, "Hadoop是一个由Apache基金会所开发的分布式系统基础架构", false, t._2.asJava))
    })
    val oo: ColonyVo = new ColonyVo(
      nodesMsgList, componentMsgList
    )
    res.setData(oo)
    res.setCode(200)
    res
  }
}

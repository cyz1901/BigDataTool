package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.stub.StreamObserver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.{Component, Controller}
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestMapping, RequestParam, ResponseBody, RestController}
import org.springframework.web.multipart.MultipartFile
import pers.cyz.bigdatatool.common.pojo.ComponentMap
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo.{ComponentMsgData, NodesMsgData}
import pers.cyz.bigdatatool.uiservice.service.ColonyService
import pers.cyz.bigdatatool.uiservice.untils.Result

import java.util
import scala.jdk.CollectionConverters._


@RestController
class ColonyController {

  @Autowired var colonyService: ColonyService = _

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

  @PostMapping
  @ResponseBody
  @RequestMapping(value = Array("v1/colony/file"))
  def uploadFile(@RequestParam("fileUpload") fileUpload: Array[MultipartFile]): Unit = {
    colonyService.uploadFile(fileUpload)
    println(fileUpload.length)
  }
}

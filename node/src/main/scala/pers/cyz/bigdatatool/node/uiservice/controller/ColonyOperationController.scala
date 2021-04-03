package pers.cyz.bigdatatool.node.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, ResponseBody}
import pers.cyz.bigdatatool.node.common.pojo.ComponentMap
import pers.cyz.bigdatatool.node.uiservice.pojo.ColonyObj
import pers.cyz.bigdatatool.node.uiservice.pojo.ColonyObj.{ComponentMsgData, NodesMsgData}
import pers.cyz.bigdatatool.node.uiservice.service.{ClustersService, ColonyOperationService}
import pers.cyz.bigdatatool.node.uiservice.untils.Result

import java.util
import scala.jdk.CollectionConverters._


//@Controller
//class ColonyOperationController {
//  @Autowired var colonyOperationService: ColonyOperationService= _
//
//  @ResponseBody
//  @GetMapping(value = Array("v1/clusters"))
//  def responseNodes = {
//
//  }
//}

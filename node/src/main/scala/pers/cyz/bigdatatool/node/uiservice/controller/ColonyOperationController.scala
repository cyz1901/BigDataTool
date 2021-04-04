package pers.cyz.bigdatatool.node.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RequestMapping, ResponseBody, RestController}
import pers.cyz.bigdatatool.node.common.pojo.ComponentMap
import pers.cyz.bigdatatool.node.uiservice.bean.vo.ColonyOperationVo
import pers.cyz.bigdatatool.node.uiservice.bean.vo.ColonyVo.{ComponentMsgData, NodesMsgData}
import pers.cyz.bigdatatool.node.uiservice.service.{ClustersService, ColonyOperationService}
import pers.cyz.bigdatatool.node.uiservice.untils.Result

import java.util
import scala.jdk.CollectionConverters._


//@RestController
//class ColonyOperationController {
//  @Autowired var colonyOperationService: ColonyOperationService= _
//
//  @RequestBody
//  @GetMapping(value = Array("v1/colonyOperation"))
//  def getFileList(@RequestBody body: ColonyOperationVo): Result = {
//    colonyOperationService
//  }
//}

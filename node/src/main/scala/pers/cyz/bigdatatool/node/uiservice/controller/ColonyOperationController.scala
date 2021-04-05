package pers.cyz.bigdatatool.node.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, PutMapping, RequestBody, RequestMapping, RequestParam, ResponseBody, RestController}
import pers.cyz.bigdatatool.node.common.pojo.ComponentMap
import pers.cyz.bigdatatool.node.uiservice.bean.vo.ColonyOperationVo
import pers.cyz.bigdatatool.node.uiservice.bean.vo.ColonyVo.{ComponentMsgData, NodesMsgData}
import pers.cyz.bigdatatool.node.uiservice.service.{ClustersService, ColonyOperationService}
import pers.cyz.bigdatatool.node.uiservice.untils.Result

import java.util
import scala.jdk.CollectionConverters._


@RestController
class ColonyOperationController {
  @Autowired var colonyOperationService: ColonyOperationService = _


  @RequestBody
  @PutMapping(value = Array("v1/colonyOperation/colony"))
  def operationColony(@RequestParam("operation") body: String): Result = {
    val result: Result = new Result()
    println(body)
    body match {
      case "start" => {
        colonyOperationService.startColony()
      }
      case "stop" => {
        colonyOperationService.stopColony()
      }
    }
    result
  }

  @RequestBody
  @GetMapping(value = Array("v1/colonyOperation/file"))
  def getFileList(): Result = {
    val result: Result = new Result()
    val re = colonyOperationService.getFileList()
    result.setData(re)
    result.setCode(200)
    result
  }

  @RequestBody
  @GetMapping(value = Array("v1/colonyOperation/colony"))
  def getColonyState(): Unit ={

  }
}

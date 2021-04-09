package pers.cyz.bigdatatool.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, PathVariable, PostMapping, PutMapping, RequestBody, RequestMapping, RequestParam, ResponseBody, RestController}
import pers.cyz.bigdatatool.uiservice.service.ColonyOperationService
import pers.cyz.bigdatatool.uiservice.untils.Result



@RestController
class ColonyOperationController {
  @Autowired var colonyOperationService: ColonyOperationService = _


  @RequestBody
  @PutMapping(value = Array("v1/colonyOperation/colony/{operation}"))
  def operationColony(@PathVariable("operation") operation: String): Result = {
    val result: Result = new Result()
    operation match {
      case "start" => {
        colonyOperationService.startColony() match {
          case 1 => result.setData("alive")
          case _ => result.setData("error")
        }
      }
      case "stop" => {
        colonyOperationService.stopColony() match {
          case 1 => result.setData("stop")
          case _ => result.setData("error")
        }
      }
    }
    println(s"noew result is ${result.getData}")
    result.setCode(200)
    result
  }

  @RequestBody
  @PutMapping(value = Array("v1/colonyOperation/file"))
//  @GetMapping(value = Array("v1/colonyOperation/file"))
  def getFileList(@RequestParam("fileAddr") body: String): Result = {
    val result: Result = new Result()
    val re = colonyOperationService.getFileList(body)
    result.setData(re)
    result.setCode(200)
    result
  }

  @RequestBody
  @GetMapping(value = Array("v1/colonyOperation/colony"))
  def getColonyStatus: Result ={
    val result: Result = new Result()
    val re = colonyOperationService.getColonyStatus
    result.setCode(200)
    re match {
      case "\"active\"" => result.setData("alive")
      case "stop" => result.setData("stop")
      case _ => result.setData("error")
    }
    result
  }

  @RequestBody
  def toColonyWebUi ={
    val re = colonyOperationService.toColonyWebUi()
    val result: Result = new Result()
    result.setCode(200)
    result.setData(re)
    result
  }
}

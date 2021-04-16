package pers.cyz.bigdatatool.uiservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.grpc.stub.StreamObserver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.{Component, Controller}
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestMapping, RequestParam, ResponseBody, RestController}
import org.springframework.web.multipart.MultipartFile
import pers.cyz.bigdatatool.common.pojo.{ComponentMap, RuntimeMeta}
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
  def getNodes: Result = {
    val res = new Result()
    res.setData(colonyService.getNodes)
    res.setCode(200)
    res
  }

  @PostMapping
  @ResponseBody
  @RequestMapping(value = Array("v1/colony/file"))
  def uploadFile(@RequestParam("fileUpload") fileUpload: Array[MultipartFile]): Unit = {
    colonyService.uploadFile(fileUpload)
    println(s"fileUpload(0).getName is ${fileUpload(0).getName}")
    println(s"fileUpload(0).getOriginalFilename is ${fileUpload(0).getOriginalFilename}")

  }
}

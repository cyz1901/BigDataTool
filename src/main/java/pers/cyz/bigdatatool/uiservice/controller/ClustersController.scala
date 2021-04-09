package pers.cyz.bigdatatool.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.web.bind.annotation.{GetMapping, RequestBody, RestController}
import pers.cyz.bigdatatool.uiservice.service.ClustersService
import pers.cyz.bigdatatool.uiservice.untils.Result

import scala.collection.JavaConverters._


@RestController
class ClustersController {

  @Autowired var clustersService: ClustersService = _

  @RequestBody
  @GetMapping(value = Array("v1/clusters"))
  def getClusters: Result ={
    val response = new Result(200,this.clustersService.getClusters.asJava)
    response
  }

}

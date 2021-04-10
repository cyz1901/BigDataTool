//TODO 增加setting设置需要重新构造AppConfig(double check) 下一个版本实现
/*
package pers.cyz.bigdatatool.node.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{GetMapping, PostMapping, RequestBody, RestController}
import pers.cyz.bigdatatool.node.uiservice.service.SettingService
import pers.cyz.bigdatatool.node.uiservice.untils.Result


@RestController
class SettingController {

  @Autowired var settingService: SettingService = _

  @RequestBody
  @GetMapping(value = Array("v1/setting/get"))
  def getSetting: Result = {
    println("get")
    val res = new Result
    val rr = settingService.getSetting
    res.setData(rr)
    res.setCode(200)
    res
  }

  @RequestBody
  @PostMapping(value = Array("v1/setting/update"))
  def updateSetting(@RequestBody body: String): Unit ={
    settingService.updateSetting(body)

  }
}
*/

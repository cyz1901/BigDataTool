package pers.cyz.bigdatatool.node.uiservice.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import pers.cyz.bigdatatool.node.common.config.AppConfig
import pers.cyz.bigdatatool.node.common.utils.loader.Loader
import pers.cyz.bigdatatool.node.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.node.uiservice.bean.pojo.SettingPojo
import pers.cyz.bigdatatool.node.uiservice.service.SettingService
import pers.cyz.bigdatatool.node.uiservice.untils.Result


@Controller
@RequestMapping(value = Array("v1/setting"))
class SettingController {

  @Autowired var settingService: SettingService = _

  @GetMapping
  @ResponseBody
  def response: Result = {
    val loader = new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("node/src/main/resource/etc/node.yml").build()
    val xx: AppConfig.type = loader.fileToObjMapping()
    val res = new Result
    val rr = settingService.getSetting
    res.setData(rr)
    res.setCode(200)
    res
  }
}

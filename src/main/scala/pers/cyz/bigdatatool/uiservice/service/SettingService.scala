/*
package pers.cyz.bigdatatool.node.uiservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pers.cyz.bigdatatool.node.common.config.AppConfig
import pers.cyz.bigdatatool.node.common.utils.loader.Loader
import pers.cyz.bigdatatool.node.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.node.uiservice.bean.pojo.SettingPojo
import pers.cyz.bigdatatool.node.uiservice.dao.SettingDao
import com.fasterxml.jackson.databind.ObjectMapper

import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

@Service
class SettingService {

  @Autowired var settingDao :SettingDao = _

  def saveSetting(): Unit = {

  }

  def getSetting: java.util.List[SettingPojo] = {
    val loader = new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("node/src/main/resource/etc/node.yml").build()
    val config: AppConfig.type = loader.fileToObjMapping()
    val list: ArrayBuffer[SettingPojo] = new ArrayBuffer[SettingPojo]()
    list.addOne(new SettingPojo("仓库地址",config.repository.url))
    list.addOne(new SettingPojo("元数据地址",config.serve.metaAddress))
    list.asJava
  }

  def updateSetting(body: String): Unit ={
    val om = new ObjectMapper()
    val setting = om.readTree(body)
    AppConfig.repository.url = setting.get(0).get("value").toString
    AppConfig.serve.metaAddress = setting.get(1).get("value").toString
    println(AppConfig.serve.metaAddress)
    om.writeValue(new File("node/src/main/resource/etc/node.yml"), AppConfig)

  }
}
*/

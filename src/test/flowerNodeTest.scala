import pers.cyz.bigdatatool.common.config.AppConfig
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.core.flower.FlowerNode

object flowerNodeTest {
  def hello (): Unit ={

  }
  def main(args: Array[String]): Unit = {
    // 获取配置
    new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("src/main/resources/etc/node.yml").build().fileToObjMapping()
    val node = FlowerNode
    node.run()
    //    println(AppConfig.repository.url)
    //    println(UrlUtils.getUrl("hadoop","123456"))
    //    println(SystemConfig.compressedFormat)
    //    println(SystemConfig.localHostIp)
    //    println(SystemConfig.localHostName)
  }
}

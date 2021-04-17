package pers.cyz.bigdatatool.core.distributed

import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml
import pers.cyz.bigdatatool.core.flower.FlowerNode

import java.io.File

object FlowerMain {
  def main(args: Array[String]): Unit = {
    new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("src/main/resources/etc/node.yml").build().fileToObjMapping()

    //检测默认文件是否存在
    val path = new File(s"${SystemConfig.userHomePath}/${AppConfig.repository.downloadFile}/")
    path.mkdirs()
    val node = FlowerNode
    node.run()
  }
}

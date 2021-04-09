import com.fasterxml.jackson.databind.ObjectMapper
import org.dom4j.Element
import pers.cyz.bigdatatool.common.config.AppConfig
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml

import java.io.File

object ShellTest {


  def editProperty(root: Element, name: String, value: String): Unit = {

    def addProperty(root: Element, name: String, value: String): Unit = {
      val p = root.addElement("property")
      val n = p.addElement("name")
      n.setText(name)
      val v = p.addElement("value")
      v.setText(value)
    }

    val list = root.selectNodes("property").asInstanceOf[java.util.List[Element]]
    println(list)
    if (list.isEmpty) {
      addProperty(root, name, value)
    } else {
      list.forEach(x => {
        if (x.element("name").getData == name) {
          x.element("value").setText(value)
        } else {
          addProperty(root, name, value)
        }
      })
    }
  }

  def main(args: Array[String]): Unit = {
    val om = new ObjectMapper()
    val loader = new Loader[AppConfig.type]().Builder
      .setLoaderType(Yaml)
      .setConfigFilePath("node/src/main/resource/etc/node.yml").build()
    val config: AppConfig.type = loader.fileToObjMapping()
    config.repository.url = "lll"
    config.serve.metaAddress = "kkk"
    println(config.repository.url)
    om.writeValue(new File("node/src/main/resource/etc/node.yml"), config)
  }
}

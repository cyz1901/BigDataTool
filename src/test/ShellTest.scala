import com.fasterxml.jackson.databind.ObjectMapper
import org.dom4j.Element
import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml

import java.io.File

object ShellTest {


  def main(args: Array[String]): Unit = {
    val path = new File(s"hello/world")
    path.mkdirs()




  }
}

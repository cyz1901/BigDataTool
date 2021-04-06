import org.dom4j.{Document, Element}
import org.dom4j.io.{SAXReader, XMLWriter}
import pers.cyz.bigdatatool.node.common.config.SystemConfig
import pers.cyz.bigdatatool.node.common.utils.IpUtils
import pers.cyz.bigdatatool.node.uiservice.dao.{ClustersDao}

import java.io.{BufferedWriter, File, FileWriter, Writer}
import java.nio.file.Paths
import sys.process._
import scala.collection.JavaConverters._

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
    //    val d = new ClustersDao()
    //    val aa = d.selectAllClusters()
    //    println(aa)
    //    val client = new ColonyOperationDao()
    //    client.getFileList()
    Process("ls").###("ls -all").!
    println("111")

  }
}

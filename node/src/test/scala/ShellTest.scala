import org.dom4j.{Document, Element}
import org.dom4j.io.{SAXReader, XMLWriter}
import pers.cyz.bigdatatool.node.common.config.SystemConfig
import pers.cyz.bigdatatool.node.common.utils.IpUtils

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
      list.forEach(x=>{
        if (x.element("name").getData == name){
          x.element("value").setText(value)
        }else{
          addProperty(root, name, value)
        }
      })
    }
  }

  def main(args: Array[String]): Unit = {
    //    Seq("tar","xvf","/home/cyz/BDMData/hadoop-3.3.0-src.tar.gz","-C","/home/cyz/BDMData/").!
    //    println(System.getProperty("java.home")) // ->/usr/lib/jvm/java-15-openjdk

    //    var bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
    //      s"/BDMData/hadoop-3.3.0/etc/hadoop/hadoop-env.sh",true))
    //    bufferWriter.write(s"JAVA_HOME=${System.getProperty("java.home")}")
    //    bufferWriter.close()


//    val reader: SAXReader = new SAXReader()
//    var doc: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
//      s"etc/hadoop/core-site.xml"))
//    val root = doc.getRootElement
//    editProperty(root, "fs.defaultFS","222")
//    import java.io.FileWriter
//    val writer = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
//      s"etc/hadoop/core-site.xml"))
//    writer.write(doc) // 向流写入数据
//    println(root)
//    writer.close()
    println(IpUtils.getLocalName)
    //    doc.write()
    //    println(root)


  }
}

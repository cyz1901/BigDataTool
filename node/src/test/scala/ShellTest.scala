import org.dom4j.{Document, Element}
import org.dom4j.io.{SAXReader, XMLWriter}
import pers.cyz.bigdatatool.node.common.config.SystemConfig

import java.io.{BufferedWriter, File, FileWriter, Writer}
import java.nio.file.Paths
import sys.process._

object ShellTest {


  def findProperty(root: Element, name: String): Unit = {
    val i = root.elementIterator("property")
    while ( {
      i.hasNext
    }) {
      val foo = i.next.asInstanceOf[Element]
      if (foo.element("name").getData == name) {
        val aa = foo.element("name").getParent.element("value")
        aa.setText("hello")
        println(aa.getData)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    //    Seq("tar","xvf","/home/cyz/BDMData/hadoop-3.3.0-src.tar.gz","-C","/home/cyz/BDMData/").!
    //    println(System.getProperty("java.home")) // ->/usr/lib/jvm/java-15-openjdk

    //    var bufferWriter: BufferedWriter = new BufferedWriter(new FileWriter(s"${SystemConfig.userHomePath}" +
    //      s"/BDMData/hadoop-3.3.0/etc/hadoop/hadoop-env.sh",true))
    //    bufferWriter.write(s"JAVA_HOME=${System.getProperty("java.home")}")
    //    bufferWriter.close()


    val reader: SAXReader = new SAXReader()
    var doc: Document = reader.read(new File(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
      s"etc/hadoop/core-site.xml"));
    val root = doc.getRootElement
    findProperty(root,"fs.defaultFS")
    import java.io.FileWriter
    val writer = new XMLWriter(new FileWriter(s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/" +
      s"etc/hadoop/core-site.xml"))
    writer.write(doc) // 向流写入数据
    writer.close()

    //    doc.write()
    //    println(root)


  }
}

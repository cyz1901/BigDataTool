package pers.cyz.BigDataTool.Master.XML

import org.dom4j.Document
import org.dom4j.io.SAXReader

import java.net.URL

class XmlUtil {

}

object XmlUtil {
  def read(): Unit ={
    val doc: Document = new SAXReader().read("XmlTest/hello.xml")
    val root = doc.getRootElement()

    val nodes = root.elements("property")
    println(nodes)
  }
}

object XmlTest {
  def main(args: Array[String]): Unit = {
    XmlUtil.read()
  }
}



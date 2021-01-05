package pers.cyz.BigDataTool.Master.Reptile
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.collection.mutable
class HadoopReptile {
}
object HadoopReptile {
  val html = "https://archive.apache.org/dist/hadoop/common/"
  def GetDownloadAddr() ={
    val doc = Jsoup.connect(html).get()
    val elements : Elements = doc.select("a[href~=^hadoop-[0-9.]*/]")
    val hs: mutable.HashMap[String,String] =  mutable.HashMap()
    elements.forEach(x => hs.put(x.text().stripSuffix("/"), html + x.html() + x.text().stripSuffix("/") + ".tar.gz"))
    hs
  }
}
object Mytest {

  def main(args: Array[String]): Unit = {
  }
}

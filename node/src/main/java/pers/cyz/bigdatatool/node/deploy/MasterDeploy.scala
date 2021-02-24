package pers.cyz.bigdatatool.node.deploy


import org.dom4j.io.{OutputFormat, SAXReader, XMLWriter}
import org.dom4j.{Document, DocumentHelper, Element}
import pers.cyz.bigdatatool.node.common.download.DownloadExecutor
import pers.cyz.bigdatatool.node.config.SystemConfig

import java.io.File
import java.net.URL
import java.io.FileOutputStream
import scala.collection.immutable.HashMap
import sys.process._

class MasterDeploy extends Deploy {

  override def deploy(data: NodeConfigurationData): Unit = {

    //下载文件
    /*    val DownloadConfig: String = "https://archive.apache.org/dist/hadoop/common/"
        val downArray: Array[URL] = getDownloadUrlArray(data.configMap, DownloadConfig)
        println(downArray.mkString("Array(", ", ", ")"))
        val downloader: DownloadExecutor = new DownloadExecutor()
        downloader.downloadExecute(downArray)*/
    //解压
    /*    if (("tar -zvxf" + SystemTypeConfig.userHomePath + "/BDMData/hadoop-3.3.0.tar.gz -C " + SystemTypeConfig.userHomePath + "/BDMData/").! == 0) {
          println("成功")
        }
        else {
          println("失败")
        }*/
    //改xml
/*    xmlEdit()*/
    //初始化集群

    //群起
  }

  def getDownloadUrlArray(configMap: HashMap[String, String], DownloadConfig: String): Array[URL] = {
    configMap.keySet.map(x => {
      val fileName: String = x + "-" + configMap(x)
      new URL(DownloadConfig + fileName + "/" + fileName + SystemConfig.compressedFormat)
    }).toArray[URL]
  }

  def DownloadFile(): Unit = {

  }

  def tar(): Unit = {

  }

  def xmlEdit(): Unit = {
    val path: String = "core-site.xml"

    val reader: SAXReader = new SAXReader();
    //2、调用read方法
    var doc: Document = reader.read(new File(path));
    //3、获取根元素
    var root: Element = doc.getRootElement; //books
    root.addElement("property").addAttribute("attr","hello")

    val format: OutputFormat = new OutputFormat()
    format.setExpandEmptyElements(true)
    format.setIndentSize(8) // 行缩进
    format.setNewlines(true) // 一个结点为一行

    // 输出xml文件// 输出xml文件
    val writer: XMLWriter  = new XMLWriter(new FileOutputStream(new File(path)),format)
    writer.write(doc)
    writer.close()


  }

}


object MasterDeployTest {
  def main(args: Array[String]): Unit = {
    val m = new MasterDeploy()
    val data = new NodeConfigurationData("test", HashMap("hadoop" -> "1.0.1"))
    m.deploy(data)


  }
}

/*
git log --author=cyz1901 --since=2020-01-01 --until=2021-02-19 --format='%aN' | sort -u | while read name; do echo -en "$name\t"; git log --author="$name" --pretty=tformat: --numstat | grep "\(.html\|.java\|.scala\|.properties\)$" | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }' -; done
cyz1901 added lines: 5788, removed lines: 5668, total lines: 120
 */
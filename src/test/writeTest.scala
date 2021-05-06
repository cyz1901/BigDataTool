import pers.cyz.bigdatatool.common.config.{AppConfig, SystemConfig}

import java.io.{BufferedWriter, File, FileWriter}
import java.util
import java.util.Map

object writeTest {

  def wrr2(): Unit ={
    val b :util.Map[String, String] = new util.HashMap[String, String]()
    b.put("hello","1")
    b.put("world","2")
    b.put("ll","3")
    val bufferWork: BufferedWriter = new BufferedWriter(new FileWriter(s"/home/cyz/BDMData/hadoop-3.3.0/etc/hadoop/workers", true))
    b.forEach((k,v)=> {
      bufferWork.write(k)
      bufferWork.flush()
      bufferWork.newLine()
      bufferWork.flush()
    })
    bufferWork.close()
  }

  def wrr(): Unit = {
    val a: Array[String] = Array[String]("hello", "world", "ll")
    val bufferWork: BufferedWriter = new BufferedWriter(new FileWriter(s"/home/cyz/BDMData/hadoop-3.3.0/etc/hadoop/workers", true))
    a.foreach(k => {
      bufferWork.write(k)
      bufferWork.flush()
      bufferWork.newLine()
      bufferWork.flush()
    })
    bufferWork.close()
  }

  def main(args: Array[String]): Unit = {
    val a = System.getProperties.getProperty("os.arch")
    println(a)
    println(System.getProperty("user.home"))

//    val path = new File(s"${System.getProperty("user.home")}/BDMData/")
//    path.mkdir()
//    println(path)
  }
}

import com.fasterxml.jackson.databind.ObjectMapper
import org.dom4j.Element
import pers.cyz.bigdatatool.common.config.AppConfig
import pers.cyz.bigdatatool.common.utils.loader.Loader
import pers.cyz.bigdatatool.common.utils.loader.LoaderType.Yaml

import java.io.File

object ShellTest {


  def main(args: Array[String]): Unit = {
    val local = new ThreadLocal[Boolean](){
      override def initialValue(): Boolean = {
        false
      }
    }

    for (j <- 0 to 4){
      println(s"now active is ${Thread.activeCount()}")
      val thread = new Thread(){
        override def run(): Unit = {
          for (i <- 1 to 10){
            println(s"${Thread.currentThread().getName} - id - $i")
          }
          println(s"${Thread.currentThread().getName} - ThreadLocal - ${local.get()}")
        }
      }
      thread.start()
    }




  }
}

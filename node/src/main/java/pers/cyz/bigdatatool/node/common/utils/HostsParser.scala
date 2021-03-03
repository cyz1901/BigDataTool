package pers.cyz.bigdatatool.node.common.utils

import scala.io.Source

object HostsParser {

  val filepath : String = "/etc/hosts"

  def parser(): Unit ={

    Source.fromFile(filepath).getLines().foreach(
      line=>{
        for(word <- line.split(" ")){
          //对word处理，word即是每个单词
          println(word)
        }
      }
    )
  }

  def main(args: Array[String]): Unit = {
    parser()
  }
}

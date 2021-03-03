package pers.cyz.bigdatatool.node.config

import pers.cyz.bigdatatool.node.config.AppConfig.repositoryConfig

import scala.collection.immutable.HashMap

object AppConfig {

  var master: nodeConfig.type = _
  var repository: repositoryConfig.type = _

  def setMaster(master: nodeConfig.type): Unit = {
    this.master = master
  }

  def setRepository(repository: repositoryConfig.type): Unit = {
    this.repository = repository
  }


  object nodeConfig {
    var host: String = _

    def setHost(host: String): Unit = {
      this.host = host
    }
  }

  object repositoryConfig {
    var url: String = _

    def setUrl(url: String): Unit = {
      this.url = url
    }

    object componentUrl {

      val hadoopBaseUrl: String = "/apache/hadoop/common/"
      val hadoopMap: HashMap[String,String] = HashMap("hadoop-3.3.0" -> "https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-3.3.0/hadoop-3.3.0.tar.gz")

    }

  }

}


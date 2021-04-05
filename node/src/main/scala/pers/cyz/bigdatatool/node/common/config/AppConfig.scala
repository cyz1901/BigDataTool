package pers.cyz.bigdatatool.node.common.config

import scala.collection.immutable.HashMap

object AppConfig {

  var serve: nodeConfig.type = _
  var repository: repositoryConfig.type = _

  def setServe(serve: nodeConfig.type): Unit = {
    this.serve = serve
  }

  def setRepository(repository: repositoryConfig.type): Unit = {
    this.repository = repository
  }


  object nodeConfig {
    var masterName: String = _
    var nodeCount: Int = _
    var metaAddress: String = _

    def setMasterName(masterName: String): Unit = {
      this.masterName = masterName
    }

    def setNodeCount(nodeCount: Int): Unit = {
      this.nodeCount = nodeCount
    }

    def setMetaAddress(metaAddress: String): Unit = {
      this.metaAddress = metaAddress
    }
  }

  object repositoryConfig {
    var url: String = _
    var downloadFile: String = _

    def setUrl(url: String): Unit = {
      this.url = url
    }

    def setDownloadFile(downloadFile: String): Unit = {
      this.downloadFile = downloadFile
    }

    object componentUrl {

      val hadoopBaseUrl: String = "/apache/hadoop/common/"
      val hadoopMap: HashMap[String, String] = HashMap("hadoop-3.3.0" -> "https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-3.3.0/hadoop-3.3.0.tar.gz")

    }

  }

}


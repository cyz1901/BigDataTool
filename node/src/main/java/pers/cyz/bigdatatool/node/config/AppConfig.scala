package pers.cyz.bigdatatool.node.config

class AppConfig() {

  var master: nodeConfig = _

  def setMaster(master: nodeConfig): Unit = {
    this.master = master
  }
}

class nodeConfig() {
  var host: String = _

  def setHost(Host: String): Unit = {
    this.host = Host
  }
}

object AppConfig {
  {

  }
}

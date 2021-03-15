package pers.cyz.bigdatatool.node.core.flower

import java.net.InetAddress

object FlowerStatusObj {

  var ip: String = _
  var status: String = _

  {
    ip = InetAddress.getLocalHost.getHostAddress
    status = "ok"
  }
}

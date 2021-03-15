package pers.cyz.bigdatatool.node.common.utils

import java.net.{InetAddress, NetworkInterface, UnknownHostException}
import java.util
import java.util.ArrayList
import scala.util.control.Breaks.{break, breakable}


/*public static String getLocalIpAddr() {
ArrayList<String> ipList = new ArrayList<String>();
InetAddress[] addrList;
try {
Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
while (interfaces.hasMoreElements()) {
NetworkInterface ni = (NetworkInterface) interfaces.nextElement();
Enumeration ipAddrEnum = ni.getInetAddresses();
while (ipAddrEnum.hasMoreElements()) {
InetAddress addr = (InetAddress) ipAddrEnum.nextElement();
if (addr.isLoopbackAddress() == true) {
continue;
}

String ip = addr.getHostAddress();
if (ip.indexOf(":") != -1) {
//skip the IPv6 addr
continue;
}

logger.debug("Interface: " + ni.getName()
+ ", IP: " + ip);
ipList.add(ip);
}
}

Collections.sort(ipList);
} catch (Exception e) {
e.printStackTrace();
logger.error("Failed to get local ip list. " + e.getMessage());
throw new RuntimeException("Failed to get local ip list");
}

return ipList.iterator().next();
}

public static String getLocalName() throws UnknownHostException {
return InetAddress.getLocalHost().getHostName();
}*/
object IpUtils {
  def getLocalIpAddr: String = {
    val ipList = new util.ArrayList[String]
    val interfaces: util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces
    while ( {
      interfaces.hasMoreElements
    }) {
      val ni: NetworkInterface = interfaces.nextElement
      val ipAddrEnum = ni.getInetAddresses
      while ( {
        ipAddrEnum.hasMoreElements
      }) {
        val addr: InetAddress = ipAddrEnum.nextElement
        breakable {
          if (addr.isLoopbackAddress) break
        }
        val ip : String = addr.getHostAddress
        breakable {
          if (ip.indexOf(":") != -1) break
          ipList.add(ip)
        }
      }
    }
    ipList.iterator.next
  }

  @throws[UnknownHostException]
  def getLocalName: String = InetAddress.getLocalHost.getHostName
}

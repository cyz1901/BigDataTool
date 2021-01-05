package pers.cyz.BigDataTool.Master.Service

import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.server.{TServer, TSimpleServer}
import org.apache.thrift.transport.TServerSocket
import pers.cyz.BigDataTool.Master.Impl.{HadoopDownloadImpl, HelloWorldServiceImpl}
import pers.cyz.BigDataTool.Master.ThirftService.HadoopDownloadService

import java.net.ServerSocket

class HadoopServer {

}
object HadoopServer {
  def main(args: Array[String]): Unit = {
    val serverSocket = new ServerSocket(9090)
    val serverTransport = new TServerSocket(serverSocket)
    val processor = new HadoopDownloadService.Processor[HadoopDownloadService.Iface](new HadoopDownloadImpl())

    val protocolFactory = new TBinaryProtocol.Factory
    val tArgs = new TServer.Args(serverTransport)
    tArgs.processor(processor)
    tArgs.protocolFactory(protocolFactory)

    // 简单的单线程服务模型 一般用于测试
    val tServer = new TSimpleServer(tArgs)
    System.out.println("Running Simple Server")
    tServer.serve()
  }
}

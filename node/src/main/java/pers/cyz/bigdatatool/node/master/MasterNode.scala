package pers.cyz.bigdatatool.node.master

import io.grpc.stub.StreamObserver
import io.grpc.{ManagedChannel, ManagedChannelBuilder, Server, ServerBuilder}
import pers.cyz.bigdatatool.node.distributed.Node
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, FlowerStatus, RegisterRequest, RegisterResponse}

import java.io.File
import java.lang.Thread.sleep
import java.util.logging.Logger

class MasterNode extends Node {
  private val logger = Logger.getLogger(classOf[MasterNode].getName)
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50055).usePlaintext().build()
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.newStub(channel)


  def invokeRegister(): Unit = {
    //    val finishLatch = new CountDownLatch(1)
    @volatile var lock = true
    var bb: StreamObserver[RegisterResponse] = new StreamObserver[RegisterResponse] {
      override def onNext(v: RegisterResponse): Unit = {
        logger.info("Now the Flower: " + v.getStatus.getIp + " status is: " + v.getStatus.getStatus)
        if (v.getStatus.getStatus == "error") {
          lock = false
        }
      }

      override def onError(throwable: Throwable): Unit = {
        logger.info("Error")
      }

      override def onCompleted(): Unit = {
        logger.info("Completed")
      }
    }

    asyncStub.register(RegisterRequest.newBuilder().setC(10).build(), bb)
    println("")

    while (lock) {

    }


  }

  override def run(): Unit = {

    def initMetaData(): Unit = {
      val file = new File("metaData/nodeMap.txt")
      if (file.exists()) {

      } else {
        file.mkdirs()
      }

    }

    invokeRegister()
  }
}

object MasterNodeTest {
  def main(args: Array[String]): Unit = {
    val a = new MasterNode()
    a.run()
  }
}

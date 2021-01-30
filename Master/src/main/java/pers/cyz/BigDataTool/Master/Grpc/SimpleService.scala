package pers.cyz.BigDataTool.Master.Grpc

import io.grpc.stub.StreamObserver

class SimpleService extends AddServiceGrpc.AddServiceImplBase {
  override def add(request: AddRequest, responseObserver: StreamObserver[AddReply]): Unit = {
    super.add(request, responseObserver)
  }

  def myadd(a: Int, b: Int): Int = {
    a + b
  }

  def main(args: Array[String]): Unit = {

  }
}

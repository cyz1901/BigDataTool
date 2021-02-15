//package pers.cyz.BigDataTool.Master.Grpc
//
//import io.grpc.ServerBuilder
//import io.grpc.stub.StreamObserver
//
//class SimpleService extends AddServiceGrpc.AddServiceImplBase {
//  override def add(request: AddRequest, responseObserver: StreamObserver[AddReply]): Unit = {
//    var res: Int = myadd(request.getA(), request.getB())
//    responseObserver.onNext(AddReply.newBuilder().setC(res).build())
//    responseObserver.onCompleted()
//  }
//
//  def myadd(a: Int, b: Int): Int = {
//    a + b
//  }
//
//}
//
//object SimpleService {
//  def main(args: Array[String]): Unit = {
//    ServerBuilder.forPort(9999)
//      .addService(new SimpleService())
//      .build()
//      .start()
//    println("正在监听999")
//    while (true) {
//
//    }
//  }
//}

package pers.cyz.bigdatatool.node.flower

import io.grpc.{ManagedChannel, ManagedChannelBuilder}
import pers.cyz.bigdatatool.node.grpc.com.{ConnectGrpc, ConnectService}

class FlowerClient {
//  OrderManagementOuterClass.Order order = OrderManagementOuterClass.Order
//    .newBuilder()
//    .setId("101")
//    .addItems("iPhone XS").addItems("Mac Book Pro")
//    .setDestination("San Jose, CA")
//    .setPrice(2300)
//    .build();
  val channel: ManagedChannel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build()
  val asyncStub: ConnectGrpc.ConnectStub = ConnectGrpc.newStub(channel)
  val res: ConnectService = ConnectService
}

object FlowerClient{
  def main(args: Array[String]): Unit = {

  }
}

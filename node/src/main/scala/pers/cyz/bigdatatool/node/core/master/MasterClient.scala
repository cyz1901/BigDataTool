package pers.cyz.bigdatatool.node.core.master

import io.grpc.ManagedChannel
import pers.cyz.bigdatatool.node.grpc.com.ConnectGrpc

import java.util.logging.Logger


class MasterClient(
                    channel: ManagedChannel,
                    stub: ConnectGrpc.ConnectBlockingStub,
                    asyncStub: ConnectGrpc.ConnectStub
                  ) {
  private val logger = Logger.getLogger(classOf[MasterService].getName)


}
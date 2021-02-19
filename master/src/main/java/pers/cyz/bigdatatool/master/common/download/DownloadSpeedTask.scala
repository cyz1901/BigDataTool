package pers.cyz.bigdatatool.master.common.download

import java.io.{File, RandomAccessFile}
import java.lang.Thread.sleep
import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.Callable

class DownloadSpeedTask(val totalSize: Long) extends Runnable {

  def getSpeed(nowSize: Long): Unit = {
    println("downloadSize is " + DownloadExecutor.downloadSize + " now speed is " + (DownloadExecutor.downloadSize - nowSize))
  }

  override def run(): Unit = {
    println("ThreadName:" + Thread.currentThread().getName() + "线程执行: 速度为" + DownloadExecutor.downloadSpeed)
    while (true) {
      val nowSize: Long = DownloadExecutor.downloadSize
      sleep(1000)
      getSpeed(nowSize)
    }
  }
}

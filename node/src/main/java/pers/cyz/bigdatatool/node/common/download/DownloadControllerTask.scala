package pers.cyz.bigdatatool.node.common.download

import pers.cyz.bigdatatool.node.config.SystemConfig

import java.io.File
import java.lang.Thread.sleep

class DownloadControllerTask(val totalSize: Long) extends Runnable {

  def getSpeed(nowSize: Long) = {
    println("下载进度为" + DownloadExecutor.downloadSize.get()   + "速度为 " + (DownloadExecutor.downloadSize.get - nowSize) / 1024 / 1024)
  }

  override def run(): Unit = {
    println("ThreadName:" + Thread.currentThread().getName + "线程执行: 速度为" + DownloadExecutor.downloadSpeed)
    while (DownloadExecutor.downloadSize.get() < totalSize) {
      val nowSize: Long = DownloadExecutor.downloadSize.get
      sleep(1000)
      getSpeed(nowSize)
    }
  }
}

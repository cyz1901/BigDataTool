package pers.cyz.bigdatatool.core.download

import java.lang.Thread.sleep
import java.net.URL
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{Executors, ThreadPoolExecutor}
import scala.util.{Failure, Success, Try}

class DownloadExecutor {
  var totalSize: Long = 0
  var threadNum: Int = 3

  def getDownloadTaskSize(array: Array[URL]): Unit = {
    for (url <- array) {
      Try(url.openConnection().getHeaderField("Content-Length").toLong) match {
        case Success(value) =>
          totalSize += value
        case Failure(exception) =>
          throw exception
      }
    }
  }

  def getBlockSize(url: URL, threadNum: Int): Long = {
    Try(url.openConnection().getHeaderField("Content-Length").toLong) match {
      case Success(value) =>
        println("value is " + value)
        value / threadNum
      case Failure(exception) =>
        throw exception
    }
  }

  // get filename
  def getFileName(url: URL): String = {
    Try(url.openConnection().getHeaderField("Content-Disposition").toString) match {
      case Success(value) => value
      case Failure(exception) =>
        url.toString.substring(url.toString.lastIndexOf("/") + 1)
    }
  }

  def downloadTaskSpeedController(): Unit = {
    while (true) {
      val nowSize: Long = DownloadExecutor.downloadSize.get
      sleep(3000)
      println("下载进度为" + DownloadExecutor.downloadSize.get() + "速度为 " + (DownloadExecutor.downloadSize.get - nowSize) / 1024 / 1024)
    }
  }

  def downloadExecute(array: Array[URL]): Unit = {
    DownloadExecutor.downloadSize = new AtomicLong(0)
    DownloadExecutor.downloadSpeed = 0
    getDownloadTaskSize(array)
    val ex = Executors.newFixedThreadPool(threadNum).asInstanceOf[ThreadPoolExecutor]
//    ex.execute(new DownloadControllerTask(totalSize))
    for (url <- array) {
      val blockSize = getBlockSize(url, threadNum )
      for (i <- 1 to threadNum) {
          ex.submit(new DownloadFileTask(url, blockSize * (i - 1), blockSize * i, getFileName(url)))
      }
    }
    ex.shutdown()
  }

}

object DownloadExecutor {
  var downloadSize: AtomicLong = _
  var downloadSpeed: Long = _
}


object ExecutorTest {
  def main(args: Array[String]): Unit = {
    //new URL("https://archive.apache.org/dist/accumulo/1.10.0/accumulo-1.10.0-src.tar.gz"),
    val array = Array(new URL("https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-3.3.0/hadoop-3.3.0.tar.gz"))
    //      new URL("https://archive.apache.org/dist/accumulo/1.10.0/accumulo-1.10.0-bin.tar.gz.sha512"))
    val a = new DownloadExecutor()
    println("this is " + a.downloadExecute(array))
    println("all size is " + a.totalSize)
  }
}
package pers.cyz.bigdatatool.master.common.download

import java.net.URL
import java.util.concurrent.{Executors, ThreadPoolExecutor}
import scala.util.{Failure, Success, Try}

class DownloadExecutor {
  var totalSize: Long = 0

  def getDownloadTaskSize(array: Array[URL]) = {
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

  //  // get filename
  def getFileName(url: URL): String = {
    Try(url.openConnection().getHeaderField("Content-Disposition").toString) match {
      case Success(value) => value
      case Failure(exception) =>
        url.toString.substring(url.toString.lastIndexOf("/") + 1)
    }
  }

  def downloadExecute(array: Array[URL]): Unit = {
    getDownloadTaskSize(array)
    val ex = Executors.newFixedThreadPool(4).asInstanceOf[ThreadPoolExecutor]
    ex.execute(new DownloadSpeedTask(totalSize))
    for (a <- array) {
      val blockSize = getBlockSize(a, 3)
      for (i <- 1 to 3) {
        val task = new DownloadFileTask(a, blockSize * (i - 1), blockSize * i, getFileName(a))
        ex.submit(task)
      }
    }
    ex.shutdown()
  }

}

object DownloadExecutor {
  @volatile var downloadSpeed: Long = 0
  @volatile var downloadSize: Long = 0
}


object ExecutorTest {
  def main(args: Array[String]): Unit = {
    //new URL("https://archive.apache.org/dist/accumulo/1.10.0/accumulo-1.10.0-src.tar.gz"),
    val array = Array(
      new URL("https://archive.apache.org/dist/accumulo/1.10.0/accumulo-1.10.0-bin.tar.gz.sha512"))
    val a = new DownloadExecutor()
    println("this is " + a.downloadExecute(array))
    println("all size is " + a.totalSize)
  }
}
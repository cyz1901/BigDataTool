package pers.cyz.BigDataTool.Master.Download

import java.io.{File, FileOutputStream, RandomAccessFile}
import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.Callable
import scala.util.{Failure, Success, Try}

class DownloadFileTask(var url: URL, val startIndex: Long, val endIndex: Long, val fileName: String) extends Callable[Int] {

  def downloadFile() = {
    var totalSize = 0
    val file: File = new File("DownloadData/" + fileName)
    val threadAccessFile = new RandomAccessFile(file, "rwd")
    var conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")
    // 设置请求是否超时时间
    conn.setConnectTimeout(5000)
    conn.setRequestProperty("User-Agent", " Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)")
    conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex)
    println("size is " + conn.getContentLength())
    val readableByteChannel = Channels.newChannel(conn.getInputStream)
    val fileChannel = threadAccessFile.getChannel()
    val buf = ByteBuffer.allocate(50)

    threadAccessFile.seek(startIndex)
    while (readableByteChannel.read(buf) != -1) {
      DownloadExecutor.downloadSize += buf.position()
      buf.flip()
      fileChannel.write(buf)
      buf.clear()
    }
    println(totalSize)
    threadAccessFile.close()
    readableByteChannel.close()
    totalSize
  }

  def cover(buf: ByteBuffer): Array[Byte] = {
    new Array[Byte](buf.limit() - buf.position())
  }

  override def call(): Int = {
    println("ThreadName:" + Thread.currentThread().getName() + "线程执行: " + fileName)
    val size = downloadFile
    size
  }
}

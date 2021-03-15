package pers.cyz.bigdatatool.node.core.download

import pers.cyz.bigdatatool.node.common.config.SystemConfig

import java.io.{File, RandomAccessFile}
import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.Callable


class DownloadFileTask(var url: URL, val startIndex: Long, val endIndex: Long, val fileName: String) extends Callable[Int] {

  def updateDownloadSize(size: Long): Unit = {
    DownloadExecutor.downloadSize.addAndGet(size)
  }

  def downloadFile(): Int = {
    val totalSize = 0
    val file: File = new File(SystemConfig.userHomePath + "/BDMData/" + fileName)
    val threadAccessFile = new RandomAccessFile(file, "rwd")
    val conn = url.openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")
    // 设置请求是否超时时间
    conn.setConnectTimeout(5000)
    conn.setRequestProperty("User-Agent", " Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)")
    conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex)
    val readableByteChannel = Channels.newChannel(conn.getInputStream)
    val fileChannel = threadAccessFile.getChannel
    val buf = ByteBuffer.allocate(1024)

    threadAccessFile.seek(startIndex)
    while (readableByteChannel.read(buf) != -1) {
      updateDownloadSize(buf.position())
      buf.flip()
//      updateDownloadSize(buf.limit())
      fileChannel.write(buf)
      buf.clear()
    }
    threadAccessFile.close()
    readableByteChannel.close()
    totalSize
  }

  //  def cover(buf: ByteBuffer): Array[Byte] = {
  //    new Array[Byte](buf.limit() - buf.position())
  //  }

  override def call(): Int = {
    println("ThreadName:" + Thread.currentThread().getName + "线程执行: " + fileName)
    val size = downloadFile()
    size
  }
}

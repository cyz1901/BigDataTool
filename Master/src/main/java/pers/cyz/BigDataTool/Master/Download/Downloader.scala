package pers.cyz.BigDataTool.Master.Download

import java.io.{File, FileNotFoundException, FileOutputStream, IOException, InputStream}
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.ByteBuffer
import scala.util.{Failure, Success, Try}

class Downloader(var url: URL) {

  val conn = url.openConnection()

  // get filename
  def getFileName(): String = {
    Try(conn.getHeaderField("Content-Disposition").toString) match {
      case Success(value) => value
      case Failure(exception) => {
        url.toString.substring(url.toString.lastIndexOf("/") + 1)
      }
    }
  }

  def getFileSize() = {
    Try(conn.getHeaderField("Content-Length").toLong) match {
      case Success(value) => value
      case Failure(exception) => {
        exception
      }
    }
  }

  def downloadFile() = {
    var totalSize = 0

    val file: File = new File(getFileName())
    val readableByteChannel = Channels.newChannel(url.openStream)
    val fileOutputStream = new FileOutputStream(file)
    val fileChannel = fileOutputStream.getChannel()
    val buf = ByteBuffer.allocate(1024)
    while (readableByteChannel.read(buf) != -1) {
      totalSize += buf.position()
      buf.flip()
      fileChannel.write(buf)
      buf.clear()
    }
    println(totalSize)
    fileOutputStream.close()
    readableByteChannel.close()
    totalSize
  }
}

object DownloaderTest {
  def main(args: Array[String]): Unit = {
    val t = new Downloader(new URL("https://archive.apache.org/dist/accumulo/1.10.0/accumulo-1.10.0-bin.tar.gz.sha512"))
    t.downloadFile()
  }
}

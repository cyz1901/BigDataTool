import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import java.net.URI

class HadoopTest {

}

object HadoopTest {
  def main(args: Array[String]): Unit = {
    val conf: Configuration = new Configuration()
    val hdfsPath: URI = new URI("hdfs://localhost:9000")
    val hdfs: FileSystem = FileSystem.get(hdfsPath,conf)
    val newDir = "/hdfstest"
    val result = hdfs.mkdirs(new Path(newDir))
    if (result) System.out.println("Success!")
    else System.out.println("Failed!")
  }
}

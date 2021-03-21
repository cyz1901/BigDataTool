import java.io.File
import java.nio.file.Paths
import sys.process._

object ShellTest {
  def main(args: Array[String]): Unit = {
    Seq("tar","xvf","/home/cyz/BDMData/hadoop-3.3.0-src.tar.gz","-C","/home/cyz/BDMData/").!
  }
}

import java.net.InetAddress
import javax.servlet.http.HttpServletRequest
import scala.collection.immutable.HashSet
import scala.util.control.Breaks.break
import sys.process._
class Test {
  var oo : Option[String] = _

  def checkIsNameNode(): Unit ={
    val list: Array[String] = "jps".!!.split("\n")
    val patten = "NameNode".r
    for (x <- list){
      patten.findFirstMatchIn(x) match {
        case Some(x) => println("yes " + x)
        case None => println("no")
      }
    }
  }

}
object Test {
  def main(args: Array[String]): Unit = {

    var ll : Test = new  Test()
    println(ll.oo)
  }
}

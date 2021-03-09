package pers.cyz.bigdatatool.node.common.utils.loader

import com.fasterxml.jackson.databind.ObjectMapper
import org.dom4j.{Document, Element}
import org.dom4j.io.SAXReader
import org.yaml.snakeyaml.constructor.Constructor
import pers.cyz.bigdatatool.node.common.utils.loader
import pers.cyz.bigdatatool.node.common.utils.loader.LoaderType.{Json, LoaderType, Xml, Yaml}
import pers.cyz.bigdatatool.node.config.AppConfig
import pers.cyz.bigdatatool.node.pojo.HostIpMeta

import java.io.{File, FileInputStream}
import scala.reflect.{ClassTag, classTag}
import scala.reflect._ //引入反射

object LoaderType extends Enumeration {
  type LoaderType = Value
  val Xml: loader.LoaderType.Value = Value("Xml")
  val Yaml: loader.LoaderType.Value = Value("Yaml")
  val Json: loader.LoaderType.Value = Value("Json")

  //  val System: loader.LoaderType.Value = Value("System")


}

class Loader[T: ClassTag] {

  object Builder {

    private[this] var loaderType: Option[LoaderType] = _
    private[this] var configFilePath: Option[String] = _
    private[this] var loader: Load[T] = _


    def setLoaderType(loaderType: LoaderType): Builder.type = {
      this.loaderType = Option(loaderType)
      this
    }

    def setConfigFilePath(configFilePath: String): Builder.type = {
      this.configFilePath = Option(configFilePath)
      this
    }

    implicit def optToString(option: Option[String]): String = {
      option match {
        case Some(x) => x
      }
    }

    def build(): Load[T] = {
      (loaderType, configFilePath) match {
        case (Some(x), Some(y)) =>
          x match {
            //            case Xml =>
            //              new XmlLoad(configFilePath)
            case Yaml =>
              new YamlLoad(configFilePath)
            case Json =>
              new JsonLoader(configFilePath)

          }
        case _ =>
          throw new Exception
      }
    }
  }

  class YamlLoad(
                  override var configFilePath: String = null
                ) extends Load[T] {

    import org.yaml.snakeyaml.Yaml

    var yaml: Yaml = _

    override def init(): Unit = {
      yaml = new Yaml(new Constructor(classTag[T].runtimeClass))
    }

    override def fileToObjMapping(): T = {
      init()
      val result: T = yaml.load(new FileInputStream(new File(configFilePath)))
      result
    }

    override def objMappingToFile(t: T): Unit = {

    }
  }

  class JsonLoader(
                    override var configFilePath: String = null
                  ) extends Load[T] {

    var mapper: ObjectMapper = _

    override def init(): Unit = {
      mapper = new ObjectMapper()
    }

    override def fileToObjMapping(): T = {
      init()
      val result : T = mapper.readValue(new File(configFilePath), classTag[T].runtimeClass).asInstanceOf[T]
      result
    }

    override def objMappingToFile(t: T): Unit = {

    }
  }

  //  class XmlLoad(
  //                 override var configFilePath: String = null
  //               ) extends Load[T] {
  //
  //    val reader: SAXReader = new SAXReader();
  //
  //    override def load(): T = {
  //      //2、调用read方法
  //      var doc: Document = reader.read(new File(configFilePath));
  //      //3、获取根元素
  //      var root: Element = doc.getRootElement; //books
  //    }
  //  }

}

object LoaderFactoryTest {
  def main(args: Array[String]): Unit = {
    //    val loader = new Loader[AppConfig.type]().Builder
    //      .setLoaderType(Yaml)
    //      .setConfigFilePath("node/src/main/resource/etc/node.yml").build()
    //    val res: AppConfig.type = loader.getObjMapping
    //    println(res.repository.url)

    val loader = new Loader[HostIpMeta]().Builder
      .setLoaderType(Json)
      .setConfigFilePath("node/src/main/resource/etc/hostIpMeta.json").build()
    val res: HostIpMeta = loader.fileToObjMapping()
    println(res.getHostName)



  }
}


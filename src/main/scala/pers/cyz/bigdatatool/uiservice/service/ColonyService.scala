package pers.cyz.bigdatatool.uiservice.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.common.pojo.{ComponentMap, RuntimeMeta}
import pers.cyz.bigdatatool.common.utils.FileUtils
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo
import pers.cyz.bigdatatool.uiservice.bean.vo.ColonyVo.{ComponentMsgData, NodesMsgData}

import java.io.File
import java.util
import scala.jdk.CollectionConverters._
import scala.sys.process.stringSeqToProcess

@Service
class ColonyService {

  def getNodes: ColonyVo = {

    //初始化数据
    val nodesMsgList: util.ArrayList[ColonyVo.NodesMsgData] = new util.ArrayList[ColonyVo.NodesMsgData]()
    val componentMsgList: util.ArrayList[ColonyVo.ComponentMsgData] = new util.ArrayList[ColonyVo.ComponentMsgData]()

    RuntimeMeta.hostIpMap.map(x => {
      nodesMsgList.add(new NodesMsgData(x._1, x._2, false))
    })

    ComponentMap.componentVersionMap.foreach(t => {
      componentMsgList.add(new ComponentMsgData(t._1, "Hadoop是一个由Apache基金会所开发的分布式系统基础架构", false, t._2.asJava))
    })
    new ColonyVo(nodesMsgList, componentMsgList)
  }

  def uploadFile(fileUpload: Array[MultipartFile]): String = {
    try {
      val filePath = new File(s"${SystemConfig.userHomePath}/BDMData/cache/", fileUpload(0).getOriginalFilename)
      FileUtils.createFile(filePath)
      fileUpload(0).transferTo(filePath)
      "success"
    } catch {
      case exception: Exception => exception.toString
    }
    //    // 对文件进行解压
    //    Seq("tar", "xvf", s"${SystemConfig.userHomePath}/BDMData/cache/${fileUpload(0).getOriginalFilename}", "-C", "/home/cyz/BDMData/").!!
  }

}

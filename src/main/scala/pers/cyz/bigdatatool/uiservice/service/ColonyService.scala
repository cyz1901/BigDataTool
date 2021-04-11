package pers.cyz.bigdatatool.uiservice.service

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pers.cyz.bigdatatool.common.config.SystemConfig
import pers.cyz.bigdatatool.common.utils.FileUtils

import java.io.File

@Service
class ColonyService {
  def uploadFile(fileUpload: Array[MultipartFile]) = {
    val filePath = new File(s"${SystemConfig.userHomePath}/BDMData/cache/","ll")
    FileUtils.createFile(filePath)
    fileUpload(0).transferTo(filePath)
  }

}

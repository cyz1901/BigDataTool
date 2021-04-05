package pers.cyz.bigdatatool.node.uiservice.service

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.util.EntityUtils
import org.springframework.stereotype.Service
import pers.cyz.bigdatatool.node.common.config.SystemConfig
import pers.cyz.bigdatatool.node.uiservice.bean.pojo.FileStatusPojo
import sys.process._
import java.util

@Service
class ColonyOperationService {

  def startColony() = {
    // 单机
    s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/sbin/hadoop-daemon.sh start namenode".!!
    s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/sbin/hadoop-daemon.sh start datanode".!!
  }

  def stopColony() = {
    // 单机
    s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/sbin/hadoop-daemon.sh stop datanode".!!
    s"${SystemConfig.userHomePath}/BDMData/hadoop-3.3.0/sbin/hadoop-daemon.sh stop namenode".!!

  }


  //webHDFS
  def getFileList() = {
    // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
    val httpClient: CloseableHttpClient = HttpClientBuilder.create().build();
    // 创建Get请求
    val httpGet: HttpGet = new HttpGet("http://localhost:9870/webhdfs/v1/hello?op=LISTSTATUS")

    // 响应模型
    var response: CloseableHttpResponse = null;
    try {
      // 由客户端执行(发送)Get请求
      response = httpClient.execute(httpGet);
      // 从响应模型中获取响应实体
      val responseEntity: HttpEntity = response.getEntity;
      System.out.println("响应状态为:" + response.getStatusLine);
      if (responseEntity != null) {
        val ss = EntityUtils.toString(responseEntity)
        System.out.println("响应内容为:" + ss)
        val mapper: ObjectMapper = new ObjectMapper()
        val voJson = mapper.readTree(ss)
        val a = voJson.get("FileStatuses").get("FileStatus").toString
        println(a)
        val ll: util.ArrayList[FileStatusPojo] = mapper.readValue(
          a,
          new TypeReference[util.ArrayList[FileStatusPojo]]() {})
        ll
      }
    } catch {
      case exception: Exception => throw exception
    }
    finally {
      try {
        // 释放资源
        if (httpClient != null) {
          httpClient.close();
        }
        if (response != null) {
          response.close();
        }
      } catch {
        case exception: Exception => throw exception
      }
    }
  }
}

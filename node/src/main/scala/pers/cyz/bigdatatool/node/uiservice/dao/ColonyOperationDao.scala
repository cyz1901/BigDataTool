//package pers.cyz.bigdatatool.node.uiservice.dao
//
//
//import com.fasterxml.jackson.core.`type`.TypeReference
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.apache.http.HttpEntity
//import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
//import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
//import org.apache.http.util.EntityUtils
//import org.springframework.stereotype.Repository
//import pers.cyz.bigdatatool.node.uiservice.bean.pojo.FileStatusPojo
//
//import java.io.IOException
//import java.util
//
//@Repository
//class ColonyOperationDao {
//  //webHDFS
//  def getFileList(): Unit = {
//    // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
//    val httpClient: CloseableHttpClient = HttpClientBuilder.create().build();
//    // 创建Get请求
//    val httpGet: HttpGet = new HttpGet("http://localhost:9870/webhdfs/v1/?op=LISTSTATUS");
//
//    // 响应模型
//    var response: CloseableHttpResponse = null;
//    try {
//      // 由客户端执行(发送)Get请求
//      response = httpClient.execute(httpGet);
//      // 从响应模型中获取响应实体
//      val responseEntity: HttpEntity = response.getEntity;
//      System.out.println("响应状态为:" + response.getStatusLine);
//      if (responseEntity != null) {
//        val ss = EntityUtils.toString(responseEntity)
//        System.out.println("响应内容为:" + ss)
//        val mapper: ObjectMapper = new ObjectMapper()
//        val voJson = mapper.readTree(ss)
//        val a = voJson.get("FileStatuses").get("FileStatus").toString
//        val ll: util.ArrayList[FileStatusPojo] = mapper.readValue(
//          a,
//          new TypeReference[util.ArrayList[FileStatusPojo]]() {})
//        println(ll.get(0).getOwner)
//      }
//    } catch {
//      case exception: Exception => throw exception
//    }
//    finally {
//      try {
//        // 释放资源
//        if (httpClient != null) {
//          httpClient.close();
//        }
//        if (response != null) {
//          response.close();
//        }
//      } catch {
//        case exception: Exception => throw exception
//      }
//    }
//  }
//}

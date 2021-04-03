package pers.cyz.bigdatatool.node.uiservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pers.cyz.bigdatatool.node.uiservice.dao.ClustersDao
import pers.cyz.bigdatatool.node.uiservice.pojo.Clusters

import scala.collection.mutable.ArrayBuffer

@Service
class ClustersService {

  @Autowired
  private var clustersDao: ClustersDao = _

  def getClusters: ArrayBuffer[Clusters] ={
    val list: ArrayBuffer[Clusters] = new ArrayBuffer[Clusters]
    list.addOne(clustersDao.selectAllClusters())
    list
  }

}

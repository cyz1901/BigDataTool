package pers.cyz.bigdatatool.uiservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pers.cyz.bigdatatool.uiservice.bean.Clusters
import pers.cyz.bigdatatool.uiservice.dao.ClustersDao

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

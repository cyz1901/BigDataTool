package pers.cyz.bigdatatool.node.uiservice.dao


import org.springframework.stereotype.Repository
import pers.cyz.bigdatatool.node.common.config.SystemConfig
import pers.cyz.bigdatatool.node.common.utils.FileUtils
import pers.cyz.bigdatatool.node.uiservice.pojo.Clusters

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}


@Repository
class ClustersDao {

  private var clusters: File = _

  {
    clusters = new File(s"${SystemConfig.userHomePath}/BDMData/Meta/cluster")
    FileUtils.createFile(clusters)
  }

  def addClusters(clusters: Clusters): Unit = {
    val oo = new ObjectOutputStream(new FileOutputStream(this.clusters))
    oo.writeObject(clusters)
    oo.close()
  }

  def deleteClusters(clusters: Clusters): Unit = {

  }

  def updateClusters(clusters: Clusters): Unit = {

  }

  def selectAllClusters(): Clusters = {
    val oi = new ObjectInputStream(new FileInputStream(this.clusters))
    val clusters: Clusters = oi.readObject().asInstanceOf[Clusters]
    clusters
  }

}

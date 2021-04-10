package pers.cyz.bigdatatool.common.pojo

import java.util.concurrent.locks.ReentrantLock

class LayerDownloadService{
  private[this] var totalSizeThread: Long = 0
  private[this] var alreadyDownloadSizeThread: Long = 0
  val lock : ReentrantLock= new ReentrantLock()

  def set(totalSizeThread: Long, alreadyDownloadSizeThread: Long): Unit ={
    try {
      lock.lock()
      this.totalSizeThread = totalSizeThread
      this.alreadyDownloadSizeThread = alreadyDownloadSizeThread

    }finally {
      lock.unlock()
    }
  }

   def update(): Unit = {

  }
}

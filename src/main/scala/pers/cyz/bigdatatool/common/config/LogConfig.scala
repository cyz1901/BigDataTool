package pers.cyz.bigdatatool.common.config

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.color.{ANSIConstants, ForegroundCompositeConverterBase}

class LogConfig extends ForegroundCompositeConverterBase[ILoggingEvent]{

  @Override
  def getForegroundColorCode(event: ILoggingEvent): String = {
    val level:Level = event.getLevel;
    level.toInt match {
      //ERROR等级为红色
      case Level.ERROR_INT => ANSIConstants.RED_FG
      //WARN等级为黄色
      case Level.WARN_INT => ANSIConstants.YELLOW_FG
      //INFO等级为蓝色
      case Level.INFO_INT => ANSIConstants.BLUE_FG
      //DEBUG等级为绿色
      case Level.DEBUG_INT => ANSIConstants.GREEN_FG
      //其他为默认颜色
      case _ => ANSIConstants.DEFAULT_FG;
    }
  }

}

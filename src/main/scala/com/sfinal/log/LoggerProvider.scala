package com.sfinal.log

import org._
import org.slf4j.LoggerFactory

/**
 * Created by ice on 14-12-3.
 */
trait LoggerProvider {
  def logger(clazz: Class[_]): Logger

  def logger(name: String): Logger
}

object LoggerProvider{

}


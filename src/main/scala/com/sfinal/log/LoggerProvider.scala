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

/**
 * Created by ice on 14-12-3.
 * The five logging levels used by Log are (in order):
 * 1. DEBUG (the least serious)
 * 2. INFO
 * 3. WARN
 * 4. ERROR
 * 5. FATAL (the most serious)
 */

object Logger {
  private var loggerProviderPrivate: LoggerProvider = {
    try {
      Class.forName("org.slf4j.Logger")
      Slf4jLoggerProvider
    } catch {
      case ex: ClassNotFoundException => {
        JdkLoggerProvider
      }
    }
  }

  def loggerProvider: LoggerProvider = {
    loggerProviderPrivate
  }

  def loggerProvider_=(loggerProvider: LoggerProvider) {
    loggerProviderPrivate = loggerProvider
  }

  def logger(clazz: Class[_]): Logger = {
    loggerProviderPrivate.logger(clazz)
  }

  def logger(name: String): Logger = {
    loggerProviderPrivate.logger(name)
  }

}

trait Logger {

  def debug(message: String)

  def debug(message: String, t: Throwable)

  def info(message: String)

  def info(message: String, t: Throwable)

  def warn(message: String)

  def warn(message: String, t: Throwable)

  def error(message: String)

  def error(message: String, t: Throwable)

  def fatal(message: String)

  def fatal(message: String, t: Throwable)

  def isDebugEnabled: Boolean

  def isInfoEnabled: Boolean

  def isWarnEnabled: Boolean

  def isErrorEnabled: Boolean

  def isFatalEnabled: Boolean
}




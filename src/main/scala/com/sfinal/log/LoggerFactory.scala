package com.sfinal.log

/**
 * Created by ice on 14-12-3.
 */
object LoggerFactory {
  private var loggerProviderPrivate: LoggerProvider = {
    try {
      Class.forName("org.slf4j.Logger")
      new Slf4jLoggerProvider
    } catch {
      case ex: ClassNotFoundException => {
        new JdkLoggerProvider
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

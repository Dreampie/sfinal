package com.sfinal.log

import org.slf4j

/**
 * Created by ice on 14-12-3.
 */
class Slf4jLoggerProvider extends LoggerProvider {
  def logger(clazz: Class[_]): Logger = {
    new Slf4jLogger(clazz)
  }

  def logger(name: String): Logger = {
    new Slf4jLogger(name)
  }
}

class Slf4jLogger private extends Logger {
  private var log: slf4j.Logger = null

  def this(clazz: Class[_]) {
    this()
    log = slf4j.LoggerFactory.getLogger(clazz)
  }

  def this(name: String) {
    this()
    log = slf4j.LoggerFactory.getLogger(name)
  }

  def info(message: String) {
    log.info(message)
  }

  def info(message: String, t: Throwable) {
    log.info(message, t)
  }

  def debug(message: String) {
    log.debug(message)
  }

  def debug(message: String, t: Throwable) {
    log.debug(message, t)
  }

  def warn(message: String) {
    log.warn(message)
  }

  def warn(message: String, t: Throwable) {
    log.warn(message, t)
  }

  def error(message: String) {
    log.error(message)
  }

  def error(message: String, t: Throwable) {
    log.error(message, t)
  }

  def fatal(message: String) {
    log.error(message)
  }

  def fatal(message: String, t: Throwable) {
    log.error(message, t)
  }

  def isDebugEnabled: Boolean = {
    return log.isDebugEnabled
  }

  def isInfoEnabled: Boolean = {
    return log.isInfoEnabled
  }

  def isWarnEnabled: Boolean = {
    return log.isWarnEnabled
  }

  def isErrorEnabled: Boolean = {
    return log.isErrorEnabled
  }

  def isFatalEnabled: Boolean = {
    return log.isErrorEnabled
  }
}

package com.sfinal.log

import org.slf4j

/**
 * Created by ice on 14-12-3.
 */
class Slf4jLoggerProvider extends LoggerProvider {
  def logger(clazz: Class[_]): Logger = {
    Slf4jLogger.logger(clazz)
  }

  def logger(name: String): Logger = {
    Slf4jLogger.logger(name)
  }
}

object Slf4jLogger extends Logger {
  private var log: slf4j.Logger = null

  def logger(clazz: Class[_]): Logger = {
    log = slf4j.LoggerFactory.getLogger(clazz)
    this
  }

  def logger(name: String): Logger = {
    log = slf4j.LoggerFactory.getLogger(name)
    this
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
    log.isDebugEnabled
  }

  def isInfoEnabled: Boolean = {
    log.isInfoEnabled
  }

  def isWarnEnabled: Boolean = {
    log.isWarnEnabled
  }

  def isErrorEnabled: Boolean = {
    log.isErrorEnabled
  }

  def isFatalEnabled: Boolean = {
    log.isErrorEnabled
  }
}

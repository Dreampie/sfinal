package com.sfinal.log

import java.util._
import java.util.logging.Level

/**
 * Created by ice on 14-12-3.
 */
class JdkLoggerProvider extends LoggerProvider {
  def logger(clazz: Class[_]): Logger = {
    JdkLogger.logger(clazz)
  }

  def logger(name: String): Logger = {
    JdkLogger.logger(name)
  }
}

object JdkLogger extends Logger {
  private var log: logging.Logger = null
  private var clazzName: String = null

  def logger(clazz: Class[_]): Logger = {
    clazzName = clazz.getName
    log = logging.Logger.getLogger(clazzName)
    this
  }

  def logger(name: String): Logger = {
    clazzName = name
    log = logging.Logger.getLogger(clazzName)
    this
  }

  def debug(message: String) {
    log.logp(Level.FINE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message)
  }

  def debug(message: String, t: Throwable) {
    log.logp(Level.FINE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message, t)
  }

  def info(message: String) {
    log.logp(Level.INFO, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message)
  }

  def info(message: String, t: Throwable) {
    log.logp(Level.INFO, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message, t)
  }

  def warn(message: String) {
    log.logp(Level.WARNING, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message)
  }

  def warn(message: String, t: Throwable) {
    log.logp(Level.WARNING, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message, t)
  }

  def error(message: String) {
    log.logp(Level.SEVERE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message)
  }

  def error(message: String, t: Throwable) {
    log.logp(Level.SEVERE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message, t)
  }

  /**
   * JdkLogger fatal is the same as the error.
   */
  def fatal(message: String) {
    log.logp(Level.SEVERE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message)
  }

  /**
   * JdkLogger fatal is the same as the error.
   */
  def fatal(message: String, t: Throwable) {
    log.logp(Level.SEVERE, clazzName, Thread.currentThread.getStackTrace()(1).getMethodName, message, t)
  }

  def isDebugEnabled: Boolean = {
    log.isLoggable(Level.FINE)
  }

  def isInfoEnabled: Boolean = {
    log.isLoggable(Level.INFO)
  }

  def isWarnEnabled: Boolean = {
    log.isLoggable(Level.WARNING)
  }

  def isErrorEnabled: Boolean = {
    log.isLoggable(Level.SEVERE)
  }

  def isFatalEnabled: Boolean = {
    log.isLoggable(Level.SEVERE)
  }
}


package com.sfinal.log

/**
 * Created by ice on 14-12-3.
 * The five logging levels used by Log are (in order):
 * 1. DEBUG (the least serious)
 * 2. INFO
 * 3. WARN
 * 4. ERROR
 * 5. FATAL (the most serious)
 */
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

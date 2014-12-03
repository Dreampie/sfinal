package com.sfinal.config

/**
 * Created by wangrenhui on 14/12/3.
 */
trait SFConfig {
  /**
   * Config constant
   */
  def configConstant(constans: Constants)

  /**
   * Config route
   */
  def configRoute(routes: Routes)

  /**
   * Config plugin
   */
  def configPlugin(plugins: Plugins)

  /**
   * Config interceptor applied to all actions.
   */
  def configInterceptor(interceptors: Interceptors)

  /**
   * Config handler
   */
  def configHandler(handlers: Handlers)

  /**
   * Call back after JFinal start
   */
  def afterJFinalStart {
  }

  /**
   * Call back before JFinal stop
   */
  def beforeJFinalStop {
  }
}

package com.sfinal.config

/**
 * Created by wangrenhui on 14/12/3.
 */
trait JFinalConfig {
  /**
   * Config constant
   */
  def configConstant(constans: Constants)

  /**
   * Config route
   */
  def configRoute(me: Routes)

  /**
   * Config plugin
   */
  def configPlugin(me: Plugins)

  /**
   * Config interceptor applied to all actions.
   */
  def configInterceptor(me: Interceptors)

  /**
   * Config handler
   */
  def configHandler(me: Handlers)

  /**
   * Call back after JFinal start
   */
  def afterJFinalStart {
  };

  /**
   * Call back before JFinal stop
   */
  def beforeJFinalStop {
  };
}

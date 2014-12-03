package com.sfinal.core

import com.sfinal.config._
import com.sfinal.log.{LoggerFactory, Logger}
import com.sfinal.plugin.IPlugin
import com.sfinal.plugin.activerecord.ActiveRecordPlugin

/**
 * Created by ice on 14-12-3.
 */
object Config {
  private val constants: Constants = new Constants
  private val routes: Routes = new Routes {
    override def config: Unit = ???
  }
  private val plugins: Plugins = new Plugins
  private val interceptors: Interceptors = new Interceptors
  private val handlers: Handlers = new Handlers
  private val log: Logger = LoggerFactory.logger(Config.getClass)


  private def configSFinal(sfinalConfig: SFConfig) {
    sfinalConfig.configConstant(constants)
    sfinalConfig.configRoute(routes)
    sfinalConfig.configPlugin(plugins)
    startPlugins
    sfinalConfig.configInterceptor(interceptors)
    sfinalConfig.configHandler(handlers)
  }

  private def startPlugins {
    val pluginList: List[IPlugin] = plugins.pluginList
    if (pluginList == null) return
    for (plugin <- pluginList) {
      try {
        if (plugin.isInstanceOf[ActiveRecordPlugin]) {
          val arp: ActiveRecordPlugin = plugin.asInstanceOf[ActiveRecordPlugin]
          if (arp.getDevMode == null) arp.setDevMode(constants.devMode)
        }
        if (plugin.start == false) {
          val message: String = "Plugin start error: " + plugin.getClass.getName
          log.error(message)
          throw new RuntimeException(message)
        }
      }
      catch {
        case e: Exception => {
          val message: String = "Plugin start error: " + plugin.getClass.getName + ". \n" + e.getMessage
          log.error(message, e)
          throw new RuntimeException(message, e)
        }
      }
    }
  }

}

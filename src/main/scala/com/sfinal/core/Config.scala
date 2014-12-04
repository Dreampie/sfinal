/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sfinal.core

import java.util.List
import com.sfinal.config.Constants
import com.sfinal.config.JFinalConfig
import com.sfinal.config.Routes
import com.sfinal.config.Plugins
import com.sfinal.config.Handlers
import com.sfinal.config.Interceptors
import com.sfinal.core.JFinalFilter
import com.sfinal.log.Logger
import com.sfinal.log.Logger
import com.sfinal.plugin.IPlugin

object Config {
  private[core] def configJFinal(jfinalConfig: JFinalConfig) {
    jfinalConfig.configConstant(constants)
    initLoggerFactory
    jfinalConfig.configRoute(routes)
    jfinalConfig.configPlugin(plugins)
    startPlugins
    jfinalConfig.configInterceptor(interceptors)
    jfinalConfig.configHandler(handlers)
  }

  final def getConstants: Constants = {
    return constants
  }

  final def getRoutes: Routes = {
    return routes
  }

  final def getPlugins: Plugins = {
    return plugins
  }

  final def getInterceptors: Interceptors = {
    return interceptors
  }

  def getHandlers: Handlers = {
    return handlers
  }

  private def startPlugins {
    val pluginList: List[IPlugin] = plugins.getPluginList
    if (pluginList == null) return
    import scala.collection.JavaConversions._
    for (plugin <- pluginList) {
      try {
        if (plugin.isInstanceOf[ActiveRecordPlugin]) {
          val arp: ActiveRecordPlugin = plugin.asInstanceOf[ActiveRecordPlugin]
          if (arp.getDevMode == null) arp.setDevMode(constants.getDevMode)
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

  private def initLoggerFactory {
    Logger.init
    log = Logger.getLogger(classOf[Config])
    JFinalFilter.initLogger
  }

  private final val constants: Constants = new Constants
  private final val routes: Routes = new Routes {
    def config {
    }
  }
  private final val plugins: Plugins = new Plugins
  private final val interceptors: Interceptors = new Interceptors
  private final val handlers: Handlers = new Handlers
  private var log: Logger = null
}

class Config {
  private def this() {
    this()
  }
}


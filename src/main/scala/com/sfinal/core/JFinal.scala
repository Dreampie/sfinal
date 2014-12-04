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

import java.io.File
import java.util.List
import javax.servlet.ServletContext
import com.sfinal.config.Constants
import com.sfinal.config.JFinalConfig
import com.sfinal.handler.Handler
import com.sfinal.handler.Handler
import com.sfinal.handler.HandlerFactory
import com.sfinal.handler.HandlerFactory
import com.sfinal.i18n.I18N
import com.sfinal.i18n.I18N
import com.sfinal.kit.PathKit
import com.sfinal.kit.PathKit
import com.sfinal.plugin.IPlugin
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory
import com.sfinal.server.IServer
import com.sfinal.server.IServer
import com.sfinal.server.ServerFactory
import com.sfinal.server.ServerFactory
import com.sfinal.token.ITokenCache
import com.sfinal.token.ITokenCache
import com.sfinal.token.TokenManager
import com.sfinal.token.TokenManager
import com.sfinal.upload.OreillyCos
import com.sfinal.upload.OreillyCos

/**
 * JFinal
 */
final object JFinal {
  def me: JFinal = {
    return me
  }

  def start {
    server = ServerFactory.getServer
    server.start
  }

  def start(webAppDir: String, port: Int, context: String, scanIntervalSeconds: Int) {
    server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds)
    server.start
  }

  def stop {
    server.stop
  }

  /**
   * Run JFinal Server with Debug Configurations or Run Configurations in Eclipse JavaEE
   * args example: WebRoot 80 / 5
   */
  def main(args: Array[String]) {
    if (args == null || args.length == 0) {
      server = ServerFactory.getServer
      server.start
    }
    else {
      val webAppDir: String = args(0)
      val port: Int = Integer.parseInt(args(1))
      val context: String = args(2)
      val scanIntervalSeconds: Int = Integer.parseInt(args(3))
      server = ServerFactory.getServer(webAppDir, port, context, scanIntervalSeconds)
      server.start
    }
  }

  private var server: IServer = null
  private final val me: JFinal = new JFinal
}

final class JFinal {
  private[core] def getHandler: Handler = {
    return handler
  }

  private def this() {
    this()
  }

  private[core] def init(jfinalConfig: JFinalConfig, servletContext: ServletContext): Boolean = {
    this.servletContext = servletContext
    this.contextPath = servletContext.getContextPath
    initPathUtil
    Config.configJFinal(jfinalConfig)
    constants = Config.getConstants
    initActionMapping
    initHandler
    initRender
    initOreillyCos
    initI18n
    initTokenManager
    return true
  }

  private def initTokenManager {
    val tokenCache: ITokenCache = constants.getTokenCache
    if (tokenCache != null) TokenManager.init(tokenCache)
  }

  private def initI18n {
    val i18nResourceBaseName: String = constants.getI18nResourceBaseName
    if (i18nResourceBaseName != null) {
      I18N.init(i18nResourceBaseName, constants.getI18nDefaultLocale, constants.getI18nMaxAgeOfCookie)
    }
  }

  private def initHandler {
    val actionHandler: Handler = new ActionHandler(actionMapping, constants)
    handler = HandlerFactory.getHandler(Config.getHandlers.getHandlerList, actionHandler)
  }

  private def initOreillyCos {
    val ct: Constants = constants
    if (OreillyCos.isMultipartSupported) {
      var uploadedFileSaveDirectory: String = ct.getUploadedFileSaveDirectory
      if (uploadedFileSaveDirectory == null || ("" == uploadedFileSaveDirectory.trim)) {
        uploadedFileSaveDirectory = PathKit.getWebRootPath + File.separator + "upload" + File.separator
        ct.setUploadedFileSaveDirectory(uploadedFileSaveDirectory)
      }
      OreillyCos.init(uploadedFileSaveDirectory, ct.getMaxPostSize, ct.getEncoding)
    }
  }

  private def initPathUtil {
    val path: String = servletContext.getRealPath("/")
    PathKit.setWebRootPath(path)
  }

  private def initRender {
    val renderFactory: RenderFactory = RenderFactory.me
    renderFactory.init(constants, servletContext)
  }

  private def initActionMapping {
    actionMapping = new ActionMapping(Config.getRoutes, Config.getInterceptors)
    actionMapping.buildActionMapping
  }

  private[core] def stopPlugins {
    val plugins: List[IPlugin] = Config.getPlugins.getPluginList
    if (plugins != null) {
      {
        var i: Int = plugins.size - 1
        while (i >= 0) {
          {
            var success: Boolean = false
            try {
              success = plugins.get(i).stop
            }
            catch {
              case e: Exception => {
                success = false
                e.printStackTrace
              }
            }
            if (!success) {
              System.err.println("Plugin stop error: " + plugins.get(i).getClass.getName)
            }
          }
          ({
            i -= 1; i + 1
          })
        }
      }
    }
  }

  def getServletContext: ServletContext = {
    return this.servletContext
  }

  def getAllActionKeys: List[String] = {
    return actionMapping.getAllActionKeys
  }

  def getConstants: Constants = {
    return Config.getConstants
  }

  def getAction(url: String, urlPara: Array[String]): Action = {
    return actionMapping.getAction(url, urlPara)
  }

  def getContextPath: String = {
    return contextPath
  }

  private var constants: Constants = null
  private var actionMapping: ActionMapping = null
  private var handler: Handler = null
  private var servletContext: ServletContext = null
  private var contextPath: String = ""
}












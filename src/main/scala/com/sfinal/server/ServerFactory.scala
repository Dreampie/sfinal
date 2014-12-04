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
package com.sfinal.server

import java.io.File
import com.sfinal.kit.PathKit

/**
 * ServerFactory
 */
object ServerFactory {
  /**
   * Return web server.
   * <p>
   * important: if scanIntervalSeconds < 1 then you will turn off the hot swap
   * @param webAppDir the directory of the project web root
   * @param port the port
   * @param context the context
   * @param scanIntervalSeconds the scan interval seconds
   */
  def getServer(webAppDir: String, port: Int, context: String, scanIntervalSeconds: Int): IServer = {
    return new JettyServer(webAppDir, port, context, scanIntervalSeconds)
  }

  def getServer(webAppDir: String, port: Int, context: String): IServer = {
    return getServer(webAppDir, port, context, DEFAULT_SCANINTERVALSECONDS)
  }

  def getServer(port: Int, context: String, scanIntervalSeconds: Int): IServer = {
    return getServer(detectWebAppDir, port, context, scanIntervalSeconds)
  }

  def getServer(port: Int, context: String): IServer = {
    return getServer(detectWebAppDir, port, context, DEFAULT_SCANINTERVALSECONDS)
  }

  def getServer(port: Int): IServer = {
    return getServer(detectWebAppDir, port, "/", DEFAULT_SCANINTERVALSECONDS)
  }

  def getServer: IServer = {
    return getServer(detectWebAppDir, DEFAULT_PORT, "/", DEFAULT_SCANINTERVALSECONDS)
  }

  private def detectWebAppDir: String = {
    val rootClassPath: String = PathKit.getRootClassPath
    var temp: Array[String] = null
    if (rootClassPath.indexOf("\\WEB-INF\\") != -1) temp = rootClassPath.split("\\\\")
    else if (rootClassPath.indexOf("/WEB-INF/") != -1) temp = rootClassPath.split("/")
    else throw new RuntimeException("WEB-INF directory not found.")
    return temp(temp.length - 3)
  }

  @SuppressWarnings(Array("unused"))
  @Deprecated private def detectWebAppDir_old: String = {
    val rootClassPath: String = PathKit.getRootClassPath
    var temp: Array[String] = null
    try {
      temp = rootClassPath.split(File.separator)
    }
    catch {
      case e: Exception => {
        temp = rootClassPath.split("\\\\")
      }
    }
    return temp(temp.length - 3)
  }

  private final val DEFAULT_PORT: Int = 80
  private final val DEFAULT_SCANINTERVALSECONDS: Int = 5
}

class ServerFactory {
  private def this() {
    this()
  }
}






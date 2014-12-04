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
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import com.sfinal.server.{JFinalClassLoader, Scanner}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.SessionManager
import org.eclipse.jetty.server.SessionManager
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.server.session.HashSessionManager
import org.eclipse.jetty.server.session.HashSessionManager
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.webapp.WebAppContext
import com.sfinal.core.Const
import com.sfinal.kit.FileKit
import com.sfinal.kit.PathKit
import com.sfinal.kit.StrKit
import org.eclipse.jetty.webapp.WebAppContext

/**
 * JettyServer is used to config and start jetty web server.
 * Jetty version 8.1.8
 */
object JettyServer {
  private def available(port: Int): Boolean = {
    if (port <= 0) {
      throw new IllegalArgumentException("Invalid start port: " + port)
    }
    var ss: ServerSocket = null
    var ds: DatagramSocket = null
    try {
      ss = new ServerSocket(port)
      ss.setReuseAddress(true)
      ds = new DatagramSocket(port)
      ds.setReuseAddress(true)
      return true
    }
    catch {
      case e: IOException => {
      }
    }
    finally {
      if (ds != null) {
        ds.close
      }
      if (ss != null) {
        try {
          ss.close
        }
        catch {
          case e: IOException => {
          }
        }
      }
    }
    return false
  }
}

class JettyServer extends IServer {
  private[server] def this(webAppDir: String, port: Int, context: String, scanIntervalSeconds: Int) {
    this()
    if (webAppDir == null) throw new IllegalStateException("Invalid webAppDir of web server: " + webAppDir)
    if (port < 0 || port > 65536) throw new IllegalArgumentException("Invalid port of web server: " + port)
    if (StrKit.isBlank(context)) throw new IllegalStateException("Invalid context of web server: " + context)
    this.webAppDir = webAppDir
    this.port = port
    this.context = context
    this.scanIntervalSeconds = scanIntervalSeconds
  }

  def start {
    if (!running) {
      try {
        doStart
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      running = true
    }
  }

  def stop {
    if (running) {
      try {
        server.stop
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      running = false
    }
  }

  private def doStart {
    if (!available(port)) throw new IllegalStateException("port: " + port + " already in use!")
    deleteSessionData
    System.out.println("Starting JFinal " + Const.JFINAL_VERSION)
    server = new Server
    val connector: SelectChannelConnector = new SelectChannelConnector
    connector.setPort(port)
    server.addConnector(connector)
    webApp = new WebAppContext
    webApp.setContextPath(context)
    webApp.setResourceBase(webAppDir)
    webApp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false")
    webApp.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false")
    persistSession(webApp)
    server.setHandler(webApp)
    changeClassLoader(webApp)
    if (scanIntervalSeconds > 0) {
      val scanner: Scanner = new Scanner((PathKit.getRootClassPath, scanIntervalSeconds)) {
        def onChange {
          try {
            System.err.println("\nLoading changes ......")
            webApp.stop
            val loader: JFinalClassLoader = new JFinalClassLoader(webApp, getClassPath)
            webApp.setClassLoader(loader)
            webApp.start
            System.err.println("Loading complete.")
          }
          catch {
            case e: Exception => {
              System.err.println("Error reconfiguring/restarting webapp after change in watched files")
              e.printStackTrace
            }
          }
        }
      }
      System.out.println("Starting scanner at interval of " + scanIntervalSeconds + " seconds.")
      scanner.start
    }
    try {
      System.out.println("Starting web server on port: " + port)
      server.start
      System.out.println("Starting Complete. Welcome To The JFinal World :)")
      server.join
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        System.exit(100)
      }
    }
    return
  }

  @SuppressWarnings(Array("resource")) private def changeClassLoader(webApp: WebAppContext) {
    try {
      val classPath: String = getClassPath
      val wacl: JFinalClassLoader = new JFinalClassLoader(webApp, classPath)
      wacl.addClassPath(classPath)
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
  }

  private def getClassPath: String = {
    return System.getProperty("java.class.path")
  }

  private def deleteSessionData {
    try {
      FileKit.delete(new File(getStoreDir))
    }
    catch {
      case e: Exception => {
      }
    }
  }

  private def getStoreDir: String = {
    var storeDir: String = PathKit.getWebRootPath + "/../../session_data" + context
    if ("\\" == File.separator) storeDir = storeDir.replaceAll("/", "\\\\")
    return storeDir
  }

  private def persistSession(webApp: WebAppContext) {
    val storeDir: String = getStoreDir
    val sm: SessionManager = webApp.getSessionHandler.getSessionManager
    if (sm.isInstanceOf[HashSessionManager]) {
      (sm.asInstanceOf[HashSessionManager]).setStoreDirectory(new File(storeDir))
      return
    }
    val hsm: HashSessionManager = new HashSessionManager
    hsm.setStoreDirectory(new File(storeDir))
    val sh: SessionHandler = new SessionHandler
    sh.setSessionManager(hsm)
    webApp.setSessionHandler(sh)
  }

  private var webAppDir: String = null
  private var port: Int = 0
  private var context: String = null
  private var scanIntervalSeconds: Int = 0
  private var running: Boolean = false
  private var server: Server = null
  private var webApp: WebAppContext = null
}








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
package com.sfinal.render

import java.io.File
import java.util.Locale
import javax.servlet.ServletContext
import com.sfinal.config.Constants
import com.sfinal.kit.PathKit
import com.sfinal.core.Const._
import com.sfinal.render.{XmlRender, TextRender, ViewType, VelocityRender}

/**
 * RenderFactory.
 */
object RenderFactory {
  private[render] def getServletContext: ServletContext = {
    return servletContext
  }

  def me: RenderFactory = {
    return me
  }

  def setMainRenderFactory(mainRenderFactory: IMainRenderFactory) {
    if (mainRenderFactory != null) RenderFactory.mainRenderFactory = mainRenderFactory
  }

  def setErrorRenderFactory(errorRenderFactory: IErrorRenderFactory) {
    if (errorRenderFactory != null) RenderFactory.errorRenderFactory = errorRenderFactory
  }

  private var mainRenderFactory: IMainRenderFactory = null
  private var errorRenderFactory: IErrorRenderFactory = null
  private var servletContext: ServletContext = null
  private final val me: RenderFactory = new RenderFactory

  private final class FreeMarkerRenderFactory extends IMainRenderFactory {
    def getRender(view: String): Render = {
      return new FreeMarkerRender(view)
    }

    def getViewExtension: String = {
      return ".html"
    }
  }

  private final class JspRenderFactory extends IMainRenderFactory {
    def getRender(view: String): Render = {
      return new JspRender(view)
    }

    def getViewExtension: String = {
      return ".jsp"
    }
  }

  private final class VelocityRenderFactory extends IMainRenderFactory {
    def getRender(view: String): Render = {
      return new VelocityRender(view)
    }

    def getViewExtension: String = {
      return ".html"
    }
  }

  private final class ErrorRenderFactory extends IErrorRenderFactory {
    def getRender(errorCode: Int, view: String): Render = {
      return new ErrorRender(errorCode, view)
    }
  }

}

class RenderFactory {
  private def this() {
    this()
  }

  def init(constants: Constants, servletContext: ServletContext) {
    this.constants = constants
    RenderFactory.servletContext = servletContext
    Render.init(constants.getEncoding, constants.getDevMode)
    initFreeMarkerRender(servletContext)
    initVelocityRender(servletContext)
    initJspRender(servletContext)
    initFileRender(servletContext)
    if (mainRenderFactory == null) {
      val defaultViewType: ViewType = constants.getViewType
      if (defaultViewType eq ViewType.FREE_MARKER) mainRenderFactory = new RenderFactory.FreeMarkerRenderFactory
      else if (defaultViewType eq ViewType.JSP) mainRenderFactory = new RenderFactory.JspRenderFactory
      else if (defaultViewType eq ViewType.VELOCITY) mainRenderFactory = new RenderFactory.VelocityRenderFactory
      else throw new RuntimeException("View Type can not be null.")
    }
    if (errorRenderFactory == null) {
      errorRenderFactory = new RenderFactory.ErrorRenderFactory
    }
  }

  private def initFreeMarkerRender(servletContext: ServletContext) {
    try {
      Class.forName("freemarker.template.Template")
      FreeMarkerRender.init(servletContext, Locale.getDefault, constants.getFreeMarkerTemplateUpdateDelay)
    }
    catch {
      case e: ClassNotFoundException => {
      }
    }
  }

  private def initVelocityRender(servletContext: ServletContext) {
    try {
      Class.forName("org.apache.velocity.VelocityContext")
      VelocityRender.init(servletContext)
    }
    catch {
      case e: ClassNotFoundException => {
      }
    }
  }

  private def initJspRender(servletContext: ServletContext) {
    try {
      Class.forName("javax.el.ELResolver")
      Class.forName("javax.servlet.jsp.JspFactory")
      com.jfinal.plugin.activerecord.ModelRecordElResolver.init(servletContext)
    }
    catch {
      case e: ClassNotFoundException => {
      }
      case e: Exception => {
      }
    }
  }

  private def initFileRender(servletContext: ServletContext) {
    FileRender.init(getFileRenderPath, servletContext)
  }

  private def getFileRenderPath: String = {
    var result: String = constants.getFileRenderPath
    if (result == null) {
      result = PathKit.getWebRootPath + DEFAULT_FILE_RENDER_BASE_PATH
    }
    if (!result.endsWith(File.separator) && !result.endsWith("/")) {
      result = result + File.separator
    }
    return result
  }

  /**
   * Return Render by default ViewType which config in JFinalConfig
   */
  def getRender(view: String): Render = {
    return mainRenderFactory.getRender(view)
  }

  def getFreeMarkerRender(view: String): Render = {
    return new FreeMarkerRender(view)
  }

  def getJspRender(view: String): Render = {
    return new JspRender(view)
  }

  def getVelocityRender(view: String): Render = {
    return new VelocityRender(view)
  }

  def getJsonRender: Render = {
    return new JsonRender
  }

  def getJsonRender(key: String, value: AnyRef): Render = {
    return new JsonRender(key, value)
  }

  def getJsonRender(attrs: Array[String]): Render = {
    return new JsonRender(attrs)
  }

  def getJsonRender(jsonText: String): Render = {
    return new JsonRender(jsonText)
  }

  def getJsonRender(`object`: AnyRef): Render = {
    return new JsonRender(`object`)
  }

  def getTextRender(text: String): Render = {
    return new TextRender(text)
  }

  def getTextRender(text: String, contentType: String): Render = {
    return new TextRender(text, contentType)
  }

  def getTextRender(text: String, contentType: ContentType): Render = {
    return new TextRender(text, contentType)
  }

  def getDefaultRender(view: String): Render = {
    val viewType: ViewType = constants.getViewType
    if (viewType eq ViewType.FREE_MARKER) {
      return new FreeMarkerRender(view + constants.getFreeMarkerViewExtension)
    }
    else if (viewType eq ViewType.JSP) {
      return new JspRender(view + constants.getJspViewExtension)
    }
    else if (viewType eq ViewType.VELOCITY) {
      return new VelocityRender(view + constants.getVelocityViewExtension)
    }
    else {
      return mainRenderFactory.getRender(view + mainRenderFactory.getViewExtension)
    }
  }

  def getErrorRender(errorCode: Int, view: String): Render = {
    return errorRenderFactory.getRender(errorCode, view)
  }

  def getErrorRender(errorCode: Int): Render = {
    return errorRenderFactory.getRender(errorCode, constants.getErrorView(errorCode))
  }

  def getFileRender(fileName: String): Render = {
    return new FileRender(fileName)
  }

  def getFileRender(file: File): Render = {
    return new FileRender(file)
  }

  def getRedirectRender(url: String): Render = {
    return new RedirectRender(url)
  }

  def getRedirectRender(url: String, withQueryString: Boolean): Render = {
    return new RedirectRender(url, withQueryString)
  }

  def getRedirect301Render(url: String): Render = {
    return new Redirect301Render(url)
  }

  def getRedirect301Render(url: String, withQueryString: Boolean): Render = {
    return new Redirect301Render(url, withQueryString)
  }

  def getNullRender: Render = {
    return new NullRender
  }

  def getJavascriptRender(jsText: String): Render = {
    return new JavascriptRender(jsText)
  }

  def getHtmlRender(htmlText: String): Render = {
    return new HtmlRender(htmlText)
  }

  def getXmlRender(view: String): Render = {
    return new XmlRender(view)
  }

  private var constants: Constants = null
}




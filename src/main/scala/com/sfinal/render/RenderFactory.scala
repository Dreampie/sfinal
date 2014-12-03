package com.sfinal.render

import java.util.Locale
import javax.servlet.ServletContext

import com.sfinal.config.Constants
import com.sfinal.render.ViewType.ViewType

/**
 * Created by ice on 14-12-3.
 */
object RenderFactory {
  private var constants: Constants = null
  private var mainRenderFactoryPrivate: IMainRenderFactory = null
  private var errorRenderFactoryPrivate: IErrorRenderFactory = null
  private var servletContextPrivate: ServletContext = null

  private def servletContext: ServletContext = {
    servletContextPrivate
  }

  def mainRenderFactory: IMainRenderFactory = {
    mainRenderFactoryPrivate
  }

  def mainRenderFactory_=(mainRenderFactory: IMainRenderFactory) {
    if (mainRenderFactory != null) mainRenderFactoryPrivate = mainRenderFactory
  }

  def errorRenderFactory: IErrorRenderFactory = {
    errorRenderFactoryPrivate
  }

  def errorRenderFactory_=(errorRenderFactory: IErrorRenderFactory) {
    if (errorRenderFactory != null) errorRenderFactoryPrivate = errorRenderFactory
  }

  def init(constants: Constants, servletContext: ServletContext) {
    this.constants = constants
    this.servletContextPrivate = servletContext
    Render.init(constants.encoding, constants.devMode)
    initFreeMarkerRender(servletContext)
    initVelocityRender(servletContext)
    initJspRender(servletContext)
    initFileRender(servletContext)
    if (mainRenderFactory == null) {
      val defaultViewType: ViewType = constants.viewType
      if (defaultViewType eq ViewType.FREE_MARKER) mainRenderFactory = new RenderFactory.FreeMarkerRenderFactory
      else if (defaultViewType eq ViewType.JSP) mainRenderFactory = new RenderFactory.JspRenderFactory
      else if (defaultViewType eq ViewType.VELOCITY) mainRenderFactory = new RenderFactory.VelocityRenderFactory
      else throw new Nothing("View Type can not be null.")
    }
    if (errorRenderFactory == null) {
      errorRenderFactory = new RenderFactory.ErrorRenderFactory
    }
  }

  private def initFreeMarkerRender(servletContext: ServletContext) {
    try {
      Class.forName("freemarker.template.Template")
      FreeMarkerRender.init(servletContext, Locale.getDefault, constants.freeMarkerTemplateUpdateDelay)
    }
    catch {
      case e: Exception => {
      }
    }
  }

  private def initVelocityRender(servletContext: ServletContext) {
    try {
      Class.forName("org.apache.velocity.VelocityContext")
      VelocityRender.init(servletContext)
    }
    catch {
      case e: Nothing => {
      }
    }
  }

  private def initJspRender(servletContext: ServletContext) {
    try {
      Class.forName("javax.el.ELResolver")
      Class.forName("javax.servlet.jsp.JspFactory")
      com.sfinal.plugin.activerecord.ModelRecordElResolver.init(servletContext)
    }
    catch {
      case e: Nothing => {
      }
      case e: Nothing => {
      }
    }
  }

  private def initFileRender(servletContext: ServletContext) {
    FileRender.init(getFileRenderPath, servletContext)
  }

  private def getFileRenderPath: Nothing = {
    var result: Nothing = constants.getFileRenderPath
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
  def getRender(view: Nothing): Render = {
    return mainRenderFactory.render(view)
  }

  def getFreeMarkerRender(view: Nothing): Render = {
    return new FreeMarkerRender(view)
  }

  def getJspRender(view: Nothing): Render = {
    return new JspRender(view)
  }

  def getVelocityRender(view: Nothing): Render = {
    return new VelocityRender(view)
  }

  def getJsonRender: Render = {
    return new JsonRender
  }

  def getJsonRender(key: Nothing, value: Nothing): Render = {
    return new JsonRender(key, value)
  }

  def getJsonRender(attrs: Array[Nothing]): Render = {
    return new JsonRender(attrs)
  }

  def getJsonRender(jsonText: Nothing): Render = {
    return new JsonRender(jsonText)
  }

  def getJsonRender(`object`: Nothing): Render = {
    return new JsonRender(`object`)
  }

  def getTextRender(text: Nothing): Render = {
    return new TextRender(text)
  }

  def getTextRender(text: Nothing, contentType: Nothing): Render = {
    return new TextRender(text, contentType)
  }

  def getTextRender(text: Nothing, contentType: ContentType): Render = {
    return new TextRender(text, contentType)
  }

  def getDefaultRender(view: Nothing): Render = {
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
      return mainRenderFactory.render(view + mainRenderFactory.viewExtension)
    }
  }

  def getErrorRender(errorCode: Int, view: Nothing): Render = {
    return errorRenderFactory.render(errorCode, view)
  }

  def getErrorRender(errorCode: Int): Render = {
    return errorRenderFactory.render(errorCode, constants.getErrorView(errorCode))
  }

  def getFileRender(fileName: Nothing): Render = {
    return new FileRender(fileName)
  }

  def getFileRender(file: Nothing): Render = {
    return new FileRender(file)
  }

  def getRedirectRender(url: Nothing): Render = {
    return new RedirectRender(url)
  }

  def getRedirectRender(url: Nothing, withQueryString: Boolean): Render = {
    return new RedirectRender(url, withQueryString)
  }

  def getRedirect301Render(url: Nothing): Render = {
    return new Redirect301Render(url)
  }

  def getRedirect301Render(url: Nothing, withQueryString: Boolean): Render = {
    return new Redirect301Render(url, withQueryString)
  }

  def getNullRender: Render = {
    return new NullRender
  }

  def getJavascriptRender(jsText: Nothing): Render = {
    return new JavascriptRender(jsText)
  }

  def getHtmlRender(htmlText: Nothing): Render = {
    return new HtmlRender(htmlText)
  }

  def getXmlRender(view: Nothing): Render = {
    return new XmlRender(view)
  }

  private final class FreeMarkerRenderFactory extends IMainRenderFactory {
    def render(view: Nothing): Render = {
      return new FreeMarkerRender(view)
    }

    def viewExtension: Nothing = {
      return ".html"
    }
  }

  private final class JspRenderFactory extends IMainRenderFactory {
    def render(view: Nothing): Render = {
      return new JspRender(view)
    }

    def viewExtension: Nothing = {
      return ".jsp"
    }
  }

  private final class VelocityRenderFactory extends IMainRenderFactory {
    def render(view: Nothing): Render = {
      return new VelocityRender(view)
    }

    def viewExtension: Nothing = {
      return ".html"
    }
  }

  private final class ErrorRenderFactory extends IErrorRenderFactory {
    def render(errorCode: Int, view: Nothing): Render = {
      return new ErrorRender(errorCode, view)
    }
  }

}

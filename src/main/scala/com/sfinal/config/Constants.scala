package com.sfinal.config

import java.io.File
import java.util.Locale

import com.sfinal.core.Const
import com.sfinal.kit.{PathKit, StrKit}
import com.sfinal.log.{LoggerProvider, LoggerFactory}
import com.sfinal.render.{IErrorRenderFactory, RenderFactory, IMainRenderFactory, ViewType}
import com.sfinal.render.ViewType.ViewType
import com.sfinal.render.ViewType.ViewType
import com.sfinal.token.ITokenCache
import org.slf4j.ILoggerFactory

import scala.collection.immutable.HashMap

/**
 * Created by ice on 14-12-3.
 */

class Constants  {
  private var fileRenderPathPrivate: String = null
  private var uploadedFileSaveDirectoryPrivate: String = null
  var devMode: Boolean = false
  var encoding: String = Const.DEFAULT_ENCODING
  private var urlParaSeparatorPrivate: String = Const.DEFAULT_URL_PARA_SEPARATOR
  var viewType: ViewType = Const.DEFAULT_VIEW_TYPE
  private var jspViewExtensionPrivate: String = Const.DEFAULT_JSP_EXTENSION
  private var freeMarkerViewExtensionPrivate: String = Const.DEFAULT_FREE_MARKER_EXTENSION
  private var velocityViewExtensionPrivate: String = Const.DEFAULT_VELOCITY_EXTENSION

  private var errorViewMappingPrivate: Map[Int, String] = HashMap[Int, String]()

  private var maxPostSizePrivate: Int = Const.DEFAULT_MAX_POST_SIZE
  private var freeMarkerTemplateUpdateDelayPrivate: Int = Const.DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY
  var tokenCache: ITokenCache = null


  def urlParaSeparator: String = {
    urlParaSeparatorPrivate
  }

  /**
   * Set urlPara separator. The default value is "-"
   * @param urlParaSeparator the urlPara separator
   */
  def urlParaSeparator_=(urlParaSeparator: String) {
    if (StrKit.isBlank(urlParaSeparator) || urlParaSeparator.contains("/")) throw new IllegalArgumentException("urlParaSepartor can not be blank and can not contains \"/\"")
    this.urlParaSeparatorPrivate = urlParaSeparator
  }

  def jspViewExtension: String = {
    jspViewExtensionPrivate
  }

  /**
   * Set Jsp view extension. The default value is ".jsp"
   * @param jspViewExtension the Jsp view extension
   */
  def jspViewExtension_=(jspViewExtension: String) {
    this.jspViewExtensionPrivate = if (jspViewExtension.startsWith(".")) jspViewExtension else "." + jspViewExtension
  }

  def freeMarkerViewExtension: String = {
    freeMarkerViewExtensionPrivate
  }

  /**
   * Set FreeMarker view extension. The default value is ".html" not ".ftl"
   * @param freeMarkerViewExtension the FreeMarker view extension
   */
  def freeMarkerViewExtension_=(freeMarkerViewExtension: String) {
    this.freeMarkerViewExtensionPrivate = if (freeMarkerViewExtension.startsWith(".")) freeMarkerViewExtension else "." + freeMarkerViewExtension
  }

  def velocityViewExtension: String = {
    velocityViewExtensionPrivate
  }

  /**
   * Set Velocity view extension. The default value is ".vm"
   * @param velocityViewExtension the Velocity view extension
   */
  def velocityViewExtension_=(velocityViewExtension: String) {
    this.velocityViewExtensionPrivate = if (velocityViewExtension.startsWith(".")) velocityViewExtension else "." + velocityViewExtension
  }

  /**
   * Set error 404 view.
   * @param error404View the error 404 view
   */
  def error404View_=(error404View: Nothing) {
    errorViewMappingPrivate += ((404, error404View))
  }

  /**
   * Set error 500 view.
   * @param error500View the error 500 view
   */
  def error500View_=(error500View: String) {
    errorViewMappingPrivate += ((500, error500View))
  }

  /**
   * Set error 401 view.
   * @param error401View the error 401 view
   */
  def error401View_=(error401View: String) {
    errorViewMappingPrivate += ((401, error401View))
  }

  /**
   * Set error 403 view.
   * @param error403View the error 403 view
   */
  def error403View_=(error403View: String) {
    errorViewMappingPrivate += ((403, error403View))
  }


  def errorView_=(errorCode: Int, errorView: String) {
    errorViewMappingPrivate += ((errorCode, errorView))
  }

  def errorView(errorCode: Int): Option[String] = {
    errorViewMappingPrivate.get(errorCode)
  }

  def fileRenderPath: String = {
    fileRenderPathPrivate
  }

  /**
   * Set the path of file render of controller.
   * <p>
   * The path is start with root path of this web application.
   * The default value is "/download" if you do not config this parameter.
   */
  def fileRenderPath_=(fileRenderPath: String) {
    if (StrKit.isBlank(fileRenderPath)) throw new IllegalArgumentException("The argument fileRenderPath can not be blank")
    if (!fileRenderPath.startsWith("/") && !fileRenderPath.startsWith(File.separator)) fileRenderPathPrivate = File.separator + fileRenderPath
    this.fileRenderPathPrivate = PathKit.webRootPath + fileRenderPath
  }

  /**
   * Set the save directory for upload file. You can use PathUtil.getWebRootPath()
   * to get the web root path of this application, then create a path based on
   * web root path conveniently.
   */
  def uploadedFileSaveDirectory_=(uploadedFileSaveDirectory: String) {
    if (StrKit.isBlank(uploadedFileSaveDirectory)) throw new IllegalArgumentException("uploadedFileSaveDirectory can not be blank")
    if (uploadedFileSaveDirectory.endsWith("/") || uploadedFileSaveDirectory.endsWith("\\")) this.uploadedFileSaveDirectoryPrivate = uploadedFileSaveDirectory
    else this.uploadedFileSaveDirectoryPrivate = uploadedFileSaveDirectory + File.separator
  }

  def getUploadedFileSaveDirectory: String = {
    uploadedFileSaveDirectoryPrivate
  }

  def maxPostSize: Int = {
    maxPostSize
  }

  /**
   * Set max size of http post. The upload file size depend on this value.
   */
  def maxPostSize_(maxPostSize: Int) {
    if (maxPostSize != null && maxPostSize > 0) {
      this.maxPostSizePrivate = maxPostSize
    }
  }

  private var i18nResourceBaseNamePrivate: String = null
  private var defaultLocalePrivate: Locale = null
  private var i18nMaxAgeOfCookiePrivate: Int = null

  def i18n_=(i18nResourceBaseName: String, defaultLocale: Locale, i18nMaxAgeOfCookie: Int) {
    this.i18nResourceBaseNamePrivate = i18nResourceBaseName
    this.defaultLocalePrivate = defaultLocale
    this.i18nMaxAgeOfCookiePrivate = i18nMaxAgeOfCookie
  }

  def i18n_=(i18nResourceBaseName: String) {
    this.i18nResourceBaseNamePrivate = i18nResourceBaseName
  }

  def i18nResourceBaseName: String = {
    i18nResourceBaseNamePrivate
  }

  def i18nDefaultLocale: Locale = {
    defaultLocalePrivate
  }

  def i18nMaxAgeOfCookie: Int = {
    i18nMaxAgeOfCookiePrivate
  }

  // -----
  /**
   * FreeMarker template update delay for not devMode.
   */
  def freeMarkerTemplateUpdateDelay_=(delayInSeconds: Int) {
    if (delayInSeconds < 0) throw new IllegalArgumentException("template_update_delay must more than -1.")
    this.freeMarkerTemplateUpdateDelayPrivate = delayInSeconds
  }

  def freeMarkerTemplateUpdateDelay: Int = {
    freeMarkerTemplateUpdateDelayPrivate
  }

  /**
   * Set the base path for all views
   */
  def baseViewPath_=(baseViewPath: String) {
    Routes.baseViewPath = baseViewPath
  }

  /**
   * Set the mainRenderFactory then your can use your custom render in controller as render(String).
   */
  def mainRenderFactory_=(mainRenderFactory: IMainRenderFactory) {
    if (mainRenderFactory == null) throw new IllegalArgumentException("mainRenderFactory can not be null.")
    this.viewType = ViewType.OTHER
    RenderFactory.mainRenderFactory = mainRenderFactory
  }


  def loggerProvidder_=(loggerProvider: LoggerProvider) {
    if (loggerProvider==null) throw new IllegalArgumentException("loggerFactory can not be null.")
    LoggerFactory.loggerProvider = loggerProvider
  }

  def errorRenderFactory_(errorRenderFactory: IErrorRenderFactory) {
    if (errorRenderFactory == null) throw new IllegalArgumentException("errorRenderFactory can not be null.")
    RenderFactory.errorRenderFactory = errorRenderFactory
  }
}

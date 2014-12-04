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
package com.sfinal.config

import java.io.File
import java.util.Locale

import com.sfinal.config.Routes
import com.sfinal.core.Const
import com.sfinal.core.Const
import com.sfinal.kit.PathKit
import com.sfinal.kit.PathKit
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit
import com.sfinal.log.{LoggerFactory, Logger}
import com.sfinal.render.IErrorRenderFactory
import com.sfinal.render.IMainRenderFactory
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory
import com.sfinal.render.ViewType
import com.sfinal.render.ViewType
import com.sfinal.render.ViewType
import com.sfinal.render.ViewType.ViewType
import com.sfinal.token.ITokenCache
import com.sfinal.token.ITokenCache

import scala.collection.immutable.HashMap


/**
 * The constant for JFinal runtime.
 */
final class Constants {
  /**
   * Set ITokenCache implementation otherwise JFinal will use the HttpSesion to hold the token.
   * @param tokenCache the token cache
   */
  def setTokenCache(tokenCache: ITokenCache) {
    this.tokenCache = tokenCache
  }

  def getTokenCache: ITokenCache = {
    tokenCache
  }

  /**
   * Set development mode.
   * @param devMode the development mode
   */
  def setDevMode(devMode: Boolean) {
    this.devMode = devMode
  }

  /**
   * Set encoding. The default encoding is UTF-8.
   * @param encoding the encoding
   */
  def setEncoding(encoding: String) {
    this.encoding = encoding
  }

  def getEncoding: String = {
    encoding
  }

  def getDevMode: Boolean = {
    devMode
  }

  def getUrlParaSeparator: String = {
    urlParaSeparator
  }

  def getViewType: ViewType = {
    viewType
  }

  /**
   * Set view type. The default value is ViewType.FREE_MARKER
   * Controller.render(String view) will use the view type to render the view.
   * @param viewType the view type
   */
  def setViewType(viewType: ViewType) {
    if (viewType == null) throw new IllegalArgumentException("viewType can not be null")
    if (viewType ne ViewType.OTHER) this.viewType = viewType
  }

  /**
   * Set urlPara separator. The default value is "-"
   * @param urlParaSeparator the urlPara separator
   */
  def setUrlParaSeparator(urlParaSeparator: String) {
    if (StrKit.isBlank(urlParaSeparator) || urlParaSeparator.contains("/")) throw new IllegalArgumentException("urlParaSepartor can not be blank and can not contains \"/\"")
    this.urlParaSeparator = urlParaSeparator
  }

  def getJspViewExtension: String = {
    jspViewExtension
  }

  /**
   * Set Jsp view extension. The default value is ".jsp"
   * @param jspViewExtension the Jsp view extension
   */
  def setJspViewExtension(jspViewExtension: String) {
    this.jspViewExtension = if (jspViewExtension.startsWith(".")) jspViewExtension else "." + jspViewExtension
  }

  def getFreeMarkerViewExtension: String = {
    freeMarkerViewExtension
  }

  /**
   * Set FreeMarker view extension. The default value is ".html" not ".ftl"
   * @param freeMarkerViewExtension the FreeMarker view extension
   */
  def setFreeMarkerViewExtension(freeMarkerViewExtension: String) {
    this.freeMarkerViewExtension = if (freeMarkerViewExtension.startsWith(".")) freeMarkerViewExtension else "." + freeMarkerViewExtension
  }

  def getVelocityViewExtension: String = {
    velocityViewExtension
  }

  /**
   * Set Velocity view extension. The default value is ".vm"
   * @param velocityViewExtension the Velocity view extension
   */
  def setVelocityViewExtension(velocityViewExtension: String) {
    this.velocityViewExtension = if (velocityViewExtension.startsWith(".")) velocityViewExtension else "." + velocityViewExtension
  }

  /**
   * Set error 404 view.
   * @param error404View the error 404 view
   */
  def setError404View(error404View: String) {
    errorViewMapping += ((404, error404View))
  }

  /**
   * Set error 500 view.
   * @param error500View the error 500 view
   */
  def setError500View(error500View: String) {
    errorViewMapping += ((500, error500View))
  }

  /**
   * Set error 401 view.
   * @param error401View the error 401 view
   */
  def setError401View(error401View: String) {
    errorViewMapping + ((401, error401View))
  }

  /**
   * Set error 403 view.
   * @param error403View the error 403 view
   */
  def setError403View(error403View: String) {
    errorViewMapping + ((403, error403View))
  }

  def setErrorView(errorCode: Int, errorView: String) {
    errorViewMapping + ((errorCode, errorView))
  }

  def getErrorView(errorCode: Int): Option[String] = {
    errorViewMapping.get(errorCode)
  }

  def getFileRenderPath: String = {
    fileRenderPath
  }

  /**
   * Set the path of file render of controller.
   * <p>
   * The path is start with root path of this web application.
   * The default value is "/download" if you do not config this parameter.
   */
  def setFileRenderPath(fileRenderPath: String) {
    if (StrKit.isBlank(fileRenderPath)) throw new IllegalArgumentException("The argument fileRenderPath can not be blank")
    if (!fileRenderPath.startsWith("/") && !fileRenderPath.startsWith(File.separator))
      this.fileRenderPath = PathKit.getWebRootPath + File.separator + fileRenderPath
    else
      this.fileRenderPath = PathKit.getWebRootPath + fileRenderPath
  }

  /**
   * Set the save directory for upload file. You can use PathUtil.getWebRootPath()
   * to get the web root path of this application, then create a path based on
   * web root path conveniently.
   */
  def setUploadedFileSaveDirectory(uploadedFileSaveDirectory: String) {
    if (StrKit.isBlank(uploadedFileSaveDirectory)) throw new IllegalArgumentException("uploadedFileSaveDirectory can not be blank")
    if (uploadedFileSaveDirectory.endsWith("/") || uploadedFileSaveDirectory.endsWith("\\")) this.uploadedFileSaveDirectory = uploadedFileSaveDirectory
    else this.uploadedFileSaveDirectory = uploadedFileSaveDirectory + File.separator
  }

  def getUploadedFileSaveDirectory: String = {
    uploadedFileSaveDirectory
  }

  def getMaxPostSize: Int = {
    maxPostSize
  }

  /**
   * Set max size of http post. The upload file size depend on this value.
   */
  def setMaxPostSize(maxPostSize: Int) {
    if (maxPostSize > 0) {
      this.maxPostSize = maxPostSize
    }
  }

  def setI18n(i18nResourceBaseName: String, defaultLocale: Locale, i18nMaxAgeOfCookie: Int) {
    this.i18nResourceBaseName = i18nResourceBaseName
    this.defaultLocale = defaultLocale
    this.i18nMaxAgeOfCookie = i18nMaxAgeOfCookie
  }

  def setI18n(i18nResourceBaseName: String) {
    this.i18nResourceBaseName = i18nResourceBaseName
  }

  def getI18nResourceBaseName: String = {
    i18nResourceBaseName
  }

  def getI18nDefaultLocale: Locale = {
    defaultLocale
  }

  def getI18nMaxAgeOfCookie: Int = {
    this.i18nMaxAgeOfCookie
  }

  /**
   * FreeMarker template update delay for not devMode.
   */
  def setFreeMarkerTemplateUpdateDelay(delayInSeconds: Int) {
    if (delayInSeconds < 0) throw new IllegalArgumentException("template_update_delay must more than -1.")
    this.freeMarkerTemplateUpdateDelay = delayInSeconds
  }

  def getFreeMarkerTemplateUpdateDelay: Int = {
    freeMarkerTemplateUpdateDelay
  }

  /**
   * Set the base path for all views
   */
  def setBaseViewPath(baseViewPath: String) {
    Routes.setBaseViewPath(baseViewPath)
  }

  /**
   * Set the mainRenderFactory then your can use your custom render in controller as render(String).
   */
  def setMainRenderFactory(mainRenderFactory: IMainRenderFactory) {
    if (mainRenderFactory == null) throw new IllegalArgumentException("mainRenderFactory can not be null.")
    this.viewType = ViewType.OTHER
    RenderFactory.setMainRenderFactory(mainRenderFactory)
  }

  def setLogger(logger: Logger) {
    if (logger == null) throw new IllegalArgumentException("loggerFactory can not be null.")
    LoggerFactory.logger = logger
  }

  def setErrorRenderFactory(errorRenderFactory: IErrorRenderFactory) {
    if (errorRenderFactory == null) throw new IllegalArgumentException("errorRenderFactory can not be null.")
    RenderFactory.setErrorRenderFactory(errorRenderFactory)
  }

  private var fileRenderPath: String = null
  private var uploadedFileSaveDirectory: String = null
  private var devMode: Boolean = false
  private var encoding: String = Const.DEFAULT_ENCODING
  private var urlParaSeparator: String = Const.DEFAULT_URL_PARA_SEPARATOR
  private var viewType: ViewType = Const.DEFAULT_VIEW_TYPE
  private var jspViewExtension: String = Const.DEFAULT_JSP_EXTENSION
  private var freeMarkerViewExtension: String = Const.DEFAULT_FREE_MARKER_EXTENSION
  private var velocityViewExtension: String = Const.DEFAULT_VELOCITY_EXTENSION
  private var maxPostSize: Int = Const.DEFAULT_MAX_POST_SIZE
  private var freeMarkerTemplateUpdateDelay: Int = Const.DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY
  private var tokenCache: ITokenCache = null
  private var errorViewMapping: Map[Int, String] = new HashMap[Int, String]
  private var i18nResourceBaseName: String = null
  private var defaultLocale: Locale = null
  private var i18nMaxAgeOfCookie: Int = null
}









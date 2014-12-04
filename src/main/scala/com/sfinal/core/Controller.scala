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
import java.text.ParseException
import java.util.Date
import java.util.Enumeration
import java.util.List
import java.util.Locale
import java.util.Map
import java.util.Map.Entry
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import com.sfinal.core.Const.I18N_LOCALE
import com.sfinal.core.{TypeConverter, ModelInjector}
import com.sfinal.i18n.I18N
import com.sfinal.i18n.I18N
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit
import com.sfinal.render.ContentType
import com.sfinal.render.Render
import com.sfinal.render.Render
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory
import com.sfinal.upload.MultipartRequest
import com.sfinal.upload.MultipartRequest
import com.sfinal.upload.UploadFile
import com.sfinal.upload.UploadFile

/**
 * Controller
 * <br>
 * 昨夜西风凋碧树。独上高楼，望尽天涯路。<br>
 * 衣带渐宽终不悔，为伊消得人憔悴。<br>
 * 众里寻她千百度，蓦然回首，那人却在灯火阑珊处。
 */
@SuppressWarnings(Array("unchecked", "rawtypes")) object Controller {
  private final val NULL_URL_PARA_ARRAY: Array[String] = new Array[String](0)
  private final val URL_PARA_SEPARATOR: String = Config.getConstants.getUrlParaSeparator
  private final val renderFactory: RenderFactory = RenderFactory.me
}

@SuppressWarnings(Array("unchecked", "rawtypes")) abstract class Controller {
  private[core] def init(request: HttpServletRequest, response: HttpServletResponse, urlPara: String) {
    this.request = request
    this.response = response
    this.urlPara = urlPara
  }

  def setUrlPara(urlPara: String) {
    this.urlPara = urlPara
    this.urlParaArray = null
  }

  /**
   * Stores an attribute in this request
   * @param name a String specifying the name of the attribute
   * @param value the Object to be stored
   */
  def setAttr(name: String, value: AnyRef): Controller = {
    request.setAttribute(name, value)
    return this
  }

  /**
   * Removes an attribute from this request
   * @param name a String specifying the name of the attribute to remove
   */
  def removeAttr(name: String): Controller = {
    request.removeAttribute(name)
    return this
  }

  /**
   * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
   * @param attrMap key and value as attribute of the map to be stored
   */
  def setAttrs(attrMap: Map[String, AnyRef]): Controller = {
    import scala.collection.JavaConversions._
    for (entry <- attrMap.entrySet) request.setAttribute(entry.getKey, entry.getValue)
    return this
  }

  /**
   * Returns the value of a request parameter as a String, or null if the parameter does not exist.
   * <p>
   * You should only use this method when you are sure the parameter has only one value. If the
   * parameter might have more than one value, use getParaValues(java.lang.String).
   * <p>
   * If you use this method with a multivalued parameter, the value returned is equal to the first
   * value in the array returned by getParameterValues.
   * @param name a String specifying the name of the parameter
   * @return a String representing the single value of the parameter
   */
  def getPara(name: String): String = {
    return request.getParameter(name)
  }

  /**
   * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
   * @param name a String specifying the name of the parameter
   * @param defaultValue a String value be returned when the value of parameter is null
   * @return a String representing the single value of the parameter
   */
  def getPara(name: String, defaultValue: String): String = {
    val result: String = request.getParameter(name)
    return if (result != null && !("" == result)) result else defaultValue
  }

  /**
   * Returns the values of the request parameters as a Map.
   * @return a Map contains all the parameters name and value
   */
  def getParaMap: Map[String, Array[String]] = {
    return request.getParameterMap
  }

  /**
   * Returns an Enumeration of String objects containing the names of the parameters
   * contained in this request. If the request has no parameters, the method returns
   * an empty Enumeration.
   * @return an Enumeration of String objects, each String containing the name of
   *         a request parameter; or an empty Enumeration if the request has no parameters
   */
  def getParaNames: Enumeration[String] = {
    return request.getParameterNames
  }

  /**
   * Returns an array of String objects containing all of the values the given request
   * parameter has, or null if the parameter does not exist. If the parameter has a
   * single value, the array has a length of 1.
   * @param name a String containing the name of the parameter whose value is requested
   * @return an array of String objects containing the parameter's values
   */
  def getParaValues(name: String): Array[String] = {
    return request.getParameterValues(name)
  }

  /**
   * Returns an array of Integer objects containing all of the values the given request
   * parameter has, or null if the parameter does not exist. If the parameter has a
   * single value, the array has a length of 1.
   * @param name a String containing the name of the parameter whose value is requested
   * @return an array of Integer objects containing the parameter's values
   */
  def getParaValuesToInt(name: String): Array[Integer] = {
    val values: Array[String] = request.getParameterValues(name)
    if (values == null) return null
    val result: Array[Integer] = new Array[Integer](values.length)
    {
      var i: Int = 0
      while (i < result.length) {
        result(i) = Integer.parseInt(values(i))
        ({
          i += 1; i - 1
        })
      }
    }
    return result
  }

  /**
   * Returns an Enumeration containing the names of the attributes available to this request.
   * This method returns an empty Enumeration if the request has no attributes available to it.
   * @return an Enumeration of strings containing the names of the request's attributes
   */
  def getAttrNames: Enumeration[String] = {
    return request.getAttributeNames
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttr(name: String): T = {
    return request.getAttribute(name).asInstanceOf[T]
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an String Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttrForStr(name: String): String = {
    return request.getAttribute(name).asInstanceOf[String]
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttrForInt(name: String): Integer = {
    return request.getAttribute(name).asInstanceOf[Integer]
  }

  private def toInt(value: String, defaultValue: Integer): Integer = {
    if (value == null || ("" == value.trim)) return defaultValue
    if (value.startsWith("N") || value.startsWith("n")) return -Integer.parseInt(value.substring(1))
    return Integer.parseInt(value)
  }

  /**
   * Returns the value of a request parameter and convert to Integer.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToInt(name: String): Integer = {
    return toInt(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Integer with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToInt(name: String, defaultValue: Integer): Integer = {
    return toInt(request.getParameter(name), defaultValue)
  }

  private def toLong(value: String, defaultValue: Long): Long = {
    if (value == null || ("" == value.trim)) return defaultValue
    if (value.startsWith("N") || value.startsWith("n")) return -Long.parseLong(value.substring(1))
    return Long.parseLong(value)
  }

  /**
   * Returns the value of a request parameter and convert to Long.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToLong(name: String): Long = {
    return toLong(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Long with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToLong(name: String, defaultValue: Long): Long = {
    return toLong(request.getParameter(name), defaultValue)
  }

  private def toBoolean(value: String, defaultValue: Boolean): Boolean = {
    if (value == null || ("" == value.trim)) return defaultValue
    value = value.trim.toLowerCase
    if (("1" == value) || ("true" == value)) return Boolean.TRUE
    else if (("0" == value) || ("false" == value)) return Boolean.FALSE
    throw new RuntimeException("Can not parse the parameter \"" + value + "\" to boolean value.")
  }

  /**
   * Returns the value of a request parameter and convert to Boolean.
   * @param name a String specifying the name of the parameter
   * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
   */
  def getParaToBoolean(name: String): Boolean = {
    return toBoolean(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
   */
  def getParaToBoolean(name: String, defaultValue: Boolean): Boolean = {
    return toBoolean(request.getParameter(name), defaultValue)
  }

  /**
   * Get all para from url and convert to Boolean
   */
  def getParaToBoolean: Boolean = {
    return toBoolean(getPara, null)
  }

  /**
   * Get para from url and conver to Boolean. The first index is 0
   */
  def getParaToBoolean(index: Int): Boolean = {
    return toBoolean(getPara(index), null)
  }

  /**
   * Get para from url and conver to Boolean with default value if it is null.
   */
  def getParaToBoolean(index: Int, defaultValue: Boolean): Boolean = {
    return toBoolean(getPara(index), defaultValue)
  }

  private def toDate(value: String, defaultValue: Date): Date = {
    if (value == null || ("" == value.trim)) return defaultValue
    try {
      return new SimpleDateFormat("yyyy-MM-dd").parse(value)
    }
    catch {
      case e: ParseException => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Returns the value of a request parameter and convert to Date.
   * @param name a String specifying the name of the parameter
   * @return a Date representing the single value of the parameter
   */
  def getParaToDate(name: String): Date = {
    return toDate(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Date with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Date representing the single value of the parameter
   */
  def getParaToDate(name: String, defaultValue: Date): Date = {
    return toDate(request.getParameter(name), defaultValue)
  }

  /**
   * Get all para from url and convert to Date
   */
  def getParaToDate: Date = {
    return toDate(getPara, null)
  }

  /**
   * Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller
   */
  def getRequest: HttpServletRequest = {
    return request
  }

  /**
   * Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller
   */
  def getResponse: HttpServletResponse = {
    return response
  }

  /**
   * Return HttpSession.
   */
  def getSession: HttpSession = {
    return request.getSession
  }

  /**
   * Return HttpSession.
   * @param create a boolean specifying create HttpSession if it not exists
   */
  def getSession(create: Boolean): HttpSession = {
    return request.getSession(create)
  }

  /**
   * Return a Object from session.
   * @param key a String specifying the key of the Object stored in session
   */
  def getSessionAttr(key: String): T = {
    val session: HttpSession = request.getSession(false)
    return if (session != null) session.getAttribute(key).asInstanceOf[T] else null
  }

  /**
   * Store Object to session.
   * @param key a String specifying the key of the Object stored in session
   * @param value a Object specifying the value stored in session
   */
  def setSessionAttr(key: String, value: AnyRef): Controller = {
    request.getSession.setAttribute(key, value)
    return this
  }

  /**
   * Remove Object in session.
   * @param key a String specifying the key of the Object stored in session
   */
  def removeSessionAttr(key: String): Controller = {
    val session: HttpSession = request.getSession(false)
    if (session != null) session.removeAttribute(key)
    return this
  }

  /**
   * Get cookie value by cookie name.
   */
  def getCookie(name: String, defaultValue: String): String = {
    val cookie: Cookie = getCookieObject(name)
    return if (cookie != null) cookie.getValue else defaultValue
  }

  /**
   * Get cookie value by cookie name.
   */
  def getCookie(name: String): String = {
    return getCookie(name, null)
  }

  /**
   * Get cookie value by cookie name and convert to Integer.
   */
  def getCookieToInt(name: String): Integer = {
    val result: String = getCookie(name)
    return if (result != null) Integer.parseInt(result) else null
  }

  /**
   * Get cookie value by cookie name and convert to Integer.
   */
  def getCookieToInt(name: String, defaultValue: Integer): Integer = {
    val result: String = getCookie(name)
    return if (result != null) Integer.parseInt(result) else defaultValue
  }

  /**
   * Get cookie value by cookie name and convert to Long.
   */
  def getCookieToLong(name: String): Long = {
    val result: String = getCookie(name)
    return if (result != null) Long.parseLong(result) else null
  }

  /**
   * Get cookie value by cookie name and convert to Long.
   */
  def getCookieToLong(name: String, defaultValue: Long): Long = {
    val result: String = getCookie(name)
    return if (result != null) Long.parseLong(result) else defaultValue
  }

  /**
   * Get cookie object by cookie name.
   */
  def getCookieObject(name: String): Cookie = {
    val cookies: Array[Cookie] = request.getCookies
    if (cookies != null) for (cookie <- cookies) if (cookie.getName == name) return cookie
    return null
  }

  /**
   * Get all cookie objects.
   */
  def getCookieObjects: Array[Cookie] = {
    val result: Array[Cookie] = request.getCookies
    return if (result != null) result else new Array[Cookie](0)
  }

  /**
   * Set Cookie to response.
   */
  def setCookie(cookie: Cookie): Controller = {
    response.addCookie(cookie)
    return this
  }

  /**
   * Set Cookie to response.
   * @param name cookie name
   * @param value cookie value
   * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
   * @param path see Cookie.setPath(String)
   */
  def setCookie(name: String, value: String, maxAgeInSeconds: Int, path: String): Controller = {
    setCookie(name, value, maxAgeInSeconds, path, null)
    return this
  }

  /**
   * Set Cookie to response.
   * @param name cookie name
   * @param value cookie value
   * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
   * @param path see Cookie.setPath(String)
   * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
   */
  def setCookie(name: String, value: String, maxAgeInSeconds: Int, path: String, domain: String): Controller = {
    val cookie: Cookie = new Cookie(name, value)
    if (domain != null) cookie.setDomain(domain)
    cookie.setMaxAge(maxAgeInSeconds)
    cookie.setPath(path)
    response.addCookie(cookie)
    return this
  }

  /**
   * Set Cookie with path = "/".
   */
  def setCookie(name: String, value: String, maxAgeInSeconds: Int): Controller = {
    setCookie(name, value, maxAgeInSeconds, "/", null)
    return this
  }

  /**
   * Remove Cookie with path = "/".
   */
  def removeCookie(name: String): Controller = {
    setCookie(name, null, 0, "/", null)
    return this
  }

  /**
   * Remove Cookie.
   */
  def removeCookie(name: String, path: String): Controller = {
    setCookie(name, null, 0, path, null)
    return this
  }

  /**
   * Remove Cookie.
   */
  def removeCookie(name: String, path: String, domain: String): Controller = {
    setCookie(name, null, 0, path, domain)
    return this
  }

  /**
   * Get all para with separator char from url
   */
  def getPara: String = {
    if ("" == urlPara) urlPara = null
    return urlPara
  }

  /**
   * Get para from url. The index of first url para is 0.
   */
  def getPara(index: Int): String = {
    if (index < 0) return getPara
    if (urlParaArray == null) {
      if (urlPara == null || ("" == urlPara)) urlParaArray = NULL_URL_PARA_ARRAY
      else urlParaArray = urlPara.split(URL_PARA_SEPARATOR)
      {
        var i: Int = 0
        while (i < urlParaArray.length) {
          if ("" == urlParaArray(i)) urlParaArray(i) = null
          ({
            i += 1; i - 1
          })
        }
      }
    }
    return if (urlParaArray.length > index) urlParaArray(index) else null
  }

  /**
   * Get para from url with default value if it is null or "".
   */
  def getPara(index: Int, defaultValue: String): String = {
    val result: String = getPara(index)
    return if (result != null && !("" == result)) result else defaultValue
  }

  /**
   * Get para from url and conver to Integer. The first index is 0
   */
  def getParaToInt(index: Int): Integer = {
    return toInt(getPara(index), null)
  }

  /**
   * Get para from url and conver to Integer with default value if it is null.
   */
  def getParaToInt(index: Int, defaultValue: Integer): Integer = {
    return toInt(getPara(index), defaultValue)
  }

  /**
   * Get para from url and conver to Long.
   */
  def getParaToLong(index: Int): Long = {
    return toLong(getPara(index), null)
  }

  /**
   * Get para from url and conver to Long with default value if it is null.
   */
  def getParaToLong(index: Int, defaultValue: Long): Long = {
    return toLong(getPara(index), defaultValue)
  }

  /**
   * Get all para from url and convert to Integer
   */
  def getParaToInt: Integer = {
    return toInt(getPara, null)
  }

  /**
   * Get all para from url and convert to Long
   */
  def getParaToLong: Long = {
    return toLong(getPara, null)
  }

  /**
   * Get model from http request.
   */
  def getModel(modelClass: Class[T]): T = {
    return ModelInjector.inject(modelClass, request, false).asInstanceOf[T]
  }

  /**
   * Get model from http request.
   */
  def getModel(modelClass: Class[T], modelName: String): T = {
    return ModelInjector.inject(modelClass, modelName, request, false).asInstanceOf[T]
  }

  /**
   * Get upload file from multipart request.
   */
  def getFiles(saveDirectory: String, maxPostSize: Integer, encoding: String): List[UploadFile] = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory, maxPostSize, encoding)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: String, saveDirectory: String, maxPostSize: Integer, encoding: String): UploadFile = {
    getFiles(saveDirectory, maxPostSize, encoding)
    return getFile(parameterName)
  }

  def getFiles(saveDirectory: String, maxPostSize: Int): List[UploadFile] = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory, maxPostSize)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: String, saveDirectory: String, maxPostSize: Int): UploadFile = {
    getFiles(saveDirectory, maxPostSize)
    return getFile(parameterName)
  }

  def getFiles(saveDirectory: String): List[UploadFile] = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: String, saveDirectory: String): UploadFile = {
    getFiles(saveDirectory)
    return getFile(parameterName)
  }

  def getFiles: List[UploadFile] = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile: UploadFile = {
    val uploadFiles: List[UploadFile] = getFiles
    return if (uploadFiles.size > 0) uploadFiles.get(0) else null
  }

  def getFile(parameterName: String): UploadFile = {
    val uploadFiles: List[UploadFile] = getFiles
    import scala.collection.JavaConversions._
    for (uploadFile <- uploadFiles) {
      if (uploadFile.getParameterName == parameterName) {
        return uploadFile
      }
    }
    return null
  }

  /**
   * Write Local to cookie
   */
  def setLocaleToCookie(locale: Locale): Controller = {
    setCookie(I18N_LOCALE, locale.toString, I18N.getI18nMaxAgeOfCookie)
    return this
  }

  def setLocaleToCookie(locale: Locale, maxAge: Int): Controller = {
    setCookie(I18N_LOCALE, locale.toString, maxAge)
    return this
  }

  def getText(key: String): String = {
    return I18N.getText(key, getLocaleFromCookie)
  }

  def getText(key: String, defaultValue: String): String = {
    return I18N.getText(key, defaultValue, getLocaleFromCookie)
  }

  private def getLocaleFromCookie: Locale = {
    val cookie: Cookie = getCookieObject(I18N_LOCALE)
    if (cookie != null) {
      return I18N.localeFromString(cookie.getValue)
    }
    else {
      val defaultLocale: Locale = I18N.getDefaultLocale
      setLocaleToCookie(defaultLocale)
      return I18N.localeFromString(defaultLocale.toString)
    }
  }

  /**
   * Keep all parameter's value except model value
   */
  def keepPara: Controller = {
    val map: Map[String, Array[String]] = request.getParameterMap
    import scala.collection.JavaConversions._
    for (e <- map.entrySet) {
      val values: Array[String] = e.getValue
      if (values.length == 1) request.setAttribute(e.getKey, values(0))
      else request.setAttribute(e.getKey, values)
    }
    return this
  }

  /**
   * Keep parameter's value names pointed, model value can not be kept
   */
  def keepPara(names: String*): Controller = {
    for (name <- names) {
      val values: Array[String] = request.getParameterValues(name)
      if (values != null) {
        if (values.length == 1) request.setAttribute(name, values(0))
        else request.setAttribute(name, values)
      }
    }
    return this
  }

  /**
   * Convert para to special type and keep it
   */
  def keepPara(`type`: Class[_], name: String): Controller = {
    val values: Array[String] = request.getParameterValues(name)
    if (values != null) {
      if (values.length == 1) try {
        request.setAttribute(name, TypeConverter.convert(`type`, values(0)))
      }
      catch {
        case e: ParseException => {
        }
      }
      else request.setAttribute(name, values)
    }
    return this
  }

  def keepPara(`type`: Class[_], names: String*): Controller = {
    if (`type` eq classOf[String]) return keepPara(names)
    if (names != null) for (name <- names) keepPara(`type`, name)
    return this
  }

  def keepModel(modelClass: Class[_], modelName: String): Controller = {
    val model: AnyRef = ModelInjector.inject(modelClass, modelName, request, true)
    request.setAttribute(modelName, model)
    return this
  }

  def keepModel(modelClass: Class[_]): Controller = {
    val modelName: String = StrKit.firstCharToLowerCase(modelClass.getSimpleName)
    keepModel(modelClass, modelName)
    return this
  }

  /**
   * Create a token.
   * @param tokenName the token name used in view
   * @param secondsOfTimeOut the seconds of time out, secondsOfTimeOut >= Const.MIN_SECONDS_OF_TOKEN_TIME_OUT
   */
  def createToken(tokenName: String, secondsOfTimeOut: Int) {
    com.jfinal.token.TokenManager.createToken(this, tokenName, secondsOfTimeOut)
  }

  /**
   * Create a token with default token name and with default seconds of time out.
   */
  def createToken {
    createToken(Const.DEFAULT_TOKEN_NAME, Const.DEFAULT_SECONDS_OF_TOKEN_TIME_OUT)
  }

  /**
   * Create a token with default seconds of time out.
   * @param tokenName the token name used in view
   */
  def createToken(tokenName: String) {
    createToken(tokenName, Const.DEFAULT_SECONDS_OF_TOKEN_TIME_OUT)
  }

  /**
   * Check token to prevent resubmit.
   * @param tokenName the token name used in view's form
   * @return true if token is correct
   */
  def validateToken(tokenName: String): Boolean = {
    return com.jfinal.token.TokenManager.validateToken(this, tokenName)
  }

  /**
   * Check token to prevent resubmit  with default token key ---> "JFINAL_TOKEN_KEY"
   * @return true if token is correct
   */
  def validateToken: Boolean = {
    return validateToken(Const.DEFAULT_TOKEN_NAME)
  }

  /**
   * Return true if the para value is blank otherwise return false
   */
  def isParaBlank(paraName: String): Boolean = {
    val value: String = request.getParameter(paraName)
    return value == null || value.trim.length == 0
  }

  /**
   * Return true if the urlPara value is blank otherwise return false
   */
  def isParaBlank(index: Int): Boolean = {
    val value: String = getPara(index)
    return value == null || value.trim.length == 0
  }

  /**
   * Return true if the para exists otherwise return false
   */
  def isParaExists(paraName: String): Boolean = {
    return request.getParameterMap.containsKey(paraName)
  }

  /**
   * Return true if the urlPara exists otherwise return false
   */
  def isParaExists(index: Int): Boolean = {
    return getPara(index) != null
  }

  def getRender: Render = {
    return render
  }

  /**
   * Render with any Render which extends Render
   */
  def render(render: Render) {
    this.render = render
  }

  /**
   * Render with view use default type Render configured in JFinalConfig
   */
  def render(view: String) {
    render = renderFactory.getRender(view)
  }

  /**
   * Render with jsp view
   */
  def renderJsp(view: String) {
    render = renderFactory.getJspRender(view)
  }

  /**
   * Render with freemarker view
   */
  def renderFreeMarker(view: String) {
    render = renderFactory.getFreeMarkerRender(view)
  }

  /**
   * Render with velocity view
   */
  def renderVelocity(view: String) {
    render = renderFactory.getVelocityRender(view)
  }

  /**
   * Render with json
   * <p>
   * Example:<br>
   * renderJson("message", "Save successful");<br>
   * renderJson("users", users);<br>
   */
  def renderJson(key: String, value: AnyRef) {
    render = renderFactory.getJsonRender(key, value)
  }

  /**
   * Render with json
   */
  def renderJson {
    render = renderFactory.getJsonRender
  }

  /**
   * Render with attributes set by setAttr(...) before.
   * <p>
   * Example: renderJson(new String[]{"blogList", "user"});
   */
  def renderJson(attrs: Array[String]) {
    render = renderFactory.getJsonRender(attrs)
  }

  /**
   * Render with json text.
   * <p>
   * Example: renderJson("{\"message\":\"Please input password!\"}");
   */
  def renderJson(jsonText: String) {
    render = renderFactory.getJsonRender(jsonText)
  }

  /**
   * Render json with object.
   * <p>
   * Example: renderJson(new User().set("name", "JFinal").set("age", 18));
   */
  def renderJson(`object`: AnyRef) {
    render = renderFactory.getJsonRender(`object`)
  }

  /**
   * Render with text. The contentType is: "text/plain".
   */
  def renderText(text: String) {
    render = renderFactory.getTextRender(text)
  }

  /**
   * Render with text and content type.
   * <p>
   * Example: renderText("&lt;user id='5888'&gt;James&lt;/user&gt;", "application/xml");
   */
  def renderText(text: String, contentType: String) {
    render = renderFactory.getTextRender(text, contentType)
  }

  /**
   * Render with text and ContentType.
   * <p>
   * Example: renderText("&lt;html&gt;Hello James&lt;/html&gt;", ContentType.HTML);
   */
  def renderText(text: String, contentType: ContentType) {
    render = renderFactory.getTextRender(text, contentType)
  }

  /**
   * Forward to an action
   */
  def forwardAction(actionUrl: String) {
    render = new ActionRender(actionUrl)
  }

  /**
   * Render with file
   */
  def renderFile(fileName: String) {
    render = renderFactory.getFileRender(fileName)
  }

  /**
   * Render with file
   */
  def renderFile(file: File) {
    render = renderFactory.getFileRender(file)
  }

  /**
   * Redirect to url
   */
  def redirect(url: String) {
    render = renderFactory.getRedirectRender(url)
  }

  /**
   * Redirect to url
   */
  def redirect(url: String, withQueryString: Boolean) {
    render = renderFactory.getRedirectRender(url, withQueryString)
  }

  /**
   * Render with view and status use default type Render configured in JFinalConfig
   */
  def render(view: String, status: Int) {
    render = renderFactory.getRender(view)
    response.setStatus(status)
  }

  /**
   * Render with url and 301 status
   */
  def redirect301(url: String) {
    render = renderFactory.getRedirect301Render(url)
  }

  /**
   * Render with url and 301 status
   */
  def redirect301(url: String, withQueryString: Boolean) {
    render = renderFactory.getRedirect301Render(url, withQueryString)
  }

  /**
   * Render with view and errorCode status
   */
  def renderError(errorCode: Int, view: String) {
    throw new ActionException(errorCode, renderFactory.getErrorRender(errorCode, view))
  }

  /**
   * Render with render and errorCode status
   */
  def renderError(errorCode: Int, render: Render) {
    throw new ActionException(errorCode, render)
  }

  /**
   * Render with view and errorCode status configured in JFinalConfig
   */
  def renderError(errorCode: Int) {
    throw new ActionException(errorCode, renderFactory.getErrorRender(errorCode))
  }

  /**
   * Render nothing, no response to browser
   */
  def renderNull {
    render = renderFactory.getNullRender
  }

  /**
   * Render with javascript text. The contentType is: "text/javascript".
   */
  def renderJavascript(javascriptText: String) {
    render = renderFactory.getJavascriptRender(javascriptText)
  }

  /**
   * Render with html text. The contentType is: "text/html".
   */
  def renderHtml(htmlText: String) {
    render = renderFactory.getHtmlRender(htmlText)
  }

  /**
   * Render with xml view using freemarker.
   */
  def renderXml(view: String) {
    render = renderFactory.getXmlRender(view)
  }

  private var request: HttpServletRequest = null
  private var response: HttpServletResponse = null
  private var urlPara: String = null
  private var urlParaArray: Array[String] = null
  /**
   * Hold Render object when invoke renderXxx(...)
   */
  private var render: Render = null
}




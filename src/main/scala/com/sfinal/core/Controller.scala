package com.sfinal.core

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

/**
 * Created by ice on 14-12-3.
 */
trait Controller {
  private var request: HttpServletRequest = null
  private var response: HttpServletResponse = null
  private var urlPara: String = null
  private var urlParaArray: Array[String] = null
  private val NULL_URL_PARA_ARRAY: Array[String] = new Array[String](0)
  private val URL_PARA_SEPARATOR: Nothing = Config.getConstants.getUrlParaSeparator

  private[core] def init(request: Nothing, response: Nothing, urlPara: Nothing) {
    this.request = request
    this.response = response
    this.urlPara = urlPara
  }

  def setUrlPara(urlPara: Nothing) {
    this.urlPara = urlPara
    this.urlParaArray = null
  }

  /**
   * Stores an attribute in this request
   * @param name a String specifying the name of the attribute
   * @param value the Object to be stored
   */
  def setAttr(name: Nothing, value: Nothing): Controller = {
    request.setAttribute(name, value)
    return this
  }

  /**
   * Removes an attribute from this request
   * @param name a String specifying the name of the attribute to remove
   */
  def removeAttr(name: Nothing): Controller = {
    request.removeAttribute(name)
    return this
  }

  /**
   * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
   * @param attrMap key and value as attribute of the map to be stored
   */
  def setAttrs(attrMap: Nothing): Controller = {
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
  def getPara(name: Nothing): Nothing = {
    return request.getParameter(name)
  }

  /**
   * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
   * @param name a String specifying the name of the parameter
   * @param defaultValue a String value be returned when the value of parameter is null
   * @return a String representing the single value of the parameter
   */
  def getPara(name: Nothing, defaultValue: Nothing): Nothing = {
    val result: Nothing = request.getParameter(name)
    return if (result != null && !("" == result)) result else defaultValue
  }

  /**
   * Returns the values of the request parameters as a Map.
   * @return a Map contains all the parameters name and value
   */
  def getParaMap: Nothing = {
    return request.getParameterMap
  }

  /**
   * Returns an Enumeration of String objects containing the names of the parameters
   * contained in this request. If the request has no parameters, the method returns
   * an empty Enumeration.
   * @return an Enumeration of String objects, each String containing the name of
   *         a request parameter; or an empty Enumeration if the request has no parameters
   */
  def getParaNames: Nothing = {
    return request.getParameterNames
  }

  /**
   * Returns an array of String objects containing all of the values the given request
   * parameter has, or null if the parameter does not exist. If the parameter has a
   * single value, the array has a length of 1.
   * @param name a String containing the name of the parameter whose value is requested
   * @return an array of String objects containing the parameter's values
   */
  def getParaValues(name: Nothing): Array[Nothing] = {
    return request.getParameterValues(name)
  }

  /**
   * Returns an array of Integer objects containing all of the values the given request
   * parameter has, or null if the parameter does not exist. If the parameter has a
   * single value, the array has a length of 1.
   * @param name a String containing the name of the parameter whose value is requested
   * @return an array of Integer objects containing the parameter's values
   */
  def getParaValuesToInt(name: Nothing): Array[Nothing] = {
    val values: Array[Nothing] = request.getParameterValues(name)
    if (values == null) return null
    val result: Array[Nothing] = new Array[Nothing](values.length)
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
  def getAttrNames: Nothing = {
    return request.getAttributeNames
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttr[T](name: Nothing): T = {
    return request.getAttribute(name).asInstanceOf[T]
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an String Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttrForStr(name: Nothing): Nothing = {
    return request.getAttribute(name).asInstanceOf[Nothing]
  }

  /**
   * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
   * @param name a String specifying the name of the attribute
   * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
   */
  def getAttrForInt(name: Nothing): Nothing = {
    return request.getAttribute(name).asInstanceOf[Nothing]
  }

  private def toInt(value: Nothing, defaultValue: Nothing): Nothing = {
    if (value == null || ("" == value.trim)) return defaultValue
    if (value.startsWith("N") || value.startsWith("n")) return -Integer.parseInt(value.substring(1))
    return Integer.parseInt(value)
  }

  /**
   * Returns the value of a request parameter and convert to Integer.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToInt(name: Nothing): Nothing = {
    return toInt(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Integer with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToInt(name: Nothing, defaultValue: Nothing): Nothing = {
    return toInt(request.getParameter(name), defaultValue)
  }

  private def toLong(value: Nothing, defaultValue: Nothing): Nothing = {
    if (value == null || ("" == value.trim)) return defaultValue
    if (value.startsWith("N") || value.startsWith("n")) return -Long.parseLong(value.substring(1))
    return Long.parseLong(value)
  }

  /**
   * Returns the value of a request parameter and convert to Long.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToLong(name: Nothing): Nothing = {
    return toLong(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Long with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Integer representing the single value of the parameter
   */
  def getParaToLong(name: Nothing, defaultValue: Nothing): Nothing = {
    return toLong(request.getParameter(name), defaultValue)
  }

  private def toBoolean(value: Nothing, defaultValue: Nothing): Nothing = {
    if (value == null || ("" == value.trim)) return defaultValue
    value = value.trim.toLowerCase
    if (("1" == value) || ("true" == value)) return Boolean.TRUE
    else if (("0" == value) || ("false" == value)) return Boolean.FALSE
    throw new Nothing("Can not parse the parameter \"" + value + "\" to boolean value.")
  }

  /**
   * Returns the value of a request parameter and convert to Boolean.
   * @param name a String specifying the name of the parameter
   * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
   */
  def getParaToBoolean(name: Nothing): Nothing = {
    return toBoolean(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
   */
  def getParaToBoolean(name: Nothing, defaultValue: Nothing): Nothing = {
    return toBoolean(request.getParameter(name), defaultValue)
  }

  /**
   * Get all para from url and convert to Boolean
   */
  def getParaToBoolean: Nothing = {
    return toBoolean(getPara, null)
  }

  /**
   * Get para from url and conver to Boolean. The first index is 0
   */
  def getParaToBoolean(index: Int): Nothing = {
    return toBoolean(getPara(index), null)
  }

  /**
   * Get para from url and conver to Boolean with default value if it is null.
   */
  def getParaToBoolean(index: Int, defaultValue: Nothing): Nothing = {
    return toBoolean(getPara(index), defaultValue)
  }

  private def toDate(value: Nothing, defaultValue: Nothing): Nothing = {
    if (value == null || ("" == value.trim)) return defaultValue
    try {
      return new Nothing("yyyy-MM-dd").parse(value)
    }
    catch {
      case e: Nothing => {
        throw new Nothing(e)
      }
    }
  }

  /**
   * Returns the value of a request parameter and convert to Date.
   * @param name a String specifying the name of the parameter
   * @return a Date representing the single value of the parameter
   */
  def getParaToDate(name: Nothing): Nothing = {
    return toDate(request.getParameter(name), null)
  }

  /**
   * Returns the value of a request parameter and convert to Date with a default value if it is null.
   * @param name a String specifying the name of the parameter
   * @return a Date representing the single value of the parameter
   */
  def getParaToDate(name: Nothing, defaultValue: Nothing): Nothing = {
    return toDate(request.getParameter(name), defaultValue)
  }

  /**
   * Get all para from url and convert to Date
   */
  def getParaToDate: Nothing = {
    return toDate(getPara, null)
  }

  /**
   * Return HttpServletRequest. Do not use HttpServletRequest Object in constructor of Controller
   */
  def getRequest: Nothing = {
    return request
  }

  /**
   * Return HttpServletResponse. Do not use HttpServletResponse Object in constructor of Controller
   */
  def getResponse: Nothing = {
    return response
  }

  /**
   * Return HttpSession.
   */
  def getSession: Nothing = {
    return request.getSession
  }

  /**
   * Return HttpSession.
   * @param create a boolean specifying create HttpSession if it not exists
   */
  def getSession(create: Boolean): Nothing = {
    return request.getSession(create)
  }

  /**
   * Return a Object from session.
   * @param key a String specifying the key of the Object stored in session
   */
  def getSessionAttr[T](key: Nothing): T = {
    val session: Nothing = request.getSession(false)
    return if (session != null) session.getAttribute(key).asInstanceOf[T] else null
  }

  /**
   * Store Object to session.
   * @param key a String specifying the key of the Object stored in session
   * @param value a Object specifying the value stored in session
   */
  def setSessionAttr(key: Nothing, value: Nothing): Controller = {
    request.getSession.setAttribute(key, value)
    return this
  }

  /**
   * Remove Object in session.
   * @param key a String specifying the key of the Object stored in session
   */
  def removeSessionAttr(key: Nothing): Controller = {
    val session: Nothing = request.getSession(false)
    if (session != null) session.removeAttribute(key)
    return this
  }

  /**
   * Get cookie value by cookie name.
   */
  def getCookie(name: Nothing, defaultValue: Nothing): Nothing = {
    val cookie: Nothing = getCookieObject(name)
    return if (cookie != null) cookie.getValue else defaultValue
  }

  /**
   * Get cookie value by cookie name.
   */
  def getCookie(name: Nothing): Nothing = {
    return getCookie(name, null)
  }

  /**
   * Get cookie value by cookie name and convert to Integer.
   */
  def getCookieToInt(name: Nothing): Nothing = {
    val result: Nothing = getCookie(name)
    return if (result != null) Integer.parseInt(result) else null
  }

  /**
   * Get cookie value by cookie name and convert to Integer.
   */
  def getCookieToInt(name: Nothing, defaultValue: Nothing): Nothing = {
    val result: Nothing = getCookie(name)
    return if (result != null) Integer.parseInt(result) else defaultValue
  }

  /**
   * Get cookie value by cookie name and convert to Long.
   */
  def getCookieToLong(name: Nothing): Nothing = {
    val result: Nothing = getCookie(name)
    return if (result != null) Long.parseLong(result) else null
  }

  /**
   * Get cookie value by cookie name and convert to Long.
   */
  def getCookieToLong(name: Nothing, defaultValue: Nothing): Nothing = {
    val result: Nothing = getCookie(name)
    return if (result != null) Long.parseLong(result) else defaultValue
  }

  /**
   * Get cookie object by cookie name.
   */
  def getCookieObject(name: Nothing): Nothing = {
    val cookies: Array[Nothing] = request.getCookies
    if (cookies != null) for (cookie <- cookies) if (cookie.getName == name) return cookie
    return null
  }

  /**
   * Get all cookie objects.
   */
  def getCookieObjects: Array[Nothing] = {
    val result: Array[Nothing] = request.getCookies
    return if (result != null) result else new Array[Nothing](0)
  }

  /**
   * Set Cookie to response.
   */
  def setCookie(cookie: Nothing): Controller = {
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
  def setCookie(name: Nothing, value: Nothing, maxAgeInSeconds: Int, path: Nothing): Controller = {
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
  def setCookie(name: Nothing, value: Nothing, maxAgeInSeconds: Int, path: Nothing, domain: Nothing): Controller = {
    val cookie: Nothing = new Nothing(name, value)
    if (domain != null) cookie.setDomain(domain)
    cookie.setMaxAge(maxAgeInSeconds)
    cookie.setPath(path)
    response.addCookie(cookie)
    return this
  }

  /**
   * Set Cookie with path = "/".
   */
  def setCookie(name: Nothing, value: Nothing, maxAgeInSeconds: Int): Controller = {
    setCookie(name, value, maxAgeInSeconds, "/", null)
    return this
  }

  /**
   * Remove Cookie with path = "/".
   */
  def removeCookie(name: Nothing): Controller = {
    setCookie(name, null, 0, "/", null)
    return this
  }

  /**
   * Remove Cookie.
   */
  def removeCookie(name: Nothing, path: Nothing): Controller = {
    setCookie(name, null, 0, path, null)
    return this
  }

  /**
   * Remove Cookie.
   */
  def removeCookie(name: Nothing, path: Nothing, domain: Nothing): Controller = {
    setCookie(name, null, 0, path, domain)
    return this
  }

  // --------
  /**
   * Get all para with separator char from url
   */
  def getPara: Nothing = {
    if ("" == urlPara) urlPara = null
    return urlPara
  }

  /**
   * Get para from url. The index of first url para is 0.
   */
  def getPara(index: Int): Nothing = {
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
  def getPara(index: Int, defaultValue: Nothing): Nothing = {
    val result: Nothing = getPara(index)
    return if (result != null && !("" == result)) result else defaultValue
  }

  /**
   * Get para from url and conver to Integer. The first index is 0
   */
  def getParaToInt(index: Int): Nothing = {
    return toInt(getPara(index), null)
  }

  /**
   * Get para from url and conver to Integer with default value if it is null.
   */
  def getParaToInt(index: Int, defaultValue: Nothing): Nothing = {
    return toInt(getPara(index), defaultValue)
  }

  /**
   * Get para from url and conver to Long.
   */
  def getParaToLong(index: Int): Nothing = {
    return toLong(getPara(index), null)
  }

  /**
   * Get para from url and conver to Long with default value if it is null.
   */
  def getParaToLong(index: Int, defaultValue: Nothing): Nothing = {
    return toLong(getPara(index), defaultValue)
  }

  /**
   * Get all para from url and convert to Integer
   */
  def getParaToInt: Nothing = {
    return toInt(getPara, null)
  }

  /**
   * Get all para from url and convert to Long
   */
  def getParaToLong: Nothing = {
    return toLong(getPara, null)
  }

  /**
   * Get model from http request.
   */
  def getModel[T](modelClass: Nothing): T = {
    return ModelInjector.inject(modelClass, request, false).asInstanceOf[T]
  }

  /**
   * Get model from http request.
   */
  def getModel[T](modelClass: Nothing, modelName: Nothing): T = {
    return ModelInjector.inject(modelClass, modelName, request, false).asInstanceOf[T]
  }

  // TODO public <T> List<T> getModels(Class<T> modelClass, String modelName) {}
  // --------
  /**
   * Get upload file from multipart request.
   */
  def getFiles(saveDirectory: Nothing, maxPostSize: Nothing, encoding: Nothing): Nothing = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory, maxPostSize, encoding)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: Nothing, saveDirectory: Nothing, maxPostSize: Nothing, encoding: Nothing): UploadFile = {
    getFiles(saveDirectory, maxPostSize, encoding)
    return getFile(parameterName)
  }

  def getFiles(saveDirectory: Nothing, maxPostSize: Int): Nothing = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory, maxPostSize)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: Nothing, saveDirectory: Nothing, maxPostSize: Int): UploadFile = {
    getFiles(saveDirectory, maxPostSize)
    return getFile(parameterName)
  }

  def getFiles(saveDirectory: Nothing): Nothing = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request, saveDirectory)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile(parameterName: Nothing, saveDirectory: Nothing): UploadFile = {
    getFiles(saveDirectory)
    return getFile(parameterName)
  }

  def getFiles: Nothing = {
    if (request.isInstanceOf[MultipartRequest] == false) request = new MultipartRequest(request)
    return (request.asInstanceOf[MultipartRequest]).getFiles
  }

  def getFile: UploadFile = {
    val uploadFiles: Nothing = getFiles
    return if (uploadFiles.size > 0) uploadFiles.get(0) else null
  }

  def getFile(parameterName: Nothing): UploadFile = {
    val uploadFiles: Nothing = getFiles
    import scala.collection.JavaConversions._
    for (uploadFile <- uploadFiles) {
      if (uploadFile.getParameterName == parameterName) {
        return uploadFile
      }
    }
    return null
  }

  // i18n features --------
  /**
   * Write Local to cookie
   */
  def setLocaleToCookie(locale: Nothing): Controller = {
    setCookie(I18N_LOCALE, locale.toString, I18N.getI18nMaxAgeOfCookie)
    return this
  }

  def setLocaleToCookie(locale: Nothing, maxAge: Int): Controller = {
    setCookie(I18N_LOCALE, locale.toString, maxAge)
    return this
  }

  def getText(key: Nothing): Nothing = {
    return I18N.getText(key, getLocaleFromCookie)
  }

  def getText(key: Nothing, defaultValue: Nothing): Nothing = {
    return I18N.getText(key, defaultValue, getLocaleFromCookie)
  }

  private def getLocaleFromCookie: Nothing = {
    val cookie: Nothing = getCookieObject(I18N_LOCALE)
    if (cookie != null) {
      return I18N.localeFromString(cookie.getValue)
    }
    else {
      val defaultLocale: Nothing = I18N.getDefaultLocale
      setLocaleToCookie(defaultLocale)
      return I18N.localeFromString(defaultLocale.toString)
    }
  }

  /**
   * Keep all parameter's value except model value
   */
  def keepPara: Controller = {
    val map: Nothing = request.getParameterMap
    import scala.collection.JavaConversions._
    for (e <- map.entrySet) {
      val values: Array[Nothing] = e.getValue
      if (values.length == 1) request.setAttribute(e.getKey, values(0))
      else request.setAttribute(e.getKey, values)
    }
    return this
  }

  /**
   * Keep parameter's value names pointed, model value can not be kept
   */
  def keepPara(names: Nothing*): Controller = {
    for (name <- names) {
      val values: Array[Nothing] = request.getParameterValues(name)
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
  def keepPara(`type`: Nothing, name: Nothing): Controller = {
    val values: Array[Nothing] = request.getParameterValues(name)
    if (values != null) {
      if (values.length == 1) try {
        request.setAttribute(name, TypeConverter.convert(`type`, values(0)))
      }
      catch {
        case e: Nothing => {
        }
      }
      else request.setAttribute(name, values)
    }
    return this
  }

  def keepPara(`type`: Nothing, names: Nothing*): Controller = {
    if (`type` eq classOf[Nothing]) return keepPara(names)
    if (names != null) for (name <- names) keepPara(`type`, name)
    return this
  }

  def keepModel(modelClass: Nothing, modelName: Nothing): Controller = {
    val model: Nothing = ModelInjector.inject(modelClass, modelName, request, true)
    request.setAttribute(modelName, model)
    return this
  }

  def keepModel(modelClass: Nothing): Controller = {
    val modelName: Nothing = StrKit.firstCharToLowerCase(modelClass.getSimpleName)
    keepModel(modelClass, modelName)
    return this
  }

  /**
   * Create a token.
   * @param tokenName the token name used in view
   * @param secondsOfTimeOut the seconds of time out, secondsOfTimeOut >= Const.MIN_SECONDS_OF_TOKEN_TIME_OUT
   */
  def createToken(tokenName: Nothing, secondsOfTimeOut: Int) {
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
  def createToken(tokenName: Nothing) {
    createToken(tokenName, Const.DEFAULT_SECONDS_OF_TOKEN_TIME_OUT)
  }

  /**
   * Check token to prevent resubmit.
   * @param tokenName the token name used in view's form
   * @return true if token is correct
   */
  def validateToken(tokenName: Nothing): Boolean = {
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
  def isParaBlank(paraName: Nothing): Boolean = {
    val value: Nothing = request.getParameter(paraName)
    return value == null || value.trim.length eq 0
  }

  /**
   * Return true if the urlPara value is blank otherwise return false
   */
  def isParaBlank(index: Int): Boolean = {
    val value: Nothing = getPara(index)
    return value == null || value.trim.length eq 0
  }

  /**
   * Return true if the para exists otherwise return false
   */
  def isParaExists(paraName: Nothing): Boolean = {
    return request.getParameterMap.containsKey(paraName)
  }

  /**
   * Return true if the urlPara exists otherwise return false
   */
  def isParaExists(index: Int): Boolean = {
    return getPara(index) != null
  }

  private val renderFactory: RenderFactory = RenderFactory.me
  /**
   * Hold Render object when invoke renderXxx(...)
   */
  private var render: Render = null

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
  def render(view: Nothing) {
    render = renderFactory.getRender(view)
  }

  /**
   * Render with jsp view
   */
  def renderJsp(view: Nothing) {
    render = renderFactory.getJspRender(view)
  }

  /**
   * Render with freemarker view
   */
  def renderFreeMarker(view: Nothing) {
    render = renderFactory.getFreeMarkerRender(view)
  }

  /**
   * Render with velocity view
   */
  def renderVelocity(view: Nothing) {
    render = renderFactory.getVelocityRender(view)
  }

  /**
   * Render with json
   * <p>
   * Example:<br>
   * renderJson("message", "Save successful");<br>
   * renderJson("users", users);<br>
   */
  def renderJson(key: Nothing, value: Nothing) {
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
  def renderJson(attrs: Array[Nothing]) {
    render = renderFactory.getJsonRender(attrs)
  }

  /**
   * Render with json text.
   * <p>
   * Example: renderJson("{\"message\":\"Please input password!\"}");
   */
  def renderJson(jsonText: Nothing) {
    render = renderFactory.getJsonRender(jsonText)
  }

  /**
   * Render json with object.
   * <p>
   * Example: renderJson(new User().set("name", "JFinal").set("age", 18));
   */
  def renderJson(`object`: Nothing) {
    render = renderFactory.getJsonRender(`object`)
  }

  /**
   * Render with text. The contentType is: "text/plain".
   */
  def renderText(text: Nothing) {
    render = renderFactory.getTextRender(text)
  }

  /**
   * Render with text and content type.
   * <p>
   * Example: renderText("&lt;user id='5888'&gt;James&lt;/user&gt;", "application/xml");
   */
  def renderText(text: Nothing, contentType: Nothing) {
    render = renderFactory.getTextRender(text, contentType)
  }

  /**
   * Render with text and ContentType.
   * <p>
   * Example: renderText("&lt;html&gt;Hello James&lt;/html&gt;", ContentType.HTML);
   */
  def renderText(text: Nothing, contentType: ContentType) {
    render = renderFactory.getTextRender(text, contentType)
  }

  /**
   * Forward to an action
   */
  def forwardAction(actionUrl: Nothing) {
    render = new ActionRender(actionUrl)
  }

  /**
   * Render with file
   */
  def renderFile(fileName: Nothing) {
    render = renderFactory.getFileRender(fileName)
  }

  /**
   * Render with file
   */
  def renderFile(file: Nothing) {
    render = renderFactory.getFileRender(file)
  }

  /**
   * Redirect to url
   */
  def redirect(url: Nothing) {
    render = renderFactory.getRedirectRender(url)
  }

  /**
   * Redirect to url
   */
  def redirect(url: Nothing, withQueryString: Boolean) {
    render = renderFactory.getRedirectRender(url, withQueryString)
  }

  /**
   * Render with view and status use default type Render configured in JFinalConfig
   */
  def render(view: Nothing, status: Int) {
    render = renderFactory.getRender(view)
    response.setStatus(status)
  }

  /**
   * Render with url and 301 status
   */
  def redirect301(url: Nothing) {
    render = renderFactory.getRedirect301Render(url)
  }

  /**
   * Render with url and 301 status
   */
  def redirect301(url: Nothing, withQueryString: Boolean) {
    render = renderFactory.getRedirect301Render(url, withQueryString)
  }

  /**
   * Render with view and errorCode status
   */
  def renderError(errorCode: Int, view: Nothing) {
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
  def renderJavascript(javascriptText: Nothing) {
    render = renderFactory.getJavascriptRender(javascriptText)
  }

  /**
   * Render with html text. The contentType is: "text/html".
   */
  def renderHtml(htmlText: Nothing) {
    render = renderFactory.getHtmlRender(htmlText)
  }

  /**
   * Render with xml view using freemarker.
   */
  def renderXml(view: Nothing) {
    render = renderFactory.getXmlRender(view)
  }
}

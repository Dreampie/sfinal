package com.sfinal.config

import com.sfinal.core.Controller

/**
 * Created by ice on 14-12-3.
 */
trait Routes {

  private var map: Map[String, Controller] = Map[String, Controller]()
  private var viewPathMap: Map[String, String] = Map[String, String]()

  /**
   * you must implement config method and use add method to config route
   */
  def config


  def add(routes: Routes): Routes = {
    if (routes != null) {
      routes.config
      map ++= routes.map
      viewPathMap ++= routes.viewPathMap
    }
    this
  }

  /**
   * Add route
   * @param controllerKey A key can find controller
   * @param controllerClass Controller Class
   * @param viewPath View path for this Controller
   */
  def add(controllerKey: String, controllerClass: Class[Controller], viewPath: String): Routes = {
    if (controllerKey == null)
      throw new IllegalArgumentException("The controllerKey can not be null");
    // if (controllerKey.indexOf(".") != -1)
    // throw new IllegalArgumentException("The controllerKey can not contain dot character: \".\"");
    var conKey: String = controllerKey.trim();
    if ("".equals(conKey))
      throw new IllegalArgumentException("The controllerKey can not be blank");
    if (controllerClass == null)
      throw new IllegalArgumentException("The controllerClass can not be null");
    if (!conKey.startsWith("/"))
      conKey = "/" + conKey;
    if (map.contains(conKey))
      throw new IllegalArgumentException("The controllerKey already exists: " + conKey);

    map += ((conKey, controllerClass))

    var vPath: String = null
    if (viewPath == null || "".equals(viewPath.trim)) // view path is controllerKey by default
      vPath = conKey

    vPath = viewPath.trim
    if (!viewPath.startsWith("/")) // "/" added to prefix
      vPath = "/" + vPath

    if (!viewPath.endsWith("/")) // "/" added to postfix
      vPath = vPath + "/"

    if (Routes.baseViewPathPrivate != null) // support baseViewPath
      vPath = Routes.baseViewPathPrivate + vPath

    viewPathMap += ((conKey, vPath))
    this
  }

  /**
  private var baseViewPathPrivate: String = null

    * Add url mapping to controller. The view path is controllerKey
    * @param controllerkey A key can find controller
    * @param controllerClass Controller Class
    */
  def add(controllerkey: String, controllerClass: Class[Controller]): Routes = {
    add(controllerkey, controllerClass, controllerkey)
  }

  def viewPath(key: String): Option[String] = {
    viewPathMap.get(key)
  }

}

object Routes {

  private var baseViewPathPrivate: String = null

  def baseViewPath: String = {
    baseViewPathPrivate
  }

  /**
   * Set the base path for all views
   */
  def baseViewPath_=(baseViewPath: String) {
    if (baseViewPath == null) throw new Nothing("The baseViewPath can not be null")
    this.baseViewPathPrivate = baseViewPath.trim
    if ("" == baseViewPath) throw new Nothing("The baseViewPath can not be blank")
    if (!baseViewPath.startsWith("/")) this.baseViewPathPrivate = "/" + baseViewPath
    if (baseViewPath.endsWith("/")) this.baseViewPathPrivate = baseViewPath.substring(0, baseViewPath.length - 1)
    this.baseViewPathPrivate = baseViewPath
  }
}

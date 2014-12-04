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

import com.sfinal.core.Controller

import scala.collection.immutable.HashMap

/**
 * Routes.
 */
object Routes {
  /**
   * Set the base path for all views
   */
  private def setBaseViewPath(baseViewPath: String) {
    if (baseViewPath == null) throw new IllegalArgumentException("The baseViewPath can not be null")
    var baseViewPathVar = baseViewPath.trim
    if ("" == baseViewPathVar) throw new IllegalArgumentException("The baseViewPath can not be blank")
    if (!baseViewPathVar.startsWith("/")) baseViewPathVar = "/" + baseViewPathVar
    if (baseViewPathVar.endsWith("/")) baseViewPathVar = baseViewPathVar.substring(0, baseViewPathVar.length - 1)
    Routes.baseViewPath = baseViewPathVar
  }

  private var baseViewPath: String = null
}

abstract class Routes {

  private final var map: Map[String, Class[_ <: Controller]] = new HashMap[String, Class[_ <: Controller]]
  private final var viewPathMap: Map[String, String] = new HashMap[String, String]
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
  def add(controllerKey: String, controllerClass: Class[_ <: Controller], viewPath: String): Routes = {
    if (controllerKey == null) throw new IllegalArgumentException("The controllerKey can not be null")
    var controllerKeyVar = controllerKey.trim
    if ("" == controllerKeyVar) throw new IllegalArgumentException("The controllerKey can not be blank")
    if (controllerClass == null) throw new IllegalArgumentException("The controllerClass can not be null")
    if (!controllerKeyVar.startsWith("/")) controllerKeyVar = "/" + controllerKeyVar
    if (map.contains(controllerKeyVar)) throw new IllegalArgumentException("The controllerKey already exists: " + controllerKeyVar)
    map += ((controllerKeyVar, controllerClass))

    var viewPathVar = if (viewPath == null || ("" == viewPath.trim)) controllerKeyVar else viewPath.trim
    if (!viewPathVar.startsWith("/")) viewPathVar = "/" + viewPathVar
    if (!viewPathVar.endsWith("/")) viewPathVar = viewPathVar + "/"
    if (Routes.baseViewPath != null) viewPathVar = Routes.baseViewPath + viewPathVar
    viewPathMap += ((controllerKey, viewPath))
    this
  }

  /**
    * Add url mapping to controller. The view path is controllerKey
   * @param controllerkey A key can find controller
   * @param controllerClass Controller Class
   */
  def add(controllerkey: String, controllerClass: Class[_ <: Controller]): Routes = {
    add(controllerkey, controllerClass, controllerkey)
  }

  def getEntrySet: Iterable[Class[_ <: Controller]] = {
    map.values
  }

  def getViewPath(key: String): Option[String] = {
    viewPathMap.get(key)
  }
}








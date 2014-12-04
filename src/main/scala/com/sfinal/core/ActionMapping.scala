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

import java.lang.reflect.Method
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import com.sfinal.aop.Interceptor
import com.sfinal.config.Interceptors
import com.sfinal.config.Routes
import com.sfinal.core.{InterceptorBuilder, Controller}

/**
 * ActionMapping
 */
final object ActionMapping {
  private final def warnning(actionKey: String, controllerClass: Class[_ <: Controller], method: Method) {
    val sb: StringBuilder = new StringBuilder
    sb.append("--------------------------------------------------------------------------------\nWarnning!!!\n").append("ActionKey already used: \"").append(actionKey).append("\" \n").append("Action can not be mapped: \"").append(controllerClass.getName).append(".").append(method.getName).append("()\" \n").append("--------------------------------------------------------------------------------")
    System.out.println(sb.toString)
  }

  private final val SLASH: String = "/"
}

final class ActionMapping {
  private[core] def this(routes: Routes, interceptors: Interceptors) {
    this()
    this.routes = routes
    this.interceptors = interceptors
  }

  private def buildExcludedMethodName: Set[String] = {
    val excludedMethodName: Set[String] = new HashSet[String]
    val methods: Array[Method] = classOf[Controller].getMethods
    for (m <- methods) {
      if (m.getParameterTypes.length == 0) excludedMethodName.add(m.getName)
    }
    return excludedMethodName
  }

  private[core] def buildActionMapping {
    mapping.clear
    val excludedMethodName: Set[String] = buildExcludedMethodName
    val interceptorBuilder: InterceptorBuilder = new InterceptorBuilder
    val defaultInters: Array[Interceptor] = interceptors.getInterceptorArray
    interceptorBuilder.addToInterceptorsMap(defaultInters)
    import scala.collection.JavaConversions._
    for (entry <- routes.getEntrySet) {
      val controllerClass: Class[_ <: Controller] = entry.getValue
      val controllerInters: Array[Interceptor] = interceptorBuilder.buildControllerInterceptors(controllerClass)
      val methods: Array[Method] = controllerClass.getMethods
      for (method <- methods) {
        val methodName: String = method.getName
        if (!excludedMethodName.contains(methodName) && method.getParameterTypes.length == 0) {
          val methodInters: Array[Interceptor] = interceptorBuilder.buildMethodInterceptors(method)
          val actionInters: Array[Interceptor] = interceptorBuilder.buildActionInterceptors(defaultInters, controllerInters, controllerClass, methodInters, method)
          val controllerKey: String = entry.getKey
          val ak: ActionKey = method.getAnnotation(classOf[ActionKey])
          if (ak != null) {
            var actionKey: String = ak.value.trim
            if ("" == actionKey) throw new IllegalArgumentException(controllerClass.getName + "." + methodName + "(): The argument of ActionKey can not be blank.")
            if (!actionKey.startsWith(SLASH)) actionKey = SLASH + actionKey
            if (mapping.containsKey(actionKey)) {
              warnning(actionKey, controllerClass, method)
              continue //todo: continue is not supported
            }
            val action: Action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey))
            mapping.put(actionKey, action)
          }
          else if (methodName == "index") {
            val actionKey: String = controllerKey
            var action: Action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey))
            action = mapping.put(actionKey, action)
            if (action != null) {
              warnning(action.getActionKey, action.getControllerClass, action.getMethod)
            }
          }
          else {
            val actionKey: String = if ((controllerKey == SLASH)) SLASH + methodName else controllerKey + SLASH + methodName
            if (mapping.containsKey(actionKey)) {
              warnning(actionKey, controllerClass, method)
              continue //todo: continue is not supported
            }
            val action: Action = new Action(controllerKey, actionKey, controllerClass, method, methodName, actionInters, routes.getViewPath(controllerKey))
            mapping.put(actionKey, action)
          }
        }
      }
    }
    val actoin: Action = mapping.get("/")
    if (actoin != null) mapping.put("", actoin)
  }

  /**
   * Support four types of url
   * 1: http://abc.com/controllerKey                 ---> 00
   * 2: http://abc.com/controllerKey/para            ---> 01
   * 3: http://abc.com/controllerKey/method          ---> 10
   * 4: http://abc.com/controllerKey/method/para     ---> 11
   */
  private[core] def getAction(url: String, urlPara: Array[String]): Action = {
    var action: Action = mapping.get(url)
    if (action != null) {
      return action
    }
    val i: Int = url.lastIndexOf(SLASH)
    if (i != -1) {
      action = mapping.get(url.substring(0, i))
      urlPara(0) = url.substring(i + 1)
    }
    return action
  }

  private[core] def getAllActionKeys: List[String] = {
    val allActionKeys: List[String] = new ArrayList[String](mapping.keySet)
    Collections.sort(allActionKeys)
    return allActionKeys
  }

  private var routes: Routes = null
  private var interceptors: Interceptors = null
  private final val mapping: Map[String, Action] = new HashMap[String, Action]
}







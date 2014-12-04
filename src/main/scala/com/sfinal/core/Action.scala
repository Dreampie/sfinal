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
import com.sfinal.aop.Interceptor

/**
 * Action
 */
class Action {
  def this(controllerKey: String, actionKey: String, controllerClass: Class[_ <: Controller], method: Method, methodName: String, interceptors: Array[Interceptor], viewPath: String) {
    this()
    this.controllerKey = controllerKey
    this.actionKey = actionKey
    this.controllerClass = controllerClass
    this.method = method
    this.methodName = methodName
    this.interceptors = interceptors
    this.viewPath = viewPath
  }

  def getControllerClass: Class[_ <: Controller] = {
    controllerClass
  }

  def getControllerKey: String = {
    controllerKey
  }

  def getActionKey: String = {
    actionKey
  }

  def getMethod: Method = {
    method
  }

  def getInterceptors: Array[Interceptor] = {
    interceptors
  }

  def getViewPath: String = {
    viewPath
  }

  def getMethodName: String = {
    methodName
  }

  private final var controllerClass: Class[_ <: Controller] = null
  private final var controllerKey: String = null
  private final var actionKey: String = null
  private final var method: Method = null
  private final var methodName: String = null
  private final var interceptors: Array[Interceptor] = null
  private final var viewPath: String = null
}











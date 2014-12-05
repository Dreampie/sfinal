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

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import com.sfinal.aop.Interceptor
import com.sfinal.core.Controller

/**
 * ActionInvocation invoke the action
 */
object ActionInvocation {
  private final val NULL_ARGS: Array[AnyRef] = new Array[AnyRef](0)
}

class ActionInvocation protected {

  private def this(action: Action, controller: Controller) {
    this()
    this.controller = controller
    this.inters = action.getInterceptors
    this.action = action
  }

  /**
   * Invoke the action.
   */
  def invoke {
    if (index < inters.length) inters({
      index += 1
      index - 1
    }).intercept(this)
    else if ( {
      index += 1
      index - 1
    } == inters.length) try {
      action.getMethod.invoke(controller, ActionInvocation.NULL_ARGS)
    }
    catch {
      case e: InvocationTargetException => {
        throw new RuntimeException(e)
      }
      case e: RuntimeException => {
        throw e
      }
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  /**
   * Return the controller of this action.
   */
  def getController: Controller = {
    controller
  }

  /**
   * Return the action key.
   * actionKey = controllerKey + methodName
   */
  def getActionKey: String = {
    action.getActionKey
  }

  /**
   * Return the controller key.
   */
  def getControllerKey: String = {
    action.getControllerKey
  }

  /**
   * Return the method of this action.
   * <p>
   * You can getMethod.getAnnotations() to get annotation on action method to do more things
   */
  def getMethod: Method = {
    action.getMethod
  }

  /**
   * Return the method name of this action's method.
   */
  def getMethodName: String = {
    action.getMethodName
  }

  /**
   * Return view path of this controller.
   */
  def getViewPath: String = {
    action.getViewPath
  }

  private var controller: Controller = null
  private var inters: Array[Interceptor] = null
  private var action: Action = null
  private var index: Int = 0
}


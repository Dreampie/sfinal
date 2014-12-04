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
package com.sfinal.aop

import java.lang.reflect.Method
import com.sfinal.core.ActionInvocation
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller
import com.sfinal.core.Controller

/**
 * ActionInvocationWrapper invoke the InterceptorStack.
 */
class ActionInvocationWrapper private extends ActionInvocation {

  private var inters: Array[Interceptor] = null
  private var actionInvocation: ActionInvocation = null
  private var index: Int = 0

  def this(actionInvocation: ActionInvocation, inters: Array[Interceptor]) {
    this()
    this.actionInvocation = actionInvocation
    this.inters = inters
  }

  /**
   * Invoke the action
   */
  final override def invoke {
    if (index < inters.length) inters({
      index += 1
      index - 1
    }).intercept(this)
    else if ( {
      index += 1
      index - 1
    } == inters.length) actionInvocation.invoke
  }

  override def getController: Controller = {
    actionInvocation.getController
  }

  override def getActionKey: String = {
    actionInvocation.getActionKey
  }

  override def getControllerKey: String = {
    actionInvocation.getControllerKey
  }

  override def getMethod: Method = {
    actionInvocation.getMethod
  }

  override def getMethodName: String = {
    actionInvocation.getMethodName
  }

  /**
   * Return view path of this controller
   */
  override def getViewPath: String = {
    actionInvocation.getViewPath
  }
}









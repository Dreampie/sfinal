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
package com.sfinal.ext.interceptor

import java.util.HashSet
import java.util.Set
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller

/**
 * ActionInvocation 中添加 Method method
 *
The standard definition is as follows:
	index - GET - A view of all (or a selection of) the records
	show - GET - A view of a single record
	add - GET - A form to post to create
	save - POST - Create a new record
	edit - GET - A form to edit a single record
	update - PUT - Update a record
	delete - DELETE - Delete a record
 *
 * GET		/user			--->	index
 * GET		/user/id		--->	show  
 * GET		/user/add		--->	add
 * POST		/user			--->	save	
 * GET		/user/edit/id	--->	edit
 * PUT		/user/id		--->	update
 * DELETE	/user/id		--->	delete
 */
object Restful {
  private final val isRestfulForwardKey: String = "_isRestfulForward_"
}

class Restful extends Interceptor {
  /**
   * add  edit 无需处理
   *
   * GET		/user			--->	index
   * GET		/user/id		--->	show
   * POST		/user			--->	save
   * PUT		/user/id		--->	update
   * DELECT	/user/id		--->	delete
   */
  def intercept(ai: ActionInvocation) {
    val controller: Controller = ai.getController
    val isRestfulForward: Boolean = controller.getAttr(isRestfulForwardKey)
    val methodName: String = ai.getMethodName
    if (set.contains(methodName) && isRestfulForward == null) {
      ai.getController.renderError(404)
      return
    }
    if (isRestfulForward != null && isRestfulForward) {
      ai.invoke
      return
    }
    val controllerKey: String = ai.getControllerKey
    val method: String = controller.getRequest.getMethod.toUpperCase
    val urlPara: String = controller.getPara
    if ("GET" == method) {
      if (urlPara != null && !("edit" == methodName)) {
        controller.setAttr(isRestfulForwardKey, Boolean.TRUE)
        controller.forwardAction(controllerKey + "/show/" + urlPara)
        return
      }
    }
    else if ("POST" == method) {
      controller.setAttr(isRestfulForwardKey, Boolean.TRUE)
      controller.forwardAction(controllerKey + "/save")
      return
    }
    else if ("PUT" == method) {
      controller.setAttr(isRestfulForwardKey, Boolean.TRUE)
      controller.forwardAction(controllerKey + "/update/" + urlPara)
      return
    }
    else if ("DELETE" == method) {
      controller.setAttr(isRestfulForwardKey, Boolean.TRUE)
      controller.forwardAction(controllerKey + "/delete/" + urlPara)
      return
    }
    ai.invoke
  }

  private var set: Set[String] =
  new
}








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

import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit
import com.sfinal.render.Render
import com.sfinal.render.Render
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory

/**
  * ActionException.
 */
class ActionException extends RuntimeException {

  private var errorCode: Int = 0
  private var errorRender: Render = null

  def this(errorCode: Int, errorRender: Render) {
  this()
  if (errorRender == null) throw new IllegalArgumentException("The parameter errorRender can not be null.")
  this.errorCode = errorCode
  this.errorRender = errorRender
}

  def this(errorCode: Int, errorView: String) {
  this()
  if (StrKit.isBlank(errorView)) throw new IllegalArgumentException("The parameter errorView can not be blank.")
  this.errorCode = errorCode
  this.errorRender = RenderFactory.me.getErrorRender(errorCode, errorView)
}

  def getErrorCode: Int = {
    errorCode
  }

  def getErrorRender: Render = {
    errorRender
  }
}




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

import java.io.File
import java.util.Properties
import com.sfinal.core.Const
import com.sfinal.core.Const
import com.sfinal.kit.Prop
import com.sfinal.kit.Prop
import com.sfinal.kit.PropKit
import com.sfinal.kit.PropKit

/**
 * JFinalConfig.
 * <p>
 * Config order: configConstant(), configRoute(), configPlugin(), configInterceptor(), configHandler()
 */
abstract class JFinalConfig {
  /**
   * Config constant
   */
  def configConstant(me: Constants)

  /**
   * Config route
   */
  def configRoute(me: Routes)

  /**
   * Config plugin
   */
  def configPlugin(me: Plugins)

  /**
   * Config interceptor applied to all actions.
   */
  def configInterceptor(me: Interceptors)

  /**
   * Config handler
   */
  def configHandler(me: Handlers)

  /**
   * Call back after JFinal start
   */
  def afterJFinalStart {
  }

  /**
   * Call back before JFinal stop
   */
  def beforeJFinalStop {
  }

}
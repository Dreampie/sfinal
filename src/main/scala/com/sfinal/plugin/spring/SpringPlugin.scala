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
package com.sfinal.plugin.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContext
import org.springframework.context.support.FileSystemXmlApplicationContext
import com.sfinal.kit.PathKit
import com.sfinal.plugin.IPlugin
import org.springframework.context.support.FileSystemXmlApplicationContext

/**
 * SpringPlugin.
 */
class SpringPlugin extends IPlugin {
  /**
   * Use configuration under the path of WebRoot/WEB-INF.
   */
  def this() {
    this()
  }

  def this(configurations: String*) {
    this()
    this.configurations = configurations
  }

  def this(ctx: ApplicationContext) {
    this()
    this.ctx = ctx
  }

  def start: Boolean = {
    if (ctx != null) IocInterceptor.ctx = ctx
    else if (configurations != null) IocInterceptor.ctx = new FileSystemXmlApplicationContext(configurations)
    else IocInterceptor.ctx = new FileSystemXmlApplicationContext(PathKit.getWebRootPath + "/WEB-INF/applicationContext.xml")
    return true
  }

  def stop: Boolean = {
    return true
  }

  private var configurations: Array[String] = null
  private var ctx: ApplicationContext = null
}


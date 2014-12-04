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
package com.sfinal.plugin.ehcache

import java.io.InputStream
import java.net.URL
import net.sf.ehcache.CacheManager
import net.sf.ehcache.config.Configuration
import com.sfinal.plugin.IPlugin

/**
 * EhCachePlugin.
 */
object EhCachePlugin {
  private var cacheManager: CacheManager = null
}

class EhCachePlugin extends IPlugin {
  def this() {
    this()
  }

  def this(cacheManager: CacheManager) {
    this()
    EhCachePlugin.cacheManager = cacheManager
  }

  def this(configurationFileName: String) {
    this()
    this.configurationFileName = configurationFileName
  }

  def this(configurationFileURL: URL) {
    this()
    this.configurationFileURL = configurationFileURL
  }

  def this(inputStream: InputStream) {
    this()
    this.inputStream = inputStream
  }

  def this(configuration: Configuration) {
    this()
    this.configuration = configuration
  }

  def start: Boolean = {
    createCacheManager
    CacheKit.init(cacheManager)
    return true
  }

  private def createCacheManager {
    if (cacheManager != null) return
    if (configurationFileName != null) {
      cacheManager = CacheManager.create(configurationFileName)
      return
    }
    if (configurationFileURL != null) {
      cacheManager = CacheManager.create(configurationFileURL)
      return
    }
    if (inputStream != null) {
      cacheManager = CacheManager.create(inputStream)
      return
    }
    if (configuration != null) {
      cacheManager = CacheManager.create(configuration)
      return
    }
    cacheManager = CacheManager.create
  }

  def stop: Boolean = {
    cacheManager.shutdown
    return true
  }

  private var configurationFileName: String = null
  private var configurationFileURL: URL = null
  private var inputStream: InputStream = null
  private var configuration: Configuration = null
}






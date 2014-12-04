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

import java.util.List
import com.sfinal.log.Logger
import com.sfinal.log.Logger
import com.sfinal.plugin.ehcache.IDataLoader
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element

/**
 * CacheKit. Useful tool box for EhCache.
 */
object CacheKit {
  private[ehcache] def init(cacheManager: CacheManager) {
    CacheKit.cacheManager = cacheManager
  }

  def getCacheManager: CacheManager = {
    return cacheManager
  }

  private[ehcache] def getOrAddCache(cacheName: String): Cache = {
    var cache: Cache = cacheManager.getCache(cacheName)
    if (cache == null) {
      cacheManager synchronized {
        cache = cacheManager.getCache(cacheName)
        if (cache == null) {
          log.warn("Could not find cache config [" + cacheName + "], using default.")
          cacheManager.addCacheIfAbsent(cacheName)
          cache = cacheManager.getCache(cacheName)
          log.debug("Cache [" + cacheName + "] started.")
        }
      }
    }
    return cache
  }

  def put(cacheName: String, key: AnyRef, value: AnyRef) {
    getOrAddCache(cacheName).put(new Element(key, value))
  }

  @SuppressWarnings(Array("unchecked")) def get(cacheName: String, key: AnyRef): T = {
    val element: Element = getOrAddCache(cacheName).get(key)
    return if (element != null) element.getObjectValue.asInstanceOf[T] else null
  }

  @SuppressWarnings(Array("rawtypes")) def getKeys(cacheName: String): List[_] = {
    return getOrAddCache(cacheName).getKeys
  }

  def remove(cacheName: String, key: AnyRef) {
    getOrAddCache(cacheName).remove(key)
  }

  def removeAll(cacheName: String) {
    getOrAddCache(cacheName).removeAll
  }

  @SuppressWarnings(Array("unchecked")) def get(cacheName: String, key: AnyRef, dataLoader: IDataLoader): T = {
    var data: AnyRef = get(cacheName, key)
    if (data == null) {
      data = dataLoader.load
      put(cacheName, key, data)
    }
    return data.asInstanceOf[T]
  }

  @SuppressWarnings(Array("unchecked")) def get(cacheName: String, key: AnyRef, dataLoaderClass: Class[_ <: IDataLoader]): T = {
    var data: AnyRef = get(cacheName, key)
    if (data == null) {
      try {
        val dataLoader: IDataLoader = dataLoaderClass.newInstance
        data = dataLoader.load
        put(cacheName, key, data)
      }
      catch {
        case e: Exception => {
          throw new RuntimeException(e)
        }
      }
    }
    return data.asInstanceOf[T]
  }

  @volatile
  private var cacheManager: CacheManager = null
  private final val log: Logger = Logger.getLogger(classOf[CacheKit])
}





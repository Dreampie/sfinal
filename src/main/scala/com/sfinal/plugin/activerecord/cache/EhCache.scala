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
package com.sfinal.plugin.activerecord.cache

import com.sfinal.plugin.activerecord.cache.ICache
import com.sfinal.plugin.ehcache.CacheKit
import com.sfinal.plugin.ehcache.CacheKit

/**
 * EhCache.
 */
class EhCache extends ICache {
  @SuppressWarnings(Array("unchecked")) def get(cacheName: String, key: AnyRef): T = {
    return CacheKit.get(cacheName, key).asInstanceOf[T]
  }

  def put(cacheName: String, key: AnyRef, value: AnyRef) {
    CacheKit.put(cacheName, key, value)
  }

  def remove(cacheName: String, key: AnyRef) {
    CacheKit.remove(cacheName, key)
  }

  def removeAll(cacheName: String) {
    CacheKit.removeAll(cacheName)
  }
}




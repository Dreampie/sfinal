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

import java.util.Enumeration
import java.util.HashMap
import java.util.Iterator
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.servlet.http.HttpServletRequest
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller
import com.sfinal.plugin.ehcache.{RenderInfo, CacheName, CacheKit}

/**
 * CacheInterceptor.
 */
object CacheInterceptor {
  private final val renderKey: String = "$renderKey$"
  @volatile
  private var lockMap: ConcurrentHashMap[String, ReentrantLock] = new ConcurrentHashMap[String, ReentrantLock]
}

class CacheInterceptor extends Interceptor {
  private def getLock(key: String): ReentrantLock = {
    var lock: ReentrantLock = lockMap.get(key)
    if (lock != null) return lock
    lock = new ReentrantLock
    val previousLock: ReentrantLock = lockMap.putIfAbsent(key, lock)
    return if (previousLock == null) lock else previousLock
  }

  final def intercept(ai: ActionInvocation) {
    val controller: Controller = ai.getController
    val cacheName: String = buildCacheName(ai, controller)
    val cacheKey: String = buildCacheKey(ai, controller)
    var cacheData: Map[String, AnyRef] = CacheKit.get(cacheName, cacheKey)
    if (cacheData == null) {
      val lock: Lock = getLock(cacheName)
      lock.lock
      try {
        cacheData = CacheKit.get(cacheName, cacheKey)
        if (cacheData == null) {
          ai.invoke
          cacheAction(cacheName, cacheKey, controller)
          return
        }
      }
      finally {
        lock.unlock
      }
    }
    useCacheDataAndRender(cacheData, controller)
  }

  private def buildCacheName(ai: ActionInvocation, controller: Controller): String = {
    var cacheName: CacheName = ai.getMethod.getAnnotation(classOf[CacheName])
    if (cacheName != null) return cacheName.value
    cacheName = controller.getClass.getAnnotation(classOf[CacheName])
    return if ((cacheName != null)) cacheName.value else ai.getActionKey
  }

  private def buildCacheKey(ai: ActionInvocation, controller: Controller): String = {
    val sb: StringBuilder = new StringBuilder(ai.getActionKey)
    val urlPara: String = controller.getPara
    if (urlPara != null) sb.append("/").append(urlPara)
    val queryString: String = controller.getRequest.getQueryString
    if (queryString != null) sb.append("?").append(queryString)
    return sb.toString
  }

  private def cacheAction(cacheName: String, cacheKey: String, controller: Controller) {
    val request: HttpServletRequest = controller.getRequest
    val cacheData: Map[String, AnyRef] = new HashMap[String, AnyRef]
    {
      val names: Enumeration[String] = request.getAttributeNames
      while (names.hasMoreElements) {
        val name: String = names.nextElement
        cacheData.put(name, request.getAttribute(name))
      }
    }
    cacheData.put(renderKey, new RenderInfo(controller.getRender))
    CacheKit.put(cacheName, cacheKey, cacheData)
  }

  private def useCacheDataAndRender(cacheData: Map[String, AnyRef], controller: Controller) {
    val request: HttpServletRequest = controller.getRequest
    val set: Set[Map.Entry[String, AnyRef]] = cacheData.entrySet
    {
      val it: Iterator[Map.Entry[String, AnyRef]] = set.iterator
      while (it.hasNext) {
        val entry: Map.Entry[String, AnyRef] = it.next
        request.setAttribute(entry.getKey, entry.getValue)
      }
    }
    request.removeAttribute(renderKey)
    controller.render((cacheData.get(renderKey).asInstanceOf[RenderInfo]).createRender)
  }
}







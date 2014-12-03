package com.sfinal.plugin.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

import com.sfinal.aop.Interceptor
import com.sfinal.core.{Controller, ActionInvocation}

/**
 * Created by ice on 14-12-3.
 */
class Cache(name: String) extends annotation.Annotation {
  val value: String = name
}

/**
 * CacheInterceptor.
 */
object CacheInterceptor {
  private val renderKey: String = "$renderKey$"
  private val lockMap: ConcurrentHashMap[String, ReentrantLock] = new ConcurrentHashMap[String, ReentrantLock]()
}

class CacheInterceptor extends Interceptor {
  private def getLock(key: String): ReentrantLock = {
    var lock: ReentrantLock = CacheInterceptor.lockMap.get(key)
    if (lock != null) return lock
    lock = new ReentrantLock
    val previousLock: ReentrantLock = CacheInterceptor.lockMap.putIfAbsent(key, lock)
    return if (previousLock == null) lock else previousLock
  }

  final def intercept(ai: ActionInvocation) {
    val controller: Controller = ai.controller
    val cacheName: Nothing = buildCacheName(ai, controller)
    val cacheKey: Nothing = buildCacheKey(ai, controller)
    var cacheData: Nothing = CacheKit.get(cacheName, cacheKey)
    if (cacheData == null) {
      val lock: ReentrantLock = getLock(cacheName)
      lock.lock
      try {
        cacheData = CacheKit.get(cacheName, cacheKey)
        if (cacheData == null) {
          ai.invoke
          cacheAction(cacheName, cacheKey, controller)
          return
        }
      } finally {
        lock.unlock
      }
    }
    useCacheDataAndRender(cacheData, controller)
  }

  private def buildCacheName(ai: ActionInvocation, controller: Controller): String = {
    var cacheName: Cache = ai.method.getAnnotation(classOf[Cache])
    if (cacheName != null) return cacheName.value
    cacheName = controller.getClass.getAnnotation(classOf[Cache])
    return if ((cacheName != null)) cacheName.value else ai.actionKey
  }

  private def buildCacheKey(ai: ActionInvocation, controller: Controller): String = {
    val sb: StringBuilder = new StringBuilder(ai.actionKey)
    val urlPara: String = controller.getPara
    if (urlPara != null) sb.append("/").append(urlPara)
    val queryString: String = controller.getRequest.
    if (queryString != null) sb.append("?").append(queryString)
    return sb.toString
  }

  private def cacheAction(cacheName: Nothing, cacheKey: Nothing, controller: Controller) {
    val request: Nothing = controller.getRequest
    val cacheData: Nothing = new Nothing {
      val names: Nothing = request.getAttributeNames
      while (names.hasMoreElements) {
        val name: Nothing = names.nextElement
        cacheData.put(name, request.getAttribute(name))
      }
    }
    cacheData.put(CacheInterceptor.renderKey, new RenderInfo(controller.getRender))
    CacheKit.put(cacheName, cacheKey, cacheData)
  }

  private def useCacheDataAndRender(cacheData: Nothing, controller: Controller) {
    val request: Nothing = controller.getRequest
    val set: Nothing = cacheData.entrySet {
      val it: Nothing = set.iterator
      while (it.hasNext) {
        val entry: Nothing = it.next
        request.setAttribute(entry.getKey, entry.getValue)
      }
    }
    request.removeAttribute(CacheInterceptor.renderKey)
    controller.render((cacheData.get(CacheInterceptor.renderKey).asInstanceOf[RenderInfo]).createRender)
  }
}






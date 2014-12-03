package com.sfinal.plugin.cache

/**
 * Created by ice on 14-12-3.
 */
trait ICache {
  def get[T](cacheName: String, key: AnyRef): T

  def put(cacheName: String, key: AnyRef, value: AnyRef)

  def remove(cacheName: String, key: AnyRef)

  def removeAll(cacheName: String)
}

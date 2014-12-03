package com.sfinal.token

/**
 * Created by ice on 14-12-3.
 */
trait ITokenCache {
  def put(token: Token)

  def remove(token: Token)

  def contains(token: Token): Boolean

  def getAll: Nothing
}

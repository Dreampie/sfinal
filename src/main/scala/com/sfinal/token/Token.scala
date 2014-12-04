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
package com.sfinal.token

import java.io.Serializable

/**
 * Token.
 */
object Token {
  private final val serialVersionUID: Long = -3667914001133777991L
}

class Token extends Serializable {
  private[token] def this(id: String, expirationTime: Long) {
    this()
    if (id == null) throw new IllegalArgumentException("id can not be null")
    this.expirationTime = expirationTime
    this.id = id
  }

  private[token] def this(id: String) {
    this()
    if (id == null) throw new IllegalArgumentException("id can not be null")
    this.id = id
  }

  /**
   * Returns a string containing the unique identifier assigned to this token.
   */
  def getId: String = {
    return id
  }

  def getExpirationTime: Long = {
    return expirationTime
  }

  /**
   * expirationTime 不予考虑, 因为就算 expirationTime 不同也认为是相同的 token.
   */
  override def hashCode: Int = {
    return id.hashCode
  }

  override def equals(`object`: AnyRef): Boolean = {
    if (`object`.isInstanceOf[Token]) return (`object`.asInstanceOf[Token]).id == this.id
    return false
  }

  private var id: String = null
  private var expirationTime: Long = 0L
}




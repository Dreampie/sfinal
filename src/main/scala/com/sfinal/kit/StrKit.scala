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
package com.sfinal.kit

/**
 * StrKit.
 */
object StrKit {
  /**
   * 首字母变小写
   */
  def firstCharToLowerCase(str: String): String = {
    val firstChar: Char = str.charAt(0)
    if (firstChar >= 'A' && firstChar <= 'Z') {
      val arr: Array[Char] = str.toCharArray
      arr(0) += ('a' - 'A')
      return new String(arr)
    }
    return str
  }

  /**
   * 首字母变大写
   */
  def firstCharToUpperCase(str: String): String = {
    val firstChar: Char = str.charAt(0)
    if (firstChar >= 'a' && firstChar <= 'z') {
      val arr: Array[Char] = str.toCharArray
      arr(0) -= ('a' - 'A')
      return new String(arr)
    }
    return str
  }

  /**
   * 字符串为 null 或者为  "" 时返回 true
   */
  def isBlank(str: String): Boolean = {
    return if (str == null || ("" == str.trim)) true else false
  }

  /**
   * 字符串不为 null 而且不为  "" 时返回 true
   */
  def notBlank(str: String): Boolean = {
    return if (str == null || ("" == str.trim)) false else true
  }

  def notBlank(strings: String*): Boolean = {
    if (strings == null) return false
    for (str <- strings) if (str == null || ("" == str.trim)) return false
    return true
  }

  def notNull(paras: AnyRef*): Boolean = {
    if (paras == null) return false
    for (obj <- paras) if (obj == null) return false
    return true
  }
}







package com.sfinal.kit

/**
 * Created by ice on 14-12-3.
 */
class StrKit private

object StrKit {
  /**
   * 首字母变小写
   */
  def firstCharToLowerCase(str: String): String = {
    val firstChar: Char = str.charAt(0)
    if (firstChar >= 'A' && firstChar <= 'Z') {
      val arr: Array[Char] = str.toCharArray
      arr(0) += ('a' - 'A')
      new String(arr)
    }
    str
  }

  /**
   * 首字母变大写
   */
  def firstCharToUpperCase(str: String): String = {
    val firstChar: Char = str.charAt(0)
    if (firstChar >= 'a' && firstChar <= 'z') {
      val arr: Array[Char] = str.toCharArray
      arr(0) -= ('a' - 'A')
      new String(arr)
    }
    str
  }

  /**
   * 字符串为 null 或者为  "" 时返回 true
   */
  def isBlank(str: String): Boolean = {
    if (str == null || ("" == str.trim)) true else false
  }

  /**
   * 字符串不为 null 而且不为  "" 时返回 true
   */
  def notBlank(str: String): Boolean = {
    if (str == null || ("" == str.trim)) false else true
  }

  def notBlank(strings: String*): Boolean = {
    for (str <- strings) if (str == null || ("" == str.trim)) false
    true
  }

  def notNull(paras: String*): Boolean = {
    for (obj <- paras) if (obj == null) false
    true
  }
}

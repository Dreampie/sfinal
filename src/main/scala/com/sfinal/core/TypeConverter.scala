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
package com.sfinal.core

import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Convert String to other type object.
 */
final object TypeConverter {
  /**
   * test for all types of mysql
   *
   * 表单提交测试结果:
   * 1: 表单中的域,就算不输入任何内容,也会传过来 "", 也即永远不可能为 null.
   * 2: 如果输入空格也会提交上来
   * 3: 需要考 model中的 string属性,在传过来 "" 时是该转成 null还是不该转换,
   * 我想, 因为用户没有输入那么肯定是 null, 而不该是 ""
   *
   * 注意: 1:当clazz参数不为String.class, 且参数s为空串blank的情况,
   * 此情况下转换结果为 null, 而不应该抛出异常
   * 2:调用者需要对被转换数据做 null 判断，参见 ModelInjector 的两处调用
   */
  final def convert(clazz: Class[_], s: String): AnyRef = {
    if (clazz eq classOf[String]) {
      return (if (("" == s)) null else s)
    }
    s = s.trim
    if ("" == s) {
      return null
    }
    var result: AnyRef = null
    if (clazz eq classOf[Integer] || clazz eq classOf[Int]) {
      result = Integer.parseInt(s)
    }
    else if (clazz eq classOf[Long] || clazz eq classOf[Long]) {
      result = Long.parseLong(s)
    }
    else if (clazz eq classOf[Date]) {
      if (s.length >= timeStampLen) {
        result = new SimpleDateFormat(timeStampPattern).parse(s)
      }
      else {
        result = new SimpleDateFormat(datePattern).parse(s)
      }
    }
    else if (clazz eq classOf[Date]) {
      if (s.length >= timeStampLen) {
        result = new Date(new SimpleDateFormat(timeStampPattern).parse(s).getTime)
      }
      else {
        result = new Date(new SimpleDateFormat(datePattern).parse(s).getTime)
      }
    }
    else if (clazz eq classOf[Time]) {
      result = java.sql.Time.valueOf(s)
    }
    else if (clazz eq classOf[Timestamp]) {
      result = java.sql.Timestamp.valueOf(s)
    }
    else if (clazz eq classOf[Double]) {
      result = Double.parseDouble(s)
    }
    else if (clazz eq classOf[Float]) {
      result = Float.parseFloat(s)
    }
    else if (clazz eq classOf[Boolean]) {
      result = Boolean.parseBoolean(s) || ("1" == s)
    }
    else if (clazz eq classOf[BigDecimal]) {
      result = new BigDecimal(s)
    }
    else if (clazz eq classOf[BigInteger]) {
      result = new BigInteger(s)
    }
    else if (clazz eq classOf[Array[Byte]]) {
      result = s.getBytes
    }
    else {
      if (Config.getConstants.getDevMode) throw new RuntimeException("Please add code in " + classOf[TypeConverter] + ". The type can't be converted: " + clazz.getName)
      else throw new RuntimeException(clazz.getName + " can not be converted, please use other type of attributes in your model!")
    }
    return result
  }

  private final val timeStampLen: Int = "2011-01-18 16:18:18".length
  private final val timeStampPattern: String = "yyyy-MM-dd HH:mm:ss"
  private final val datePattern: String = "yyyy-MM-dd"
}




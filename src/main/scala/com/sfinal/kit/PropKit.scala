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

import java.io.File
import java.util.Map
import java.util.concurrent.ConcurrentHashMap
import com.sfinal.core.Const

/**
 * PropKit. PropKit can load properties file from CLASSPATH or File object.
 */
object PropKit {
  /**
   * Using the properties file. It will loading the properties file if not loading.
   * @see #use(String, String)
   */
  def use(fileName: String): Prop = {
    return use(fileName, Const.DEFAULT_ENCODING)
  }

  /**
   * Using the properties file. It will loading the properties file if not loading.
   * <p>
   * Example:<br>
   * PropKit.use("config.txt", "UTF-8");<br>
   * PropKit.use("other_config.txt", "UTF-8");<br><br>
   * String userName = PropKit.get("userName");<br>
   * String password = PropKit.get("password");<br><br>
   *
   * userName = PropKit.use("other_config.txt").get("userName");<br>
   * password = PropKit.use("other_config.txt").get("password");<br><br>
   *
   * PropKit.use("com/jfinal/config_in_sub_directory_of_classpath.txt");
   *
   * @param fileName the properties file's name in classpath or the sub directory of classpath
   * @param encoding the encoding
   */
  def use(fileName: String, encoding: String): Prop = {
    var result: Prop = map.get(fileName)
    if (result == null) {
      result = new Prop(fileName, encoding)
      map.put(fileName, result)
      if (PropKit.prop == null) PropKit.prop = result
    }
    return result
  }

  /**
   * Using the properties file bye File object. It will loading the properties file if not loading.
   * @see #use(File, String)
   */
  def use(file: File): Prop = {
    return use(file, Const.DEFAULT_ENCODING)
  }

  /**
   * Using the properties file bye File object. It will loading the properties file if not loading.
   * <p>
   * Example:<br>
   * PropKit.use(new File("/var/config/my_config.txt"), "UTF-8");<br>
   * Strig userName = PropKit.use("my_config.txt").get("userName");
   *
   * @param file the properties File object
   * @param encoding the encoding
   */
  def use(file: File, encoding: String): Prop = {
    var result: Prop = map.get(file.getName)
    if (result == null) {
      result = new Prop(file, encoding)
      map.put(file.getName, result)
      if (PropKit.prop == null) PropKit.prop = result
    }
    return result
  }

  def useless(fileName: String): Prop = {
    val previous: Prop = map.remove(fileName)
    if (PropKit.prop eq previous) PropKit.prop = null
    return previous
  }

  def clear {
    prop = null
    map.clear
  }

  def getProp: Prop = {
    if (prop == null) throw new IllegalStateException("Load propties file by invoking PropKit.use(String fileName) method first.")
    return prop
  }

  def getProp(fileName: String): Prop = {
    return map.get(fileName)
  }

  def get(key: String): String = {
    return getProp.get(key)
  }

  def get(key: String, defaultValue: String): String = {
    return getProp.get(key, defaultValue)
  }

  def getInt(key: String): Integer = {
    return getProp.getInt(key)
  }

  def getInt(key: String, defaultValue: Integer): Integer = {
    return getProp.getInt(key, defaultValue)
  }

  def getLong(key: String): Long = {
    return getProp.getLong(key)
  }

  def getLong(key: String, defaultValue: Long): Long = {
    return getProp.getLong(key, defaultValue)
  }

  def getBoolean(key: String): Boolean = {
    return getProp.getBoolean(key)
  }

  def getBoolean(key: String, defaultValue: Boolean): Boolean = {
    return getProp.getBoolean(key, defaultValue)
  }

  def containsKey(key: String): Boolean = {
    return getProp.containsKey(key)
  }

  private var prop: Prop = null
  private final val map: Map[String, Prop] = new ConcurrentHashMap[String, Prop]
}

class PropKit {
  private def this() {
    this()
  }
}




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
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Properties
import com.sfinal.core.Const

/**
 * Prop. Prop can load properties file from CLASSPATH or File object.
 */
class Prop {
  /**
   * Prop constructor.
   * @see #Prop(String, String)
   */
  def this(fileName: String) {
    this()
    `this`(fileName, Const.DEFAULT_ENCODING)
  }

  /**
   * Prop constructor
   * <p>
   * Example:<br>
   * Prop prop = new Prop("my_config.txt", "UTF-8");<br>
   * String userName = prop.get("userName");<br><br>
   *
   * prop = new Prop("com/jfinal/file_in_sub_path_of_classpath.txt", "UTF-8");<br>
   * String value = prop.get("key");
   *
   * @param fileName the properties file's name in classpath or the sub directory of classpath
   * @param encoding the encoding
   */
  def this(fileName: String, encoding: String) {
    this()
    var inputStream: InputStream = null
    try {
      inputStream = Thread.currentThread.getContextClassLoader.getResourceAsStream(fileName)
      if (inputStream == null) throw new IllegalArgumentException("Properties file not found in classpath: " + fileName)
      properties = new Properties
      properties.load(new InputStreamReader(inputStream, encoding))
    }
    catch {
      case e: IOException => {
        throw new RuntimeException("Error loading properties file.", e)
      }
    }
    finally {
      if (inputStream != null) try {
        inputStream.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  /**
   * Prop constructor.
   * @see #Prop(File, String)
   */
  def this(file: File) {
    this()
    `this`(file, Const.DEFAULT_ENCODING)
  }

  /**
   * Prop constructor
   * <p>
   * Example:<br>
   * Prop prop = new Prop(new File("/var/config/my_config.txt"), "UTF-8");<br>
   * String userName = prop.get("userName");
   *
   * @param file the properties File object
   * @param encoding the encoding
   */
  def this(file: File, encoding: String) {
    this()
    if (file == null) throw new IllegalArgumentException("File can not be null.")
    if (file.isFile == false) throw new IllegalArgumentException("Not a file : " + file.getName)
    var inputStream: InputStream = null
    try {
      inputStream = new FileInputStream(file)
      properties = new Properties
      properties.load(new InputStreamReader(inputStream, encoding))
    }
    catch {
      case e: IOException => {
        throw new RuntimeException("Error loading properties file.", e)
      }
    }
    finally {
      if (inputStream != null) try {
        inputStream.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  def get(key: String): String = {
    return properties.getProperty(key)
  }

  def get(key: String, defaultValue: String): String = {
    val value: String = get(key)
    return if ((value != null)) value else defaultValue
  }

  def getInt(key: String): Integer = {
    val value: String = get(key)
    return if ((value != null)) Integer.parseInt(value) else null
  }

  def getInt(key: String, defaultValue: Integer): Integer = {
    val value: String = get(key)
    return if ((value != null)) Integer.parseInt(value) else defaultValue
  }

  def getLong(key: String): Long = {
    val value: String = get(key)
    return if ((value != null)) Long.parseLong(value) else null
  }

  def getLong(key: String, defaultValue: Long): Long = {
    val value: String = get(key)
    return if ((value != null)) Long.parseLong(value) else defaultValue
  }

  def getBoolean(key: String): Boolean = {
    val value: String = get(key)
    return if ((value != null)) Boolean.parseBoolean(value) else null
  }

  def getBoolean(key: String, defaultValue: Boolean): Boolean = {
    val value: String = get(key)
    return if ((value != null)) Boolean.parseBoolean(value) else defaultValue
  }

  def containsKey(key: String): Boolean = {
    return properties.containsKey(key)
  }

  def getProperties: Properties = {
    return properties
  }

  private var properties: Properties = null
}


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

/**
 * new File("..\path\abc.txt") 中的三个方法获取路径的方法
 * 1： getPath() 获取相对路径，例如   ..\path\abc.txt
 * 2： getAbslutlyPath() 获取绝对路径，但可能包含 ".." 或 "." 字符，例如  D:\otherPath\..\path\abc.txt
 * 3： getCanonicalPath() 获取绝对路径，但不包含 ".." 或 "." 字符，例如  D:\path\abc.txt
 */
object PathKit {
  @SuppressWarnings(Array("rawtypes")) def getPath(clazz: Class[_]): String = {
    val path: String = clazz.getResource("").getPath
    return new File(path).getAbsolutePath
  }

  def getPath(`object`: AnyRef): String = {
    val path: String = `object`.getClass.getResource("").getPath
    return new File(path).getAbsolutePath
  }

  def getRootClassPath: String = {
    if (rootClassPath == null) {
      try {
        val path: String = classOf[PathKit].getClassLoader.getResource("").toURI.getPath
        rootClassPath = new File(path).getAbsolutePath
      }
      catch {
        case e: Exception => {
          val path: String = classOf[PathKit].getClassLoader.getResource("").getPath
          rootClassPath = new File(path).getAbsolutePath
        }
      }
    }
    return rootClassPath
  }

  def getPackagePath(`object`: AnyRef): String = {
    val p: Package = `object`.getClass.getPackage
    return if (p != null) p.getName.replaceAll("\\.", "/") else ""
  }

  def getFileFromJar(file: String): File = {
    throw new RuntimeException("Not finish. Do not use this method.")
  }

  def getWebRootPath: String = {
    if (webRootPath == null) webRootPath = detectWebRootPath

    return webRootPath
  }

  def setWebRootPath(webRootPath: String) {
    if (webRootPath == null) return
    if (webRootPath.endsWith(File.separator)) webRootPath = webRootPath.substring(0, webRootPath.length - 1)
    PathKit.webRootPath = webRootPath
  }

  private def detectWebRootPath: String = {
    try {
      val path: String = classOf[PathKit].getResource("/").toURI.getPath
      return new File(path).getParentFile.getParentFile.getCanonicalPath
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  private var webRootPath: String = null
  private var rootClassPath: String = null
}





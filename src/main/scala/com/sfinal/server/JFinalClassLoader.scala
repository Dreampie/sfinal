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
package com.sfinal.server

import java.io.File
import java.io.IOException
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.webapp.WebAppClassLoader
import org.eclipse.jetty.webapp.WebAppClassLoader
import org.eclipse.jetty.webapp.WebAppContext

/**
 * JFinalClassLoader
 */
class JFinalClassLoader extends WebAppClassLoader {
  def this(context: WebAppContext, classPath: String) {
    this()
    `super`(context)
    if (classPath != null) {
      val tokens: Array[String] = classPath.split(String.valueOf(File.pathSeparatorChar))
      for (entry <- tokens) {
        var path: String = entry
        if (path.startsWith("-y-") || path.startsWith("-n-")) {
          path = path.substring(3)
        }
        if (entry.startsWith("-n-") == false) {
          super.addClassPath(path)
        }
      }
    }
    initialized = true
  }

  @SuppressWarnings(Array("unchecked", "rawtypes")) override def loadClass(name: String): Class[_] = {
    try {
      return loadClass(name, false)
    }
    catch {
      case e: NoClassDefFoundError => {
        throw new ClassNotFoundException(name)
      }
    }
  }

  override def addClassPath(classPath: String) {
    if (initialized) {
      if (!classPath.endsWith("WEB-INF/classes/")) return
    }
    super.addClassPath(classPath)
  }

  override def addJars(jars: Resource) {
    if (initialized) {
      return
    }
    super.addJars(jars)
  }

  private var initialized: Boolean = false
}








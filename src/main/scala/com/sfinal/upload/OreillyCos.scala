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
package com.sfinal.upload

/**
 * OreillyCos.
 */
object OreillyCos {
  def isMultipartSupported: Boolean = {
    if (isMultipartSupported == null) {
      detectOreillyCos
    }
    return isMultipartSupported
  }

  def init(saveDirectory: String, maxPostSize: Int, encoding: String) {
    if (isMultipartSupported) {
      MultipartRequest.init(saveDirectory, maxPostSize, encoding)
    }
  }

  private def detectOreillyCos {
    try {
      Class.forName("com.oreilly.servlet.MultipartRequest")
      isMultipartSupported = true
    }
    catch {
      case e: ClassNotFoundException => {
        isMultipartSupported = false
      }
    }
  }

  private var isMultipartSupported: Boolean = null
}



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

import java.io.File

/**
 * UploadFile.
 */
class UploadFile {
  def this(parameterName: String, saveDirectory: String, filesystemName: String, originalFileName: String, contentType: String) {
    this()
    this.parameterName = parameterName
    this.saveDirectory = saveDirectory
    this.fileName = filesystemName
    this.originalFileName = originalFileName
    this.contentType = contentType
  }

  def getParameterName: String = {
    return parameterName
  }

  def getFileName: String = {
    return fileName
  }

  def getOriginalFileName: String = {
    return originalFileName
  }

  def getContentType: String = {
    return contentType
  }

  def getSaveDirectory: String = {
    return saveDirectory
  }

  def getFile: File = {
    if (saveDirectory == null || fileName == null) {
      return null
    }
    else {
      return new File(saveDirectory + File.separator + fileName)
    }
  }

  private var parameterName: String = null
  private var saveDirectory: String = null
  private var fileName: String = null
  private var originalFileName: String = null
  private var contentType: String = null
}








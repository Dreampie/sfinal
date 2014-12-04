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
import java.io.IOException
import java.util.ArrayList
import java.util.Enumeration
import java.util.HashMap
import java.util.List
import java.util.Map
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import com.sfinal.upload.UploadFile
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy

/**
 * MultipartRequest.
 */
@SuppressWarnings(Array("rawtypes", "unchecked")) object MultipartRequest {
  private[upload] def init(saveDirectory: String, maxPostSize: Int, encoding: String) {
    MultipartRequest.saveDirectory = saveDirectory
    MultipartRequest.maxPostSize = maxPostSize
    MultipartRequest.encoding = encoding
    MultipartRequest.isMultipartSupported = true
  }

  private var saveDirectory: String = null
  private var maxPostSize: Int = 0
  private var encoding: String = null
  private var isMultipartSupported: Boolean = false
  private final val fileRenamePolicy: DefaultFileRenamePolicy = new DefaultFileRenamePolicy
}

@SuppressWarnings(Array("rawtypes", "unchecked")) class MultipartRequest extends HttpServletRequestWrapper {
  def this(request: HttpServletRequest, saveDirectory: String, maxPostSize: Int, encoding: String) {
    this()
    `super`(request)
    wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding)
  }

  def this(request: HttpServletRequest, saveDirectory: String, maxPostSize: Int) {
    this()
    `super`(request)
    wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding)
  }

  def this(request: HttpServletRequest, saveDirectory: String) {
    this()
    `super`(request)
    wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding)
  }

  def this(request: HttpServletRequest) {
    this()
    `super`(request)
    wrapMultipartRequest(request, saveDirectory, maxPostSize, encoding)
  }

  /**
   * 添加对相对路径的支持
   * 1: 以 "/" 开头或者以 "x:开头的目录被认为是绝对路径
   * 2: 其它路径被认为是相对路径, 需要 JFinalConfig.uploadedFileSaveDirectory 结合
   */
  private def handleSaveDirectory(saveDirectory: String): String = {
    if (saveDirectory.startsWith("/") || saveDirectory.indexOf(":") == 1) return saveDirectory
    else return MultipartRequest.saveDirectory + saveDirectory
  }

  private def wrapMultipartRequest(request: HttpServletRequest, saveDirectory: String, maxPostSize: Int, encoding: String) {
    if (!isMultipartSupported) throw new RuntimeException("Oreilly cos.jar is not found, Multipart post can not be supported.")
    saveDirectory = handleSaveDirectory(saveDirectory)
    val dir: File = new File(saveDirectory)
    if (!dir.exists) {
      if (!dir.mkdirs) {
        throw new RuntimeException("Directory " + saveDirectory + " not exists and can not create directory.")
      }
    }
    uploadFiles = new ArrayList[UploadFile]
    try {
      multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize, encoding, fileRenamePolicy)
      val files: Enumeration[_] = multipartRequest.getFileNames
      while (files.hasMoreElements) {
        val name: String = files.nextElement.asInstanceOf[String]
        val filesystemName: String = multipartRequest.getFilesystemName(name)
        if (filesystemName != null) {
          val originalFileName: String = multipartRequest.getOriginalFileName(name)
          val contentType: String = multipartRequest.getContentType(name)
          val uploadFile: UploadFile = new UploadFile(name, saveDirectory, filesystemName, originalFileName, contentType)
          if (isSafeFile(uploadFile)) uploadFiles.add(uploadFile)
        }
      }
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
  }

  private def isSafeFile(uploadFile: UploadFile): Boolean = {
    if (uploadFile.getFileName.toLowerCase.endsWith(".jsp")) {
      uploadFile.getFile.delete
      return false
    }
    return true
  }

  def getFiles: List[UploadFile] = {
    return uploadFiles
  }

  /**
   * Methods to replace HttpServletRequest methods
   */
  override def getParameterNames: Enumeration[_] = {
    return multipartRequest.getParameterNames
  }

  override def getParameter(name: String): String = {
    return multipartRequest.getParameter(name)
  }

  override def getParameterValues(name: String): Array[String] = {
    return multipartRequest.getParameterValues(name)
  }

  override def getParameterMap: Map[_, _] = {
    val map: Map[_, _] = new HashMap[_, _]
    val enumm: Enumeration[_] = getParameterNames
    while (enumm.hasMoreElements) {
      val name: String = enumm.nextElement.asInstanceOf[String]
      map.put(name, multipartRequest.getParameterValues(name))
    }
    return map
  }

  private var uploadFiles: List[UploadFile] = null
  private var multipartRequest: MultipartRequest = null
}








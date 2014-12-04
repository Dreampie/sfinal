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
package com.sfinal.render

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse
import com.sfinal.kit.PathKit
import com.sfinal.kit.StrKit
import com.sfinal.render.{RenderException, RenderFactory, Render}

/**
 * FileRender.
 */
object FileRender {
  private[render] def init(fileDownloadPath: String, servletContext: ServletContext) {
    FileRender.fileDownloadPath = fileDownloadPath
    FileRender.servletContext = servletContext
    webRootPath = PathKit.getWebRootPath
  }

  private final val DEFAULT_CONTENT_TYPE: String = "application/octet-stream"
  private var fileDownloadPath: String = null
  private var servletContext: ServletContext = null
  private var webRootPath: String = null
}

class FileRender extends Render {
  def this(file: File) {
    this()
    this.file = file
  }

  def this(fileName: String) {
    this()
    fileName = if (fileName.startsWith("/")) webRootPath + fileName else fileDownloadPath + fileName
    this.file = new File(fileName)
  }

  def render {
    if (file == null || !file.isFile) {
      RenderFactory.me.getErrorRender(404).setContext(request, response).render
      return
    }
    response.setHeader("Accept-Ranges", "bytes")
    response.setHeader("Content-disposition", "attachment; filename=" + encodeFileName(file.getName))
    val contentType: String = servletContext.getMimeType(file.getName)
    response.setContentType(if (contentType != null) contentType else DEFAULT_CONTENT_TYPE)
    if (StrKit.isBlank(request.getHeader("Range"))) normalRender
    else rangeRender
  }

  private def encodeFileName(fileName: String): String = {
    try {
      return new String(fileName.getBytes("GBK"), "ISO8859-1")
    }
    catch {
      case e: UnsupportedEncodingException => {
        return fileName
      }
    }
  }

  private def normalRender {
    response.setHeader("Content-Length", String.valueOf(file.length))
    var inputStream: InputStream = null
    var outputStream: OutputStream = null
    try {
      inputStream = new BufferedInputStream(new FileInputStream(file))
      outputStream = response.getOutputStream
      val buffer: Array[Byte] = new Array[Byte](1024)
      {
        var len: Int = -1
        while ((({
          len = inputStream.read(buffer); len
        })) != -1) {
          outputStream.write(buffer, 0, len)
        }
      }
      outputStream.flush
    }
    catch {
      case e: IOException => {
        if (getDevMode) throw new RenderException(e)
      }
      case e: Exception => {
        throw new RenderException(e)
      }
    }
    finally {
      if (inputStream != null) try {
        inputStream.close
      }
      catch {
        case e: IOException => {
        }
      }
      if (outputStream != null) try {
        outputStream.close
      }
      catch {
        case e: IOException => {
        }
      }
    }
  }

  private def rangeRender {
    val range: Array[Long] = Array(null, null)
    processRange(range)
    val contentLength: String = String.valueOf(range(1).longValue - range(0).longValue + 1)
    response.setHeader("Content-Length", contentLength)
    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT)
    val contentRange: StringBuilder = new StringBuilder("bytes ").append(String.valueOf(range(0))).append("-").append(String.valueOf(range(1))).append("/").append(String.valueOf(file.length))
    response.setHeader("Content-Range", contentRange.toString)
    var inputStream: InputStream = null
    var outputStream: OutputStream = null
    try {
      val start: Long = range(0)
      val end: Long = range(1)
      inputStream = new BufferedInputStream(new FileInputStream(file))
      if (inputStream.skip(start) != start) throw new RuntimeException("File skip error")
      outputStream = response.getOutputStream
      val buffer: Array[Byte] = new Array[Byte](1024)
      var position: Long = start
      {
        var len: Int = 0
        while (position <= end && (({
          len = inputStream.read(buffer); len
        })) != -1) {
          if (position + len <= end) {
            outputStream.write(buffer, 0, len)
            position += len
          }
          else {
            {
              var i: Int = 0
              while (i < len && position <= end) {
                {
                  outputStream.write(buffer(i))
                  position += 1
                }
                ({
                  i += 1; i - 1
                })
              }
            }
          }
        }
      }
      outputStream.flush
    }
    catch {
      case e: IOException => {
        if (getDevMode) throw new RenderException(e)
      }
      case e: Exception => {
        throw new RenderException(e)
      }
    }
    finally {
      if (inputStream != null) try {
        inputStream.close
      }
      catch {
        case e: IOException => {
        }
      }
      if (outputStream != null) try {
        outputStream.close
      }
      catch {
        case e: IOException => {
        }
      }
    }
  }

  /**
   * Examples of byte-ranges-specifier values (assuming an entity-body of length 10000):
   * The first 500 bytes (byte offsets 0-499, inclusive): bytes=0-499
   * The second 500 bytes (byte offsets 500-999, inclusive): bytes=500-999
   * The final 500 bytes (byte offsets 9500-9999, inclusive): bytes=-500
   * Or bytes=9500-
   */
  private def processRange(range: Array[Long]) {
    var rangeStr: String = request.getHeader("Range")
    val index: Int = rangeStr.indexOf(',')
    if (index != -1) rangeStr = rangeStr.substring(0, index)
    rangeStr = rangeStr.replace("bytes=", "")
    val arr: Array[String] = rangeStr.split("-", 2)
    if (arr.length < 2) throw new RuntimeException("Range error")
    val fileLength: Long = file.length
    {
      var i: Int = 0
      while (i < range.length) {
        {
          if (StrKit.notBlank(arr(i))) {
            range(i) = Long.parseLong(arr(i).trim)
            if (range(i) >= fileLength) range(i) = fileLength - 1
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    if (range(0) != null && range(1) == null) {
      range(1) = fileLength - 1
    }
    else if (range(0) == null && range(1) != null) {
      range(0) = fileLength - range(1)
      range(1) = fileLength - 1
    }
    if (range(0) == null || range(1) == null || range(0).longValue > range(1).longValue) throw new RuntimeException("Range error")
  }

  private var file: File = null
}


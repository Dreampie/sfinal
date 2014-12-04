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

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.Map
import java.util.Map.Entry
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.servlet.http.HttpServletRequest

import com.sfinal.kit.StrKit

/**
 * HttpKit
 */
object HttpKit {
  private def initSSLSocketFactory: SSLSocketFactory = {
    try {
      val tm: Array[TrustManager] = Array(new HttpKit#TrustAnyTrustManager)
      val sslContext: SSLContext = SSLContext.getInstance("TLS", "SunJSSE")
      sslContext.init(null, tm, new SecureRandom)
      return sslContext.getSocketFactory
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  private def getHttpConnection(url: String, method: String, headers: Map[String, String]): HttpURLConnection = {
    val _url: URL = new URL(url)
    val conn: HttpURLConnection = _url.openConnection.asInstanceOf[HttpURLConnection]
    if (conn.isInstanceOf[HttpsURLConnection]) {
      (conn.asInstanceOf[HttpsURLConnection]).setSSLSocketFactory(sslSocketFactory)
      (conn.asInstanceOf[HttpsURLConnection]).setHostnameVerifier(trustAnyHostnameVerifier)
    }
    conn.setRequestMethod(method)
    conn.setDoOutput(true)
    conn.setDoInput(true)
    conn.setConnectTimeout(19000)
    conn.setReadTimeout(19000)
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36")
    if (headers != null && !headers.isEmpty)
    import scala.collection.JavaConversions._
    for (entry <- headers.entrySet) conn.setRequestProperty(entry.getKey, entry.getValue)
    return conn
  }

  /**
   * Send GET request
   */
  def get(url: String, queryParas: Map[String, String], headers: Map[String, String]): String = {
    var conn: HttpURLConnection = null
    try {
      conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), GET, headers)
      conn.connect
      return readResponseString(conn)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    finally {
      if (conn != null) {
        conn.disconnect
      }
    }
  }

  def get(url: String, queryParas: Map[String, String]): String = {
    return get(url, queryParas, null)
  }

  def get(url: String): String = {
    return get(url, null, null)
  }

  /**
   * Send POST request
   */
  def post(url: String, queryParas: Map[String, String], data: String, headers: Map[String, String]): String = {
    var conn: HttpURLConnection = null
    try {
      conn = getHttpConnection(buildUrlWithQueryString(url, queryParas), POST, headers)
      conn.connect
      val out: OutputStream = conn.getOutputStream
      out.write(data.getBytes(CHARSET))
      out.flush
      out.close
      return readResponseString(conn)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    finally {
      if (conn != null) {
        conn.disconnect
      }
    }
  }

  def post(url: String, queryParas: Map[String, String], data: String): String = {
    return post(url, queryParas, data, null)
  }

  def post(url: String, data: String, headers: Map[String, String]): String = {
    return post(url, null, data, headers)
  }

  def post(url: String, data: String): String = {
    return post(url, null, data, null)
  }

  private def readResponseString(conn: HttpURLConnection): String = {
    val sb: StringBuilder = new StringBuilder
    var inputStream: InputStream = null
    try {
      inputStream = conn.getInputStream
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(inputStream, CHARSET))
      var line: String = null
      while ((({
        line = reader.readLine; line
      })) != null) {
        sb.append(line).append("\n")
      }
      return sb.toString
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    finally {
      if (inputStream != null) {
        try {
          inputStream.close
        }
        catch {
          case e: IOException => {
            e.printStackTrace
          }
        }
      }
    }
  }

  /**
   * Build queryString of the url
   */
  private def buildUrlWithQueryString(url: String, queryParas: Map[String, String]): String = {
    if (queryParas == null || queryParas.isEmpty) return url
    val sb: StringBuilder = new StringBuilder(url)
    var isFirst: Boolean = false
    if (url.indexOf("?") == -1) {
      isFirst = true
      sb.append("?")
    }
    else {
      isFirst = false
    }
    import scala.collection.JavaConversions._
    for (entry <- queryParas.entrySet) {
      if (isFirst) isFirst = false
      else sb.append("&")
      val key: String = entry.getKey
      var value: String = entry.getValue
      if (StrKit.notBlank(value)) try {
        value = URLEncoder.encode(value, CHARSET)
      }
      catch {
        case e: UnsupportedEncodingException => {
          throw new RuntimeException(e)
        }
      }
      sb.append(key).append("=").append(value)
    }
    return sb.toString
  }

  def readIncommingRequestData(request: HttpServletRequest): String = {
    var br: BufferedReader = null
    try {
      val result: StringBuilder = new StringBuilder
      br = request.getReader
      {
        var line: String = null
        while ((({
          line = br.readLine; line
        })) != null) {
          result.append(line).append("\n")
        }
      }
      return result.toString
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
    finally {
      if (br != null) try {
        br.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  private final val GET: String = "GET"
  private final val POST: String = "POST"
  private final val CHARSET: String = "UTF-8"
  private final val sslSocketFactory: SSLSocketFactory = initSSLSocketFactory
  private final val trustAnyHostnameVerifier: HttpKit#TrustAnyHostnameVerifier = new HttpKit#TrustAnyHostnameVerifier
}

class HttpKit {
  private def this() {
    this()
  }

  /**
   * https 域名校验
   */
  private class TrustAnyHostnameVerifier extends HostnameVerifier {
    def verify(hostname: String, session: SSLSession): Boolean = {
      return true
    }
  }

  /**
   * https 证书管理
   */
  private class TrustAnyTrustManager extends X509TrustManager {
    def getAcceptedIssuers: Array[X509Certificate] = {
      return null
    }

    def checkClientTrusted(chain: Array[X509Certificate], authType: String) {
    }

    def checkServerTrusted(chain: Array[X509Certificate], authType: String) {
    }
  }

}








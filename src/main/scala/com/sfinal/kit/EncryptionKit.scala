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

import java.security.MessageDigest

object EncryptionKit {
  def md5Encrypt(srcStr: String): String = {
    return encrypt("MD5", srcStr)
  }

  def sha1Encrypt(srcStr: String): String = {
    return encrypt("SHA-1", srcStr)
  }

  def sha256Encrypt(srcStr: String): String = {
    return encrypt("SHA-256", srcStr)
  }

  def sha384Encrypt(srcStr: String): String = {
    return encrypt("SHA-384", srcStr)
  }

  def sha512Encrypt(srcStr: String): String = {
    return encrypt("SHA-512", srcStr)
  }

  def encrypt(algorithm: String, srcStr: String): String = {
    try {
      val result: StringBuilder = new StringBuilder
      val md: MessageDigest = MessageDigest.getInstance(algorithm)
      val bytes: Array[Byte] = md.digest(srcStr.getBytes("utf-8"))
      for (b <- bytes) {
        val hex: String = Integer.toHexString(b & 0xFF)
        if (hex.length == 1) result.append("0")
        result.append(hex)
      }
      return result.toString
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }
}







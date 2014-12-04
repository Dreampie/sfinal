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
package com.sfinal.plugin.auth

import java.security.SecureRandom
import java.util.Random
import javax.servlet.http.HttpServletRequest

/**
 * TODO 考虑改名为 SessionIdBuilder
 */
object AccessTokenBuilder {
  def getAccessToken(request: HttpServletRequest): String = {
    var accessToken: String = null
    while (accessToken == null || accessToken.length == 0) {
      var r0: Long = if (weakRandom) (hashCode ^ Runtime.getRuntime.freeMemory ^ random.nextInt ^ ((request.hashCode.asInstanceOf[Long]) << 32)) else random.nextLong
      var r1: Long = random.nextLong
      if (r0 < 0) r0 = -r0
      if (r1 < 0) r1 = -r1
      accessToken = Long.toString(r0, 36) + Long.toString(r1, 36)
    }
    return accessToken
  }

  private var random: Random = null
  private var weakRandom: Boolean = false
  private var hashCode: Int = new AccessTokenBuilder().hashCode
}

class AccessTokenBuilder {
  private def this() {
    this()
    try {
      random = new SecureRandom
      weakRandom = false
    }
    catch {
      case e: Exception => {
        random = new Random
        weakRandom = true
        System.err.println("Could not generate SecureRandom for accessToken randomness")
      }
    }
  }
}


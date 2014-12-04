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
package com.sfinal.token

import java.util.ArrayList
import java.util.List
import java.util.Random
import java.util.Timer
import java.util.TimerTask
import com.sfinal.core.Const
import com.sfinal.core.Controller
import com.sfinal.kit.StrKit

/**
 * TokenManager.
 */
object TokenManager {
  def init(tokenCache: ITokenCache) {
    if (tokenCache == null) return
    TokenManager.tokenCache = tokenCache
    val halfTimeOut: Long = Const.MIN_SECONDS_OF_TOKEN_TIME_OUT * 1000 / 2
    new Timer().schedule(new TimerTask {
      def run {
        removeTimeOutToken
      }
    }, halfTimeOut, halfTimeOut)
  }

  /**
   * Create Token.
   * @param controller
   * @param tokenName token name
   * @param secondsOfTimeOut seconds of time out, for ITokenCache only.
   */
  def createToken(controller: Controller, tokenName: String, secondsOfTimeOut: Int) {
    if (tokenCache == null) {
      val tokenId: String = String.valueOf(random.nextLong)
      controller.setAttr(tokenName, tokenId)
      controller.setSessionAttr(tokenName, tokenId)
      createTokenHiddenField(controller, tokenName, tokenId)
    }
    else {
      createTokenUseTokenIdGenerator(controller, tokenName, secondsOfTimeOut)
    }
  }

  /**
   * Use ${token!} in view for generate hidden input field.
   */
  private def createTokenHiddenField(controller: Controller, tokenName: String, tokenId: String) {
    val sb: StringBuilder = new StringBuilder
    sb.append("<input type='hidden' name='").append(tokenName).append("' value='" + tokenId).append("' />")
    controller.setAttr("token", sb.toString)
  }

  private def createTokenUseTokenIdGenerator(controller: Controller, tokenName: String, secondsOfTimeOut: Int) {
    if (secondsOfTimeOut < Const.MIN_SECONDS_OF_TOKEN_TIME_OUT) secondsOfTimeOut = Const.MIN_SECONDS_OF_TOKEN_TIME_OUT
    var tokenId: String = null
    var token: Token = null
    var safeCounter: Int = 8
    do {
      if (({
        safeCounter -= 1; safeCounter + 1
      }) == 0) throw new RuntimeException("Can not create tokenId.")
      tokenId = String.valueOf(random.nextLong)
      token = new Token(tokenId, System.currentTimeMillis + (secondsOfTimeOut * 1000))
    } while (tokenId == null || tokenCache.contains(token))
    controller.setAttr(tokenName, tokenId)
    tokenCache.put(token)
    createTokenHiddenField(controller, tokenName, tokenId)
  }

  /**
   * Check token to prevent resubmit.
   * @param tokenName the token name used in view's form
   * @return true if token is correct
   */
  def validateToken(controller: Controller, tokenName: String): Boolean = {
    val clientTokenId: String = controller.getPara(tokenName)
    if (tokenCache == null) {
      val serverTokenId: String = controller.getSessionAttr(tokenName)
      controller.removeSessionAttr(tokenName)
      return StrKit.notBlank(clientTokenId) && (clientTokenId == serverTokenId)
    }
    else {
      val token: Token = new Token(clientTokenId)
      val result: Boolean = tokenCache.contains(token)
      tokenCache.remove(token)
      return result
    }
  }

  private def removeTimeOutToken {
    val tokenInCache: List[Token] = tokenCache.getAll
    if (tokenInCache == null) return
    val timeOutTokens: List[Token] = new ArrayList[Token]
    val currentTime: Long = System.currentTimeMillis
    import scala.collection.JavaConversions._
    for (token <- tokenInCache) if (token.getExpirationTime <= currentTime) timeOutTokens.add(token)
    import scala.collection.JavaConversions._
    for (token <- timeOutTokens) tokenCache.remove(token)
  }

  private var tokenCache: ITokenCache = null
  private var random: Random = new Random
}

class TokenManager {
  private def this() {
    this()
  }
}







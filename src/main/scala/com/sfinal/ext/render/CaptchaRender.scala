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
package com.sfinal.ext.render

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.IOException
import java.security.MessageDigest
import java.util.Random
import javax.imageio.ImageIO
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import com.sfinal.core.Controller
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit
import com.sfinal.render.Render
import com.sfinal.render.Render

object CaptchaRender {
  private final def encrypt(srcStr: String): String = {
    try {
      var result: String = ""
      val md: MessageDigest = MessageDigest.getInstance("MD5")
      val bytes: Array[Byte] = md.digest(srcStr.getBytes("utf-8"))
      for (b <- bytes) {
        val hex: String = Integer.toHexString(b & 0xFF).toUpperCase
        result += (if ((hex.length == 1)) "0" else "") + hex
      }
      return result
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  def validate(controller: Controller, inputRandomCode: String, randomCodeKey: String): Boolean = {
    if (StrKit.isBlank(inputRandomCode)) return false
    try {
      inputRandomCode = encrypt(inputRandomCode)
      return inputRandomCode == controller.getCookie(randomCodeKey)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        return false
      }
    }
  }

  private final val WIDTH: Int = 85
  private final val HEIGHT: Int = 20
  private final val strArr: Array[String] = Array("3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y")
}

class CaptchaRender extends Render {
  def this(randomCodeKey: String) {
    this()
    if (StrKit.isBlank(randomCodeKey)) throw new IllegalArgumentException("randomCodeKey can not be blank")
    this.randomCodeKey = randomCodeKey
  }

  def render {
    val image: BufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
    var vCode: String = drawGraphic(image)
    vCode = encrypt(vCode)
    val cookie: Cookie = new Cookie(randomCodeKey, vCode)
    cookie.setMaxAge(-1)
    cookie.setPath("/")
    response.addCookie(cookie)
    response.setHeader("Pragma", "no-cache")
    response.setHeader("Cache-Control", "no-cache")
    response.setDateHeader("Expires", 0)
    response.setContentType("image/jpeg")
    var sos: ServletOutputStream = null
    try {
      sos = response.getOutputStream
      ImageIO.write(image, "jpeg", sos)
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    finally {
      if (sos != null) try {
        sos.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  private def drawGraphic(image: BufferedImage): String = {
    val g: Graphics = image.createGraphics
    val random: Random = new Random
    g.setColor(getRandColor(200, 250))
    g.fillRect(0, 0, WIDTH, HEIGHT)
    g.setFont(new Font("Times New Roman", Font.PLAIN, 18))
    g.setColor(getRandColor(160, 200))
    {
      var i: Int = 0
      while (i < 155) {
        {
          val x: Int = random.nextInt(WIDTH)
          val y: Int = random.nextInt(HEIGHT)
          val xl: Int = random.nextInt(12)
          val yl: Int = random.nextInt(12)
          g.drawLine(x, y, x + xl, y + yl)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    var sRand: String = ""
    {
      var i: Int = 0
      while (i < 4) {
        {
          val rand: String = String.valueOf(strArr(random.nextInt(strArr.length)))
          sRand += rand
          g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)))
          g.drawString(rand, 13 * i + 6, 16)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    g.dispose
    return sRand
  }

  private def getRandColor(fc: Int, bc: Int): Color = {
    val random: Random = new Random
    if (fc > 255) fc = 255
    if (bc > 255) bc = 255
    val r: Int = fc + random.nextInt(bc - fc)
    val g: Int = fc + random.nextInt(bc - fc)
    val b: Int = fc + random.nextInt(bc - fc)
    return new Color(r, g, b)
  }

  private var randomCodeKey: String = null
}




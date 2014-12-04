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
package com.sfinal.validate

import java.lang.reflect.Method
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller
import com.sfinal.validate.ValidateException

/**
 * Validator.
 */
object Validator {
  private final val emailAddressPattern: String = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b"
  private final val datePattern: String = "yyyy-MM-dd"
}

abstract class Validator extends Interceptor {
  protected def setShortCircuit(shortCircuit: Boolean) {
    this.shortCircuit = shortCircuit
  }

  final def intercept(invocation: ActionInvocation) {
    var validator: Validator = null
    try {
      validator = getClass.newInstance
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    validator.controller = invocation.getController
    validator.invocation = invocation
    try {
      validator.validate(validator.controller)
    }
    catch {
      case e: ValidateException => {
      }
    }
    if (validator.invalid) validator.handleError(validator.controller)
    else invocation.invoke
  }

  /**
   * Use validateXxx method to validate the parameters of this action.
   */
  protected def validate(c: Controller)

  /**
   * Handle the validate error.
   * Example:<br>
   * controller.keepPara();<br>
   * controller.render("register.html");
   */
  protected def handleError(c: Controller)

  /**
   * Add message when validate failure.
   */
  protected def addError(errorKey: String, errorMessage: String) {
    invalid = true
    controller.setAttr(errorKey, errorMessage)
    if (shortCircuit) {
      throw new ValidateException
    }
  }

  /**
   * Return the action key of this action.
   */
  protected def getActionKey: String = {
    return invocation.getActionKey
  }

  /**
   * Return the controller key of this action.
   */
  protected def getControllerKey: String = {
    return invocation.getControllerKey
  }

  /**
   * Return the method of this action.
   */
  protected def getActionMethod: Method = {
    return invocation.getMethod
  }

  /**
   * Return view path of this controller.
   */
  protected def getViewPath: String = {
    return invocation.getViewPath
  }

  /**
   * Validate Required.
   */
  protected def validateRequired(field: String, errorKey: String, errorMessage: String) {
    val value: String = controller.getPara(field)
    if (value == null || ("" == value)) addError(errorKey, errorMessage)
  }

  /**
   * Validate required string.
   */
  protected def validateRequiredString(field: String, errorKey: String, errorMessage: String) {
    val value: String = controller.getPara(field)
    if (value == null || ("" == value.trim)) addError(errorKey, errorMessage)
  }

  /**
   * Validate integer.
   */
  protected def validateInteger(field: String, min: Int, max: Int, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      val temp: Int = Integer.parseInt(value)
      if (temp < min || temp > max) addError(errorKey, errorMessage)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate long.
   */
  protected def validateLong(field: String, min: Long, max: Long, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      val temp: Long = Long.parseLong(value)
      if (temp < min || temp > max) addError(errorKey, errorMessage)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate long.
   */
  protected def validateLong(field: String, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      Long.parseLong(value)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate double.
   */
  protected def validateDouble(field: String, min: Double, max: Double, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      val temp: Double = Double.parseDouble(value)
      if (temp < min || temp > max) addError(errorKey, errorMessage)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate double.
   */
  protected def validateDouble(field: String, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      Double.parseDouble(value)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate date.
   */
  protected def validateDate(field: String, min: Date, max: Date, errorKey: String, errorMessage: String) {
    try {
      val value: String = controller.getPara(field)
      val temp: Date = new SimpleDateFormat(datePattern).parse(value)
      if (temp.before(min) || temp.after(max)) addError(errorKey, errorMessage)
    }
    catch {
      case e: Exception => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate date. Date formate: yyyy-MM-dd
   */
  protected def validateDate(field: String, min: String, max: String, errorKey: String, errorMessage: String) {
    try {
      val sdf: SimpleDateFormat = new SimpleDateFormat(datePattern)
      validateDate(field, sdf.parse(min), sdf.parse(max), errorKey, errorMessage)
    }
    catch {
      case e: ParseException => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate equal field. Usually validate password and password again
   */
  protected def validateEqualField(field_1: String, field_2: String, errorKey: String, errorMessage: String) {
    val value_1: String = controller.getPara(field_1)
    val value_2: String = controller.getPara(field_2)
    if (value_1 == null || value_2 == null || (!(value_1 == value_2))) addError(errorKey, errorMessage)
  }

  /**
   * Validate equal string.
   */
  protected def validateEqualString(s1: String, s2: String, errorKey: String, errorMessage: String) {
    if (s1 == null || s2 == null || (!(s1 == s2))) addError(errorKey, errorMessage)
  }

  /**
   * Validate equal integer.
   */
  protected def validateEqualInteger(i1: Integer, i2: Integer, errorKey: String, errorMessage: String) {
    if (i1 == null || i2 == null || (i1.intValue != i2.intValue)) addError(errorKey, errorMessage)
  }

  /**
   * Validate email.
   */
  protected def validateEmail(field: String, errorKey: String, errorMessage: String) {
    validateRegex(field, emailAddressPattern, false, errorKey, errorMessage)
  }

  /**
   * Validate URL.
   */
  protected def validateUrl(field: String, errorKey: String, errorMessage: String) {
    try {
      var value: String = controller.getPara(field)
      if (value.startsWith("https://")) value = "http://" + value.substring(8)
      new URL(value)
    }
    catch {
      case e: MalformedURLException => {
        addError(errorKey, errorMessage)
      }
    }
  }

  /**
   * Validate regular expression.
   */
  protected def validateRegex(field: String, regExpression: String, isCaseSensitive: Boolean, errorKey: String, errorMessage: String) {
    val value: String = controller.getPara(field)
    if (value == null) {
      addError(errorKey, errorMessage)
      return
    }
    val pattern: Pattern = if (isCaseSensitive) Pattern.compile(regExpression) else Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE)
    val matcher: Matcher = pattern.matcher(value)
    if (!matcher.matches) addError(errorKey, errorMessage)
  }

  /**
   * Validate regular expression and case sensitive.
   */
  protected def validateRegex(field: String, regExpression: String, errorKey: String, errorMessage: String) {
    validateRegex(field, regExpression, true, errorKey, errorMessage)
  }

  protected def validateString(field: String, notBlank: Boolean, minLen: Int, maxLen: Int, errorKey: String, errorMessage: String) {
    val value: String = controller.getPara(field)
    if (value == null || value.length < minLen || value.length > maxLen) addError(errorKey, errorMessage)
    else if (notBlank && ("" == value.trim)) addError(errorKey, errorMessage)
  }

  /**
   * Validate string.
   */
  protected def validateString(field: String, minLen: Int, maxLen: Int, errorKey: String, errorMessage: String) {
    validateString(field, true, minLen, maxLen, errorKey, errorMessage)
  }

  /**
   * Validate token created by Controller.createToken(String).
   */
  protected def validateToken(tokenName: String, errorKey: String, errorMessage: String) {
    if (controller.validateToken(tokenName) == false) addError(errorKey, errorMessage)
  }

  /**
   * Validate token created by Controller.createToken().
   */
  protected def validateToken(errorKey: String, errorMessage: String) {
    if (controller.validateToken == false) addError(errorKey, errorMessage)
  }

  private var controller: Controller = null
  private var invocation: ActionInvocation = null
  private var shortCircuit: Boolean = false
  private var invalid: Boolean = false
}






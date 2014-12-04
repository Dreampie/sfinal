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
package com.sfinal.i18n

import java.util.Enumeration
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import com.sfinal.core.Const

/**
 * I18N support.
 *
 * 1: Config parameters in JFinalConfig
 * 2: Init I18N in JFinal 
 * 3: I18N support text with Locale
 * 4: Controller use I18N.getText(...) with Local setting in I18nInterceptor
 * 5: The resource file in WEB-INF/classes
 *
 * important: Locale can create with language like new Locale("xxx");
 *
 * need test
 * Using String get Locale was learned from Strus2
 */
object I18N {
  def me: I18N = {
    if (me == null) classOf[I18N] synchronized {
      if (me == null) me = new I18N
    }
    return me
  }

  def init(baseName: String, defaultLocale: Locale, i18nMaxAgeOfCookie: Integer) {
    I18N.baseName = baseName
    if (defaultLocale != null) I18N.defaultLocale = defaultLocale
    if (i18nMaxAgeOfCookie != null) I18N.i18nMaxAgeOfCookie = i18nMaxAgeOfCookie
  }

  def getDefaultLocale: Locale = {
    return defaultLocale
  }

  final def getI18nMaxAgeOfCookie: Int = {
    return i18nMaxAgeOfCookie
  }

  private def getResourceBundle(locale: Locale): ResourceBundle = {
    val resourceBundleKey: String = getresourceBundleKey(locale)
    var resourceBundle: ResourceBundle = bundlesMap.get(resourceBundleKey)
    if (resourceBundle == null) {
      try {
        resourceBundle = ResourceBundle.getBundle(baseName, locale)
        bundlesMap.put(resourceBundleKey, resourceBundle)
      }
      catch {
        case e: MissingResourceException => {
          resourceBundle = NULL_RESOURCE_BUNDLE
        }
      }
    }
    return resourceBundle
  }

  /**
   * 将来只改这里就可以了: resourceBundleKey的生成规则
   */
  private def getresourceBundleKey(locale: Locale): String = {
    return baseName + locale.toString
  }

  def getText(key: String): String = {
    return getResourceBundle(defaultLocale).getString(key)
  }

  def getText(key: String, defaultValue: String): String = {
    val result: String = getResourceBundle(defaultLocale).getString(key)
    return if (result != null) result else defaultValue
  }

  def getText(key: String, locale: Locale): String = {
    return getResourceBundle(locale).getString(key)
  }

  def getText(key: String, defaultValue: String, locale: Locale): String = {
    val result: String = getResourceBundle(locale).getString(key)
    return if (result != null) result else defaultValue
  }

  def localeFromString(localeStr: String): Locale = {
    if ((localeStr == null) || (localeStr.trim.length == 0) || (("_" == localeStr))) {
      return defaultLocale
    }
    var index: Int = localeStr.indexOf('_')
    if (index < 0) {
      return new Locale(localeStr)
    }
    val language: String = localeStr.substring(0, index)
    if (index == localeStr.length) {
      return new Locale(language)
    }
    localeStr = localeStr.substring(index + 1)
    index = localeStr.indexOf('_')
    if (index < 0) {
      return new Locale(language, localeStr)
    }
    val country: String = localeStr.substring(0, index)
    if (index == localeStr.length) {
      return new Locale(language, country)
    }
    localeStr = localeStr.substring(index + 1)
    return new Locale(language, country, localeStr)
  }

  def main(args: Array[String]) {
    System.out.println(Locale.CHINESE.getLanguage)
    System.out.println(Locale.CHINA.getLanguage)
    System.out.println(Locale.SIMPLIFIED_CHINESE.getLanguage)
    System.out.println(Locale.TRADITIONAL_CHINESE.getLanguage)
    System.out.println(Locale.TAIWAN.getLanguage)
    val shoudong: Locale = new Locale("en")
    System.out.println(shoudong.getLanguage == Locale.US.getLanguage)
    System.out.println(shoudong.getLanguage == Locale.ENGLISH.getLanguage)
    System.out.println(shoudong.getLanguage == Locale.CANADA.getLanguage)
    System.out.println(shoudong.getLanguage == Locale.UK.getLanguage)
    System.out.println(shoudong.getLanguage == Locale.CANADA_FRENCH.getLanguage)
  }

  private var baseName: String = null
  private var defaultLocale: Locale = Locale.getDefault
  private var i18nMaxAgeOfCookie: Int = Const.DEFAULT_I18N_MAX_AGE_OF_COOKIE
  private final val NULL_RESOURCE_BUNDLE: I18N.NullResourceBundle = new I18N.NullResourceBundle
  private final val bundlesMap: ConcurrentMap[String, ResourceBundle] = new ConcurrentHashMap[String, ResourceBundle]
  @volatile
  private var me: I18N = null

  private class NullResourceBundle extends ResourceBundle {
    def getKeys: Enumeration[String] = {
      return null
    }

    protected def handleGetObject(key: String): AnyRef = {
      return null
    }
  }

}

class I18N {
  private def this() {
    this()
  }
}









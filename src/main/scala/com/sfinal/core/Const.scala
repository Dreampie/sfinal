package com.sfinal.core

import java.io.File

import com.sfinal.render.ViewType
import com.sfinal.render.ViewType.ViewType

/**
 * Created by wangrenhui on 14/12/2.
 */
trait Const {
  final val JFINAL_VERSION: String = "1.9"
  final val DEFAULT_VIEW_TYPE: ViewType = ViewType.FREE_MARKER
  final val DEFAULT_ENCODING: String = "UTF-8"
  final val DEFAULT_DEV_MODE: Boolean = false
  final val DEFAULT_URL_PARA_SEPARATOR: String = "-"
  final val DEFAULT_JSP_EXTENSION: String = ".jsp"
  final val DEFAULT_FREE_MARKER_EXTENSION: String = ".html"
  final val DEFAULT_VELOCITY_EXTENSION: String = ".vm"
  final val DEFAULT_FILE_RENDER_BASE_PATH: String = File.separator + "download" + File.separator
  final val DEFAULT_MAX_POST_SIZE: Int = 1024 * 1024 * 10
  final val I18N_LOCALE: String = "__I18N_LOCALE__"
  final val DEFAULT_I18N_MAX_AGE_OF_COOKIE: Int = 999999999
  final val DEFAULT_FREEMARKER_TEMPLATE_UPDATE_DELAY: Int = 3600
  final val DEFAULT_TOKEN_NAME: String = "jfinal_token"
  final val DEFAULT_SECONDS_OF_TOKEN_TIME_OUT: Int = 900
  final val MIN_SECONDS_OF_TOKEN_TIME_OUT: Int = 300
}

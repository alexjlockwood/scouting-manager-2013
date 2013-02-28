package edu.cmu.girlsofsteel.scout.util;

import android.util.Log;

/**
 * Logging utilities stolen from the Google I/O 2012 application source code.
 *
 * @author Alex Lockwood
 */
public final class LogUtil {

  public static String makeLogTag(Class<?> cls) {
    return cls.getSimpleName();
  }

  public static void LOGE(String tag, String message) {
    if (Log.isLoggable(tag, Log.ERROR)) {
      Log.e(tag, message);
    }
  }

  public static void LOGE(String tag, String message, Exception ex) {
    if (Log.isLoggable(tag, Log.ERROR)) {
      Log.e(tag, message, ex);
    }
  }

  public static void LOGW(String tag, String message) {
    if (Log.isLoggable(tag, Log.WARN)) {
      Log.w(tag, message);
    }
  }

  public static void LOGD(String tag, String message) {
    if (Log.isLoggable(tag, Log.DEBUG)) {
      Log.d(tag, message);
    }
  }

  public static void LOGI(String tag, String message) {
    if (Log.isLoggable(tag, Log.INFO)) {
      Log.i(tag, message);
    }
  }

  public static void LOGV(String tag, String message) {
    if (Log.isLoggable(tag, Log.VERBOSE)) {
      Log.v(tag, message);
    }
  }
}

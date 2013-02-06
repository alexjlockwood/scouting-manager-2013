package edu.cmu.girlsofsteel.scout.util;

import android.util.Log;

public final class LogUtil {

  public static String makeLogTag(Class<?> cls) {
    return cls.getSimpleName();
  }

  public static void LOGE(String tag, String message) {
    if (Log.isLoggable(tag, Log.ERROR)) {
      Log.e(tag, message);
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

  public static void LOGV(String tag, String message) {
    if (Log.isLoggable(tag, Log.VERBOSE)) {
      Log.v(tag, message);
    }
  }
}

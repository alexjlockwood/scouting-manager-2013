package edu.cmu.girlsofsteel.scout.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

/**
 * An assortment of compatibility helpers.
 */
public final class CompatUtil {

  /**
   * May 20, 2010: Android 2.2 (API 8).
   */
  public static boolean hasFroyo() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
  }

  /**
   * December 6, 2010: Android 2.3 (API 9)
   */
  public static boolean hasGingerbread() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
  }

  /**
   * February 22, 2011: Android 3.0 (API 11)
   */
  public static boolean hasHoneycomb() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  /**
   * May 10, 2011: Android 3.1 (API 12)
   */
  public static boolean hasHoneycombMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
  }

  /**
   * July 15, 2011: Android 3.2 (API 13)
   */
  public static boolean hasHoneycombMR2() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
  }

  /**
   * October 19, 2011: Android 4.0 (API 14)
   */
  public static boolean hasICS() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  /**
   * December 16, 2011: Android 4.0.3 (API 15)
   */
  public static boolean hasICSMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
  }

  /**
   * July 9, 2012: Android 4.1 (API 16)
   */
  public static boolean hasJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  /**
   * November 13, 2012: Android 4.2 (API 17)
   */
  public static boolean hasJellyBeanMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
  }

  /**
   * Returns true if the device has a LARGE or XLARGE screen size.
   */
  public static boolean isTablet(Context ctx) {
    int screenLayout = ctx.getResources().getConfiguration().screenLayout;
    return Configuration.SCREENLAYOUT_SIZE_LARGE <= (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
  }

  /**
   * Returns true if the device runs Honeycomb or greater and has a LARGE or
   * XLARGE screen size.
   */
  public static boolean isHoneycombTablet(Context ctx) {
    return hasHoneycomb() && isTablet(ctx);
  }
}

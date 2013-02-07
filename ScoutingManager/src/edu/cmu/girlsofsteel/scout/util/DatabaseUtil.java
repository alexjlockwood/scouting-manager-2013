package edu.cmu.girlsofsteel.scout.util;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public final class DatabaseUtil {

  public static void insert(Context ctx, Uri uri, ContentValues initialValues) {
    ContentResolver cr = ctx.getContentResolver();
    AsyncQueryHandler handler = new AsyncQueryHandler(cr) {};
    handler.startInsert(-1, null, uri, initialValues);
  }

  private DatabaseUtil() {
  }
}

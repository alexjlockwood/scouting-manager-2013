package edu.cmu.girlsofsteel.scout.provider;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGI;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGV;

import java.util.Arrays;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.provider.ScoutDatabase.Tables;

/**
 * The ContentProvider for the Scouting application. This class serves as an
 * abstraction layer over the SQLiteDatabase.
 *
 * This class is not responsible for deep-insertions, deletions, queries, etc.
 * The client must perform such actions on their own.
 */
public class ScoutProvider extends ContentProvider {
  private static final String TAG = ScoutProvider.class.getSimpleName();

  private static final int TEAMS = 100;
  private static final int TEAMS_ID = 101;
  private static final int TEAM_MATCHES = 200;
  private static final int TEAM_MATCHES_TID = 201;
  private static final UriMatcher sUriMatcher;

  static {
    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    sUriMatcher.addURI(ScoutContract.AUTHORITY, "teams", TEAMS);
    sUriMatcher.addURI(ScoutContract.AUTHORITY, "teams/#", TEAMS_ID);
    sUriMatcher.addURI(ScoutContract.AUTHORITY, "team_matches", TEAM_MATCHES);
    sUriMatcher.addURI(ScoutContract.AUTHORITY, "team_matches/teams/#", TEAM_MATCHES_TID);
  }

  private ScoutDatabase mOpenHelper;

  @Override
  public boolean onCreate() {
    mOpenHelper = new ScoutDatabase(getContext());
    return true;
  }

  @Override
  public String getType(Uri uri) {
    switch (sUriMatcher.match(uri)) {
      case TEAMS:
        return Teams.CONTENT_TYPE;
      case TEAMS_ID:
        return Teams.CONTENT_ITEM_TYPE;
      case TEAM_MATCHES:
        return TeamMatches.CONTENT_TYPE;
      case TEAM_MATCHES_TID:
        return TeamMatches.CONTENT_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    LOGV(TAG, "delete(uri=" + uri + ")");

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    String table, where = null;

    switch (sUriMatcher.match(uri)) {
      case TEAMS:
        table = Tables.TEAMS;
        break;
      case TEAMS_ID:
        table = Tables.TEAMS;
        where = Teams._ID + " = " + uri.getLastPathSegment();
        break;
      case TEAM_MATCHES:
        table = Tables.TEAM_MATCHES;
        break;
      case TEAM_MATCHES_TID:
        table = Tables.TEAM_MATCHES;
        where = TeamMatches.TEAM_ID + " = " + uri.getLastPathSegment();
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    if (!TextUtils.isEmpty(selection)) {
      if (where != null) {
        where += " AND " + selection;
      } else {
        where = selection;
      }
    }

    int rowsAffected = db.delete(table, where, selectionArgs);
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsAffected;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    switch (sUriMatcher.match(uri)) {
      case TEAMS: {
        long newId = db.insert(Tables.TEAMS, null, values);
        if (newId > 0) {
          Uri newUri = Teams.teamIdUri(newId);
          getContext().getContentResolver().notifyChange(newUri, null);
          return newUri;
        }
        // TODO: consider throwing an SQLiteException here...
        return null;
      }
      case TEAM_MATCHES: {
        long newId = db.insert(Tables.TEAM_MATCHES, null, values);
        if (newId > 0) {
          Uri newUri = TeamMatches.CONTENT_URI.buildUpon().appendPath("" + newId).build();
          getContext().getContentResolver().notifyChange(newUri, null);
          return newUri;
        }
        // TODO: consider throwing an SQLiteException here...
        return null;
      }
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs, String sortOrder) {
    LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ", where=" + where
        + ", whereArgs=" + Arrays.toString(whereArgs) + ", sortOrder=" + sortOrder + ")");

    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    switch (sUriMatcher.match(uri)) {
      case TEAMS:
        qb.setTables(Tables.TEAMS);
        break;
      case TEAMS_ID:
        qb.setTables(Tables.TEAMS);
        qb.appendWhere(Teams._ID + " = " + uri.getLastPathSegment());
        break;
      case TEAM_MATCHES:
        qb.setTables(Tables.TEAM_MATCHES);
        break;
      case TEAM_MATCHES_TID:
        qb.setTables(Tables.TEAM_MATCHES);
        qb.appendWhere(TeamMatches.TEAM_ID + " = " + uri.getLastPathSegment());
        break;
      default:
        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    Cursor cur = qb.query(mOpenHelper.getReadableDatabase(), projection, where, whereArgs, null, null, sortOrder);
    cur.setNotificationUri(getContext().getContentResolver(), uri);
    return cur;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    LOGI(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");

    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    String table, where = null;
    switch (sUriMatcher.match(uri)) {
      case TEAMS:
        table = Tables.TEAMS;
        break;
      case TEAMS_ID:
        table = Tables.TEAMS;
        where = Teams._ID + " = " + uri.getLastPathSegment();
        break;
      case TEAM_MATCHES:
        table = Tables.TEAM_MATCHES;
        break;
      case TEAM_MATCHES_TID:
        table = Tables.TEAM_MATCHES;
        where = TeamMatches.TEAM_ID + " = " + uri.getLastPathSegment();
        break;
      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    if (!TextUtils.isEmpty(selection)) {
      if (where != null) {
        where += " AND " + selection;
      } else {
        where = selection;
      }
    }

    int rowsAffected = db.update(table, values, where, selectionArgs);
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsAffected;
  }
}
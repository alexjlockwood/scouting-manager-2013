package edu.cmu.girlsofsteel.scout.util;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import edu.cmu.girlsofsteel.scout.ScoutMode;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

/**
 * A bunch of storage utility methods which perform asynchronous disk I/O (ie,
 * reading/writing to the shared preferences and SQLite database).
 *
 * @author Alex Lockwood
 */
public final class StorageUtil {

  private static final String KEY_SCOUT_MODE = "scout_mode";
  private static final String KEY_TEAM_MATCH_ID = "team_match_id";

  /**
   * Insert a single team into the database. This method is asynchronous.
   */
  public static void insertTeam(Context ctx, ContentValues values) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startInsert(-1, null, Teams.CONTENT_URI, values);
  }

  /**
   * Insert a single team match into the database. This method is asynchronous.
   */
  public static void insertTeamMatch(Context ctx, ContentValues values) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startInsert(-1, null, TeamMatches.CONTENT_URI, values);
  }

  /**
   * Update a single team's record in the database. This method is asynchronous.
   */
  public static void updateTeam(Context ctx, long teamId, ContentValues values) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startUpdate(-1, null, Teams.teamIdUri(teamId), values, null, null);
  }

  /**
   * Update a single team's record in the database. This method is asynchronous.
   */
  public static void updateTeamMatch(Context ctx, long teamMatchId, ContentValues values) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startUpdate(-1, null, TeamMatches.CONTENT_URI, values, TeamMatches._ID + "=?",
        new String[] { "" + teamMatchId });
  }

  /**
   * Delete a single team match from the database. This method is asynchronous.
   */
  public static void deleteTeamMatch(Context ctx, long teamMatchId) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startDelete(-1, null, TeamMatches.CONTENT_URI, TeamMatches._ID + "=?",
        new String[] { "" + teamMatchId });
  }

  /**
   * Delete multiple teams from the database. This method is asynchronous.
   */
  public static void deleteTeams(final Context ctx, final long... teamIds) {
    new Thread() {
      @Override
      public void run() {
        final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (int i = 0; i < teamIds.length; i++) {
          ops.add(ContentProviderOperation.newDelete(Teams.CONTENT_URI)
              .withSelection(Teams._ID + "=" + teamIds[i], null)
              .build());
          ops.add(ContentProviderOperation.newDelete(TeamMatches.CONTENT_URI)
              .withSelection(TeamMatches.TEAM_ID + "=" + teamIds[i], null)
              .build());
        }
        try {
          ctx.getContentResolver().applyBatch(ScoutContract.AUTHORITY, ops);
        } catch (RemoteException e) {
          // Will never happen
        } catch (OperationApplicationException e) {
          // Will never happen (I think)
        }
      }
    }.start();
  }

  public static void purgeAll(Context ctx) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startDelete(-1, null, ScoutContract.BASE_URI, null, null);
  }

  /**
   * Saves the most recent scouting mode ('team' or 'match').
   */
  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  public static void setScoutMode(Context ctx, ScoutMode mode) {
    // Store false for 'team', true for 'match'
    Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
    editor.putBoolean(KEY_SCOUT_MODE, mode == ScoutMode.MATCH);
    if (CompatUtil.hasGingerbread()) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

  /**
   * Gets the most recent scouting mode ('team' or 'match').
   */
  public static ScoutMode getScoutMode(Context ctx) {
    // Store "false" for TEAM, "true" for MATCH
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    return prefs.getBoolean(KEY_SCOUT_MODE, false) ? ScoutMode.MATCH : ScoutMode.TEAM;
  }

  /**
   * Saves the last selected team match id in match scout mode.
   */
  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  public static void setLastSelectedTeamMatchId(Context ctx, long id) {
    Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
    editor.putLong(KEY_TEAM_MATCH_ID, id);
    if (CompatUtil.hasGingerbread()) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

  /**
   * Gets the last selected team match id in match scout mode. Returns -1 if
   * none exists.
   */
  public static long getLastSelectedTeamMatchId(Context ctx) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    return prefs.getLong(KEY_TEAM_MATCH_ID, -1);
  }

  private StorageUtil() {
  }
}

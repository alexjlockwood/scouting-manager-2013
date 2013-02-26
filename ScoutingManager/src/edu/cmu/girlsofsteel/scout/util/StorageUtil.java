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
import edu.cmu.girlsofsteel.scout.MainActivity.ScoutMode;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public final class StorageUtil {

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
   * Delete multiple teams from the database. This method is asynchronous.
   */
  public static void deleteTeams(final Context ctx, final long... teamIds) {
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
    // TODO: delete foreign key references
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

  private static final String KEY_SCOUT_MODE = "scout_mode";

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

  private StorageUtil() {
  }
}

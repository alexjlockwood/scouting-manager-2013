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

// TODO: delete foreign key references
public final class StorageUtil {

  public static void insertTeam(Context ctx, ContentValues initialValues) {
    AsyncQueryHandler handler = new AsyncQueryHandler(ctx.getContentResolver()) {
    };
    handler.startInsert(-1, null, Teams.CONTENT_URI, initialValues);
  }

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

  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  public static void setScoutMode(Context ctx, ScoutMode mode) {
    Editor editor = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
    // Store "false" for TEAM, "true" for MATCH
    editor.putBoolean("scout_mode", mode == ScoutMode.MATCH);
    if (UIUtil.hasGingerbread()) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

  public static ScoutMode getScoutMode(Context ctx) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    // Store "false" for TEAM, "true" for MATCH
    return prefs.getBoolean("scout_mode", false) ? ScoutMode.MATCH : ScoutMode.TEAM;
  }

  private StorageUtil() {
  }
}

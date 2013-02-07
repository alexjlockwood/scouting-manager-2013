package edu.cmu.girlsofsteel.scout.util;

import java.util.ArrayList;

import android.content.AsyncQueryHandler;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

// TODO: delete foreign key references
public final class DatabaseUtil {

  public static void insertTeam(Context ctx, ContentValues initialValues) {
    ContentResolver cr = ctx.getContentResolver();
    AsyncQueryHandler handler = new AsyncQueryHandler(cr) {
    };
    handler.startInsert(-1, null, Teams.CONTENT_URI, initialValues);
  }

  public static void deleteTeams(final Context ctx, long[] teamIds) {
    final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    for (int i = 0; i < teamIds.length; i++) {
      ops.add(ContentProviderOperation.newDelete(Teams.CONTENT_URI)
          .withSelection(Teams._ID + "=" + teamIds[i], null)
          .build());
      ops.add(ContentProviderOperation.newDelete(TeamMatches.CONTENT_URI)
          .withSelection(TeamMatches.TEAM_ID + "=" + teamIds[i], null)
          .build());
    }

    new Thread() {
      @Override
      public void run() {
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

  private DatabaseUtil() {
  }
}

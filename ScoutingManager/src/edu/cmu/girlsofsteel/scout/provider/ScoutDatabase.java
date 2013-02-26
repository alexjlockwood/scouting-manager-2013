package edu.cmu.girlsofsteel.scout.provider;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGW;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!
// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!
// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!
// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!
// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!
// TODO: Don't use ON CONFLICT IGNORE! Weird incompatibilities with 2.3 vs 4.0!

/**
 * This class is responsible for the creation of the database. The ScoutProvider
 * class allows access to the data stored in the database.
 */
public class ScoutDatabase extends SQLiteOpenHelper {
  private static final String TAG = makeLogTag(ScoutDatabase.class);

  private static final String DATABASE_NAME = "scouting_manager.db";
  private static final int DATABASE_VERSION = 1;

  /** SQLite table names. */
  interface Tables {
    String TEAMS = "teams";
    String TEAM_MATCHES = "team_matches";
  }

  /** {@code REFERENCES} clauses. */
  private interface References {
    String TEAM_ID = "REFERENCES " + Tables.TEAMS + "(" + Teams._ID + ")";
  }

  public ScoutDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Tables.TEAMS + " ("
        + Teams._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

        + Teams.NUMBER + " INTEGER NOT NULL,"
        + Teams.NAME + " TEXT,"
        + Teams.PHOTO + " TEXT,"

        + Teams.ROBOT_CAN_SCORE_ON_LOW + " INTEGER,"
        + Teams.ROBOT_CAN_SCORE_ON_MID + " INTEGER,"
        + Teams.ROBOT_CAN_SCORE_ON_HIGH + " INTEGER,"

        // + Teams.ROBOT_CAN_CLIMB + " INTEGER,"
        + Teams.ROBOT_CAN_CLIMB_LEVEL_ONE + " INTEGER,"
        + Teams.ROBOT_CAN_CLIMB_LEVEL_TWO + " INTEGER,"
        + Teams.ROBOT_CAN_CLIMB_LEVEL_THREE + " INTEGER,"
        + Teams.ROBOT_CAN_HELP_CLIMB + " INTEGER,"

        + Teams.ROBOT_CAN_GO_UNDER_TOWER + " INTEGER,"
        + Teams.ROBOT_NUM_DRIVING_GEARS + " INTEGER,"
        + Teams.ROBOT_DRIVE_TRAIN + " INTEGER,"
        + Teams.ROBOT_TYPE_OF_WHEEL + " INTEGER,"

        + "UNIQUE (" + Teams.NUMBER + ") ON CONFLICT IGNORE);");

    db.execSQL("CREATE TABLE " + Tables.TEAM_MATCHES + " ("
        + TeamMatches._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

        + TeamMatches.TEAM_ID + " INTEGER NOT NULL " + References.TEAM_ID + ","
        + TeamMatches.MATCH_NUMBER + " INTEGER NOT NULL, "

        + TeamMatches.AUTO_SHOTS_MADE_LOW + " INTEGER,"
        + TeamMatches.AUTO_SHOTS_MADE_MID + " INTEGER,"
        + TeamMatches.AUTO_SHOTS_MADE_HIGH + " INTEGER,"
        + TeamMatches.TELE_SHOTS_MADE_LOW + " INTEGER,"
        + TeamMatches.TELE_SHOTS_MADE_MID + " INTEGER,"
        + TeamMatches.TELE_SHOTS_MADE_HIGH + " INTEGER,"

        + TeamMatches.SHOOTS_FROM_BACK_RIGHT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_BACK_LEFT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_FRONT_RIGHT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_FRONT_LEFT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_SIDE_RIGHT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_SIDE_LEFT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_FRONT + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_ANYWHERE + " INTEGER,"
        + TeamMatches.SHOOTS_FROM_OTHER + " INTEGER,"

        // + TeamMatches.TOWER_LEVEL_NONE + " INTEGER,"
        + TeamMatches.TOWER_LEVEL_ONE + " INTEGER,"
        + TeamMatches.TOWER_LEVEL_TWO + " INTEGER,"
        + TeamMatches.TOWER_LEVEL_THREE + " INTEGER,"
        + TeamMatches.TOWER_FELL_OFF + " INTEGER,"

        + TeamMatches.HUMAN_PLAYER_ABILITY + " INTEGER,"

        + TeamMatches.FRISBEES_FROM_FEEDER + " INTEGER,"
        + TeamMatches.FRISBEES_FROM_FLOOR + " INTEGER,"

        + TeamMatches.ROBOT_STRATEGY + " INTEGER,"
        + TeamMatches.ROBOT_SPEED + " INTEGER,"
        + TeamMatches.ROBOT_MANEUVERABILITY + " INTEGER,"
        + TeamMatches.ROBOT_PENALTY + " INTEGER,"

        + "UNIQUE (" + TeamMatches.MATCH_NUMBER + "," + TeamMatches.TEAM_ID
        + ") ON CONFLICT IGNORE);");
  }

  /**
   * The database currently upgrades the database by destroying the existing
   * data. The real application MUST upgrade the database in place.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    LOGW(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ".");
  }
}

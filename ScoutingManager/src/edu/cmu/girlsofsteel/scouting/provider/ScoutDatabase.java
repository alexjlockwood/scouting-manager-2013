  package edu.cmu.girlsofsteel.scouting.provider;

import static edu.cmu.girlsofsteel.scouting.util.LogUtil.LOGW;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import edu.cmu.girlsofsteel.scouting.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scouting.provider.ScoutContract.Teams;

/**
 * This class is responsible for the creation of the database. The ScoutProvider
 * class allows access to the data stored in the database.
 */
public class ScoutDatabase extends SQLiteOpenHelper {
  private static final String TAG = ScoutDatabase.class.getSimpleName();

  private static final String DATABASE_NAME = "scouting_manager.db";
  private static final int DATABASE_VERSION = 2;

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
        + Teams.RANK + " INTEGER,"

        + Teams.ROBOT_CAN_SCORE_ON_LOW + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_CAN_SCORE_ON_MID + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_CAN_SCORE_ON_HIGH + " INTEGER DEFAULT 0,"

        + Teams.ROBOT_CAN_CLIMB_LEVEL_ONE + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_CAN_CLIMB_LEVEL_TWO + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_CAN_CLIMB_LEVEL_THREE + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_CAN_HELP_CLIMB + " INTEGER DEFAULT 0,"

        + Teams.ROBOT_NUM_DRIVING_GEARS + " INTEGER DEFAULT 0,"
        + Teams.ROBOT_DRIVE_TRAIN + " INTEGER DEFAULT -1,"
        + Teams.ROBOT_DRIVE_TRAIN_OTHER + " TEXT,"
        + Teams.ROBOT_TYPE_OF_WHEEL + " INTEGER DEFAULT -1,"
        + Teams.ROBOT_CAN_GO_UNDER_TOWER + " INTEGER DEFAULT 0,"


        + "UNIQUE (" + Teams.NUMBER + ") ON CONFLICT IGNORE);");

    db.execSQL("CREATE TABLE " + Tables.TEAM_MATCHES + " ("
        + TeamMatches._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"

        + TeamMatches.TEAM_ID + " INTEGER NOT NULL " + References.TEAM_ID + ","
        + TeamMatches.MATCH_NUMBER + " INTEGER NOT NULL, "

        + TeamMatches.AUTO_SHOTS_MADE_TOWER + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MISS_TOWER + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MADE_LOW + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MISS_LOW + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MADE_MID + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MISS_MID + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MADE_HIGH + " INTEGER DEFAULT 0,"
        + TeamMatches.AUTO_SHOTS_MISS_HIGH + " INTEGER DEFAULT 0,"

        + TeamMatches.TELE_SHOTS_MADE_TOWER + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MISS_TOWER + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MADE_LOW + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MISS_LOW + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MADE_MID + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MISS_MID + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MADE_HIGH + " INTEGER DEFAULT 0,"
        + TeamMatches.TELE_SHOTS_MISS_HIGH + " INTEGER DEFAULT 0,"

        + TeamMatches.SHOOTS_FROM_WHERE + " TEXT,"

        + TeamMatches.TOWER_LEVEL_ONE + " INTEGER DEFAULT 0,"
        + TeamMatches.TOWER_LEVEL_TWO + " INTEGER DEFAULT 0,"
        + TeamMatches.TOWER_LEVEL_THREE + " INTEGER DEFAULT 0,"
        + TeamMatches.TOWER_FELL_OFF + " INTEGER DEFAULT 0,"

        + TeamMatches.HUMAN_PLAYER_ABILITY + " INTEGER DEFAULT -1,"

        + TeamMatches.FRISBEES_FROM_FEEDER + " INTEGER DEFAULT 0,"
        + TeamMatches.FRISBEES_FROM_FLOOR + " INTEGER DEFAULT 0,"

        + TeamMatches.ROBOT_STRATEGY + " INTEGER DEFAULT -1,"
        + TeamMatches.ROBOT_SPEED + " INTEGER DEFAULT -1,"
        + TeamMatches.ROBOT_MANEUVERABILITY + " INTEGER DEFAULT -1,"
        + TeamMatches.ROBOT_PENALTY + " INTEGER DEFAULT -1,"
        + TeamMatches.COMMENTS + " TEXT,"

        + "UNIQUE (" + TeamMatches.MATCH_NUMBER + "," + TeamMatches.TEAM_ID + ") ON CONFLICT IGNORE);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    LOGW(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ".");
    db.execSQL("DROP TABLE IF EXISTS " + Tables.TEAMS);
    db.execSQL("DROP TABLE IF EXISTS " + Tables.TEAM_MATCHES);
    onCreate(db);
  }

  static void deleteDatabase(Context context) {
    context.deleteDatabase(DATABASE_NAME);
  }
}
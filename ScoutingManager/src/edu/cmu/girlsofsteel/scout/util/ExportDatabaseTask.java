package edu.cmu.girlsofsteel.scout.util;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import edu.cmu.girlsofsteel.scout.R;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

/**
 * Exports the teams and matches to a CSV file on the disk.
 * 
 * @author Alex Lockwood
 */
public class ExportDatabaseTask extends AsyncTask<Void, Void, String> {
  private static final String TAG = makeLogTag(ExportDatabaseTask.class);
  private Context mCtx;

  private static final String[] TEAMS_PROJECTION = {
    Teams.NUMBER,
    Teams.NAME,
    Teams.RANK,
    Teams.ROBOT_CAN_SCORE_ON_LOW,
    Teams.ROBOT_CAN_SCORE_ON_MID,
    Teams.ROBOT_CAN_SCORE_ON_HIGH,
    Teams.ROBOT_CAN_CLIMB_LEVEL_ONE,
    Teams.ROBOT_CAN_CLIMB_LEVEL_TWO,
    Teams.ROBOT_CAN_CLIMB_LEVEL_THREE,
    Teams.ROBOT_CAN_HELP_CLIMB,
    Teams.ROBOT_NUM_DRIVING_GEARS,
    Teams.ROBOT_DRIVE_TRAIN,
    Teams.ROBOT_TYPE_OF_WHEEL,
    Teams.ROBOT_CAN_GO_UNDER_TOWER,
  };
  
  private static final String[] MATCHES_PROJECTION = {
    TeamMatches.TEAM_ID,
    TeamMatches.MATCH_NUMBER,
    TeamMatches.AUTO_SHOTS_MADE_LOW,
    TeamMatches.AUTO_SHOTS_MISS_LOW,
    TeamMatches.AUTO_SHOTS_MADE_MID,
    TeamMatches.AUTO_SHOTS_MISS_MID,
    TeamMatches.AUTO_SHOTS_MADE_HIGH,
    TeamMatches.AUTO_SHOTS_MISS_HIGH,
    TeamMatches.TELE_SHOTS_MADE_LOW,
    TeamMatches.TELE_SHOTS_MISS_LOW,
    TeamMatches.TELE_SHOTS_MADE_MID,
    TeamMatches.TELE_SHOTS_MISS_MID,
    TeamMatches.TELE_SHOTS_MADE_HIGH,
    TeamMatches.TELE_SHOTS_MISS_HIGH,
    TeamMatches.SHOOTS_FROM_WHERE,
    TeamMatches.TOWER_LEVEL_ONE,
    TeamMatches.TOWER_LEVEL_TWO,
    TeamMatches.TOWER_LEVEL_THREE,
    TeamMatches.TOWER_FELL_OFF,
    TeamMatches.FRISBEES_FROM_FEEDER,
    TeamMatches.FRISBEES_FROM_FLOOR,
    TeamMatches.ROBOT_STRATEGY,
    TeamMatches.ROBOT_SPEED,
    TeamMatches.ROBOT_MANEUVERABILITY,
    TeamMatches.ROBOT_PENALTY,
  };
  
  private String[] wheelType;
  private String[] driveTrain;
  
  public ExportDatabaseTask(Context ctx) {
    mCtx = ctx.getApplicationContext();
    wheelType = mCtx.getResources().getStringArray(R.array.wheel_options);
    driveTrain = mCtx.getResources().getStringArray(R.array.drive_options);
  }

  @Override
  protected String doInBackground(Void... args) {
    Resources res = mCtx.getResources();
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      return res.getString(R.string.export_not_mounted);
    }

    String exportDirName = res.getString(R.string.export_dir_name);
    File exportDir = new File(Environment.getExternalStorageDirectory(), exportDirName);
    exportDir.mkdir();

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    File teamsFile = new File(exportDir, res.getString(R.string.export_teams_file_name) + "_" + timeStamp + ".csv");
    File matchesFile = new File(exportDir, res.getString(R.string.export_matches_file_name) + "_" + timeStamp + ".csv");

    CSVWriter writer;
    Cursor cursor;
    ContentResolver cr = mCtx.getContentResolver();

    try {
      // Write team data
      teamsFile.createNewFile();
      writer = new CSVWriter(new FileWriter(teamsFile));
      cursor = cr.query(Teams.CONTENT_URI, TEAMS_PROJECTION, null, null, null);

      writer.writeNext(cursor.getColumnNames());
      if (cursor.moveToFirst()) {
        do {
          writeTeamRow(writer, cursor);
        } while (cursor.moveToNext());
      }
      writer.close();
      cursor.close();

      // Write match data
      matchesFile.createNewFile();
      writer = new CSVWriter(new FileWriter(matchesFile));
      cursor = cr.query(TeamMatches.CONTENT_URI, MATCHES_PROJECTION, null, null, null);

      writer.writeNext(cursor.getColumnNames());
      if (cursor.moveToFirst()) {
        do {
          writeMatchRow(writer, cursor);
        } while (cursor.moveToNext());
      }
      writer.close();
      cursor.close();

      return res.getString(R.string.export_success);
    } catch (SQLException ex) {
      LOGE(TAG, ex.getMessage(), ex);
    } catch (IOException ex) {
      LOGE(TAG, ex.getMessage(), ex);
    }
    return res.getString(R.string.export_failed);
  }

  private void writeTeamRow(CSVWriter writer, Cursor cursor) {
    String[] row = new String[cursor.getColumnCount()];
    for (int i = 0; i < row.length; i++) {
      String currentCol = cursor.getColumnName(i);
      
      // Replace '0' and '1' with 'no' and 'yes'
      if (currentCol.equals(Teams.ROBOT_CAN_SCORE_ON_LOW)
          || currentCol.equals(Teams.ROBOT_CAN_SCORE_ON_MID)
          || currentCol.equals(Teams.ROBOT_CAN_SCORE_ON_HIGH)
          || currentCol.equals(Teams.ROBOT_CAN_HELP_CLIMB) 
          || currentCol.equals(Teams.ROBOT_CAN_CLIMB_LEVEL_ONE)
          || currentCol.equals(Teams.ROBOT_CAN_CLIMB_LEVEL_TWO)
          || currentCol.equals(Teams.ROBOT_CAN_CLIMB_LEVEL_THREE)
          || currentCol.equals(Teams.ROBOT_CAN_GO_UNDER_TOWER)) {
        row[i] = (cursor.getInt(i) == 0) ? "no" : "yes";
      }
      
      else if (currentCol.equals(Teams.ROBOT_NUM_DRIVING_GEARS)) {
        row[i] = (cursor.getInt(i) == 0) ? "one" : "multiple";
      }
      
      else if (currentCol.equals(Teams.ROBOT_DRIVE_TRAIN)) {
        int cellVal = cursor.getInt(i);
        row[i] = (cellVal != -1) ? driveTrain[cellVal] : "";
      }
      
      else if (currentCol.equals(Teams.ROBOT_TYPE_OF_WHEEL)) {
        int cellVal = cursor.getInt(i);
        row[i] = (cellVal != -1) ? wheelType[cellVal] : "";
      } 
      
      else {
        // No need to process the cell's value
        row[i] = cursor.getString(i);
      }
    }
    writer.writeNext(row);
  }
  
  private void writeMatchRow(CSVWriter writer, Cursor cursor) {
    String[] row = new String[cursor.getColumnCount()];
    for (int i = 0; i <row.length; i++) {
      String currentCol = cursor.getColumnName(i);
      
      // Replace the team id with the team number (because I'm too
      // lazy to figure out how to do joins in Android :/)
      if (currentCol.equals(TeamMatches.TEAM_ID)) {
        ContentResolver cr = mCtx.getContentResolver();
        Uri uri = Teams.teamIdUri(cursor.getInt(i));
        Cursor cur = cr.query(uri, new String[] { Teams._ID, Teams.NUMBER }, null, null, null);
        if (cur.moveToFirst()) {
          row[i] = cur.getString(cur.getColumnIndexOrThrow(Teams.NUMBER));
        } else {
          // Will never happen (just to be safe)
          row[i] = "";
        }
      }
      
      // Replace '0' and '1' with 'no' and 'yes'
      else if (currentCol.equals(TeamMatches.TOWER_LEVEL_ONE)
          || currentCol.equals(TeamMatches.TOWER_LEVEL_TWO)
          || currentCol.equals(TeamMatches.TOWER_LEVEL_THREE)
          || currentCol.equals(TeamMatches.TOWER_FELL_OFF)
          || currentCol.equals(TeamMatches.FRISBEES_FROM_FEEDER)) {
        row[i] = (cursor.getInt(i) == 0) ? "no" : "yes";
      } 
      
      else if (currentCol.equals(TeamMatches.HUMAN_PLAYER_ABILITY)) {
        switch (cursor.getInt(i)) {
          case 0: row[i] = "high scoring"; break;
          case 1: row[i] = "neutral"; break;
          case 2: row[i] = "impedes performance"; break;
          default: row[i] = "";
        }
      } 
      
      else if (currentCol.equals(TeamMatches.ROBOT_STRATEGY)) {
        switch (cursor.getInt(i)) {
          case 0: row[i] = "offense"; break;
          case 1: row[i] = "neutral"; break;
          case 2: row[i] = "defense"; break;
          default: row[i] = "";
        }
      } 
      
      else if (currentCol.equals(TeamMatches.ROBOT_SPEED)) {
        switch (cursor.getInt(i)) {
          case 0: row[i] = "fast"; break;
          case 1: row[i] = "medium"; break;
          case 2: row[i] = "slow"; break;
          default: row[i] = "";
        }
      }
      
      else if (currentCol.equals(TeamMatches.ROBOT_MANEUVERABILITY)) {
        switch (cursor.getInt(i)) {
          case 0: row[i] = "high"; break;
          case 1: row[i] = "medium"; break;
          case 2: row[i] = "low"; break;
          default: row[i] = "";
        }
      }
      
      else if (currentCol.equals(TeamMatches.ROBOT_PENALTY)) {
        switch (cursor.getInt(i)) {
          case 0: row[i] = "high"; break;
          case 1: row[i] = "medium"; break;
          case 2: row[i] = "low"; break;
          default: row[i] = "";
        }
      } 
      
      else {
        // No need to process the cell's value
        row[i] = cursor.getString(i);
      }
    }
    writer.writeNext(row);
  }
  
  @Override
  protected void onPostExecute(String toastMsg) {
    Toast.makeText(mCtx, toastMsg, Toast.LENGTH_SHORT).show();
  }
}
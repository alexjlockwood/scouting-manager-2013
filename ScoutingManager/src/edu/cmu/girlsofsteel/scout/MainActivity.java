package edu.cmu.girlsofsteel.scout;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.util.ExportDatabaseTask;
import edu.cmu.girlsofsteel.scout.util.ExportDatabaseTask.ExportTaskListener;

/**
 * {@link MainActivity} is the main activity for the application. It displays a
 * {@link TeamListFragment} as its sole content.
 *
 * @author Alex Lockwood
 */
public class MainActivity extends SherlockFragmentActivity {
  @SuppressWarnings("unused")
  private static final String TAG = MainActivity.class.getSimpleName();

  /** Used to pass team ids to the next activity. */
  static final String ARG_TEAM_ID = "team_id";

  /** Used to pass the current scout mode to the next activity. */
  static final String ARG_SCOUT_MODE = "scout_mode";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Disable up navigation on the application's home screen
    getSupportActionBar().setHomeButtonEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
  }

  @Override
  public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    getSupportMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_share:
        // Export all database tables to CSV files on external storage and
        // launch an Activity chooser to share these files via email, Google
        // docs, etc.
        new ExportDatabaseTask(this, mShareListener).execute();
        return true;
      case R.id.menu_export:
        // Export all database tables to CSV files on external storage
        new ExportDatabaseTask(this, mExportListener).execute();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  // Callback for the "share" menu item.
  private final ExportTaskListener mShareListener = new ExportTaskListener() {
    @Override
    public void onExportComplete(String result) { 
      Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
      if (!result.equals(getString(R.string.export_success))) {
        // Not very pretty, I know... but it was a last minute addition
        // on the day of the robotics competition. :)
        return;
      }
      
      Resources res = getResources();
      Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
      emailIntent.setType("text/csv");
      emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      String exportDirName = res.getString(R.string.export_dir_name);
      File exportDir = new File(Environment.getExternalStorageDirectory(), exportDirName);
      File teamsFile = new File(exportDir, res.getString(R.string.export_teams_file_name) + ".csv");
      File matchesFile = new File(exportDir, res.getString(R.string.export_matches_file_name) + ".csv");

      if (!teamsFile.isFile()) {
        Toast.makeText(getApplicationContext(), R.string.no_data_to_share, Toast.LENGTH_SHORT).show();
        return;
      }
        
      ArrayList<Uri> csvUris = new ArrayList<Uri>();
      csvUris.add(Uri.fromFile(teamsFile));
      if (matchesFile.isFile()) {
        csvUris.add(Uri.fromFile(matchesFile));
      }

      emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Scouting Manager - scouting data");
      emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "See attachments...");
      emailIntent.putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, csvUris);
      startActivity(Intent.createChooser(emailIntent, getString(R.string.share_scouting_data)));
    }
  };
  
  // Callback for the "export" menu item.
  private final ExportTaskListener mExportListener = new ExportTaskListener() {
    @Override
    public void onExportComplete(String result) { 
      Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }
  };
}
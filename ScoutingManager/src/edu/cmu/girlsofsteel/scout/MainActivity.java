package edu.cmu.girlsofsteel.scout;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.ExportDatabaseTask;
import edu.cmu.girlsofsteel.scout.util.LogUtil;

/**
 * {@link MainActivity} is the main activity for the application. It displays a
 * {@link TeamListFragment} as its sole content.
 *
 * @author Alex Lockwood
 */
public class MainActivity extends SherlockFragmentActivity {

  @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(MainActivity.class);
  // Used to pass team ids to the next activity
  static final String ARG_TEAM_ID = "team_id";
  // Used to pass the current scout mode to the next activity
  static final String ARG_SCOUT_MODE = "scout_mode";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Disable up navigation on the application's home screen
    ActionBar actionBar = getSupportActionBar();
    actionBar.setHomeButtonEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(false);
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    getSupportMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_export:
        new ExportDatabaseTask(this).execute(Teams.CONTENT_URI);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Defines an enum type to determine whether the application is in
   * "team scouting mode" or "match scouting mode".
   */
  public static enum ScoutMode {
    TEAM,
    MATCH,
  }
}

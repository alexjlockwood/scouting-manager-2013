package edu.cmu.girlsofsteel.scout;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.util.ExportDatabaseTask;

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
      case R.id.menu_export:
        // Export all database tables to CSV files on external storage
        new ExportDatabaseTask(this).execute();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
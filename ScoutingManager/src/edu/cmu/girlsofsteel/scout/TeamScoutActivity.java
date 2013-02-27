package edu.cmu.girlsofsteel.scout;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.util.LogUtil;

/**
 * {@link TeamScoutActivity} is the base activity for team-scouting. It displays
 * a {@link TeamDetailsFragment} as its sole content.
 *
 * This activity requires a team id in order to function. It should always be
 * passed a team id as an intent extra from the {@link TeamListFragment}.
 *
 * @author Alex Lockwood
 */
public class TeamScoutActivity extends SherlockFragmentActivity {

  @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(TeamScoutActivity.class);
  private static final String ARG_TEAM_ID = MainActivity.ARG_TEAM_ID;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getIntent() == null || getIntent().getLongExtra(ARG_TEAM_ID, -1) == -1) {
      throw new RuntimeException("This activity requires a valid team id in order to function!");
    }

    // Enable up navigation
    ActionBar actionBar = getSupportActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);

    // Add the ScoutTeamDetailFragment as the activity's sole content
    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(android.R.id.content) == null) {
      TeamDetailsFragment frag = new TeamDetailsFragment();
      frag.setArguments(getIntent().getExtras());
      fm.beginTransaction().add(android.R.id.content, frag).commit();
    }
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}

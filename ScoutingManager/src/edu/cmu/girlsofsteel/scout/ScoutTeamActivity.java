package edu.cmu.girlsofsteel.scout;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.util.LogUtil;

/**
 * {@link ScoutTeamActivity} is the base activity for team-scouting. It displays
 * a {@link ScoutTeamDetailsFragment} as its sole content.
 *
 * This activity requires a team id in order to function. It should always be
 * passed a team id as an intent extra from the {@link TeamListFragment}.
 *
 * @author Alex Lockwood
 */
public class ScoutTeamActivity extends SherlockFragmentActivity {

  @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(ScoutTeamActivity.class);
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
    actionBar.setTitle(R.string.title_team_scout);

    // Add the ScoutTeamDetailFragment as the activity's sole content
    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(android.R.id.content) == null) {
      ScoutTeamDetailsFragment frag = new ScoutTeamDetailsFragment();
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

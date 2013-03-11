package edu.cmu.girlsofsteel.scout;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

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
  private static final String TAG = TeamScoutActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Enable up navigation
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Add the ScoutTeamDetailFragment as the activity's sole content
    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(android.R.id.content) == null) {
      TeamDetailsFragment frag = new TeamDetailsFragment();
      frag.setArguments(getIntent().getExtras());
      fm.beginTransaction().add(android.R.id.content, frag).commit();
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}

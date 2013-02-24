package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.util.LogUtil;

public class TeamScoutActivity extends SherlockFragmentActivity {

  // @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(TeamScoutActivity.class);

  private long mTeamId = -1L;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getIntent() != null) {
      mTeamId = getIntent().getLongExtra(MainActivity.TEAM_ID_EXTRA, -1L);
    }
    if (mTeamId == -1L) {
      LOGE(TAG, "This activity requires a valid team id in order to function properly.");
    }

    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(android.R.id.content) == null) {
      TeamScoutFragment frag = TeamScoutFragment.newInstance(mTeamId);
      fm.beginTransaction().add(android.R.id.content, frag).commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    getSupportMenuInflater().inflate(R.menu.activity_team_scout, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    // switch (item.getItemId()) {
    // }
    return super.onOptionsItemSelected(item);
  }
}

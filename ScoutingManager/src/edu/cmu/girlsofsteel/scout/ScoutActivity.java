package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.MainActivity.ScoutMode;
import edu.cmu.girlsofsteel.scout.util.LogUtil;

public class ScoutActivity extends SherlockFragmentActivity {

  // @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(ScoutActivity.class);

  private long mTeamId = -1L;
  private ScoutMode mScoutMode = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (getIntent() != null) {
      mTeamId = getIntent().getLongExtra(MainActivity.TEAM_ID_EXTRA, -1L);
      boolean scoutMode = getIntent().getBooleanExtra(MainActivity.SCOUT_MODE_EXTRA, false);
      mScoutMode = scoutMode ? ScoutMode.MATCH : ScoutMode.TEAM;
    }

    if (mTeamId == -1L || mScoutMode == null) {
      LOGE(TAG, "This activity requires valid intent extras to function properly!");
    }

    FragmentManager fm = getSupportFragmentManager();
    if (fm.findFragmentById(android.R.id.content) == null) {
      Fragment frag;
      if (mScoutMode == ScoutMode.TEAM) {
        frag = ScoutTeamFragment.newInstance(mTeamId);
      } else {
        frag = ScoutMatchFragment.newInstance(mTeamId);
      }
      fm.beginTransaction().add(android.R.id.content, frag).commit();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
    int menuId;
    if (mScoutMode == ScoutMode.TEAM) {
      menuId = R.menu.activity_team_scout;
    } else {
      menuId = R.menu.activity_match_scout;
    }
    getSupportMenuInflater().inflate(menuId, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // NavUtils.navigateUpFromSameTask(this);
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}

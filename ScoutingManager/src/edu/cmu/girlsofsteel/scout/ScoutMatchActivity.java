package edu.cmu.girlsofsteel.scout;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.LogUtil;

/**
 * {@link ScoutMatchActivity} is the base activity for match-scouting. It
 * displays one of two fragments on small screens and a two-pane, fragment-based
 * layout on large screens. The two fragments it displays are the
 * {@link ScoutMatchListFragment} and {@link ScoutMatchDetailsFragment} as its
 * sole content.
 *
 * This fragment receives callbacks from the {@link ScoutMatchListFragment} when
 * matches are selected. See {@link #onMatchSelected(long)}.
 *
 * This activity requires a team id in order to function. It should always be
 * passed a team id as an intent extra from the {@link TeamListFragment}.
 *
 * @author Alex Lockwood
 */
public class ScoutMatchActivity extends SherlockFragmentActivity implements
    ScoutMatchListFragment.OnMatchSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

  @SuppressWarnings("unused")
  private static final String TAG = LogUtil.makeLogTag(ScoutMatchActivity.class);

  private static final String ARG_TEAM_ID = MainActivity.ARG_TEAM_ID;

  // Used to pass team match ids to the ScoutMatchDetailsFragment
  static final String ARG_TEAM_MATCH_ID = "team_match_id";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_match_scout);

    // Enable up navigation
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Check whether the activity is using the layout version with the
    // fragment_container FrameLayout. If so, we must add the first fragment.
    if (findViewById(R.id.fragment_container) != null) {

      // However, if we're being restored from a previous state, then we don't
      // need to do anything and should return or else we could end up with
      // overlapping fragments.
      if (savedInstanceState != null) {
        return;
      }

      // Add the fragment to the 'fragment_container' FrameLayout
      ScoutMatchListFragment listFragment = new ScoutMatchListFragment();
      listFragment.setArguments(getIntent().getExtras());
      FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().add(R.id.fragment_container, listFragment).commit();
    }

    getSupportLoaderManager().initLoader(TEAM_LOADER_ID, getIntent().getExtras(), this);
  }

  /**
   * Called when the user selects a match in the {@link ScoutMatchListFragment}.
   */
  @Override
  public void onMatchSelected(long teamMatchId) {
    ScoutMatchDetailsFragment detailsFrag = (ScoutMatchDetailsFragment)
        getSupportFragmentManager().findFragmentById(R.id.match_details_fragment);

    if (detailsFrag != null) {
      // If details frag is available, we're in two-pane layout. Tell the
      // details fragment to update its content with the new team match id.
      detailsFrag.updateDetailsView(teamMatchId);
    } else {
      // If the fragment is not available, we're in the one-pane layout and must
      // swap fragments. Replace whatever is in the fragment_container view with
      // this fragment, and add the transaction to the back stack so the user
      // can navigate back.
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      Fragment fragment = ScoutMatchDetailsFragment.newInstance(teamMatchId);
      transaction.replace(R.id.fragment_container, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_LOADER_ID = 1;
  private static final String[] PROJECTION = { Teams._ID, Teams.NUMBER };

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(ARG_TEAM_ID);
    return new CursorLoader(this, Teams.teamIdUri(teamId), PROJECTION, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data.moveToFirst()) {
      String teamNumber = data.getString(data.getColumnIndexOrThrow(Teams.NUMBER));
      getSupportActionBar().setSubtitle("Team " + teamNumber);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }
}

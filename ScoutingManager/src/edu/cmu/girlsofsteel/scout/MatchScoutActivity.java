package edu.cmu.girlsofsteel.scout;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import edu.cmu.girlsofsteel.scout.dialogs.DeleteMatchDialog;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

/**
 * {@link MatchScoutActivity} is the base activity for match-scouting. It
 * displays one of two fragments on small screens and a two-pane, fragment-based
 * layout on large screens. The two fragments it displays are the
 * {@link MatchListFragment} and {@link MatchDetailsFragment} as its sole
 * content.
 *
 * This fragment receives callbacks from the {@link MatchListFragment} when
 * matches are selected. See {@link #onMatchSelected(long)}. It also receives
 * callbacks from the {@link MatchDetailsFragment} when a match is deleted.
 *
 * This activity requires a team id in order to function. It should always be
 * passed a team id as an intent extra from the {@link TeamListFragment}.
 *
 * @author Alex Lockwood
 */
public class MatchScoutActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
    MatchListFragment.OnMatchSelectedListener, MatchDetailsFragment.OnMatchDeletedListener {
  // private static final String TAG = makeLogTag(MatchScoutActivity.class);

  /** Used to pass team match ids to the {@link ScoutMatchDetailsFragment}. */
  static final String ARG_TEAM_MATCH_ID = "team_match_id";
  
  private static final int TEAM_LOADER_ID = 1;
  private static final String[] PROJECTION = { Teams._ID, Teams.NUMBER };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_match_scout);

    // Enable up navigation
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportLoaderManager().initLoader(TEAM_LOADER_ID, getIntent().getExtras(), this);

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
      MatchListFragment listFragment = new MatchListFragment();
      listFragment.setArguments(getIntent().getExtras());
      FragmentManager fm = getSupportFragmentManager();
      fm.beginTransaction().add(R.id.fragment_container, listFragment).commit();
    }
  }

  @Override
  public void onMatchSelected(long teamMatchId) {
    FragmentManager fm = getSupportFragmentManager();
    MatchDetailsFragment detailsFrag = (MatchDetailsFragment) fm.findFragmentById(R.id.match_details_fragment);

    if (detailsFrag != null) {
      // If details frag is available, we're in two-pane layout. Tell the
      // details fragment to update its content with the new team match id.
      detailsFrag.updateDetailsView(teamMatchId);
    } else {
      // If the fragment is not available, we're in the one-pane layout and must
      // swap fragments. Replace whatever is in the fragment_container view with
      // this fragment, and add the transaction to the back stack so the user
      // can navigate back.
      FragmentTransaction transaction = fm.beginTransaction();
      Fragment fragment = MatchDetailsFragment.newInstance(teamMatchId, mTeamNumber);
      transaction.replace(R.id.fragment_container, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
    }
  }

  @Override
  public void onShowConfirmationDialog(long teamMatchId) {
    DialogFragment dialog = DeleteMatchDialog.newInstance(teamMatchId);
    dialog.show(getSupportFragmentManager(), DeleteMatchDialog.class.getSimpleName());
  }

  @Override
  public void onMatchDeleted(long teamMatchId) {
    FragmentManager fm = getSupportFragmentManager();
    MatchDetailsFragment detailsFrag = (MatchDetailsFragment) fm.findFragmentById(R.id.match_details_fragment);
    if (detailsFrag != null) {
      // Notify the fragment that the match will be deleted.
      detailsFrag.matchDeleted();
    }
    System.out.println("" + teamMatchId);
    if (fm.getBackStackEntryCount() > 0) {
      fm.popBackStack();
    }
    StorageUtil.deleteTeamMatch(this, teamMatchId);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  static final String ARG_TEAM_NUMBER = "team_number";
  private String mTeamNumber;

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putString(ARG_TEAM_NUMBER, mTeamNumber);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public void onRestoreInstanceState(Bundle outState) {
    super.onRestoreInstanceState(outState);
    mTeamNumber = outState.getString(ARG_TEAM_NUMBER);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(MainActivity.ARG_TEAM_ID);
    return new CursorLoader(this, Teams.teamIdUri(teamId), PROJECTION, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data.moveToFirst()) {
      String teamNumber = data.getString(data.getColumnIndexOrThrow(Teams.NUMBER));
      getSupportActionBar().setSubtitle("Team " + teamNumber);
      mTeamNumber = teamNumber;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
          fm.popBackStack();
        } else {
          NavUtils.navigateUpFromSameTask(this);
        }
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}

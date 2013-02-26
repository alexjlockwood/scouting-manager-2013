package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;

/**
 * {@link ScoutMatchDetailsFragment} displays the match details for a particular
 * team. It's parent activity is the {@link ScoutMatchActivity}.
 *
 * This fragment requires a valid team match id in order to function properly.
 *
 * @author Alex Lockwood
 */
public class ScoutMatchDetailsFragment extends SherlockFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListFragment.class);
  private static final String ARG_TEAM_MATCH_ID = ScoutMatchActivity.ARG_TEAM_MATCH_ID;
  private long mTeamMatchId = -1L;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // If activity recreated (such as from screen rotate), restore the previous
    // match selection set by onSaveInstanceState(). This is primarily necessary
    // when in the two-pane layout.
    if (savedInstanceState != null) {
      mTeamMatchId = savedInstanceState.getLong(ARG_TEAM_MATCH_ID);
    }

    View view = inflater.inflate(R.layout.fragment_match_scout, container, false);
    // TODO: initialize views here...
    // TODO: initialize views here...
    // TODO: initialize views here...
    // TODO: initialize views here...
    // TODO: initialize views here...
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Bundle args = getArguments();
    if (args != null) {
      mTeamMatchId = args.getLong(ARG_TEAM_MATCH_ID);
    }

    getLoaderManager().initLoader(TEAM_MATCH_LOADER_ID, null, this);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(ARG_TEAM_MATCH_ID, mTeamMatchId);
  }

  public void updateDetailsView(long teamMatchId) {
    mTeamMatchId = teamMatchId;
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_MATCH_LOADER_ID = 1;
  private static final String[] PROJECTION = null;

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(mActivity, TeamMatches.CONTENT_URI, PROJECTION,
        TeamMatches._ID + "=?", new String[] { "" + mTeamMatchId }, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    populateTeamMatchData(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  private void populateTeamMatchData(Cursor data) {
    if (data.moveToFirst()) {
      // TODO: implement this
    }
  }

  /*****************/
  /** OTHER STUFF **/
  /*****************/

  private static final String TEAM_MATCH_ID_ARG = "team_match_id_arg";

  public static ScoutMatchDetailsFragment newInstance(long teamMatchId) {
    ScoutMatchDetailsFragment frag = new ScoutMatchDetailsFragment();
    Bundle args = new Bundle();
    args.putLong(TEAM_MATCH_ID_ARG, teamMatchId);
    frag.setArguments(args);
    return frag;
  }

  // Hold a reference to the underlying Activity for convenience
  private static Activity mActivity;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = activity;
  }
}

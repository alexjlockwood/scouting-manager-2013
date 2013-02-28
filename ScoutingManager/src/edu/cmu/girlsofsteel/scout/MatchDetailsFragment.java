package edu.cmu.girlsofsteel.scout;

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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.cmu.girlsofsteel.scout.dialogs.DeleteMatchDialog;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;

/**
 * {@link MatchDetailsFragment} displays the match details for a particular
 * team. It's parent activity is the {@link MatchScoutActivity}.
 *
 * This fragment requires a valid 'team_match_id' in order to function properly.
 *
 * @author Alex Lockwood
 */
public class MatchDetailsFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {
  // private static final String TAG = makeLogTag(TeamListFragment.class);

  private static final String KEY_TEAM_MATCH_ID = "key_team_match_id";
  private static final int TEAM_MATCH_LOADER_ID = 1;
  private static final String[] PROJECTION = null;
  private long mTeamMatchId = -1;
  private OnMatchDeletedListener mCallback;

  /**
   * Callback interface for the {@link DeleteMatchDialog} and the
   * {@link MatchScoutActivity}.
   */
  public interface OnMatchDeletedListener {
    /**
     * Called by the {@link MatchDetailsFragment} when the user clicks the
     * 'delete match' menu item.
     */
    public void onShowConfirmationDialog(long teamMatchId);

    /**
     * Called by the {@link DeleteMatchDialog} when the user confirms that a
     * match should be deleted.
     */
    public void onMatchDeleted(long teamMatchId);
  }

  /**
   * Static factory method which returns a new instance with the given team
   * match id set in its arguments.
   */
  public static MatchDetailsFragment newInstance(long teamMatchId) {
    MatchDetailsFragment frag = new MatchDetailsFragment();
    Bundle args = new Bundle();
    args.putLong(MatchScoutActivity.ARG_TEAM_MATCH_ID, teamMatchId);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallback = (OnMatchDeletedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnMatchDeletedListener");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      // If activity recreated due to a configuration change, restore the
      // previous match selection set by onSaveInstanceState(). This is
      // primarily necessary when in the two-pane layout.
      mTeamMatchId = savedInstanceState.getLong(KEY_TEAM_MATCH_ID);
    } else if (getArguments() != null) {
      mTeamMatchId = getArguments().getLong(MatchScoutActivity.ARG_TEAM_MATCH_ID);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(KEY_TEAM_MATCH_ID, mTeamMatchId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(TEAM_MATCH_LOADER_ID, null, this);
  }

  /**
   * Setter method that is called by the {@link MatchScoutActivity} to set the
   * current team match id to display.
   */
  public void setTeamMatchId(long teamMatchId) {
    mTeamMatchId = teamMatchId;
  }

  public void updateDetailsView(long teamMatchId) {
    mTeamMatchId = teamMatchId;
    // TODO: implement this
  }

  public void clearDetailsView() {
    // TODO: implement this
    // TODO: implement this
    // TODO: implement this
    // TODO: implement this
    // TODO: implement this
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), TeamMatches.CONTENT_URI, PROJECTION,
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
      // TODO: implement this
      // TODO: implement this
      // TODO: implement this
      // TODO: implement this
    }
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.match_details_actionbar, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_delete_match:
        mCallback.onShowConfirmationDialog(mTeamMatchId);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

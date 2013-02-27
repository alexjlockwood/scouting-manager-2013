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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;

/**
 * {@link MatchDetailsFragment} displays the match details for a particular
 * team. It's parent activity is the {@link MatchScoutActivity}.
 *
 * This fragment requires a valid 'team_match_id' in order to function properly.
 *
 * @author Alex Lockwood
 */
public class MatchDetailsFragment extends SherlockFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListFragment.class);
  private static final String ARG_TEAM_MATCH_ID = MatchScoutActivity.ARG_TEAM_MATCH_ID;
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
    setHasOptionsMenu(true);
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
    // TODO: implement this
  }

  public void clearDetailsView() {
    // TODO: implement this
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

  /*******************************/
  /** ON MATCH DELETED LISTENER **/
  /*******************************/

  private OnMatchDeletedListener mCallback;

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

  /*****************/
  /** OTHER STUFF **/
  /*****************/

  public static MatchDetailsFragment newInstance(long teamMatchId) {
    MatchDetailsFragment frag = new MatchDetailsFragment();
    Bundle args = new Bundle();
    args.putLong(ARG_TEAM_MATCH_ID, teamMatchId);
    frag.setArguments(args);
    return frag;
  }

  // Hold a reference to the underlying Activity for convenience
  private static Activity mActivity;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = activity;

    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception.
    try {
      mCallback = (OnMatchDeletedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnMatchDeletedListener");
    }
  }
}

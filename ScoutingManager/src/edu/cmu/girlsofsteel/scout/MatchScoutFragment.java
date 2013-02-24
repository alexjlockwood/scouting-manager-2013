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

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public class MatchScoutFragment extends SherlockFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListFragment.class);

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_match_scout, container, false);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getSherlockActivity().getSupportActionBar().setTitle(R.string.title_match_scout);
    getLoaderManager().initLoader(TEAM_LOADER_ID, getArguments(), this);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_LOADER_ID = 0x01;
  private static final int TEAM_MATCH_LOADER_ID = 0x02;
  private static final String[] TEAM_PROJECTION = { Teams._ID, Teams.NUMBER };

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(TEAM_ID_ARG);
    switch (id) {
      case TEAM_LOADER_ID:
        return new CursorLoader(mActivity, Teams.teamIdUri(teamId), TEAM_PROJECTION, null, null,
            null);
      case TEAM_MATCH_LOADER_ID:
        // TODO: implement this
        return null;
      default:
        // Will never happen
        return null;
    }
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case TEAM_LOADER_ID:
        populateTeamData(data);
        break;
      case TEAM_MATCH_LOADER_ID:
        populateTeamMatchData(data);
        break;
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  private void populateTeamData(Cursor data) {
    if (data.moveToFirst()) {
      String teamNumber = data.getString(data.getColumnIndexOrThrow(Teams.NUMBER));
      getSherlockActivity().getSupportActionBar().setSubtitle("Team " + teamNumber);
    }
  }

  private void populateTeamMatchData(Cursor data) {
    if (data.moveToFirst()) {
      // TODO: implement this
    }
  }

  /*****************/
  /** OTHER STUFF **/
  /*****************/

  private static final String TEAM_ID_ARG = "team_id_arg";

  public static MatchScoutFragment newInstance(long teamId) {
    MatchScoutFragment frag = new MatchScoutFragment();
    Bundle args = new Bundle();
    args.putLong(TEAM_ID_ARG, teamId);
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

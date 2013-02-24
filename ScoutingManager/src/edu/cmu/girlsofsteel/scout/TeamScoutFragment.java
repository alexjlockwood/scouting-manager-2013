package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public class TeamScoutFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {
  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListFragment.class);

  private TextView mTextView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_team_scout, container, false);
    mTextView = (TextView) view.findViewById(R.id.example_text_view);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(TEAM_LOADER_ID, getArguments(), this);
    mTextView.setText("" + getArguments().getLong(TEAM_ID_ARG));
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_LOADER_ID = 0x01;

  // private static final String[] PROJ = new String[] { Teams._ID,
  // Teams.NUMBER, Teams.PHOTO };

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(TEAM_ID_ARG);
    return new CursorLoader(mActivity, Teams.teamIdUri(teamId), null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    populateViews(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  private void populateViews(Cursor data) {
    // populate the screen with the data
  }

  /*****************/
  /** OTHER STUFF **/
  /*****************/

  private static final String TEAM_ID_ARG = "team_id_arg";

  public static TeamScoutFragment newInstance(long teamId) {
    TeamScoutFragment frag = new TeamScoutFragment();
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

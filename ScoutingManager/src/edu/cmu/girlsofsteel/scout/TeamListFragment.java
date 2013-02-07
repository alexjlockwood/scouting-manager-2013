package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockListFragment;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract;

// For small screens
public class TeamListFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {
  private static final String TAG = makeLogTag(TeamListFragment.class);

  private static final int LOADER_ID = 0x01;
  private TeamListAdapter mAdapter;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new TeamListAdapter(getActivity());
    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText("No teams");
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(LOADER_ID, null, this);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), ScoutContract.Teams.CONTENT_URI, null, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
    if (isResumed()) {
      setListShown(true);
    } else {
      setListShownNoAnimation(true);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
    mAdapter.swapCursor(null);
  }
}

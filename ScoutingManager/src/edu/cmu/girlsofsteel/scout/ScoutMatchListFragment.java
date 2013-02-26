package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.util.CompatUtil;

/**
 * {@link ScoutMatchListFragment} displays the all of the matches for a
 * particular team. It's parent activity is the {@link ScoutMatchActivity}.
 *
 * This fragment requires a valid team id in order to function properly.
 *
 * @author Alex Lockwood
 */
public class ScoutMatchListFragment extends SherlockListFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(ScoutMatchListFragment.class);
  private MatchListAdapter mAdapter;

  @SuppressLint("InlinedApi")
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Use fancier activated list item layout for Honeycomb and above
    int layout = CompatUtil.hasHoneycomb() ? android.R.layout.simple_list_item_activated_1
        : android.R.layout.simple_list_item_1;

    mAdapter = new MatchListAdapter(mActivity, layout);

    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText(mActivity.getString(R.string.message_no_matches));
    setHasOptionsMenu(true);

    // When in two-pane layout, highlight the selected list item
    if (getFragmentManager().findFragmentById(R.id.match_details_fragment) != null) {
      getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    // A little hacky... but I'm feeling kinda lazy tonight :)
    Bundle args = ((ScoutMatchActivity) mActivity).getIntent().getExtras();
    getLoaderManager().initLoader(TEAM_MATCH_LOADER_ID, args, this);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    // Notify the parent activity of selected item
    mCallback.onMatchSelected(id);

    // Set the item as checked to be highlighted when in two-pane layout
    getListView().setItemChecked(position, true);
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.match_list_actionbar, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add_match:
        Bundle args = ((ScoutMatchActivity) mActivity).getIntent().getExtras();
        long teamId = args.getLong(MainActivity.ARG_TEAM_ID);
        DialogFragment dialog = AddMatchDialog.newInstance(teamId);
        dialog.show(getFragmentManager(), AddMatchDialog.class.getSimpleName());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_MATCH_LOADER_ID = 1;
  private static final String[] PROJECTION = { TeamMatches._ID, TeamMatches.TEAM_ID,
      TeamMatches.MATCH_NUMBER };
  private static final String DEFAULT_SORT = TeamMatches.MATCH_NUMBER + " COLLATE LOCALIZED ASC";

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(MainActivity.ARG_TEAM_ID);
    return new CursorLoader(mActivity, TeamMatches.CONTENT_URI, PROJECTION,
        TeamMatches.TEAM_ID + "=?", new String[] { "" + teamId }, DEFAULT_SORT);
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

  /*****************/
  /** OTHER STUFF **/
  /*****************/

  // Hold a reference to the underlying Activity for convenience
  private Activity mActivity;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = activity;

    // This makes sure that the container activity has implemented
    // the callback interface. If not, it throws an exception.
    try {
      mCallback = (OnMatchSelectedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnHeadlineSelectedListener");
    }
  }

  private OnMatchSelectedListener mCallback;

  // The container Activity must implement this interface so the frag can
  // deliver messages
  public interface OnMatchSelectedListener {
    /** Called by ScoutMatchListFragment when a list item is selected */
    public void onMatchSelected(long teamMatchId);
  }

  /************************/
  /** MATCH LIST ADAPTER **/
  /************************/

  private static class MatchListAdapter extends ResourceCursorAdapter {

    public MatchListAdapter(Context ctx, int layout) {
      super(ctx, layout, null, 0);
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
      ViewHolder holder = (ViewHolder) view.getTag();
      if (holder == null) {
        holder = new ViewHolder();

        // cache TextView ids
        holder.matchNum = (TextView) view.findViewById(android.R.id.text1);

        // cache column indices
        holder.matchNumCol = cur.getColumnIndexOrThrow(TeamMatches.MATCH_NUMBER);
        view.setTag(holder);
      }

      holder.matchNum.setText("Match " + cur.getString(holder.matchNumCol));
    }

    private static class ViewHolder {
      TextView matchNum;
      int matchNumCol;
    }
  }
}

package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.DatabaseUtil;

// For small screens
public class TeamListFragment extends SherlockListFragment implements LoaderCallbacks<Cursor>,
    OnQueryTextListener {
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

  /**********/
  /** MENU **/
  /**********/

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.team_list_options_menu, menu);
    ((SearchView) menu.findItem(R.id.search_view).getActionView()).setOnQueryTextListener(this);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add_team:
        showDialog();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), Teams.CONTENT_URI, null, null, null, null);
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

  /*************************/
  /** QUERY TEXT LISTENER **/
  /*************************/

  @Override
  public boolean onQueryTextSubmit(String query) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    // TODO Auto-generated method stub
    return false;
  }

  /*********************/
  /** ADD TEAM DIALOG **/
  /*********************/

  public static class AddTeamDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      final LayoutInflater factory = LayoutInflater.from(getActivity());
      final EditText edit = (EditText) factory.inflate(R.layout.dialog_add_team, null);
      return new AlertDialog.Builder(getActivity())
          .setTitle(R.string.title_add_team)
          .setView(edit)
          .setPositiveButton(R.string.ok,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  String team = edit.getText().toString();
                  ContentValues values = new ContentValues();
                  values.put(Teams.NUMBER, team);
                  DatabaseUtil.insert(getActivity(), Teams.CONTENT_URI, values);
                }
              })
          .setNegativeButton(R.string.cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  // do something
                }
              })
          .create();
    }
  }

  private void showDialog() {
    new AddTeamDialog().show(getFragmentManager(), AddTeamDialog.class.getSimpleName());
  }
}

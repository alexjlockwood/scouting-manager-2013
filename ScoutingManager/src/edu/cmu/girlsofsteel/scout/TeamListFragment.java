package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;

import java.util.LinkedHashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.DatabaseUtil;
import edu.cmu.girlsofsteel.scout.util.ExportDatabaseTask;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.ActionMode;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.MultiChoiceModeListener;

// For small screens
public class TeamListFragment extends SherlockListFragment implements MultiChoiceModeListener,
    LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListFragment.class);
  private TeamListAdapter mAdapter;
  private Set<Integer> mSelectedPositions = new LinkedHashSet<Integer>();

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setBackgroundColor(Color.WHITE);
    ListView listView = getListView();
    listView.setSelector(android.R.color.transparent);
    listView.setCacheColorHint(Color.WHITE);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new TeamListAdapter(getActivity());
    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText(getActivity().getString(R.string.message_no_teams));
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(TEAM_LOADER_ID, null, this);
    ActionMode.setMultiChoiceMode(getListView(), getActivity(), this);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Toast.makeText(getActivity(), "Team clicked!", Toast.LENGTH_SHORT).show();
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.team_list_actionbar, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
    searchView.setOnQueryTextListener(this);
    searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add_team:
        AddTeamDialog dialog = AddTeamDialog.newInstance();
        dialog.show(getFragmentManager(), AddTeamDialog.class.getSimpleName());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /***************************/
  /** CONTEXTUAL ACTION BAR **/
  /***************************/

  @Override
  public boolean onActionItemClicked(ActionMode mode, android.view.MenuItem item) {
    mode.finish();
    switch (item.getItemId()) {
      case R.id.cab_action_delete:
        Toast.makeText(getActivity(), "Delete teams!", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.cab_action_export:
        new ExportDatabaseTask(getActivity()).execute(Teams.CONTENT_URI);
        return true;
    }
    return false;
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
    android.view.MenuInflater inflater = mode.getMenuInflater();
    inflater.inflate(R.menu.team_list_cab, menu);
    mSelectedPositions.clear();
    return true;
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
    return false;
  }

  @Override
  public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
    if (checked) {
      mSelectedPositions.add(position);
    } else {
      mSelectedPositions.remove(position);
    }
    int numSelectedTeams = mSelectedPositions.size();
    mode.setTitle(getResources().getQuantityString(R.plurals.title_selected_teams,
        numSelectedTeams, numSelectedTeams));
  }

  /*************************/
  /** QUERY TEXT LISTENER **/
  /*************************/

  @Override
  public boolean onQueryTextSubmit(String query) {
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    mFilter = newText;
    getLoaderManager().restartLoader(TEAM_LOADER_ID, null, this);
    return true;
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  private static final int TEAM_LOADER_ID = 0x01;
  private static final String[] PROJECTION = new String[] { Teams._ID, Teams.NUMBER, Teams.PHOTO };
  private static final String DEFAULT_SORT = Teams.NUMBER + " COLLATE LOCALIZED ASC";
  private String mFilter;

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String where = TextUtils.isEmpty(mFilter) ? null : Teams.NUMBER + " LIKE ?";
    String[] whereArgs = TextUtils.isEmpty(mFilter) ? null : new String[] { mFilter + "%" };
    return new CursorLoader(getActivity(), Teams.CONTENT_URI, PROJECTION, where, whereArgs,
        DEFAULT_SORT);
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

  /*********************/
  /** ADD TEAM DIALOG **/
  /*********************/

  public static class AddTeamDialog extends DialogFragment {
    public static AddTeamDialog newInstance() {
      return new AddTeamDialog();
    }

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
                  DatabaseUtil.insertTeam(getActivity(), values);
                }
              })
          .setNegativeButton(R.string.cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  // Do nothing
                }
              })
          .create();
    }
  }

  /************************/
  /** DELETE TEAM DIALOG **/
  /************************/

  public static class DeleteTeamDialog extends DialogFragment {
    private static final String KEY_IDS = "key_ids";

    public static DeleteTeamDialog newInstance(long[] ids) {
      DeleteTeamDialog dialog = new DeleteTeamDialog();
      Bundle args = new Bundle();
      args.putLongArray(KEY_IDS, ids);
      dialog.setArguments(args);
      return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Resources res = getResources();
      final long[] ids = getArguments().getLongArray(KEY_IDS);
      String title = res.getQuantityString(R.plurals.title_delete_teams, ids.length);
      String msg = res.getQuantityString(R.plurals.message_delete_teams, ids.length, ids.length);
      return new AlertDialog.Builder(getActivity())
          .setTitle(title)
          .setMessage(msg)
          .setPositiveButton(R.string.ok,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  DatabaseUtil.deleteTeams(getActivity(), ids);
                }
              })
          .setNegativeButton(R.string.cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  // Do nothing
                }
              })
          .create();
    }
  }
}
package edu.cmu.girlsofsteel.scout;




// TODO: switch to MultiChoiceModeListener if it is available
// TODO: if not, check out https://snipt.net/tweakt/sherlocklistviewjava/
// TODO: also check out http://stackoverflow.com/a/14296781/844882




import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.DatabaseUtil;

// For small screens
public class TeamListFragment extends SherlockListFragment implements ActionMode.Callback,
    AdapterView.OnItemLongClickListener, LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

  private static final String TAG = makeLogTag(TeamListFragment.class);
  private static final int LOADER_ID = 0x01;
  private static final String DEFAULT_SORT = " COLLATE LOCALIZED ASC";
  private String mFilter;
  private TeamListAdapter mAdapter;
  private ActionMode mMode;
  private ListView mListView;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mAdapter = new TeamListAdapter(getActivity());
    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText(getActivity().getString(R.string.message_no_teams));
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(LOADER_ID, null, this);

    mMode = null;
    mListView = getListView();
    mListView.setItemsCanFocus(false);
    mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    mListView.setOnItemClickListener(this);
    mListView.setOnItemLongClickListener(this);
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
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // TODO Auto-generated method stub
    Toast.makeText(getActivity(), "Team clicked!", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
    mListView.setItemChecked(position, true);
    if (mListView.getCheckedItemPositions().size() > 0) {
      if (mMode == null) {
        mMode = getSherlockActivity().startActionMode(this);
      }
    } else {
      if (mMode != null) {
        mMode.finish();
      }
    }
    return true;
  }

  @Override
  public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    getSherlockActivity().getSupportMenuInflater().inflate(R.menu.team_list_cab, menu);
    return true;
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    return false;
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
    int count = mListView.getAdapter().getCount();
    for (int i = 0; i < count; i++) {
      mListView.setItemChecked(i, false);
    }
    if (mMode == mode) {
      mMode = null;
    }
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    switch (item.getItemId()) {
      case R.id.cab_action_export:
        Toast.makeText(getActivity(), "Export!", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.cab_action_delete:
        long[] ids = mListView.getCheckedItemIds();
        DeleteTeamDialog dialog = DeleteTeamDialog.newInstance(ids);
        dialog.show(getFragmentManager(), AddTeamDialog.class.getSimpleName());
        mode.finish();
        return true;
    }
    return false;
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] proj = new String[] { Teams._ID, Teams.NUMBER, Teams.PHOTO };
    String where = TextUtils.isEmpty(mFilter) ? null : Teams.NUMBER + " LIKE ?";
    String[] whereArgs = TextUtils.isEmpty(mFilter) ? null : new String[] { mFilter };
    String sort = Teams.NUMBER + DEFAULT_SORT;
    return new CursorLoader(getActivity(), Teams.CONTENT_URI, proj, where, whereArgs, sort);
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
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    mFilter = newText;
    getLoaderManager().restartLoader(LOADER_ID, null, this);
    return true;
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
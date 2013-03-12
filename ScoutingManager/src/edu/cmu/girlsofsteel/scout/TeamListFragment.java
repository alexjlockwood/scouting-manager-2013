package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGW;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import edu.cmu.girlsofsteel.scout.dialogs.AddTeamDialog;
import edu.cmu.girlsofsteel.scout.dialogs.DeleteTeamDialog;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.CameraUtil;
import edu.cmu.girlsofsteel.scout.util.CameraUtil.ScaleBitmapTask;
import edu.cmu.girlsofsteel.scout.util.CompatUtil;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.ActionMode;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.MultiChoiceModeListener;

// TODO: figure out why ActionMode doesn't persist on config changes!
// TODO: figure out how to save selected team ids across config changes!

/**
 * {@link TeamListFragment} displays the all of the teams currently in the
 * database. It's parent activity is the {@link MainActivity}. It's view is seen
 * by the user as the application's "home screen".
 *
 * @author Alex Lockwood
 */
public class TeamListFragment extends SherlockListFragment implements MultiChoiceModeListener,
    LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
  private static final String TAG = TeamListFragment.class.getSimpleName();

  private static final String KEY_SELECTED_TEAM_IDS = "selected_team_ids";
  private static final String KEY_CAMERA_TEAM_ID = "camera_team_id";
  private static final String KEY_PHOTO_FILE_PATH = "photo_file_path";

  private static final int TEAM_LOADER_ID = 1;
  private static final String[] PROJECTION = { Teams._ID, Teams.NUMBER, Teams.PHOTO };
  private static final String DEFAULT_SORT = Teams.NUMBER + " COLLATE LOCALIZED ASC";
  private String mFilter;

  private TeamListAdapter mAdapter;
  private CompoundButton mScoutModeView;

  private List<Long> mSelectedTeamIds = new LinkedList<Long>();
  private android.view.MenuItem mTakePictureMenuItem;

  private static final int REQUEST_CODE_TAKE_TEAM_PICTURE = 1;
  private File mPhotoFile = null;
  private long mTeamId = 0L;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    if (savedInstanceState != null) {
      // (1) Restore selected team ids
      mSelectedTeamIds.clear();
      long[] ids = savedInstanceState.getLongArray(KEY_SELECTED_TEAM_IDS);
      if (ids != null) {
        for (long id : ids) {
          mSelectedTeamIds.add(id);
        }
      }

      // (2) Restore camera info/state
      mTeamId = savedInstanceState.getLong(KEY_CAMERA_TEAM_ID);
      String photoFilePath = savedInstanceState.getString(KEY_PHOTO_FILE_PATH);
      if (photoFilePath != null) {
        mPhotoFile = new File(photoFilePath);
      }
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // (1) Save selected team ids
    if (mSelectedTeamIds.size() > 0) {
      int numSelected = mSelectedTeamIds.size();
      long[] ids = new long[numSelected];
      Object[] temp = mSelectedTeamIds.toArray();
      for (int i = 0; i < numSelected; i++) {
        ids[i] = (Long) temp[i];
      }
      outState.putLongArray(KEY_SELECTED_TEAM_IDS, ids);
    }

    // (2) Save camera info/state
    if (mTeamId > 0 && mPhotoFile != null) {
      outState.putLong(KEY_CAMERA_TEAM_ID, mTeamId);
      outState.putString(KEY_PHOTO_FILE_PATH, mPhotoFile.getPath());
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.setBackgroundColor(Color.WHITE);
    getListView().setSelector(android.R.color.transparent);
    getListView().setCacheColorHint(Color.WHITE);
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();

    mAdapter = new TeamListAdapter(activity);
    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText(getString(R.string.message_no_teams));
    getLoaderManager().initLoader(TEAM_LOADER_ID, null, this);

    if (CompatUtil.hasICS()) {
      mScoutModeView = new Switch(activity);
      ((Switch) mScoutModeView).setTextOff(getString(R.string.toggle_off_team));
      ((Switch) mScoutModeView).setTextOn(getString(R.string.toggle_on_match));
    } else {
      mScoutModeView = new ToggleButton(activity);
      ((ToggleButton) mScoutModeView).setTextOff(getString(R.string.toggle_off_team));
      ((ToggleButton) mScoutModeView).setTextOn(getString(R.string.toggle_on_match));
    }

    // Gravity.END introduced in API 14
    int gravityCompat = CompatUtil.hasICS() ? (Gravity.CENTER_VERTICAL | Gravity.END) : Gravity.CENTER_VERTICAL;
    activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
    activity.getSupportActionBar().setCustomView(mScoutModeView, new ActionBar.LayoutParams(
        ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, gravityCompat));

    mScoutModeView.setChecked(StorageUtil.getScoutMode(activity) == ScoutMode.MATCH);
    mScoutModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        StorageUtil.setScoutMode(activity, isChecked ? ScoutMode.MATCH : ScoutMode.TEAM);
      }
    });

    ActionMode.setMultiChoiceMode(getListView(), activity, this);
  }

  @Override
  public void onListItemClick(ListView lv, View v, int position, long id) {
    Intent intent = mScoutModeView.isChecked()
        ? new Intent(getActivity(), MatchScoutActivity.class)
        : new Intent(getActivity(), TeamScoutActivity.class);
    intent.putExtra(MainActivity.ARG_TEAM_ID, id);
    startActivity(intent);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String selection = TextUtils.isEmpty(mFilter) ? null : Teams.NUMBER + " LIKE ?";
    String[] selectionArgs = TextUtils.isEmpty(mFilter) ? null : new String[] { mFilter + "%" };
    return new CursorLoader(getActivity(), Teams.CONTENT_URI, PROJECTION, selection, selectionArgs, DEFAULT_SORT);
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
    getLoaderManager().restartLoader(TEAM_LOADER_ID, null, this);
    return true;
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @SuppressLint("InlinedApi")
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.team_list_actionbar, menu);
    SearchView searchView = (SearchView) menu.findItem(R.id.search_view).getActionView();
    searchView.setOnQueryTextListener(this);
    searchView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
    searchView.setQueryHint(getString(R.string.menu_search_team_hint));
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add_team:
        DialogFragment dialog = AddTeamDialog.newInstance();
        dialog.show(getFragmentManager(), AddTeamDialog.class.getSimpleName());
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /***************************/
  /** CONTEXTUAL ACTION BAR **/
  /***************************/

  @Override
  public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
    android.view.MenuInflater inflater = mode.getMenuInflater();
    inflater.inflate(R.menu.team_list_cab, menu);
    mTakePictureMenuItem = menu.findItem(R.id.cab_take_team_picture);

    // Handle coming back from configuration change
    int numSelected = mSelectedTeamIds.size();
    if (numSelected > 0) {
      Resources res = getResources();
      mode.setTitle(res.getQuantityString(R.plurals.title_selected_teams, numSelected, numSelected));
    }

    // Display menu item if the camera is available
    mTakePictureMenuItem.setVisible(CameraUtil.hasCamera(getActivity()));
    return true;
  }

  @Override
  public boolean onActionItemClicked(ActionMode mode, android.view.MenuItem item) {
    int numSelected = mSelectedTeamIds.size();
    long[] ids = new long[numSelected];
    Object[] temp = mSelectedTeamIds.toArray();
    for (int i = 0; i < numSelected; i++) {
      ids[i] = (Long) temp[i];
    }
    mode.finish();

    // WARNING: This is a hack! When commenting out this line, the contextual
    // action bar will not close after the first time an action item has been
    // clicked. Either this is a bug with ABS or I am missing something...
    ActionMode.setMultiChoiceMode(getListView(), getSherlockActivity(), this);

    switch (item.getItemId()) {
      case R.id.cab_action_delete:
        DialogFragment dialog = DeleteTeamDialog.newInstance(ids);
        dialog.show(getFragmentManager(), DeleteTeamDialog.class.getSimpleName());
        return true;
      case R.id.cab_take_team_picture:
        if (ids.length == 1) {
          takeTeamPicture(ids[0]);
        } else {
          LOGW(TAG, "Warning! Only one list item id should be selected!");
        }
        return true;
    }
    return false;
  }

  @Override
  public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
    if (checked) {
      mSelectedTeamIds.add(id);
    } else {
      mSelectedTeamIds.remove(id);
    }

    int numSelectedTeams = mSelectedTeamIds.size();
    mode.setTitle(getResources().getQuantityString(R.plurals.title_selected_teams,
        numSelectedTeams, numSelectedTeams));

    if (numSelectedTeams == 1) {
      mTakePictureMenuItem.setVisible(CameraUtil.hasCamera(getActivity()));
    } else {
      // Can't take picture if more than one team is selected
      mTakePictureMenuItem.setVisible(false);
    }
  }

  @Override
  public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
    return false;
  }

  @Override
  public void onDestroyActionMode(ActionMode mode) {
    mSelectedTeamIds.clear();
  }

  /****************************/
  /** CAMERA & TEAM PICTURES **/
  /****************************/

  public void takeTeamPicture(long teamId) {
    try {
      mTeamId = teamId;
      mPhotoFile = CameraUtil.createImageFile(getActivity());
      Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
      startActivityForResult(takePicture, REQUEST_CODE_TAKE_TEAM_PICTURE);
    } catch (IOException e) {
      Toast.makeText(getActivity(), R.string.camera_take_picture_failed, Toast.LENGTH_SHORT).show();
      LOGE(TAG, "Could not create image file!");
      return;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_TEAM_PICTURE) {
      new ScaleBitmapTask(getActivity(), mPhotoFile, mTeamId).execute();
    }
  }

  /***********************/
  /** TEAM LIST ADAPTER **/
  /***********************/

  private static class TeamListAdapter extends ResourceCursorAdapter {

    public TeamListAdapter(Context ctx) {
      super(ctx, R.layout.team_list_row, null, 0);
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
      ViewHolder holder = (ViewHolder) view.getTag();
      if (holder == null) {
        holder = new ViewHolder();
        holder.teamNum = (TextView) view.findViewById(R.id.team_list_row_number);
        holder.teamPhoto = (ImageView) view.findViewById(R.id.team_list_row_photo);
        holder.teamNumCol = cur.getColumnIndexOrThrow(Teams.NUMBER);
        holder.teamPhotoCol = cur.getColumnIndexOrThrow(Teams.PHOTO);
        view.setTag(holder);
      }

      holder.teamNum.setText(cur.getString(holder.teamNumCol));

      String uri = cur.getString(holder.teamPhotoCol);
      if (!TextUtils.isEmpty(uri)) {
        long photoId = Long.parseLong(Uri.parse(uri).getLastPathSegment());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(ctx.getContentResolver(),
            photoId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        holder.teamPhoto.setImageBitmap(bitmap);
      } else {
        Resources res = ctx.getResources();
        holder.teamPhoto.setImageDrawable(res.getDrawable(R.drawable.ic_contact_picture));
      }
    }

    private static class ViewHolder {
      TextView teamNum;
      ImageView teamPhoto;
      int teamNumCol, teamPhotoCol;
    }
  }
}
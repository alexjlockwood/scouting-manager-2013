package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGW;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.CameraUtil;
import edu.cmu.girlsofsteel.scout.util.CameraUtil.ScaleBitmapTask;
import edu.cmu.girlsofsteel.scout.util.CompatUtil;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.ActionMode;
import edu.cmu.girlsofsteel.scout.util.actionmodecompat.MultiChoiceModeListener;

public class TeamListFragment extends SherlockListFragment implements MultiChoiceModeListener,
    LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
  private static final String TAG = makeLogTag(TeamListFragment.class);

  private static final String KEY_SELECTED_TEAM_IDS = "selected_team_ids";
  private static final String KEY_CAMERA_TEAM_ID = "camera_team_id";
  private static final String KEY_PHOTO_FILE_PATH = "photo_file_path";

  private TeamListAdapter mAdapter;
  private CompoundButton mScoutModeView;

  // TODO: figure out why ActionMode doesn't persist on config changes!
  // TODO: figure out how to save selected team ids across config changes!

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      // Restore selected team ids
      mSelectedTeamIds.clear();
      long[] ids = savedInstanceState.getLongArray(KEY_SELECTED_TEAM_IDS);
      if (ids != null) {
        for (long id : ids) {
          mSelectedTeamIds.add(id);
        }
      }

      // Restore camera info/state
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

    // Save selected team ids
    if (mSelectedTeamIds.size() > 0) {
      int numSelected = mSelectedTeamIds.size();
      long[] ids = new long[numSelected];
      Object[] temp = mSelectedTeamIds.toArray();
      for (int i = 0; i < numSelected; i++) {
        ids[i] = (Long) temp[i];
      }
      outState.putLongArray(KEY_SELECTED_TEAM_IDS, ids);

      // Save camera info/state
      if (mTeamId > 0 && mPhotoFile != null) {
        outState.putLong(KEY_CAMERA_TEAM_ID, mTeamId);
        outState.putString(KEY_PHOTO_FILE_PATH, mPhotoFile.getPath());
      }
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
    mAdapter = new TeamListAdapter(mActivity);
    setListAdapter(mAdapter);
    setListShown(false);
    setEmptyText(mActivity.getString(R.string.message_no_teams));
    getLoaderManager().initLoader(TEAM_LOADER_ID, null, this);
    setHasOptionsMenu(true);

    if (CompatUtil.hasICS()) {
      mScoutModeView = new Switch(mActivity);
      ((Switch) mScoutModeView).setTextOff(getString(R.string.toggle_off_team));
      ((Switch) mScoutModeView).setTextOn(getString(R.string.toggle_on_match));
    } else {
      mScoutModeView = new ToggleButton(mActivity);
      ((ToggleButton) mScoutModeView).setTextOff(getString(R.string.toggle_off_team));
      ((ToggleButton) mScoutModeView).setTextOn(getString(R.string.toggle_on_match));
    }

    // int padding =
    // getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
    // mScoutModeView.setPadding(0, 0, padding, 0);

    final SherlockFragmentActivity activity = (SherlockFragmentActivity) mActivity;
    activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
        ActionBar.DISPLAY_SHOW_CUSTOM);
    activity.getSupportActionBar().setCustomView(mScoutModeView, new ActionBar.LayoutParams(
        ActionBar.LayoutParams.WRAP_CONTENT,
        ActionBar.LayoutParams.WRAP_CONTENT,
        Gravity.CENTER_VERTICAL | Gravity.END));

    mScoutModeView.setChecked(StorageUtil.getScoutMode(mActivity) == ScoutMode.MATCH);
    mScoutModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        StorageUtil.setScoutMode(mActivity, isChecked ? ScoutMode.MATCH : ScoutMode.TEAM);
      }
    });

    ActionMode.setMultiChoiceMode(getListView(), activity, this);
  }

  @Override
  public void onListItemClick(ListView lv, View v, int position, long id) {
    Intent intent = new Intent(mActivity, ScoutActivity.class);
    intent.putExtra(MainActivity.TEAM_ID_EXTRA, id);
    intent.putExtra(MainActivity.SCOUT_MODE_EXTRA, mScoutModeView.isChecked());
    startActivity(intent);
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
    int inputTypeCompat = InputType.TYPE_CLASS_NUMBER;
    if (CompatUtil.hasHoneycomb()) {
      inputTypeCompat |= InputType.TYPE_NUMBER_VARIATION_NORMAL;
    }
    searchView.setInputType(inputTypeCompat);
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

  private List<Long> mSelectedTeamIds = new LinkedList<Long>();
  private android.view.MenuItem mDeleteTeamMenuItem;
  private android.view.MenuItem mTakePictureMenuItem;

  @Override
  public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
    android.view.MenuInflater inflater = mode.getMenuInflater();
    inflater.inflate(R.menu.team_list_cab, menu);
    mDeleteTeamMenuItem = menu.findItem(R.id.cab_action_delete);
    mTakePictureMenuItem = menu.findItem(R.id.cab_take_team_picture);

    // Handle coming back from configuration change
    int numSelectedTeams = mSelectedTeamIds.size();
    if (numSelectedTeams > 0) {
      mode.setTitle(getResources().getQuantityString(R.plurals.title_selected_teams,
          numSelectedTeams, numSelectedTeams));
    }

    // Display menu item if the camera is available
    mTakePictureMenuItem.setVisible(CameraUtil.hasCamera(mActivity));

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
        if (ids.length != 1) {
          LOGW(TAG, "Warning! Only one list item id should be selected!");
        } else {
          takeTeamPicture(ids[0]);
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
      mDeleteTeamMenuItem.setVisible(true);
      mTakePictureMenuItem.setVisible(CameraUtil.hasCamera(mActivity));
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
  private static final String[] PROJ = new String[] { Teams._ID, Teams.NUMBER, Teams.PHOTO };
  private static final String DEFAULT_SORT = Teams.NUMBER + " COLLATE LOCALIZED ASC";
  private String mFilter;

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String where = TextUtils.isEmpty(mFilter) ? null : Teams.NUMBER + " LIKE ?";
    String[] whereArgs = TextUtils.isEmpty(mFilter) ? null : new String[] { mFilter + "%" };
    return new CursorLoader(mActivity, Teams.CONTENT_URI, PROJ, where, whereArgs, DEFAULT_SORT);
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
      final Dialog dialog = new AlertDialog.Builder(getActivity())
          .setTitle(R.string.title_add_team)
          .setView(edit)
          .setPositiveButton(R.string.ok,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                  int team = Integer.valueOf(edit.getText().toString());
                  ContentValues values = new ContentValues();
                  values.put(Teams.NUMBER, team);
                  StorageUtil.insertTeam(getActivity(), values);
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
      edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
          if (hasFocus) {
            Window window = dialog.getWindow();
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
          }
        }
      });
      return dialog;
    }
  }

  /************************/
  /** DELETE TEAM DIALOG **/
  /************************/

  public static class DeleteTeamDialog extends DialogFragment {

    private static final String KEY_IDS = "key_ids";

    public static DeleteTeamDialog newInstance(long... ids) {
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
                  StorageUtil.deleteTeams(getActivity(), ids);
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

  /****************************/
  /** CAMERA & TEAM PICTURES **/
  /****************************/

  private static final int REQUEST_CODE_TAKE_TEAM_PICTURE = 1;

  private File mPhotoFile = null;
  private long mTeamId = 0L;

  public void takeTeamPicture(long teamId) {
    try {
      mTeamId = teamId;
      mPhotoFile = CameraUtil.createImageFile(mActivity);
      Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
      startActivityForResult(takePicture, REQUEST_CODE_TAKE_TEAM_PICTURE);
    } catch (IOException e) {
      Toast.makeText(mActivity, R.string.camera_take_picture_failed, Toast.LENGTH_SHORT).show();
      LOGE(TAG, "Could not create image file!");
      return;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TAKE_TEAM_PICTURE) {
      new ScaleBitmapTask(mActivity, mPhotoFile, mTeamId).execute();
    }
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
  }

  /***********************/
  /** Team List Adapter **/
  /***********************/

  private static class TeamListAdapter extends ResourceCursorAdapter {

    @SuppressWarnings("unused")
    private static final String TAG = makeLogTag(TeamListAdapter.class);
    private ContentResolver mContentResolver;

    public TeamListAdapter(Context ctx) {
      super(ctx, R.layout.team_list_row, null, 0);
      mContentResolver = ctx.getContentResolver();
    }

    @Override
    public void bindView(View view, Context ctx, Cursor cur) {
      ViewHolder holder = (ViewHolder) view.getTag();
      if (holder == null) {
        holder = new ViewHolder();

        // cache TextView ids
        holder.teamNum = (TextView) view.findViewById(R.id.team_list_row_number);
        holder.teamPhoto = (ImageView) view.findViewById(R.id.team_list_row_photo);

        // cache column indices
        holder.teamNumCol = cur.getColumnIndexOrThrow(Teams.NUMBER);
        holder.teamPhotoCol = cur.getColumnIndexOrThrow(Teams.PHOTO);
        view.setTag(holder);
      }

      holder.teamNum.setText(cur.getString(holder.teamNumCol));

      String uri = cur.getString(holder.teamPhotoCol);
      if (!TextUtils.isEmpty(uri)) {
        long photoId = Long.parseLong(Uri.parse(uri).getLastPathSegment());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, photoId,
            MediaStore.Images.Thumbnails.MICRO_KIND, null);
        holder.teamPhoto.setImageBitmap(bitmap);
      } else {
        Resources res = ctx.getResources();
        holder.teamPhoto.setImageDrawable(res.getDrawable(R.drawable.ic_contact_picture));
      }
    }

    private static class ViewHolder {
      public TextView teamNum;
      public ImageView teamPhoto;
      public int teamNumCol, teamPhotoCol;
    }
  }
}
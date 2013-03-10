package edu.cmu.girlsofsteel.scout;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.CameraUtil;
import edu.cmu.girlsofsteel.scout.util.CompatUtil;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

/**
 * {@link MatchDetailsFragment} displays the team details for a particular team.
 * It's parent activity is the {@link TeamScoutActivity}.
 *
 * This fragment requires a valid team id in order to function properly.
 *
 * @author Alex Lockwood
 */
public class TeamDetailsFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
    View.OnClickListener {
  // private static final String TAG = makeLogTag(TeamListFragment.class);

  private static final int TEAM_LOADER_ID = 1;
  private static final String[] PROJECTION = null;

  private ImageView mTeamPicture;
  private EditText mTeamName;
  private CheckBox mScoreLow, mScoreMid, mScoreHigh;
  private CheckBox mClimbLevelOne, mClimbLevelTwo, mClimbLevelThree;
  private CompoundButton mHelpsClimb, mDrivingGears, mGoesUnderTower;
  private Spinner mDriveTrain, mWheelType;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_team_scout, container, false);

    mTeamPicture = (ImageView) view.findViewById(R.id.team_picture);
    mTeamName = (EditText) view.findViewById(R.id.team_name);

    if (CompatUtil.hasHoneycomb()) {
      // Uses a PopupMenu for API 11+
      mTeamPicture.setOnClickListener(this);
    } else {
      // Otherwise uses a context menu
      registerForContextMenu(mTeamPicture);
    }

    mScoreLow = (CheckBox) view.findViewById(R.id.checkbox_low_goal);
    mScoreMid = (CheckBox) view.findViewById(R.id.checkbox_mid_goal);
    mScoreHigh = (CheckBox) view.findViewById(R.id.checkbox_high_goal);

    mClimbLevelOne = (CheckBox) view.findViewById(R.id.checkbox_level_one);
    mClimbLevelTwo = (CheckBox) view.findViewById(R.id.checkbox_level_two);
    mClimbLevelThree = (CheckBox) view.findViewById(R.id.checkbox_level_three);

    mHelpsClimb = (CompoundButton) view.findViewById(R.id.toggle_helps_climb);
    mDrivingGears = (CompoundButton) view.findViewById(R.id.toggle_driving_gears);
    mGoesUnderTower = (CompoundButton) view.findViewById(R.id.toggle_goes_under_tower);

    mDriveTrain = (Spinner) view.findViewById(R.id.spinner_drive_train);
    mWheelType = (Spinner) view.findViewById(R.id.spinner_wheel_type);

    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(TEAM_LOADER_ID, getArguments(), this);
  }

  @Override
  public void onPause() {
    super.onPause();
    saveTeamData();
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
      com.actionbarsherlock.view.MenuInflater inflater) {
    inflater.inflate(R.menu.team_details_actionbar, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_clear_team_screen:
        clearTeamData();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    long teamId = args.getLong(MainActivity.ARG_TEAM_ID);
    return new CursorLoader(getActivity(), Teams.teamIdUri(teamId), PROJECTION, null, null, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    populateTeamData(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  private void populateTeamData(Cursor data) {
    if (data.moveToFirst()) {
      // Team number
      int teamNumber = data.getInt(data.getColumnIndexOrThrow(Teams.NUMBER));
      getSherlockActivity().getSupportActionBar().setSubtitle("Team " + teamNumber);

      // Team name
      String teamName = data.getString(data.getColumnIndexOrThrow(Teams.NAME));
      mTeamName.setText(teamName != null ? teamName : "");

      // Team picture
      String uri = data.getString(data.getColumnIndexOrThrow(Teams.PHOTO));
      if (!TextUtils.isEmpty(uri)) {
        long pictureId = Long.parseLong(Uri.parse(uri).getLastPathSegment());
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getActivity()
            .getContentResolver(), pictureId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        mTeamPicture.setImageBitmap(bitmap);
      } else {
        Resources res = getResources();
        mTeamPicture.setImageDrawable(res.getDrawable(R.drawable.ic_contact_picture));
      }

      // Can score on which hoops?
      int scoreLow = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_SCORE_ON_LOW));
      int scoreMid = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_SCORE_ON_MID));
      int scoreHigh = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_SCORE_ON_HIGH));
      mScoreLow.setChecked(scoreLow != 0);
      mScoreMid.setChecked(scoreMid != 0);
      mScoreHigh.setChecked(scoreHigh != 0);

      // Can climb which levels?
      int levelOne = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_CLIMB_LEVEL_ONE));
      int levelTwo = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_CLIMB_LEVEL_TWO));
      int levelThree = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_CLIMB_LEVEL_THREE));
      mClimbLevelOne.setChecked(levelOne != 0);
      mClimbLevelTwo.setChecked(levelTwo != 0);
      mClimbLevelThree.setChecked(levelThree != 0);

      // Can help climb?
      int helpClimb = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_HELP_CLIMB));
      mHelpsClimb.setChecked(helpClimb != 0);

      // One or multiple driving gears?
      int numDrivingGears = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_NUM_DRIVING_GEARS));
      mDrivingGears.setChecked(numDrivingGears != 0);

      // Drive train?
      int driveTrain = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_DRIVE_TRAIN));
      mDriveTrain.setSelection(driveTrain);

      // Wheel type?
      int wheelType = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_TYPE_OF_WHEEL));
      mWheelType.setSelection(wheelType);

      // Goes under tower?
      int goesUnderTower = data.getInt(data.getColumnIndexOrThrow(Teams.ROBOT_CAN_GO_UNDER_TOWER));
      mGoesUnderTower.setChecked(goesUnderTower != 0);
    }
  }

  private void saveTeamData() {
    long teamId = getArguments().getLong(MainActivity.ARG_TEAM_ID);
    ContentValues values = new ContentValues();
    values.put(Teams.NAME, mTeamName.getText().toString());
    values.put(Teams.ROBOT_CAN_SCORE_ON_LOW, mScoreLow.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_SCORE_ON_MID, mScoreMid.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_SCORE_ON_HIGH, mScoreHigh.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_CLIMB_LEVEL_ONE, mClimbLevelOne.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_CLIMB_LEVEL_TWO, mClimbLevelTwo.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_CLIMB_LEVEL_THREE, mClimbLevelThree.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_CAN_HELP_CLIMB, mHelpsClimb.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_NUM_DRIVING_GEARS, mDrivingGears.isChecked() ? 1 : 0);
    values.put(Teams.ROBOT_DRIVE_TRAIN, mDriveTrain.getSelectedItemPosition());
    values.put(Teams.ROBOT_TYPE_OF_WHEEL, mWheelType.getSelectedItemPosition());
    values.put(Teams.ROBOT_CAN_GO_UNDER_TOWER, mGoesUnderTower.isChecked() ? 1 : 0);
    StorageUtil.updateTeam(getActivity(), teamId, values);
  }

  private void clearTeamData() {
    mTeamName.setText("");
    mScoreLow.setChecked(false);
    mScoreMid.setChecked(false);
    mScoreHigh.setChecked(false);
    mClimbLevelOne.setChecked(false);
    mClimbLevelTwo.setChecked(false);
    mClimbLevelThree.setChecked(false);
    mHelpsClimb.setChecked(false);
    mDrivingGears.setChecked(false);
    mGoesUnderTower.setChecked(false);
    mDriveTrain.setSelection(0);
    mWheelType.setSelection(0);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    getActivity().getMenuInflater().inflate(R.menu.team_picture_menu, menu);
    menu.findItem(R.id.menu_take_picture).setEnabled(CameraUtil.hasCamera(getActivity()));
  }

  @Override
  public boolean onContextItemSelected(android.view.MenuItem item) {
    return pickTeamPictureMenuItem(item);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.team_picture) {
      if (CompatUtil.hasHoneycomb()) {
        // Show the popup menu for devices on API 11+
        PopupMenu popUp = new PopupMenu(getActivity(), v);
        popUp.getMenuInflater().inflate(R.menu.team_picture_menu, popUp.getMenu());
        popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
          @Override
          public boolean onMenuItemClick(android.view.MenuItem item) {
            return pickTeamPictureMenuItem(item);
          }
        });
        boolean hasCamera = CameraUtil.hasCamera(getActivity());
        popUp.getMenu().findItem(R.id.menu_take_picture).setEnabled(hasCamera);
        popUp.show();
      }
    }
  }

  private boolean pickTeamPictureMenuItem(android.view.MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_take_picture:
        takePicture();
        return true;
      case R.id.menu_delete_picture:
        deletePicture();
        return true;
    }
    return false;
  }

  private void takePicture() {
    // TODO: implement this
    Toast.makeText(getActivity(), "Coming soon!", Toast.LENGTH_SHORT).show();
  }

  private void deletePicture() {
    long teamId = getArguments().getLong(MainActivity.ARG_TEAM_ID);
    ContentValues updateValues = new ContentValues();
    updateValues.put(Teams.PHOTO, "");
    StorageUtil.updateTeam(getActivity(), teamId, updateValues);
  }
}

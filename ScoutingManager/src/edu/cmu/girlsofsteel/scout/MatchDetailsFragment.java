package edu.cmu.girlsofsteel.scout;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.cmu.girlsofsteel.scout.dialogs.DeleteMatchDialog;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

/**
 * {@link MatchDetailsFragment} displays the match details for a particular
 * team. It's parent activity is the {@link MatchScoutActivity}.
 *
 * This fragment requires a valid 'team_match_id' in order to function properly.
 *
 * @author Alex Lockwood
 */
public class MatchDetailsFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
    View.OnClickListener {
  // private static final String TAG = makeLogTag(TeamListFragment.class);

  private static final String KEY_TEAM_MATCH_ID = "key_team_match_id";
  private static final int TEAM_MATCH_LOADER_ID = 1;
  private static final String[] PROJECTION = null;

  private long mTeamMatchId = -1;
  private OnMatchDeletedListener mCallback;

  private ScrollView mScrollView;

  private Button mButtonAutoHitHigh, mButtonAutoMissHigh, mButtonAutoHitMed, mButtonAutoMissMed, mButtonAutoHitLow,
      mButtonAutoMissLow;
  private Button mButtonTeleHitHigh, mButtonTeleMissHigh, mButtonTeleHitMed, mButtonTeleMissMed, mButtonTeleHitLow,
      mButtonTeleMissLow;
  private EditText mEditAutoHitHigh, mEditAutoMissHigh, mEditAutoHitMed, mEditAutoMissMed, mEditAutoHitLow,
      mEditAutoMissLow;
  private EditText mEditTeleHitHigh, mEditTeleMissHigh, mEditTeleHitMed, mEditTeleMissMed, mEditTeleHitLow,
      mEditTeleMissLow;
  private CheckBox mTowerLevelOne, mTowerLevelTwo, mTowerLevelThree;
  private CompoundButton mFellOffTower;
  private RadioGroup mHumanPlayerAbility;
  private CheckBox mFrisbeesFromFeeder, mFrisbeesFromFloor;
  private RadioGroup mStrategy, mSpeed, mManeuverability, mPenalty;

  /**
   * Callback interface for the {@link DeleteMatchDialog} and the
   * {@link MatchScoutActivity}.
   */
  public interface OnMatchDeletedListener {
    /**
     * Called by the {@link MatchDetailsFragment} when the user clicks the
     * 'delete match' menu item.
     */
    public void onShowConfirmationDialog(long teamMatchId);

    /**
     * Called by the {@link DeleteMatchDialog} when the user confirms that a
     * match should be deleted.
     */
    public void onMatchDeleted(long teamMatchId);
  }

  /**
   * Static factory method which returns a new instance with the given team
   * match id and number set in its arguments.
   */
  public static MatchDetailsFragment newInstance(long teamMatchId, String teamNumber) {
    MatchDetailsFragment frag = new MatchDetailsFragment();
    Bundle args = new Bundle();
    args.putLong(MatchScoutActivity.ARG_TEAM_MATCH_ID, teamMatchId);
    args.putString(MatchScoutActivity.ARG_TEAM_NUMBER, teamNumber);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallback = (OnMatchDeletedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnMatchDeletedListener");
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      // If activity recreated due to a configuration change, restore the
      // previous match selection set by onSaveInstanceState(). This is
      // primarily necessary when in the two-pane layout.
      mTeamMatchId = savedInstanceState.getLong(KEY_TEAM_MATCH_ID);
    } else if (getArguments() != null) {
      // If the fragment was created programatically (and not inflated from XML)
      // then the arguments should have been set with the selected team match
      // id.
      mTeamMatchId = getArguments().getLong(MatchScoutActivity.ARG_TEAM_MATCH_ID);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(KEY_TEAM_MATCH_ID, mTeamMatchId);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_match_scout, container, false);
    mScrollView = (ScrollView) view.findViewById(R.id.match_scout_scrollview);

    View autoPanel = view.findViewById(R.id.auto_shots_panel);
    mButtonAutoHitHigh = (Button) autoPanel.findViewById(R.id.button_auto_shots_hit_high);
    mButtonAutoMissHigh = (Button) autoPanel.findViewById(R.id.button_auto_shots_miss_high);
    mButtonAutoHitMed = (Button) autoPanel.findViewById(R.id.button_auto_shots_hit_medium);
    mButtonAutoMissMed = (Button) autoPanel.findViewById(R.id.button_auto_shots_miss_medium);
    mButtonAutoHitLow = (Button) autoPanel.findViewById(R.id.button_auto_shots_hit_low);
    mButtonAutoMissLow = (Button) autoPanel.findViewById(R.id.button_auto_shots_miss_low);
    mEditAutoHitHigh = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_hit_high);
    mEditAutoMissHigh = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_miss_high);
    mEditAutoHitMed = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_hit_medium);
    mEditAutoMissMed = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_miss_medium);
    mEditAutoHitLow = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_hit_low);
    mEditAutoMissLow = (EditText) autoPanel.findViewById(R.id.edittext_auto_shots_miss_low);
    mButtonAutoHitHigh.setOnClickListener(this);
    mButtonAutoMissHigh.setOnClickListener(this);
    mButtonAutoHitMed.setOnClickListener(this);
    mButtonAutoMissMed.setOnClickListener(this);
    mButtonAutoHitLow.setOnClickListener(this);
    mButtonAutoMissLow.setOnClickListener(this);

    View teleOpPanel = view.findViewById(R.id.teleop_shots_panel);
    mButtonTeleHitHigh = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_hit_high);
    mButtonTeleMissHigh = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_miss_high);
    mButtonTeleHitMed = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_hit_medium);
    mButtonTeleMissMed = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_miss_medium);
    mButtonTeleHitLow = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_hit_low);
    mButtonTeleMissLow = (Button) teleOpPanel.findViewById(R.id.button_tele_shots_miss_low);
    mEditTeleHitHigh = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_hit_high);
    mEditTeleMissHigh = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_miss_high);
    mEditTeleHitMed = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_hit_medium);
    mEditTeleMissMed = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_miss_medium);
    mEditTeleHitLow = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_hit_low);
    mEditTeleMissLow = (EditText) teleOpPanel.findViewById(R.id.edittext_tele_shots_miss_low);
    mButtonTeleHitHigh.setOnClickListener(this);
    mButtonTeleMissHigh.setOnClickListener(this);
    mButtonTeleHitMed.setOnClickListener(this);
    mButtonTeleMissMed.setOnClickListener(this);
    mButtonTeleHitLow.setOnClickListener(this);
    mButtonTeleMissLow.setOnClickListener(this);

    mTowerLevelOne = (CheckBox) view.findViewById(R.id.cb_tower_level_one);
    mTowerLevelTwo = (CheckBox) view.findViewById(R.id.cb_tower_level_two);
    mTowerLevelThree = (CheckBox) view.findViewById(R.id.cb_tower_level_three);
    mFellOffTower = (CompoundButton) view.findViewById(R.id.toggle_fell_off_tower);
    mHumanPlayerAbility = (RadioGroup) view.findViewById(R.id.rg_human_player_ability);
    mFrisbeesFromFeeder = (CheckBox) view.findViewById(R.id.cb_feeder);
    mFrisbeesFromFloor = (CheckBox) view.findViewById(R.id.cb_floor);
    mStrategy = (RadioGroup) view.findViewById(R.id.rg_strategy);
    mSpeed = (RadioGroup) view.findViewById(R.id.rg_speed);
    mManeuverability = (RadioGroup) view.findViewById(R.id.rg_maneuverability);
    mPenalty = (RadioGroup) view.findViewById(R.id.rg_penalty);
    return view;
  }

  @Override
  public void onPause() {
    super.onPause();
    saveTeamMatchData();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    getLoaderManager().initLoader(TEAM_MATCH_LOADER_ID, null, this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_auto_shots_hit_high:
        incrementCount(mEditAutoHitHigh);
        break;
      case R.id.button_auto_shots_miss_high:
        incrementCount(mEditAutoMissHigh);
        break;
      case R.id.button_auto_shots_hit_medium:
        incrementCount(mEditAutoHitMed);
        break;
      case R.id.button_auto_shots_miss_medium:
        incrementCount(mEditAutoMissMed);
        break;
      case R.id.button_auto_shots_hit_low:
        incrementCount(mEditAutoHitLow);
        break;
      case R.id.button_auto_shots_miss_low:
        incrementCount(mEditAutoMissLow);
        break;
      case R.id.button_tele_shots_hit_high:
        incrementCount(mEditTeleHitHigh);
        break;
      case R.id.button_tele_shots_miss_high:
        incrementCount(mEditTeleMissHigh);
        break;
      case R.id.button_tele_shots_hit_medium:
        incrementCount(mEditTeleHitMed);
        break;
      case R.id.button_tele_shots_miss_medium:
        incrementCount(mEditTeleMissMed);
        break;
      case R.id.button_tele_shots_hit_low:
        incrementCount(mEditTeleHitLow);
        break;
      case R.id.button_tele_shots_miss_low:
        incrementCount(mEditTeleMissLow);
        break;
    }
  }

  private void incrementCount(EditText edit) {
    String text = edit.getText().toString();
    int score = TextUtils.isEmpty(text) ? 0 : Integer.valueOf(text);
    edit.setText("" + Math.min(score + 1, 999));
  }

  /** Called by the {@link MatchScoutActivity} in dual pane layouts. */
  void updateDetailsView(long teamMatchId) {
    if (mTeamMatchId != teamMatchId) {
      saveTeamMatchData();
      mScrollView.fullScroll(ScrollView.FOCUS_UP);
      mTeamMatchId = teamMatchId;
      getLoaderManager().restartLoader(TEAM_MATCH_LOADER_ID, null, this);
    }
  }

  /**********************/
  /** LOADER CALLBACKS **/
  /**********************/

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), TeamMatches.CONTENT_URI, PROJECTION,
        TeamMatches._ID + "=?", new String[] { "" + mTeamMatchId }, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    populateTeamMatchData(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }

  private void populateTeamMatchData(Cursor data) {
    if (data.moveToFirst()) {
      // Match number
      int matchNumber = data.getInt(data.getColumnIndexOrThrow(TeamMatches.MATCH_NUMBER));

      String subtitle = getArguments() != null ? "Team " + getArguments().getString(MatchScoutActivity.ARG_TEAM_NUMBER)
          + ", " : "";
      getSherlockActivity().getSupportActionBar().setSubtitle(subtitle + "Match " + matchNumber);

      // Auto scores
      String autoHitHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_HIGH));
      String autoHitMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_MID));
      String autoHitLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_LOW));
      String autoMissHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_HIGH));
      String autoMissMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_MID));
      String autoMissLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_LOW));
      mEditAutoHitHigh.setText(autoHitHigh);
      mEditAutoHitMed.setText(autoHitMed);
      mEditAutoHitLow.setText(autoHitLow);
      mEditAutoMissHigh.setText(autoMissHigh);
      mEditAutoMissMed.setText(autoMissMed);
      mEditAutoMissLow.setText(autoMissLow);

      // Auto scores
      String teleHitHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_HIGH));
      String teleHitMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_MID));
      String teleHitLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_LOW));
      String teleMissHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_HIGH));
      String teleMissMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_MID));
      String teleMissLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_LOW));
      mEditTeleHitHigh.setText(teleHitHigh);
      mEditTeleHitMed.setText(teleHitMed);
      mEditTeleHitLow.setText(teleHitLow);
      mEditTeleMissHigh.setText(teleMissHigh);
      mEditTeleMissMed.setText(teleMissMed);
      mEditTeleMissLow.setText(teleMissLow);

      // Tower level
      int towerLevelOne = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_ONE));
      int towerLevelTwo = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_TWO));
      int towerLevelThree = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_THREE));
      mTowerLevelOne.setChecked(towerLevelOne != 0);
      mTowerLevelTwo.setChecked(towerLevelTwo != 0);
      mTowerLevelThree.setChecked(towerLevelThree != 0);

      // Fell off tower?
      int fellOffTower = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_FELL_OFF));
      mFellOffTower.setChecked(fellOffTower != 0);

      // Frisbees from where?
      int frisbeesFeeder = data.getInt(data.getColumnIndexOrThrow(TeamMatches.FRISBEES_FROM_FLOOR));
      int frisbeesFloor = data.getInt(data.getColumnIndexOrThrow(TeamMatches.FRISBEES_FROM_FEEDER));
      mFrisbeesFromFeeder.setChecked(frisbeesFeeder != 0);
      mFrisbeesFromFloor.setChecked(frisbeesFloor != 0);

      // Human player ability
      int humanPlayerAbility = data.getInt(data.getColumnIndexOrThrow(TeamMatches.HUMAN_PLAYER_ABILITY));
      mHumanPlayerAbility.check(mHumanPlayerAbilityMap.get(humanPlayerAbility));

      // Strategy
      int strategy = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_STRATEGY));
      mStrategy.check(mStrategyMap.get(strategy));

      // Speed
      int speed = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_SPEED));
      mSpeed.check(mSpeedMap.get(speed));

      // Maneuverability
      int maneuverability = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_MANEUVERABILITY));
      mManeuverability.check(mManeuverabilityMap.get(maneuverability));

      // Penalty
      int penalty = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_PENALTY));
      mPenalty.check(mPenaltyMap.get(penalty));
    }
  }

  private void saveTeamMatchData() {
    if (mTeamMatchId != -1) {
      String autoHitHigh = "" + mEditAutoHitHigh.getText();
      String autoHitMid = "" + mEditAutoHitMed.getText();
      String autoHitLow = "" + mEditAutoHitLow.getText();
      String autoMissHigh = "" + mEditAutoMissHigh.getText();
      String autoMissMid = "" + mEditAutoMissMed.getText();
      String autoMissLow = "" + mEditAutoMissLow.getText();

      int numAutoHitHigh = (TextUtils.isEmpty(autoHitHigh)) ? 0 : Integer.valueOf(autoHitHigh);
      int numAutoHitMid = (TextUtils.isEmpty(autoHitMid)) ? 0 : Integer.valueOf(autoHitMid);
      int numAutoHitLow = (TextUtils.isEmpty(autoHitLow)) ? 0 : Integer.valueOf(autoHitLow);
      int numAutoMissHigh = (TextUtils.isEmpty(autoMissHigh)) ? 0 : Integer.valueOf(autoMissHigh);
      int numAutoMissMid = (TextUtils.isEmpty(autoMissMid)) ? 0 : Integer.valueOf(autoMissMid);
      int numAutoMissLow = (TextUtils.isEmpty(autoMissLow)) ? 0 : Integer.valueOf(autoMissLow);

      String teleHitHigh = "" + mEditTeleHitHigh.getText();
      String teleHitMid = "" + mEditTeleHitMed.getText();
      String teleHitLow = "" + mEditTeleHitLow.getText();
      String teleMissHigh = "" + mEditTeleMissHigh.getText();
      String teleMissMid = "" + mEditTeleMissMed.getText();
      String teleMissLow = "" + mEditTeleMissLow.getText();

      int numTeleHitHigh = (TextUtils.isEmpty(teleHitHigh)) ? 0 : Integer.valueOf(teleHitHigh);
      int numTeleHitMid = (TextUtils.isEmpty(teleHitMid)) ? 0 : Integer.valueOf(teleHitMid);
      int numTeleHitLow = (TextUtils.isEmpty(teleHitLow)) ? 0 : Integer.valueOf(teleHitLow);
      int numTeleMissHigh = (TextUtils.isEmpty(teleMissHigh)) ? 0 : Integer.valueOf(teleMissHigh);
      int numTeleMissMid = (TextUtils.isEmpty(teleMissMid)) ? 0 : Integer.valueOf(teleMissMid);
      int numTeleMissLow = (TextUtils.isEmpty(teleMissLow)) ? 0 : Integer.valueOf(teleMissLow);

      int abilityId = mHumanPlayerAbility.getCheckedRadioButtonId();
      int strategyId = mStrategy.getCheckedRadioButtonId();
      int speedId = mSpeed.getCheckedRadioButtonId();
      int manevuerabilityId = mManeuverability.getCheckedRadioButtonId();
      int penaltyId = mPenalty.getCheckedRadioButtonId();

      ContentValues values = new ContentValues();
      values.put(TeamMatches.AUTO_SHOTS_MADE_HIGH, numAutoHitHigh);
      values.put(TeamMatches.AUTO_SHOTS_MADE_MID, numAutoHitMid);
      values.put(TeamMatches.AUTO_SHOTS_MADE_LOW, numAutoHitLow);
      values.put(TeamMatches.AUTO_SHOTS_MISS_HIGH, numAutoMissHigh);
      values.put(TeamMatches.AUTO_SHOTS_MISS_MID, numAutoMissMid);
      values.put(TeamMatches.AUTO_SHOTS_MISS_LOW, numAutoMissLow);
      values.put(TeamMatches.TELE_SHOTS_MADE_HIGH, numTeleHitHigh);
      values.put(TeamMatches.TELE_SHOTS_MADE_MID, numTeleHitMid);
      values.put(TeamMatches.TELE_SHOTS_MADE_LOW, numTeleHitLow);
      values.put(TeamMatches.TELE_SHOTS_MISS_HIGH, numTeleMissHigh);
      values.put(TeamMatches.TELE_SHOTS_MISS_MID, numTeleMissMid);
      values.put(TeamMatches.TELE_SHOTS_MISS_LOW, numTeleMissLow);
      values.put(TeamMatches.TOWER_LEVEL_ONE, mTowerLevelOne.isChecked() ? 1 : 0);
      values.put(TeamMatches.TOWER_LEVEL_TWO, mTowerLevelTwo.isChecked() ? 1 : 0);
      values.put(TeamMatches.TOWER_LEVEL_THREE, mTowerLevelThree.isChecked() ? 1 : 0);
      values.put(TeamMatches.TOWER_FELL_OFF, mFellOffTower.isChecked() ? 1 : 0);
      values.put(TeamMatches.FRISBEES_FROM_FEEDER, mFrisbeesFromFeeder.isChecked() ? 1 : 0);
      values.put(TeamMatches.FRISBEES_FROM_FLOOR, mFrisbeesFromFloor.isChecked() ? 1 : 0);
      values.put(TeamMatches.HUMAN_PLAYER_ABILITY, mRadioButtonIdMap.get(abilityId));
      values.put(TeamMatches.ROBOT_STRATEGY, mRadioButtonIdMap.get(strategyId));
      values.put(TeamMatches.ROBOT_SPEED, mRadioButtonIdMap.get(speedId));
      values.put(TeamMatches.ROBOT_MANEUVERABILITY, mRadioButtonIdMap.get(manevuerabilityId));
      values.put(TeamMatches.ROBOT_PENALTY, mRadioButtonIdMap.get(penaltyId));
      StorageUtil.updateTeamMatch(getActivity(), mTeamMatchId, values);
    }
  }

  private void clearScreen() {
    mEditAutoHitHigh.setText("0");
    mEditAutoMissHigh.setText("0");
    mEditAutoHitMed.setText("0");
    mEditAutoMissMed.setText("0");
    mEditAutoHitLow.setText("0");
    mEditAutoMissLow.setText("0");
    mEditTeleHitHigh.setText("0");
    mEditTeleMissHigh.setText("0");
    mEditTeleHitMed.setText("0");
    mEditTeleMissMed.setText("0");
    mEditTeleHitLow.setText("0");
    mEditTeleMissLow.setText("0");
    mTowerLevelOne.setChecked(false);
    mTowerLevelTwo.setChecked(false);
    mTowerLevelThree.setChecked(false);
    mFellOffTower.setChecked(false);
    mHumanPlayerAbility.clearCheck();
    mFrisbeesFromFeeder.setChecked(false);
    mFrisbeesFromFloor.setChecked(false);
    mStrategy.clearCheck();
    mSpeed.clearCheck();
    mManeuverability.clearCheck();
    mPenalty.clearCheck();
  }

  /****************/
  /** ACTION BAR **/
  /****************/

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.match_details_actionbar, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_clear_match_screen:
        clearScreen();
        return true;
      case R.id.menu_delete_match:
        mCallback.onShowConfirmationDialog(mTeamMatchId);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**********************/
  /** RADIOGROUP STUFF **/
  /**********************/

  // Maps RadioButton ids to ints to be stored in the database
  private static final SparseIntArray mRadioButtonIdMap;

  static {
    // Store the maps' integer values in the database instead of
    // storing the view ids directly. The reason we do this is
    // because the view ids in R.java change each time you
    // compile the APK, so re-compiling the program would break
    // apps that already had existing data in the database.
    mRadioButtonIdMap = new SparseIntArray();
    mRadioButtonIdMap.put(R.id.rb_human_player_high_scoring, 0);
    mRadioButtonIdMap.put(R.id.rb_human_player_neutral, 1);
    mRadioButtonIdMap.put(R.id.rb_human_player_impedes_performance, 2);
    mRadioButtonIdMap.put(R.id.rb_strategy_offense, 0);
    mRadioButtonIdMap.put(R.id.rb_strategy_neutral, 1);
    mRadioButtonIdMap.put(R.id.rb_strategy_defense, 2);
    mRadioButtonIdMap.put(R.id.rb_speed_fast, 0);
    mRadioButtonIdMap.put(R.id.rb_speed_medium, 1);
    mRadioButtonIdMap.put(R.id.rb_speed_slow, 2);
    mRadioButtonIdMap.put(R.id.rb_maneuverability_high, 0);
    mRadioButtonIdMap.put(R.id.rb_maneuverability_medium, 1);
    mRadioButtonIdMap.put(R.id.rb_maneuverability_low, 2);
    mRadioButtonIdMap.put(R.id.rb_penalty_high, 0);
    mRadioButtonIdMap.put(R.id.rb_penalty_medium, 1);
    mRadioButtonIdMap.put(R.id.rb_penalty_low, 2);
    mRadioButtonIdMap.put(-1, -1);
  }

  // Maps ints to ids to be loaded from the database onto the screen
  private static final SparseIntArray mHumanPlayerAbilityMap;
  private static final SparseIntArray mStrategyMap;
  private static final SparseIntArray mSpeedMap;
  private static final SparseIntArray mManeuverabilityMap;
  private static final SparseIntArray mPenaltyMap;

  static {
    mHumanPlayerAbilityMap = new SparseIntArray();
    mHumanPlayerAbilityMap.put(0, R.id.rb_human_player_high_scoring);
    mHumanPlayerAbilityMap.put(1, R.id.rb_human_player_neutral);
    mHumanPlayerAbilityMap.put(2, R.id.rb_human_player_impedes_performance);
    mHumanPlayerAbilityMap.put(-1, -1);

    mStrategyMap = new SparseIntArray();
    mStrategyMap.put(0, R.id.rb_strategy_offense);
    mStrategyMap.put(1, R.id.rb_strategy_neutral);
    mStrategyMap.put(2, R.id.rb_strategy_defense);
    mStrategyMap.put(-1, -1);

    mSpeedMap = new SparseIntArray();
    mSpeedMap.put(0, R.id.rb_speed_fast);
    mSpeedMap.put(1, R.id.rb_speed_medium);
    mSpeedMap.put(2, R.id.rb_speed_slow);
    mSpeedMap.put(-1, -1);

    mManeuverabilityMap = new SparseIntArray();
    mManeuverabilityMap.put(0, R.id.rb_maneuverability_high);
    mManeuverabilityMap.put(1, R.id.rb_maneuverability_medium);
    mManeuverabilityMap.put(2, R.id.rb_maneuverability_low);
    mManeuverabilityMap.put(-1, -1);

    mPenaltyMap = new SparseIntArray();
    mPenaltyMap.put(0, R.id.rb_penalty_high);
    mPenaltyMap.put(1, R.id.rb_penalty_medium);
    mPenaltyMap.put(2, R.id.rb_penalty_low);
    mPenaltyMap.put(-1, -1);
  }
}

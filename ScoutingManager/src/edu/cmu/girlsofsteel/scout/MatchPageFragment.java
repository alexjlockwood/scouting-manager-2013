package edu.cmu.girlsofsteel.scout;

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

import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

public abstract class MatchPageFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {
  @SuppressWarnings("unused")
  private static final String TAG = MatchPageFragment.class.getSimpleName();
  
  private static final String KEY_TEAM_MATCH_ID = "key_team_match_id";
  protected long mTeamMatchId = -1;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mTeamMatchId = savedInstanceState.getLong(KEY_TEAM_MATCH_ID);
    } else {
      mTeamMatchId = getArguments().getLong(MatchScoutActivity.ARG_TEAM_MATCH_ID);
    }
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(KEY_TEAM_MATCH_ID, mTeamMatchId);
  }
  
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getLoaderManager().initLoader(0, null, this);
  }
  
  @Override
  public void onPause() {
    super.onPause();
    if (mTeamMatchId != -1) {
      saveData();
    }
  }
  
  public abstract Loader<Cursor> onCreateLoader(int id, Bundle args);
  
  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    if (data.moveToFirst()) {
      populateViews(data);
    }
  }

  @Override
  public void onLoaderReset(Loader<Cursor> data) {
  }
  
  protected abstract void saveData();
  
  protected abstract void populateViews(Cursor data);
  
  protected abstract void clearScreen();

  /** Called by the {@link MatchDetailsFragment} when the match has been deleted. */
  protected void matchDeleted() {
    mTeamMatchId = -1;
    clearScreen();
  }
  
  /** Called by the {@link MatchDetailsFragment} in dual pane layouts. */
  protected void updateDetailsView(long teamMatchId) {
    if (mTeamMatchId != teamMatchId) {
      saveData();
      mTeamMatchId = teamMatchId;
      getLoaderManager().restartLoader(0, null, this);
    }
  }
  
  private static abstract class MatchAutoTelePage extends MatchPageFragment implements View.OnClickListener {
    @SuppressWarnings("unused")
    private static final String TAG = MatchAutoTelePage.class.getSimpleName();
    
    protected Button mButtonHitHigh, mButtonMissHigh, mButtonHitMed, mButtonMissMed, mButtonHitLow, mButtonMissLow;
    protected EditText mEditHitHigh, mEditMissHigh, mEditHitMed, mEditMissMed, mEditHitLow, mEditMissLow;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_match_scout_shots, container, false);
      mButtonHitHigh = (Button) view.findViewById(R.id.button_shots_hit_high);
      mButtonHitMed = (Button) view.findViewById(R.id.button_shots_hit_medium);
      mButtonHitLow = (Button) view.findViewById(R.id.button_shots_hit_low);      
      mButtonMissHigh = (Button) view.findViewById(R.id.button_shots_miss_high);
      mButtonMissMed = (Button) view.findViewById(R.id.button_shots_miss_medium);
      mButtonMissLow = (Button) view.findViewById(R.id.button_shots_miss_low);     
      mEditHitHigh = (EditText) view.findViewById(R.id.edittext_shots_hit_high);
      mEditHitMed = (EditText) view.findViewById(R.id.edittext_shots_hit_medium);
      mEditHitLow = (EditText) view.findViewById(R.id.edittext_shots_hit_low);
      mEditMissHigh = (EditText) view.findViewById(R.id.edittext_shots_miss_high);
      mEditMissMed = (EditText) view.findViewById(R.id.edittext_shots_miss_medium);
      mEditMissLow = (EditText) view.findViewById(R.id.edittext_shots_miss_low);
      mButtonHitHigh.setOnClickListener(this);
      mButtonHitMed.setOnClickListener(this);
      mButtonHitLow.setOnClickListener(this);
      mButtonMissHigh.setOnClickListener(this);
      mButtonMissMed.setOnClickListener(this);
      mButtonMissLow.setOnClickListener(this);    
      mEditHitHigh.setOnClickListener(this);
      mEditHitMed.setOnClickListener(this);
      mEditHitLow.setOnClickListener(this);   
      mEditMissHigh.setOnClickListener(this);
      mEditMissMed.setOnClickListener(this);
      mEditMissLow.setOnClickListener(this);   
      return view;
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
        case R.id.button_shots_hit_high:
          incrementCount(mEditHitHigh);
          break;
        case R.id.edittext_shots_hit_high:
          decrementCount(mEditHitHigh);
          break;
        case R.id.button_shots_miss_high:
          incrementCount(mEditMissHigh);
          break;
        case R.id.edittext_shots_miss_high:
          decrementCount(mEditMissHigh);
          break;
        case R.id.button_shots_hit_medium:
          incrementCount(mEditHitMed);
          break;
        case R.id.edittext_shots_hit_medium:
          decrementCount(mEditHitMed);
          break;
        case R.id.button_shots_miss_medium:
          incrementCount(mEditMissMed);
          break;
        case R.id.edittext_shots_miss_medium:
          decrementCount(mEditMissMed);
          break;
        case R.id.button_shots_hit_low:
          incrementCount(mEditHitLow);
          break;
        case R.id.edittext_shots_hit_low:
          decrementCount(mEditHitLow);
          break;
        case R.id.button_shots_miss_low:
          incrementCount(mEditMissLow);
          break;
        case R.id.edittext_shots_miss_low:
          decrementCount(mEditMissLow);
          break;
      }
    }
    
    private void incrementCount(EditText edit) {
      String text = edit.getText().toString();
      int score = TextUtils.isEmpty(text) ? 0 : Integer.valueOf(text);
      edit.setText("" + Math.min(score + 1, 999));
    }
    
    private void decrementCount(EditText edit) {
      String text = edit.getText().toString();
      int score = TextUtils.isEmpty(text) ? 0 : Integer.valueOf(text);
      edit.setText("" + Math.max(score - 1, 0));
    }
    
    @Override
    protected void clearScreen() {
      mEditHitHigh.setText("0");
      mEditHitMed.setText("0");
      mEditHitLow.setText("0");
      mEditMissHigh.setText("0");
      mEditMissMed.setText("0");
      mEditMissLow.setText("0");
    }
  }
  
  public static class MatchAutoPage extends MatchAutoTelePage {  
    @SuppressWarnings("unused")
    private static final String TAG = MatchAutoPage.class.getSimpleName();

    private static final String[] PROJECTION = {
      TeamMatches._ID,
      TeamMatches.AUTO_SHOTS_MADE_LOW,
      TeamMatches.AUTO_SHOTS_MADE_MID,
      TeamMatches.AUTO_SHOTS_MADE_HIGH,
      TeamMatches.AUTO_SHOTS_MISS_LOW,
      TeamMatches.AUTO_SHOTS_MISS_MID,
      TeamMatches.AUTO_SHOTS_MISS_HIGH,
    };
    
    /**
     * Static factory method which returns a new instance with the given team
     * match id and number set in its arguments.
     */
    public static MatchAutoPage newInstance(long teamMatchId) {
      MatchAutoPage frag = new MatchAutoPage();
      Bundle args = new Bundle();
      args.putLong(MatchScoutActivity.ARG_TEAM_MATCH_ID, teamMatchId);
      frag.setArguments(args);
      return frag;
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(getActivity(), TeamMatches.CONTENT_URI, PROJECTION,
          TeamMatches._ID + "=?", new String[] { "" + mTeamMatchId }, null);
    }
    
    @Override
    protected void populateViews(Cursor data) {
        String autoHitHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_HIGH));
        String autoHitMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_MID));
        String autoHitLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MADE_LOW));
        String autoMissHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_HIGH));
        String autoMissMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_MID));
        String autoMissLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.AUTO_SHOTS_MISS_LOW));
        mEditHitHigh.setText(autoHitHigh);
        mEditHitMed.setText(autoHitMed);
        mEditHitLow.setText(autoHitLow);
        mEditMissHigh.setText(autoMissHigh);
        mEditMissMed.setText(autoMissMed);
        mEditMissLow.setText(autoMissLow);
    }

    @Override
    protected void saveData() {
        String autoHitHigh = "" + mEditHitHigh.getText();
        String autoHitMid = "" + mEditHitMed.getText();
        String autoHitLow = "" + mEditHitLow.getText();
        String autoMissHigh = "" + mEditMissHigh.getText();
        String autoMissMid = "" + mEditMissMed.getText();
        String autoMissLow = "" + mEditMissLow.getText();
        
        int numAutoHitHigh = (TextUtils.isEmpty(autoHitHigh)) ? 0 : Integer.valueOf(autoHitHigh);
        int numAutoHitMid = (TextUtils.isEmpty(autoHitMid)) ? 0 : Integer.valueOf(autoHitMid);
        int numAutoHitLow = (TextUtils.isEmpty(autoHitLow)) ? 0 : Integer.valueOf(autoHitLow);
        int numAutoMissHigh = (TextUtils.isEmpty(autoMissHigh)) ? 0 : Integer.valueOf(autoMissHigh);
        int numAutoMissMid = (TextUtils.isEmpty(autoMissMid)) ? 0 : Integer.valueOf(autoMissMid);
        int numAutoMissLow = (TextUtils.isEmpty(autoMissLow)) ? 0 : Integer.valueOf(autoMissLow);
        
        ContentValues values = new ContentValues();
        values.put(TeamMatches.AUTO_SHOTS_MADE_HIGH, numAutoHitHigh);
        values.put(TeamMatches.AUTO_SHOTS_MADE_MID, numAutoHitMid);
        values.put(TeamMatches.AUTO_SHOTS_MADE_LOW, numAutoHitLow);
        values.put(TeamMatches.AUTO_SHOTS_MISS_HIGH, numAutoMissHigh);
        values.put(TeamMatches.AUTO_SHOTS_MISS_MID, numAutoMissMid);
        values.put(TeamMatches.AUTO_SHOTS_MISS_LOW, numAutoMissLow);
        StorageUtil.updateTeamMatch(getActivity(), mTeamMatchId, values);
    }
  }
  
  public static class MatchTelePage extends MatchAutoTelePage {  
    @SuppressWarnings("unused")
    private static final String TAG = MatchTelePage.class.getSimpleName();

    private static final String[] PROJECTION = {
      TeamMatches._ID,
      TeamMatches.TELE_SHOTS_MADE_LOW,
      TeamMatches.TELE_SHOTS_MADE_MID,
      TeamMatches.TELE_SHOTS_MADE_HIGH,
      TeamMatches.TELE_SHOTS_MISS_LOW,
      TeamMatches.TELE_SHOTS_MISS_MID,
      TeamMatches.TELE_SHOTS_MISS_HIGH,
    };
      
    /**
     * Static factory method which returns a new instance with the given team
     * match id and number set in its arguments.
     */
    public static MatchTelePage newInstance(long teamMatchId) {
      MatchTelePage frag = new MatchTelePage();
      Bundle args = new Bundle();
      args.putLong(MatchScoutActivity.ARG_TEAM_MATCH_ID, teamMatchId);
      frag.setArguments(args);
      return frag;
    }
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(getActivity(), TeamMatches.CONTENT_URI, PROJECTION,
          TeamMatches._ID + "=?", new String[] { "" + mTeamMatchId }, null);
    }
    
    @Override
    protected void populateViews(Cursor data) {
      String teleHitHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_HIGH));
      String teleHitMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_MID));
      String teleHitLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MADE_LOW));
      String teleMissHigh = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_HIGH));
      String teleMissMed = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_MID));
      String teleMissLow = data.getString(data.getColumnIndexOrThrow(TeamMatches.TELE_SHOTS_MISS_LOW));
      mEditHitHigh.setText(teleHitHigh);
      mEditHitMed.setText(teleHitMed);
      mEditHitLow.setText(teleHitLow);
      mEditMissHigh.setText(teleMissHigh);
      mEditMissMed.setText(teleMissMed);
      mEditMissLow.setText(teleMissLow);
    }

    @Override
    protected void saveData() {
      String teleHitHigh = "" + mEditHitHigh.getText();
      String teleHitMid = "" + mEditHitMed.getText();
      String teleHitLow = "" + mEditHitLow.getText();
      String teleMissHigh = "" + mEditMissHigh.getText();
      String teleMissMid = "" + mEditMissMed.getText();
      String teleMissLow = "" + mEditMissLow.getText();

      int numAutoHitHigh = (TextUtils.isEmpty(teleHitHigh)) ? 0 : Integer.valueOf(teleHitHigh);
      int numAutoHitMid = (TextUtils.isEmpty(teleHitMid)) ? 0 : Integer.valueOf(teleHitMid);
      int numAutoHitLow = (TextUtils.isEmpty(teleHitLow)) ? 0 : Integer.valueOf(teleHitLow);
      int numAutoMissHigh = (TextUtils.isEmpty(teleMissHigh)) ? 0 : Integer.valueOf(teleMissHigh);
      int numAutoMissMid = (TextUtils.isEmpty(teleMissMid)) ? 0 : Integer.valueOf(teleMissMid);
      int numAutoMissLow = (TextUtils.isEmpty(teleMissLow)) ? 0 : Integer.valueOf(teleMissLow);

      ContentValues values = new ContentValues();
      values.put(TeamMatches.TELE_SHOTS_MADE_HIGH, numAutoHitHigh);
      values.put(TeamMatches.TELE_SHOTS_MADE_MID, numAutoHitMid);
      values.put(TeamMatches.TELE_SHOTS_MADE_LOW, numAutoHitLow);
      values.put(TeamMatches.TELE_SHOTS_MISS_HIGH, numAutoMissHigh);
      values.put(TeamMatches.TELE_SHOTS_MISS_MID, numAutoMissMid);
      values.put(TeamMatches.TELE_SHOTS_MISS_LOW, numAutoMissLow);
      StorageUtil.updateTeamMatch(getActivity(), mTeamMatchId, values);
    }
  }
  
  public static class MatchGeneralPage extends MatchPageFragment {  
    @SuppressWarnings("unused")
    private static final String TAG = MatchGeneralPage.class.getSimpleName();
    
    private static final String[] PROJECTION = {
      TeamMatches._ID,
      TeamMatches.SHOOTS_FROM_WHERE,
      TeamMatches.TOWER_FELL_OFF,
      TeamMatches.TOWER_LEVEL_ONE,
      TeamMatches.TOWER_LEVEL_TWO,
      TeamMatches.TOWER_LEVEL_THREE,
      TeamMatches.HUMAN_PLAYER_ABILITY,
      TeamMatches.FRISBEES_FROM_FEEDER,
      TeamMatches.FRISBEES_FROM_FLOOR,
      TeamMatches.ROBOT_STRATEGY,
      TeamMatches.ROBOT_SPEED,
      TeamMatches.ROBOT_MANEUVERABILITY,
      TeamMatches.ROBOT_PENALTY,
    };
      
    private ScrollView mScrollView;
    private EditText mShootsFromWhere;
    private CheckBox mTowerLevelOne, mTowerLevelTwo, mTowerLevelThree;
    private CompoundButton mFellOffTower;
    private RadioGroup mHumanPlayerAbility;
    private CheckBox mFrisbeesFromFeeder, mFrisbeesFromFloor;
    private RadioGroup mStrategy, mSpeed, mManeuverability, mPenalty;
    
    /**
     * Static factory method which returns a new instance with the given team
     * match id and number set in its arguments.
     */
    public static MatchGeneralPage newInstance(long teamMatchId) {
      MatchGeneralPage frag = new MatchGeneralPage();
      Bundle args = new Bundle();
      args.putLong(MatchScoutActivity.ARG_TEAM_MATCH_ID, teamMatchId);
      frag.setArguments(args);
      return frag;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_match_scout_general, container, false);
      mScrollView = (ScrollView) view.findViewById(R.id.match_scout_scrollview);
      mShootsFromWhere = (EditText) view.findViewById(R.id.et_shoots_from_where);
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
    protected void updateDetailsView(long teamMatchId) {
      if (mTeamMatchId != teamMatchId) {
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
      }
      super.updateDetailsView(teamMatchId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new CursorLoader(getActivity(), TeamMatches.CONTENT_URI, PROJECTION,
          TeamMatches._ID + "=?", new String[] { "" + mTeamMatchId }, null);
    }

    @Override
    protected void populateViews(Cursor data) {
        int towerLevelOne = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_ONE));
        int towerLevelTwo = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_TWO));
        int towerLevelThree = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_LEVEL_THREE));
        String shootsFromWhere = data.getString(data.getColumnIndexOrThrow(TeamMatches.SHOOTS_FROM_WHERE));
        int fellOffTower = data.getInt(data.getColumnIndexOrThrow(TeamMatches.TOWER_FELL_OFF));
        int frisbeesFeeder = data.getInt(data.getColumnIndexOrThrow(TeamMatches.FRISBEES_FROM_FLOOR));
        int frisbeesFloor = data.getInt(data.getColumnIndexOrThrow(TeamMatches.FRISBEES_FROM_FEEDER));
        int humanPlayerAbility = data.getInt(data.getColumnIndexOrThrow(TeamMatches.HUMAN_PLAYER_ABILITY));
        int strategy = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_STRATEGY));
        int speed = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_SPEED));
        int maneuverability = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_MANEUVERABILITY));
        int penalty = data.getInt(data.getColumnIndexOrThrow(TeamMatches.ROBOT_PENALTY));
        mTowerLevelOne.setChecked(towerLevelOne != 0);
        mTowerLevelTwo.setChecked(towerLevelTwo != 0);
        mTowerLevelThree.setChecked(towerLevelThree != 0);        
        mShootsFromWhere.setText(shootsFromWhere);
        mFellOffTower.setChecked(fellOffTower != 0);
        mFrisbeesFromFeeder.setChecked(frisbeesFeeder != 0);
        mFrisbeesFromFloor.setChecked(frisbeesFloor != 0);
        mHumanPlayerAbility.check(mHumanPlayerAbilityMap.get(humanPlayerAbility));
        mStrategy.check(mStrategyMap.get(strategy));
        mSpeed.check(mSpeedMap.get(speed));
        mManeuverability.check(mManeuverabilityMap.get(maneuverability));
        mPenalty.check(mPenaltyMap.get(penalty));
    }

    @Override
    protected void saveData() {
      String shootsFromWhere = mShootsFromWhere.getText().toString();
      int abilityId = mHumanPlayerAbility.getCheckedRadioButtonId();
      int strategyId = mStrategy.getCheckedRadioButtonId();
      int speedId = mSpeed.getCheckedRadioButtonId();
      int manevuerabilityId = mManeuverability.getCheckedRadioButtonId();
      int penaltyId = mPenalty.getCheckedRadioButtonId();
      
      ContentValues values = new ContentValues();
      values.put(TeamMatches.SHOOTS_FROM_WHERE, shootsFromWhere);
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

    @Override
    protected void clearScreen() {
      mShootsFromWhere.setText("");
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
}
package edu.cmu.girlsofsteel.scout.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines a contract between the Scout content provider and its clients. A
 * contract defines the information that a client needs to access the provider
 * as one or more data tables. A contract is a public, non-extendable (final)
 * class that contains constants defining column names and URIs. A well-written
 * client depends only on the constants in the contract.
 */
public final class ScoutContract {

  //@formatter:off
  /** Column names for teams table */
  interface TeamsColumns {

    /** The team's unique number */
    String NUMBER = "team_number";
    /** The team's nickname */
    String NAME = "team_name";
    /** The uri address for the team's photo */
    String PHOTO = "team_photo";

    /** Does the robot score on the low hoop? */
    String ROBOT_CAN_SCORE_ON_LOW = "robot_can_score_on_low";
    /** Does the robot score on the middle hoop? */
    String ROBOT_CAN_SCORE_ON_MID = "robot_can_score_on_mid";
    /** Does the robot score on the high hoop? */
    String ROBOT_CAN_SCORE_ON_HIGH = "robot_can_score_on_high";

    /** Can the robot climb? */
    String ROBOT_CAN_CLIMB = "robot_can_climb";
    /** Can the robot climb on level 1? */
    String ROBOT_CAN_CLIMB_LEVEL_ONE = "robot_can_climb_level_one";
    /** Can the robot climb on level 2? */
    String ROBOT_CAN_CLIMB_LEVEL_TWO = "robot_can_climb_level_two";
    /** Can the robot climb on level 3? */
    String ROBOT_CAN_CLIMB_LEVEL_THREE = "robot_can_climb_level_three";
    /** Can the robot help climb? */
    String ROBOT_CAN_HELP_CLIMB = "robot_can_help_climb";

    /** Can the robot go under the tower? */
    String ROBOT_CAN_GO_UNDER_TOWER = "robot_go_under_tower";
    /** How many driving gears does the robot have? */
    String ROBOT_NUM_DRIVING_GEARS = "robot_num_driving_gears";
    /**
     * 0 - Basic tank drive (4 wheels)
     * 1 - Basic tank drive (6 wheels)
     * 2 - Basic tank drive (8 wheels)
     * 3 - Basic tank drive (tank tread)
     * 4 - Omni (kiwi)
     * 5 - Omni (mecanum)
     * 6 - Omni (swerve/crab)
     * 7 - Other
     */
    String ROBOT_DRIVE_TRAIN = "robot_drive_train";
    /**
     * 0 - KoP
     * 1 - plaction/traction
     * 2 - pneumatic
     * 3 - mecanum
     * 4 - other
     */
    String ROBOT_TYPE_OF_WHEEL = "robot_type_of_wheel";
  }

  /** Column names for matches table */
  interface MatchesColumns {
    /** The match's unique number */
    String NUMBER = "match_number";
  }

  /** Column names for team_matches table */
  interface TeamMatchesColumns {
    /** References '_id' in Teams table */
    String TEAM_ID = "team_id";
    /** References '_id' in Matches table */
    String MATCH_ID = "match_id";

    String AUTO_SHOTS_MADE_LOW = "auto_shots_made_low";
    String AUTO_SHOTS_MADE_MID = "auto_shots_made_mid";
    String AUTO_SHOTS_MADE_HIGH = "auto_shots_made_high";
    String TELE_SHOTS_MADE_LOW = "tele_shots_made_low";
    String TELE_SHOTS_MADE_MID = "tele_shots_made_mid";
    String TELE_SHOTS_MADE_HIGH = "tele_shots_made_high";

    String SHOOTS_FROM_BACK_RIGHT = "shoots_from_back_right";
    String SHOOTS_FROM_BACK_LEFT = "shoots_from_back_left";
    String SHOOTS_FROM_FRONT_RIGHT = "shoots_from_front_right";
    String SHOOTS_FROM_FRONT_LEFT = "shoots_from_front_left";
    String SHOOTS_FROM_SIDE_RIGHT = "shoots_from_side_right";
    String SHOOTS_FROM_SIDE_LEFT = "shoots_from_side_left";
    String SHOOTS_FROM_FRONT = "shoots_from_front";
    String SHOOTS_FROM_ANYWHERE = "shoots_from_anywhere";
    String SHOOTS_FROM_OTHER = "shoots_from_other";

    String TOWER_LEVEL_NONE = "tower_level_none";
    String TOWER_LEVEL_ONE = "tower_level_one";
    String TOWER_LEVEL_TWO = "tower_level_two";
    String TOWER_LEVEL_THREE = "tower_level_three";
    String TOWER_FELL_OFF = "tower_fell_off";

    /**
     * 0 - Impedes performance
     * 1 - Neutral
     * 2 - High scoring
     */
    String HUMAN_PLAYER_ABILITY = "human_player_ability";

    String FRISBEES_FROM_FEEDER = "frisbees_from_feeder";
    String FRISBEES_FROM_FLOOR = "frisbees_from_floor";

    /**
     * 0 - Defensive
     * 1 - Neutral
     * 2 - Offensive
     */
    String ROBOT_STRATEGY = "robot_strategy";

    /**
     * 0 - Slow
     * 1 - Medium
     * 2 - Fast
     */
    String ROBOT_SPEED = "robot_speed";

    /**
     * 0 - Low
     * 1 - Medium
     * 2 - High
     */
    String ROBOT_MANEUVERABILITY = "robot_maneuverability";

    /**
     * 0 - Low
     * 1 - Medium
     * 2 - High
     */
    String ROBOT_PENALTY = "robot_penalty";
  }
  //@formatter:on

  public static final String AUTHORITY = "edu.cmu.girlsofsteel.scout";
  private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
  private static final String PATH_TEAMS = "teams";
  private static final String PATH_MATCHES = "matches";
  private static final String PATH_TEAM_MATCHES = "team_matches";

  /**
   * Teams table contract. This table stores a list of teams and information
   * about those teams.
   */
  public static final class Teams implements BaseColumns, TeamsColumns {
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.scout.team";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.scout.team";

    // Get all teams
    public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_TEAMS).build();

    // Get a single team
    public static Uri teamIdUri(long teamId) {
      return CONTENT_URI.buildUpon().appendPath("" + teamId).build();
    }

    private Teams() {
    }
  }

  /**
   * Matches table contract. This table store matches and information about the
   * matches.
   */
  public static final class Matches implements BaseColumns, MatchesColumns {
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.scout.match";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.scout.match";

    // Get all matches
    public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_MATCHES).build();

    // Get a single match
    public static Uri matchIdUri(long matchId) {
      return CONTENT_URI.buildUpon().appendPath("" + matchId).build();
    }

    private Matches() {
    }
  }

  /**
   * The "team_match" table provides information on how a specific team
   * performed while competing during a given match. This class contains no
   * methods, but implements BaseColumns and TeamMatchesColumns for public
   * access to the table's column names.
   */
  public static final class TeamMatches implements BaseColumns,
      TeamMatchesColumns {
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.scout.team_match";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.scout.team_match";

    // Get all team-matches
    public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_TEAM_MATCHES)
        .build();

    // Get all team-matches for a team
    public static Uri matchTeamIdUri(long teamId) {
      return CONTENT_URI.buildUpon().appendPath(PATH_TEAMS).appendPath("" + teamId).build();
    }

    // Get a single team-match
    public static Uri matchIdTeamIdUri(long matchId, long teamId) {
      return CONTENT_URI.buildUpon().appendPath("" + matchId).appendPath(PATH_TEAMS)
          .appendPath("" + teamId).build();
    }

    private TeamMatches() {
    }
  }

  private ScoutContract() {
  }
}
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

  /** Column names for teams table */
  interface TeamsColumns {
    // The team's unique number
    String NUMBER = "team_number";
    // The team's nickname
    String NAME = "team_name";
    // The uri address for the team's photo
    String PHOTO = "team_photo";
  }

  /** Column names for matches table */
  interface MatchesColumns {
    // The match's unique number
    String NUMBER = "match_number";
  }

  /** Column names for team_matches table */
  interface TeamMatchesColumns {
    // References '_id' in Teams table
    String TEAM_ID = "team_id";
    // References '_id' in Matches table
    String MATCH_ID = "match_id";
  }

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
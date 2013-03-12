package edu.cmu.girlsofsteel.scout;

import java.lang.ref.WeakReference;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.cmu.girlsofsteel.scout.dialogs.DeleteMatchDialog;

public class MatchDetailsFragment extends SherlockFragment {
  @SuppressWarnings("unused")
  private static final String TAG = MatchDetailsFragment.class.getSimpleName();
  
  private static final String KEY_TEAM_MATCH_ID = "key_team_match_id";

  private long mTeamMatchId = -1;
  private OnMatchDeletedListener mCallback;
  private ViewPager mViewPager;
  private MatchAdapter mAdapter;
  
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
    setRetainInstance(true);
    if (savedInstanceState != null) {
      // If activity recreated due to a configuration change, restore the
      // previous match selection set by onSaveInstanceState(). This is
      // primarily necessary when in the two-pane layout.
      mTeamMatchId = savedInstanceState.getLong(KEY_TEAM_MATCH_ID);
    } else if (getArguments() != null) {
      // If the fragment was created programatically (not from XML) then
      // the arguments should have been set with the selected team match id.
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
    return inflater.inflate(R.layout.fragment_match_pager, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    mAdapter = new MatchAdapter(getChildFragmentManager());
    mViewPager.setAdapter(mAdapter);
    mViewPager.setOffscreenPageLimit(2);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
  }
  
  void saveAllData() {
    for (int i=0; i<3; i++) {
      MatchDetailsPageFragment frag = mAdapter.getFragment(i);
      if (frag != null) {
        frag.saveData();
      }
    }
  }
  
  /** Called by the {@link MatchScoutActivity} when the match has been deleted. */
  void matchDeleted() {
    mTeamMatchId = -1;
    for (int i=0; i<3; i++) {
      MatchDetailsPageFragment frag = mAdapter.getFragment(i);
      if (frag != null) {
        frag.matchDeleted();
      }
    }
  }
  
  /** Called by the {@link MatchScoutActivity} in dual pane layouts. */
  void updateDetailsView(long teamMatchId) {
    if (mTeamMatchId != teamMatchId) {
      mTeamMatchId = teamMatchId;
      for (int i=0; i<3; i++) {
        MatchDetailsPageFragment frag = mAdapter.getFragment(i);
        if (frag != null) {
          frag.updateDetailsView(teamMatchId);
        }
      }
    }
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
        for (int i=0; i<3; i++) {
          MatchDetailsPageFragment frag = mAdapter.getFragment(i);
          if (frag != null) {
            frag.clearScreen();
          }
        }
        return true;
      case R.id.menu_delete_match:
        if (mTeamMatchId != -1) {
          mCallback.onShowConfirmationDialog(mTeamMatchId);
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  /************************/
  /** VIEW PAGER ADAPTER **/
  /************************/
  
  private class MatchAdapter extends FragmentPagerAdapter {
    private final String[] PAGE_TITLES = { "Autonomous", "Tele-Op", "General" };
    
    private SparseArray<WeakReference<Fragment>> mFragments = new SparseArray<WeakReference<Fragment>>();
    
    public MatchAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public int getCount() {
      return PAGE_TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
      Fragment frag = null;
      switch(position) {
        case 0:
          frag = MatchDetailsPageFragment.MatchAutoPage.newInstance(mTeamMatchId);
          break;
        case 1: 
          frag = MatchDetailsPageFragment.MatchTelePage.newInstance(mTeamMatchId);
          break;
        case 2: 
          frag = MatchDetailsPageFragment.MatchGeneralPage.newInstance(mTeamMatchId);
          break;
      }
      mFragments.put(position, new WeakReference<Fragment>(frag));
      return frag;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragments.remove(position);
    }

    public MatchDetailsPageFragment getFragment(int position) {
      WeakReference<Fragment> weakRef = mFragments.get(position);
      if (weakRef != null) {
        return (MatchDetailsPageFragment) weakRef.get();
      }
      return null;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
      return PAGE_TITLES[position].toUpperCase(Locale.US);
    }
  }
}

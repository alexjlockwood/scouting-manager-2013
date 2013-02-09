package edu.cmu.girlsofsteel.scout.util.actionmodecompat;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * An implementation of {@link ActionMode} that proxies to the native
 * {@link android.view.ActionMode} implementation (shows the contextual action
 * bar).
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class ActionModeHoneycomb extends ActionMode {
  android.view.ActionMode mNativeActionMode;

  ActionModeHoneycomb() {
  }

  static ActionModeHoneycomb startInternal(final FragmentActivity activity, final Callback callback) {
    final ActionModeHoneycomb actionMode = new ActionModeHoneycomb();
    activity.startActionMode(new android.view.ActionMode.Callback() {
      @Override
      public boolean onCreateActionMode(android.view.ActionMode nativeActionMode, Menu menu) {
        actionMode.mNativeActionMode = nativeActionMode;
        return callback.onCreateActionMode(actionMode, menu);
      }

      @Override
      public boolean onPrepareActionMode(android.view.ActionMode nativeActionMode, Menu menu) {
        return callback.onPrepareActionMode(actionMode, menu);
      }

      @Override
      public boolean onActionItemClicked(android.view.ActionMode nativeActionMode,
          MenuItem menuItem) {
        return callback.onActionItemClicked(actionMode, menuItem);
      }

      @Override
      public void onDestroyActionMode(android.view.ActionMode nativeActionMode) {
        callback.onDestroyActionMode(actionMode);
      }
    });
    return actionMode;
  }

  /** {@inheritDoc} */
  @Override
  public void setTitle(CharSequence title) {
    mNativeActionMode.setTitle(title);
  }

  /** {@inheritDoc} */
  @Override
  public void setTitle(int resId) {
    mNativeActionMode.setTitle(resId);
  }

  /** {@inheritDoc} */
  @Override
  public void invalidate() {
    mNativeActionMode.invalidate();
  }

  /** {@inheritDoc} */
  @Override
  public void finish() {
    mNativeActionMode.finish();
  }

  /** {@inheritDoc} */
  @Override
  public CharSequence getTitle() {
    return mNativeActionMode.getTitle();
  }

  /** {@inheritDoc} */
  @Override
  public MenuInflater getMenuInflater() {
    return mNativeActionMode.getMenuInflater();
  }

  public static void beginMultiChoiceMode(ListView listView, FragmentActivity activity,
      final MultiChoiceModeListener listener) {
    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
      ActionModeHoneycomb mWrappedActionMode;

      @Override
      public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int position,
          long id, boolean checked) {
        listener.onItemCheckedStateChanged(mWrappedActionMode, position, id, checked);
      }

      @Override
      public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
        if (mWrappedActionMode == null) {
          mWrappedActionMode = new ActionModeHoneycomb();
          mWrappedActionMode.mNativeActionMode = actionMode;
        }
        return listener.onCreateActionMode(mWrappedActionMode, menu);
      }

      @Override
      public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
        if (mWrappedActionMode == null) {
          mWrappedActionMode = new ActionModeHoneycomb();
          mWrappedActionMode.mNativeActionMode = actionMode;
        }
        return listener.onPrepareActionMode(mWrappedActionMode, menu);
      }

      @Override
      public boolean onActionItemClicked(android.view.ActionMode actionMode,
          MenuItem menuItem) {
        return listener.onActionItemClicked(mWrappedActionMode, menuItem);
      }

      @Override
      public void onDestroyActionMode(android.view.ActionMode actionMode) {
        listener.onDestroyActionMode(mWrappedActionMode);
      }
    });
  }
}
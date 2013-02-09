package edu.cmu.girlsofsteel.scout.util.actionmodecompat;

import android.widget.AbsListView;

/**
 * A MultiChoiceModeListener receives events for
 * {@link AbsListView#CHOICE_MODE_MULTIPLE_MODAL}. It acts as the
 * {@link ActionMode.Callback} for the selection mode and also receives
 * {@link #onItemCheckedStateChanged(ActionMode, int, long, boolean)} events
 * when the user selects and deselects list items.
 */
public interface MultiChoiceModeListener extends ActionMode.Callback {

  /**
   * Called when an item is checked or unchecked during selection mode.
   *
   * @param mode The {@link ActionMode} providing the selection mode
   * @param position Adapter position of the item that was checked or unchecked
   * @param id Adapter ID of the item that was checked or unchecked
   * @param checked <code>true</code> if the item is now checked,
   *          <code>false</code> if the item is now unchecked.
   */
  public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked);
}

package edu.cmu.girlsofsteel.scouting.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import edu.cmu.girlsofsteel.scouting.MatchDetailsFragment.OnMatchDeletedListener;
import edu.cmu.girlsofsteel.scouting.R;

/**
 * Confirmation dialog which prompts the user before a match is permanently
 * deleted.
 *
 * @author Alex Lockwood
 */
public class DeleteMatchDialog extends DialogFragment {
  private static final String KEY_ID = "key_id";
  private OnMatchDeletedListener mCallback;

  public static DeleteMatchDialog newInstance(long id) {
    DeleteMatchDialog dialog = new DeleteMatchDialog();
    Bundle args = new Bundle();
    args.putLong(KEY_ID, id);
    dialog.setArguments(args);
    return dialog;
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
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final long id = getArguments().getLong(KEY_ID);
    String title = getResources().getQuantityString(R.plurals.title_delete_matches, 1);
    String msg = getResources().getQuantityString(R.plurals.message_delete_matches, 1, 1);
    return new AlertDialog.Builder(getActivity())
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                mCallback.onMatchDeleted(id);
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

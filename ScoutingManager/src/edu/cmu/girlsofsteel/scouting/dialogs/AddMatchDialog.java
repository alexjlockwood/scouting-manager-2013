package edu.cmu.girlsofsteel.scouting.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import edu.cmu.girlsofsteel.scouting.MatchListFragment.OnMatchAddedListener;
import edu.cmu.girlsofsteel.scouting.R;

/**
 * Alert dialog which prompts the user to add a new match.
 *
 * @author Alex Lockwood
 */
public class AddMatchDialog extends DialogFragment {
  private static final String ARG_TEAM_ID = "team_id_arg";
  private OnMatchAddedListener mCallback;

  public static AddMatchDialog newInstance(long teamId) {
    AddMatchDialog frag = new AddMatchDialog();
    Bundle args = new Bundle();
    args.putLong(ARG_TEAM_ID, teamId);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mCallback = (OnMatchAddedListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnMatchAddedListener");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Context ctx = getActivity();
    final LayoutInflater factory = LayoutInflater.from(ctx);
    final EditText edit = (EditText) factory.inflate(R.layout.dialog_single_edittext, null);

    // Force open soft input
    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    return new AlertDialog.Builder(ctx)
        .setTitle(R.string.title_add_match)
        .setView(edit)
        .setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                CharSequence text = edit.getText();
                if (!TextUtils.isEmpty(text)) {
                  long teamId = getArguments().getLong(ARG_TEAM_ID);
                  int matchNumber = Integer.valueOf(text.toString());
                  mCallback.onMatchAdded(teamId, matchNumber);
                }

                // Force close soft input
                ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(edit.getWindowToken(), 0);
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
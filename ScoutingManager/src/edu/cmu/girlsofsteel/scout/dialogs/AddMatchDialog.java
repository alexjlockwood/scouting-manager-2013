package edu.cmu.girlsofsteel.scout.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import edu.cmu.girlsofsteel.scout.R;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.TeamMatches;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

public class AddMatchDialog extends DialogFragment {

  private static final String ARG_TEAM_ID = "team_id_arg";

  public static AddMatchDialog newInstance(long teamId) {
    AddMatchDialog frag = new AddMatchDialog();
    Bundle args = new Bundle();
    args.putLong(ARG_TEAM_ID, teamId);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Context ctx = getActivity();
    final LayoutInflater factory = LayoutInflater.from(ctx);
    final EditText edit = (EditText) factory.inflate(R.layout.dialog_single_edittext, null);

    // Force open soft input
    InputMethodManager im = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    return new AlertDialog.Builder(ctx)
        .setTitle(R.string.title_add_match)
        .setView(edit)
        .setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                CharSequence text = edit.getText();
                if (!TextUtils.isEmpty(text)) {
                  ContentValues values = new ContentValues();
                  values.put(TeamMatches.TEAM_ID, getArguments().getLong(ARG_TEAM_ID));
                  values.put(TeamMatches.MATCH_NUMBER, Integer.valueOf(text.toString()));
                  StorageUtil.insertTeamMatch(getActivity(), values);
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
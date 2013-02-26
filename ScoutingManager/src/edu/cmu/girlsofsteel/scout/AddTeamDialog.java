package edu.cmu.girlsofsteel.scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

public class AddTeamDialog extends DialogFragment {

  public static AddTeamDialog newInstance() {
    return new AddTeamDialog();
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    final LayoutInflater factory = LayoutInflater.from(getActivity());
    final EditText edit = (EditText) factory.inflate(R.layout.dialog_add_team, null);
    final Dialog dialog = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.title_add_team)
        .setView(edit)
        .setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                int team = Integer.valueOf(edit.getText().toString());
                ContentValues values = new ContentValues();
                values.put(Teams.NUMBER, team);
                StorageUtil.insertTeam(getActivity(), values);
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
    edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          Window window = dialog.getWindow();
          window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
      }
    });
    return dialog;
  }
}
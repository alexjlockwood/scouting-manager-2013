package edu.cmu.girlsofsteel.scout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import edu.cmu.girlsofsteel.scout.util.StorageUtil;

/************************/
/** DELETE TEAM DIALOG **/
/************************/

public class DeleteTeamDialog extends DialogFragment {

  private static final String KEY_IDS = "key_ids";

  public static DeleteTeamDialog newInstance(long... ids) {
    DeleteTeamDialog dialog = new DeleteTeamDialog();
    Bundle args = new Bundle();
    args.putLongArray(KEY_IDS, ids);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Resources res = getResources();
    final long[] ids = getArguments().getLongArray(KEY_IDS);
    String title = res.getQuantityString(R.plurals.title_delete_teams, ids.length);
    String msg = res.getQuantityString(R.plurals.message_delete_teams, ids.length, ids.length);
    return new AlertDialog.Builder(getActivity())
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton(R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int whichButton) {
                StorageUtil.deleteTeams(getActivity(), ids);
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

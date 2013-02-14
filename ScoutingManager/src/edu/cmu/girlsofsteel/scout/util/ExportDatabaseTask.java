package edu.cmu.girlsofsteel.scout.util;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import edu.cmu.girlsofsteel.scout.R;

public class ExportDatabaseTask extends AsyncTask<Uri, Void, String> {

  private static final String TAG = makeLogTag(ExportDatabaseTask.class);
  private Activity mActivity;

  public ExportDatabaseTask(Activity activity) {
    mActivity = activity;
  }

  @Override
  protected String doInBackground(Uri... uris) {
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      return mActivity.getString(R.string.export_not_mounted);
    }
    String exportDirName = mActivity.getString(R.string.export_dir_name);
    File exportDir = new File(Environment.getExternalStorageDirectory(), exportDirName);
    File file = new File(exportDir, mActivity.getString(R.string.export_file_name));
    file.getParentFile().mkdir();
    try {
      file.createNewFile();
      CSVWriter writer = new CSVWriter(new FileWriter(file));
      Cursor cur = mActivity.getContentResolver().query(uris[0], null, null, null, null);
      if (cur.moveToFirst()) {
        String[] colNames = cur.getColumnNames();
        writer.writeNext(colNames);
        do {
          String[] row = new String[colNames.length];
          for (int i = 0; i < colNames.length; i++) {
            // This will throw an exception if the field value is a BLOB!
            row[i] = cur.getString(i);
          }
          writer.writeNext(row);
        } while (cur.moveToNext());
      }
      writer.close();
      cur.close();
    } catch (SQLException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      return "Could not export data.";
    } catch (IOException ex) {
      Log.e(TAG, ex.getMessage(), ex);
      return "Could not export data.";
    }

    return null;
  }

  @Override
  protected void onPostExecute(String msg) {
    if (msg == null) {
      Toast.makeText(mActivity, "Data written to external storage.", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }
  }
}

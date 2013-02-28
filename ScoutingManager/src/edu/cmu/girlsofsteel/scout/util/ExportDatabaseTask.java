package edu.cmu.girlsofsteel.scout.util;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGE;
import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import edu.cmu.girlsofsteel.scout.R;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public class ExportDatabaseTask extends AsyncTask<Void, Void, String> {

  private static final String TAG = makeLogTag(ExportDatabaseTask.class);
  private Context mCtx;

  public ExportDatabaseTask(Context ctx) {
    mCtx = ctx.getApplicationContext();
  }

  @Override
  protected String doInBackground(Void... args) {
    Resources res = mCtx.getResources();
    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      return res.getString(R.string.export_not_mounted);
    }
    String exportDirName = res.getString(R.string.export_dir_name);
    File exportDir = new File(Environment.getExternalStorageDirectory(), exportDirName);
    File file = new File(exportDir, res.getString(R.string.export_file_name));
    file.getParentFile().mkdir();
    try {
      file.createNewFile();
      CSVWriter writer = new CSVWriter(new FileWriter(file));
      Cursor cur = mCtx.getContentResolver().query(Teams.CONTENT_URI, null, null, null, null);
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
      return res.getString(R.string.export_success);
    } catch (SQLException ex) {
      LOGE(TAG, ex.getMessage(), ex);
    } catch (IOException ex) {
      LOGE(TAG, ex.getMessage(), ex);
    }
    return res.getString(R.string.export_failed);
  }

  @Override
  protected void onPostExecute(String toastMsg) {
    Toast.makeText(mCtx, toastMsg, Toast.LENGTH_SHORT).show();
  }
}

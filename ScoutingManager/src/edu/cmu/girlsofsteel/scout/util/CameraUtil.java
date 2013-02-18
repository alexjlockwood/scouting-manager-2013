package edu.cmu.girlsofsteel.scout.util;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.LOGW;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import edu.cmu.girlsofsteel.scout.R;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public final class CameraUtil {

  public static boolean isIntentAvailable(Context ctx, String action) {
    PackageManager pm = ctx.getPackageManager();
    Intent intent = new Intent(action);
    return pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
  }

  @SuppressLint("InlinedApi")
  public static boolean hasCameraFeature(Context ctx) {
    PackageManager pm = ctx.getPackageManager();
    if (CompatUtil.hasJellyBeanMR1()) {
      return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    } else if (CompatUtil.hasGingerbread()) {
      return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
          pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    } else {
      return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
  }

  public static boolean hasCameraApplication(Context ctx) {
    return isIntentAvailable(ctx, MediaStore.ACTION_IMAGE_CAPTURE);
  }

  public static boolean hasCamera(Context ctx) {
    return hasCameraFeature(ctx) && hasCameraApplication(ctx);
  }

  public static void addToGallery(Context ctx, String path) {
    if (!isIntentAvailable(ctx, Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)) {
      LOGW(ctx.getClass().getSimpleName(), "Gallery application not installed.");
      return;
    }
    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    intent.setData(Uri.fromFile(new File(path)));
    ctx.sendBroadcast(intent);
  }

  public static File getCameraStorageDirectory(Context ctx) {
    File albumDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        ctx.getResources().getString(R.string.camera_storage_directory_name));
    if (!albumDir.exists()) {
      albumDir.mkdirs();
    }
    return albumDir;
  }

  public static File createImageFile(Context ctx) throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    String imageFileName = "IMG_" + timeStamp + "_";
    return File.createTempFile(imageFileName, ".jpg", getCameraStorageDirectory(ctx));
  }

  public static class ScaleBitmapTask extends AsyncTask<Void, Void, Boolean> {
    private Context mCtx;
    private String mPhotoPath;
    private String mPhotoName;
    private long mTeamId;

    public ScaleBitmapTask(Context ctx, File photoFile, long id) {
      mCtx = ctx;
      mPhotoPath = photoFile.getAbsolutePath();
      mPhotoName = photoFile.getName();
      mTeamId = id;
    }

    @Override
    protected Boolean doInBackground(Void... args) {
      int targetW = 512;
      int targetH = 512;

      // Get the size of the image
      BitmapFactory.Options bmOptions = new BitmapFactory.Options();
      bmOptions.inJustDecodeBounds = true;
      bmOptions.inSampleSize = 4;
      BitmapFactory.decodeFile(mPhotoPath, bmOptions);

      int photoW = bmOptions.outWidth;
      int photoH = bmOptions.outHeight;

      // Figure out which way needs to be reduced less
      int scaleFactor = 1;
      if ((targetW > 0) || (targetH > 0)) {
        scaleFactor = Math.min(photoW / targetW, photoH / targetH);
      }

      // Set bitmap options to scale the image decode target
      bmOptions.inJustDecodeBounds = false;
      bmOptions.inSampleSize = scaleFactor;
      bmOptions.inPurgeable = true;

      // Decode the JPEG file into a Bitmap
      Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath, bmOptions);

      ContentResolver cr = mCtx.getContentResolver();
      String url = MediaStore.Images.Media.insertImage(cr, bitmap, mPhotoName, null);
      if (url != null) {
        ContentValues values = new ContentValues();
        values.put(Teams.PHOTO, Uri.parse(url).toString());
        cr.update(Teams.teamIdUri(mTeamId), values, null, null);
        return true;
      }
      return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      addToGallery(mCtx, mPhotoPath);
    }
  }
}

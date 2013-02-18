package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public class TeamListAdapter extends ResourceCursorAdapter {

  @SuppressWarnings("unused")
  private static final String TAG = makeLogTag(TeamListAdapter.class);
  private ContentResolver mContentResolver;

  public TeamListAdapter(Context ctx) {
    super(ctx, R.layout.team_list_row, null, 0);
    mContentResolver = ctx.getContentResolver();
  }

  @Override
  public void bindView(View view, Context ctx, Cursor cur) {
    ViewHolder holder = (ViewHolder) view.getTag();
    if (holder == null) {
      holder = new ViewHolder();

      // cache TextView ids
      holder.teamNum = (TextView) view.findViewById(R.id.team_list_row_number);
      holder.teamPhoto = (ImageView) view.findViewById(R.id.team_list_row_photo);

      // cache column indices
      holder.teamNumCol = cur.getColumnIndexOrThrow(Teams.NUMBER);
      holder.teamPhotoCol = cur.getColumnIndexOrThrow(Teams.PHOTO);
      view.setTag(holder);
    }

    holder.teamNum.setText(cur.getString(holder.teamNumCol));

    String uri = cur.getString(holder.teamPhotoCol);
    if (!TextUtils.isEmpty(uri)) {
      long photoId = Long.parseLong(Uri.parse(uri).getLastPathSegment());
      Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContentResolver, photoId,
          MediaStore.Images.Thumbnails.MICRO_KIND, null);
      holder.teamPhoto.setImageBitmap(bitmap);
    } else {
      Resources res = ctx.getResources();
      holder.teamPhoto.setImageDrawable(res.getDrawable(R.drawable.ic_contact_picture));
    }
  }

  private static class ViewHolder {
    public TextView teamNum;
    public ImageView teamPhoto;
    public int teamNumCol, teamPhotoCol;
  }
}

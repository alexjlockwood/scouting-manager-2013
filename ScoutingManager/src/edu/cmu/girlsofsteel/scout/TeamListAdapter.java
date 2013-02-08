package edu.cmu.girlsofsteel.scout;

import static edu.cmu.girlsofsteel.scout.util.LogUtil.makeLogTag;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cmu.girlsofsteel.scout.provider.ScoutContract.Teams;

public class TeamListAdapter extends ResourceCursorAdapter {
  private static final String TAG = makeLogTag(TeamListAdapter.class);

  public TeamListAdapter(Context context) {
    super(context, R.layout.team_list_row, null, 0);
  }

  @Override
  public void bindView(View view, Context ctx, Cursor cur) {
    ViewHolder holder = (ViewHolder) view.getTag();
    if (holder == null) {
      holder = new ViewHolder();

      // cache TextView ids
      holder.teamNum = (TextView) view.findViewById(R.id.team_list_row_number);
      // holder.teamPhoto = (ImageView)
      // view.findViewById(R.id.team_list_row_photo);

      // cache column indices
      holder.teamNumCol = cur.getColumnIndexOrThrow(Teams.NUMBER);
      // holder.teamPhotoCol = cur.getColumnIndexOrThrow(Teams.TEAM_PHOTO);

      view.setTag(holder);
    }

    holder.teamNum.setText("" + cur.getInt(holder.teamNumCol));

    // String uri = cur.getString(holder.teamPhotoCol);
    // if (!TextUtils.isEmpty(uri)) {
    // long photoId = Long.parseLong(Uri.parse(uri).getLastPathSegment());
    // Bitmap bitmap =
    // MediaStore.Images.Thumbnails.getThumbnail(ctx.getContentResolver(),
    // photoId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    // holder.teamPhoto.setImageBitmap(bitmap);
    // } else {
    // holder.teamPhoto.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_contact_picture));
    // }
  }

  private static class ViewHolder {
    TextView teamNum;
    ImageView teamPhoto;
    int teamNumCol, teamPhotoCol;
  }
}

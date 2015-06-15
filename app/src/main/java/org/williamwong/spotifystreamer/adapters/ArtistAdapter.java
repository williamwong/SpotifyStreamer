package org.williamwong.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.models.ArtistModel;

import java.util.List;

/**
 * Created by w.wong on 6/14/2015.
 */
public class ArtistAdapter extends ArrayAdapter<ArtistModel> {

  public ArtistAdapter(Context context, List<ArtistModel> objects) {
    super(context, 0, objects);
  }

  private static class ViewHolder {
    TextView artistNameTextView;
    ImageView artistThumbnailImageView;

    public ViewHolder(View v) {
      artistNameTextView = (TextView) v.findViewById(R.id.artistNameTextView);
      artistThumbnailImageView = (ImageView) v.findViewById(R.id.artistThumbnailImageView);
    }
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ArtistModel artist = getItem(position);

    ViewHolder viewHolder;
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext())
          .inflate(R.layout.list_item_artist_search, parent, false);
      viewHolder = new ViewHolder(convertView);
      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.artistNameTextView.setText(artist.getName());
    Picasso.with(getContext())
        .load(artist.getImageUrl())
        .fit().centerCrop()
        .into(viewHolder.artistThumbnailImageView);

    return convertView;
  }
}

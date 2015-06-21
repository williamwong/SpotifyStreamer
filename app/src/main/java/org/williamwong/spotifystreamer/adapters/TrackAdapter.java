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
import org.williamwong.spotifystreamer.models.TrackModel;

import java.util.List;

/**
 * List adapter for track list.
 * Created by w.wong on 6/19/2015.
 */
public class TrackAdapter extends ArrayAdapter<TrackModel> {

    public TrackAdapter(Context context, List<TrackModel> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackModel track = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.trackNameTextView.setText(track.getTrackName());
        viewHolder.albumNameTextView.setText(track.getAlbumName());
        Picasso.with(getContext())
                .load(track.getImageUrl())
                .fit().centerCrop()
                .into(viewHolder.trackThumbnailImageView);

        return convertView;
    }

    private static class ViewHolder {
        TextView trackNameTextView;
        TextView albumNameTextView;
        ImageView trackThumbnailImageView;

        public ViewHolder(View v) {
            trackNameTextView = (TextView) v.findViewById(R.id.trackNameTextView);
            albumNameTextView = (TextView) v.findViewById(R.id.albumNameTextView);
            trackThumbnailImageView = (ImageView) v.findViewById(R.id.trackThumbnailImageView);
        }
    }
}

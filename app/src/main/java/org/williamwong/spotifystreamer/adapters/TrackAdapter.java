package org.williamwong.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.williamwong.spotifystreamer.R;
import org.williamwong.spotifystreamer.models.TrackModel;
import org.williamwong.spotifystreamer.services.MusicService;

import java.util.List;

/**
 * List adapter for track list.
 * Created by w.wong on 6/19/2015.
 */
public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private final Context mContext;
    private final List<TrackModel> mTrackModels;

    public TrackAdapter(Context context, List<TrackModel> trackModels) {
        mContext = context;
        mTrackModels = trackModels;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_track, parent, false);
        return new TrackViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrackViewHolder holder, int position) {
        holder.bindTrack(mContext, mTrackModels, position);
    }

    @Override
    public int getItemCount() {
        return mTrackModels.size();
    }

    public interface OnTrackClickListener {
        void onTrackSelected();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView trackNameTextView;
        TextView albumNameTextView;
        ImageView trackThumbnailImageView;
        private List<TrackModel> mTrackModels;
        private int mPosition;

        public TrackViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            trackNameTextView = (TextView) v.findViewById(R.id.trackNameTextView);
            albumNameTextView = (TextView) v.findViewById(R.id.albumNameTextView);
            trackThumbnailImageView = (ImageView) v.findViewById(R.id.trackThumbnailImageView);
        }

        public void bindTrack(Context context, List<TrackModel> trackModels, int position) {
            mTrackModels = trackModels;
            mPosition = position;

            TrackModel track = mTrackModels.get(mPosition);
            trackNameTextView.setText(track.getTrackName());
            albumNameTextView.setText(track.getAlbumName());
            Picasso.with(context)
                    .load(track.getImageUrl())
                    .fit().centerCrop()
                    .into(trackThumbnailImageView);
        }

        @Override
        public void onClick(View v) {
            MusicService service = MusicService.getMusicService();
            service.setTrackModels(mTrackModels);
            service.setCurrentTrack(mPosition);
            service.playSong();

            // Launch PlayerFragment and/or Activity
            Context context = v.getContext();
            if (context instanceof OnTrackClickListener) {
                ((OnTrackClickListener) context).onTrackSelected();
            }
        }
    }
}

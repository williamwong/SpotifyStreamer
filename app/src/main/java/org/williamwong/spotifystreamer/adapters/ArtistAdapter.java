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
import org.williamwong.spotifystreamer.models.ArtistModel;

import java.util.List;

/**
 * List adapter for artist search results
 * Created by w.wong on 6/14/2015.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final List<ArtistModel> mArtistModels;
    private final Context mContext;

    public ArtistAdapter(Context context, List<ArtistModel> artistModels) {
        mContext = context;
        mArtistModels = artistModels;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_artist, parent, false);
        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        holder.bindArtist(mContext, mArtistModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtistModels.size();
    }

    public interface OnArtistClickListener {
        void onArtistSelected(String spotifyId, String artistName);
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ArtistModel mArtist;
        TextView mArtistNameTextView;
        ImageView mArtistThumbnailImageView;

        public ArtistViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mArtistNameTextView = (TextView) v.findViewById(R.id.artistNameTextView);
            mArtistThumbnailImageView = (ImageView) v.findViewById(R.id.artistThumbnailImageView);
        }

        public void bindArtist(Context context, ArtistModel artist) {
            mArtist = artist;
            mArtistNameTextView.setText(mArtist.getName());
            Picasso.with(context)
                    .load(mArtist.getImageUrl())
                    .fit().centerCrop()
                    .into(mArtistThumbnailImageView);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            if (context instanceof ArtistAdapter.OnArtistClickListener) {
                ((ArtistAdapter.OnArtistClickListener) context).onArtistSelected(mArtist.getSpotifyId(), mArtist.getName());
            }
        }
    }
}

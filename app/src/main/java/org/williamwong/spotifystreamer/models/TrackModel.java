package org.williamwong.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model for containing track information
 * Created by w.wong on 6/19/2015.
 */
public class TrackModel implements Parcelable {
    public static final Creator<TrackModel> CREATOR = new Creator<TrackModel>() {
        public TrackModel createFromParcel(Parcel source) {
            return new TrackModel(source);
        }

        public TrackModel[] newArray(int size) {
            return new TrackModel[size];
        }
    };
    private String trackName;
    private String artistName;
    private String albumName;
    private String imageUrl;
    private String previewUrl;

    public TrackModel() {
    }

    protected TrackModel(Parcel in) {
        this.trackName = in.readString();
        this.artistName = in.readString();
        this.albumName = in.readString();
        this.imageUrl = in.readString();
        this.previewUrl = in.readString();
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackName);
        dest.writeString(this.artistName);
        dest.writeString(this.albumName);
        dest.writeString(this.imageUrl);
        dest.writeString(this.previewUrl);
    }
}

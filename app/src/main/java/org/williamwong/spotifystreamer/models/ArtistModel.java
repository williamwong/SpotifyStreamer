package org.williamwong.spotifystreamer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by w.wong on 6/14/2015.
 */
public class ArtistModel implements Parcelable {

  private String name;
  private String imageUrl;
  private String spotifyId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getSpotifyId() {
    return spotifyId;
  }

  public void setSpotifyId(String spotifyId) {
    this.spotifyId = spotifyId;
  }

  public ArtistModel() {
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.imageUrl);
    dest.writeString(this.spotifyId);
  }

  protected ArtistModel(Parcel in) {
    this.name = in.readString();
    this.imageUrl = in.readString();
    this.spotifyId = in.readString();
  }

  public static final Creator<ArtistModel> CREATOR = new Creator<ArtistModel>() {
    public ArtistModel createFromParcel(Parcel source) {
      return new ArtistModel(source);
    }

    public ArtistModel[] newArray(int size) {
      return new ArtistModel[size];
    }
  };
}

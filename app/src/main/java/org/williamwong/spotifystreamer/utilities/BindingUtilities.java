package org.williamwong.spotifystreamer.utilities;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Custom binding adapters for overriding default databinding behaviors
 * Created by williamwong on 12/13/15.
 */
public class BindingUtilities {

    @BindingAdapter("src")
    public static void setImageUrl(ImageView view, String source) {
        Picasso.with(view.getContext()).load(source).into(view);
    }

    @BindingAdapter("srcFitCenterCrop")
    public static void setImageUrlFitCenterCrop(ImageView view, String source) {
        Picasso.with(view.getContext()).load(source).fit().centerCrop().into(view);
    }
}

package org.williamwong.spotifystreamer.utilities;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Base class for an Observable String that provides a built in TextWatcher and a static Binding
 * Adapter to allow for two-way binding with EditTexts
 * Created by williamwong on 12/14/15.
 */
public class BindableString implements Parcelable, TextWatcher {

    public static final Parcelable.Creator<BindableString> CREATOR = new Parcelable.Creator<BindableString>() {
        public BindableString createFromParcel(Parcel source) {
            return new BindableString(source);
        }

        public BindableString[] newArray(int size) {
            return new BindableString[size];
        }
    };
    private ObservableField<String> value = new ObservableField<>("");

    public BindableString() {
        set("");
    }

    public BindableString(String value) {
        set(value);
    }

    protected BindableString(Parcel in) {
        this.value = (ObservableField<String>) in.readSerializable();
    }

    @BindingAdapter("text")
    public static void bindEditText(EditText editText, CharSequence value) {
        if (!editText.getText().toString().equals(value.toString())) {
            editText.setText(value);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!get().equals(s.toString())) {
            set(s.toString());
        }
    }

    public String get() {
        return value.get();
    }

    public void set(String value) {
        this.value.set(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.value);
    }
}

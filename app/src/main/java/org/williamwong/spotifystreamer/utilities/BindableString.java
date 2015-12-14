package org.williamwong.spotifystreamer.utilities;

import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Base class for an Observable String that provides a built in TextWatcher and a static Binding
 * Adapter to allow for two-way binding with EditTexts
 * Created by williamwong on 12/14/15.
 */
public class BindableString {
    public ObservableField<String> value = new ObservableField<>("");
    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!value.get().equals(s.toString())) {
                value.set(s.toString());
            }
        }
    };

    public BindableString() {
        this.value.set("");
    }

    public BindableString(String value) {
        this.value.set(value);
    }

    @BindingAdapter("text")
    public static void bindEditText(EditText editText, CharSequence value) {
        if (!editText.getText().toString().equals(value.toString())) {
            editText.setText(value);
        }
    }

    public String get() {
        return value.get();
    }

    public void set(String value) {
        this.value.set(value);
    }
}

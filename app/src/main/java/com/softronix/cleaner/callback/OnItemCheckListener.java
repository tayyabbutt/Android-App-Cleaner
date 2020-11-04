package com.softronix.cleaner.callback;


import com.softronix.cleaner.model.Model_Audio;

public interface OnItemCheckListener {
    void onAudioItemChecked(boolean isChecked, Model_Audio audio);
}

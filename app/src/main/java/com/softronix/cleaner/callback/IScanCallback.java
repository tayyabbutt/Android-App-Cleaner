package com.softronix.cleaner.callback;

import com.softronix.cleaner.model.JunkInfo;


import java.util.ArrayList;

public interface IScanCallback {
    void onBegin();

    void onProgress(JunkInfo info);

    void onFinish(ArrayList<JunkInfo> children);
}

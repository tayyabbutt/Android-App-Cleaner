package com.softronix.cleaner.Util;

import android.support.design.widget.Snackbar;
import android.view.View;

public class Helper {

    public static void showSnackbar(String message, View root) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }
}

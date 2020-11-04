package com.softronix.cleaner.views.junkcleaner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.softronix.cleaner.R;
import com.softronix.cleaner.views.MainScreen;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainScreen.class);

                startActivity(intent);
                finish();
            }
        }, 2000);


    }
}

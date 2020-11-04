package com.softronix.cleaner.views;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.DiskStat;
import com.softronix.cleaner.Util.MemStat;
import com.softronix.cleaner.views.appmanager.ApplicationManager;
import com.softronix.cleaner.views.junkcleaner.JunkCleaner;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn, clear, checkjunkfile, animibtn, manager, appmanager;
    TextView text, checkfiletext;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkstats();
        text = findViewById(R.id.text);
        animationView = findViewById(R.id.animation);
        checkfiletext = findViewById(R.id.checkfiletext);
        appmanager = findViewById(R.id.appmanager);
        appmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ApplicationManager.class));
            }
        });
        manager = findViewById(R.id.manager);
        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FileManager.class));
            }
        });
        animibtn = findViewById(R.id.animibtn);
        animibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startanimation();
            }
        });
        btn = findViewById(R.id.btn);
        clear = findViewById(R.id.clear);
        checkjunkfile = findViewById(R.id.checkjunkfile);
        checkjunkfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkjunk();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvailableMemory();
            }
        });
    }

    private void showAvailableMemory() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long i = mi.totalMem / 1048576L;
        long availableMegs = mi.availMem / 1048576L;
        Log.e("Available memory = ", availableMegs + " MB");
        text.setText("total ram " + i + "\n" + "available ram " + availableMegs);
        checkstats();
    }

    void startanimation() {
        //        animationView.playAnimation();
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 2f).setDuration(2500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animationView.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });

        if (animationView.getProgress() == 0f) {
            animator.start();
        } else {
            animationView.setProgress(0f);
        }
    }

    public void clear() {
        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listofruuningapp = mActivityManager.getRunningAppProcesses();


        String myPackage = getApplicationContext().getPackageName();
        for (ApplicationInfo packageInfo : packages) {
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }


    }

    public void checkjunk() {
        Intent intent = new Intent(MainActivity.this, JunkCleaner.class);
        startActivity(intent);
    }

    public void checkstats() {
        final DiskStat diskStat = new DiskStat();
        final MemStat memStat = new MemStat(MainActivity.this);
        float s = diskStat.getTotalSpace() / 1073741824f;
        s = Math.round(s);
        Log.e("dist stat total", String.valueOf(s) + " GB");
        s = diskStat.getUsedSpace() / 1073741824f;
        s = Math.round(s);
        Log.e("dist stat used", String.valueOf(s) + " GB");
        s = memStat.getTotalMemory() / 1073741824f;
        s = Math.round(s);
        Log.e("memory stat total", String.valueOf(s) + " GB");
        s = memStat.getAvilMemory() / 1048576f;
        Log.e("memory stat avail", String.valueOf(s) + " MB");
    }
}

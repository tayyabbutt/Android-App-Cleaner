package com.softronix.cleaner.views.memorybooster;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Statm;
import com.softronix.cleaner.R;
import com.softronix.cleaner.adaptor.memorybooster.MemoryBoosterAdaptor;

import java.io.IOException;
import java.util.List;

import static com.jaredrummler.android.processes.AndroidProcesses.getRunningAppProcesses;
import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class MemoryBooster extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerview;
    MemoryBoosterAdaptor adaptor;
    RecyclerView.LayoutManager layoutManager;
    ImageView btn;
    AdView mAdView, adView1;
    TextView numberofapp, sizeoframused, animatedTextView, doneTv;
    float mSize;

    private LottieAnimationView animationView, doneAnimation;
    RelativeLayout progresslayout, mainBoosterLayout, relativeLayout, doneAnimationlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_booster);
        init();
        getlist();
        loadadd();
//        bypackagemanager();

    }

    public void loadadd() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdeviice = adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);
    }

    public void loadadd1() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
        adView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdeviice = adRequest.isTestDevice(this);
        adView1.loadAd(adRequest);
        adView1.setVisibility(View.VISIBLE);

    }

    public void init() {
        mainBoosterLayout = findViewById(R.id.mainBoosterLayout);
        relativeLayout = findViewById(R.id.relative);
        doneAnimationlayout = findViewById(R.id.doneAnimationlayout);
        doneAnimation = findViewById(R.id.doneAnimation);
        doneTv = findViewById(R.id.doneTv);
        animatedTextView = findViewById(R.id.temp);
        progresslayout = findViewById(R.id.process);
        animationView = findViewById(R.id.animation);
        sizeoframused = findViewById(R.id.size);
        List<AndroidAppProcess> runningaps = getRunningAppProcesses();
        Statm statm = null;
        try {
            statm = runningaps.get(0).statm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert statm != null;
        mSize = statm.getResidentSetSize() / 1048576f;
        sizeoframused.setText(Math.round(mSize) + " MB");
        numberofapp = findViewById(R.id.numberofapp);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);
        recyclerview = findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
    }

    List<ActivityManager.RunningAppProcessInfo> listofruuningapp;

    public void getlist() {
        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (int i = 0; i < packages.size(); i++) {

            if (isSTOPPED1(packages.get(i)) || isSYSTEM1(packages.get(i))) {
                packages.remove(i);
            }
        }
        for (int k = 0; k < packages.size(); k++) {
            if (packages.get(k).uid == 0) {
                packages.remove(k);
            }
        }
        for (int z = 0; z < packages.size(); z++) {
            for (int m = 0; m < packages.size(); m++) {
                if (packages.get(z).uid == packages.get(m).uid) {
                    packages.remove(m);
                }
            }
        }
        //get a list of installed apps.;
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> list = mActivityManager.getRunningServices(Integer.MAX_VALUE);
        for (int j = 0; j < list.size(); j++) {
            if (isSTOPPED(list.get(j)) || isSYSTEM(list.get(j))) {
                list.remove(j);
            }
        }
        for (int k = 0; k < list.size(); k++) {
            if (list.get(k).pid == 0) {
                list.remove(k);
            }
        }
        for (int z = 0; z < list.size(); z++) {
            for (int m = 0; m < list.size(); m++) {
                if (list.get(z).pid == list.get(m).pid) {
                    list.remove(m);
                }
            }
        }
        numberofapp.setText("Apps " + list.size());
        adaptor = new MemoryBoosterAdaptor(this, packages);
        recyclerview.setAdapter(adaptor);
        Log.e("what", "aapp");

    }

    private static boolean isSTOPPED(ActivityManager.RunningServiceInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_STOPPED) != 0);
    }

    private static boolean isSYSTEM(ActivityManager.RunningServiceInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private static boolean isSTOPPED1(ApplicationInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_STOPPED) != 0);
    }

    private static boolean isSYSTEM1(ApplicationInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn.getId()) {

            btn.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            recyclerview.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);

            mainBoosterLayout.setBackgroundColor(ContextCompat.getColor(MemoryBooster.this, R.color.yellow));
            progresslayout.setVisibility(View.VISIBLE);
            animatedTextView.setText((Math.round(mSize) + " MB"));

            clear();
        }
    }

    public void clear() {
        animationView.playAnimation();
        List<ApplicationInfo> packages;
        PackageManager pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ApplicationInfo packageInfo : packages) {

            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
        animateTextView((int) mSize, 0, animatedTextView);
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }

    public void animateTextView(int initialValue, final int finalValue, final TextView textview) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(4500);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString() + " %");
                if (valueAnimator.getAnimatedValue().equals(finalValue)) {
                    progresslayout.setVisibility(View.GONE);
                    doneAnimationlayout.setVisibility(View.VISIBLE);
                    doneAnimation.playAnimation();
                    loadadd1();
                }
            }

        });
        valueAnimator.start();
    }

}

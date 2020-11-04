package com.softronix.cleaner.views;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.DiskStat;
import com.softronix.cleaner.Util.MemStat;
import com.softronix.cleaner.views.appmanager.ApplicationManager;
import com.softronix.cleaner.views.cooler.CpuCooler;
import com.softronix.cleaner.views.junkcleaner.JunkCleaner;
import com.softronix.cleaner.views.memorybooster.MemoryBooster;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static int mainscreenloadtime = 0;
    ImageView drawerbtn;
    DrawerLayout drawer;
    ImageView manager, boosterbtn, junkclear, coolerbtn, deleteBigFiles;
    TextView percent, storage, clicktclear;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    private LottieAnimationView animationView;
    RelativeLayout junkclearbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        init();
        checkstats();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdeviice = adRequest.isTestDevice(this);
        mAdView.loadAd(adRequest);
        boolean shown = mAdView.isShown();
        loadinterad();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void init() {
        junkclearbtn = findViewById(R.id.junkclearbtn);
        junkclearbtn.setOnClickListener(this);
        clicktclear = findViewById(R.id.clicktclear);
        //drawerbtn = findViewById(R.id.drawerbtn);
        animationView = findViewById(R.id.animation);
        animationView.playAnimation();
        //drawerbtn.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        manager = findViewById(R.id.manager);
        manager.setOnClickListener(this);
        boosterbtn = findViewById(R.id.boosterbtn);
        boosterbtn.setOnClickListener(this);
        percent = findViewById(R.id.percent);
        storage = findViewById(R.id.storage);
        junkclear = findViewById(R.id.junkclear);
        junkclear.setOnClickListener(this);
        coolerbtn = findViewById(R.id.coolerbtn);
        coolerbtn.setOnClickListener(this);
        deleteBigFiles = findViewById(R.id.deleteFiles);
        deleteBigFiles.setOnClickListener(this);
    }

    public void animateTextView(int initialValue, final int finalValue, final TextView textview, final float used, final float total) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(2000);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString() + " %");
                if (valueAnimator.getAnimatedValue().equals(finalValue)) {
                    storage.setText(used + " GB " + "/ " + total + " GB");
                    clicktclear.setText(getString(R.string.clearjunk));
                }
            }

        });
        valueAnimator.start();
    }

    @Override
    protected void onResume() {

        if (mainscreenloadtime > 0) {
            boolean loading = mInterstitialAd.isLoading();
            boolean loaded = mInterstitialAd.isLoaded();
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mainscreenloadtime = 0;
            } else {
//                Toast.makeText(this, "not loaded ad", Toast.LENGTH_SHORT).show();
            }
        }
        super.onResume();
    }

    public void loadinterad() {
        MobileAds.initialize(this, getString(R.string.bannerappid));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                AddNewRequestAndLoad();
            }
        });
        AddNewRequestAndLoad();
    }

    public void AddNewRequestAndLoad() {
        String id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(id)
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_appmanager) {
            // Handle the camera action

            startActivity(new Intent(MainScreen.this, ApplicationManager.class));
        } else if (id == R.id.nav_booster) {

            Intent intent = new Intent(MainScreen.this, MemoryBooster.class);
            startActivity(intent);
        } else if (id == R.id.nav_cooler) {

            startActivity(new Intent(MainScreen.this, CpuCooler.class));

        } else if (id == R.id.nav_Junkcleaner) {
            isStoragePermissionGranted();


        } else if (id == R.id.nav_bigFolder) {
            Intent intent = new Intent(MainScreen.this, DeleteBigFiles.class);
            startActivity(intent);
        } else if (id == R.id.nav_rateus) {
            launchmarket();

        } else if (id == R.id.nav_Review) {
            launchmarket();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchmarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.marketerror), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if ((v.getId() == manager.getId())) {

            startActivity(new Intent(MainScreen.this, ApplicationManager.class));

        }
        if (v.getId() == boosterbtn.getId()) {


            Intent intent = new Intent(MainScreen.this, MemoryBooster.class);
            startActivity(intent);
        }
        if (v.getId() == junkclearbtn.getId()) {
            isStoragePermissionGranted();

        }
        if (v.getId() == coolerbtn.getId()) {

            startActivity(new Intent(MainScreen.this, CpuCooler.class));

        }
        if (v.getId() == deleteBigFiles.getId()) {
            startActivity(new Intent(MainScreen.this, DeleteBigFiles.class));

        }
    }

    public void checkstats() {
        final DiskStat diskStat = new DiskStat();
        final MemStat memStat = new MemStat(MainScreen.this);
        float total = diskStat.getTotalSpace() / 1073741824f;
        total = Math.round(total);
//        Log.e("dist stat total", String.valueOf(s)+" GB");
        float Used = diskStat.getUsedSpace() / 1073741824f;
        Used = Math.round(Used);


        float percentage = (Used / total) * 100;
        int round = Math.round(percentage);
        animateTextView(0, round, percent, Used, total);
    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("", "Permission is granted");
                Intent intent = new Intent(MainScreen.this, JunkCleaner.class);
                startActivity(intent);
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            Log.v("", "Permission is granted");
            Intent intent = new Intent(MainScreen.this, JunkCleaner.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("", "Permission: " + permissions[0] + "was " + grantResults[0]);
            Intent intent = new Intent(MainScreen.this, JunkCleaner.class);
            startActivity(intent);
            //resume tasks needing this permission
        } else {
            Toast.makeText(this, getString(R.string.permissionstring), Toast.LENGTH_SHORT).show();
        }
    }
}

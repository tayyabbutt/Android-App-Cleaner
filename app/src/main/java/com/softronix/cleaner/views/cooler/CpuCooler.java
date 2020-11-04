package com.softronix.cleaner.views.cooler;

import android.animation.ValueAnimator;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class CpuCooler extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    TextView temp, coolTv;
    private Sensor mTemperature;
    private LottieAnimationView animationView, snowanimation;
    RelativeLayout snowanimationlayout, progresslayout;
    AdView mAdView;
    AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_cooler);
        temp = findViewById(R.id.temp);
        progresslayout = findViewById(R.id.process);
        snowanimationlayout = findViewById(R.id.snowanimationlayout);
        coolTv = findViewById(R.id.coolTv);
        snowanimation = findViewById(R.id.snowanimation);
        animationView = findViewById(R.id.animation);
        animationView.playAnimation();
        animateTextView(0, 100, temp);

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this, getString(R.string.bannerappid));
        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder()
                .addTestDevice(id)
                .build();
        adRequest.isTestDevice(this);
        boolean istestdevice = adRequest.isTestDevice(this);

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
                    snowanimationlayout.setVisibility(View.VISIBLE);
                    snowanimation.playAnimation();
                    mAdView.loadAd(adRequest);
                }
            }

        });
        valueAnimator.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambient_temperature = event.values[0];
//        temp.setText("Ambient Temperature:\n " + String.valueOf(ambient_temperature) + " c");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}

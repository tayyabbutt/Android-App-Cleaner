package com.softronix.cleaner.views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.views.audioFolder.AudioListActivity;
import com.softronix.cleaner.views.imagesFolder.ImageListActivity;
import com.softronix.cleaner.views.videoFolder.VideoListActivity;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class DeleteBigFiles extends AppCompatActivity {
    LinearLayout mVideo, mAudio, mImages;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_big_folder);

        mVideo = findViewById(R.id.view_video);
        mAudio = findViewById(R.id.view_audios);
        mImages = findViewById(R.id.view_images);
        mAdView = findViewById(R.id.adView);
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
        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeleteBigFiles.this, VideoListActivity.class);
                startActivity(intent);
            }
        });
        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeleteBigFiles.this, AudioListActivity.class);
                startActivity(intent);
            }
        });
        mImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeleteBigFiles.this, ImageListActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}

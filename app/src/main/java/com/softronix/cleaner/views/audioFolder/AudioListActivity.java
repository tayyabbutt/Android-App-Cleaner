package com.softronix.cleaner.views.audioFolder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.Storage;
import com.softronix.cleaner.adaptor.AudioAdapter;
import com.softronix.cleaner.callback.OnItemCheckListener;
import com.softronix.cleaner.model.Model_Audio;
import com.softronix.cleaner.recyclerItemDecorator.LinearDividerItemDecoration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class AudioListActivity extends AppCompatActivity {
    AudioAdapter obj_adapter;
    RecyclerView recyclerView;
    Button deleteBtn;
    private static final int REQUEST_PERMISSIONS = 100;
    private List<Model_Audio> currentSelectedItems = new ArrayList<>();
    AdView mAdView;
    private Storage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        init();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < currentSelectedItems.size(); i++) {

                    File file = new File(currentSelectedItems.get(i).getaPath());

                    deleteFileFromMediaStore(getApplicationContext().getContentResolver(), file);


                }

            }
        });


    }

    public void deleteFileFromMediaStore(final ContentResolver contentResolver,
                                         final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
        if (!file.exists()) {
            finish();
        }
    }

    private void init() {
        mStorage = new Storage(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.listView1);
        deleteBtn = (Button) findViewById(R.id.delete);
        deleteBtn.setEnabled(false);

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
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.animate();
        recyclerView.getItemAnimator();
        LinearDividerItemDecoration seprator = new LinearDividerItemDecoration(this, getResources().getColor(R.color.seprator_color), 0.8f);
        recyclerView.addItemDecoration(seprator);
        obj_adapter = new AudioAdapter(getApplicationContext(), getAllAudioFromDevice(this), AudioListActivity.this, new OnItemCheckListener() {
            @Override
            public void onAudioItemChecked(boolean isChecked, Model_Audio audio) {
                deleteBtn.setEnabled(true);
                deleteBtn.setBackgroundColor(ContextCompat.getColor(AudioListActivity.this, R.color.green));
                if (isChecked) {
                    currentSelectedItems.add(audio);
                } else {
                    currentSelectedItems.remove(audio);
                }
            }
        });
        recyclerView.setAdapter(obj_adapter);
        // fn_checkpermission();

    }


    public List<Model_Audio> getAllAudioFromDevice(final Context context) {
        final List<Model_Audio> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                Model_Audio audioModel = new Model_Audio();

                String path = c.getString(0);   // Retrieve path.
                String name = c.getString(1);   // Retrieve name.
                String album = c.getString(2);  // Retrieve album name.
                String artist = c.getString(3); // Retrieve artist name.

                // Set data to the model object.
                audioModel.setaName(name);
                audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);
                audioModel.setaPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                // Add the model object to the list .
                tempAudioList.add(audioModel);
            }
            c.close();
        }

        return tempAudioList;
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}
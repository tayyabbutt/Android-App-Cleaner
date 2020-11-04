package com.softronix.cleaner.views.videoFolder;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.Storage;
import com.softronix.cleaner.adaptor.Adapter_VideoFolder;
import com.softronix.cleaner.callback.OnVideoItemClick;
import com.softronix.cleaner.model.Model_Video;
import com.softronix.cleaner.recyclerItemDecorator.LinearDividerItemDecoration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class VideoListActivity extends AppCompatActivity {

    Adapter_VideoFolder obj_adapter;
    ArrayList<Model_Video> al_video = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    private List<Model_Video> currentSelectedItems = new ArrayList<>();
    Button deleteBtn;
    private Storage mStorage;

    private static final int REQUEST_PERMISSIONS = 100;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        mStorage = new Storage(getApplicationContext());
        init();
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < currentSelectedItems.size(); i++) {
                    File file = new File(currentSelectedItems.get(i).getStr_path());
                    deleteFileFromMediaStore(getApplicationContext().getContentResolver(), file);
                }
            }
        });


    }

    private void init() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
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
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.animate();
        recyclerView.getItemAnimator();
        LinearDividerItemDecoration seprator = new LinearDividerItemDecoration(this, getResources().getColor(R.color.seprator_color), 0.8f);
        recyclerView.addItemDecoration(seprator);
        fn_checkpermission();
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


    private void fn_checkpermission() {
        /*RUN TIME PERMISSIONS*/

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(VideoListActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(VideoListActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(VideoListActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            fn_video();
        }
    }


    public void fn_video() {

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.VideoColumns.ALBUM, MediaStore.Video.VideoColumns.ARTIST, MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DURATION};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);


        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));
            String path = cursor.getString(0);   // Retrieve path.
            String name = cursor.getString(1);   // Retrieve name.
            String album = cursor.getString(2);  // Retrieve album name.
            String artist = cursor.getString(3); // Retrieve artist name.
            String duration = cursor.getString(4);
            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));
            obj_model.setaAlbum(album);
            obj_model.setaArtist(artist);
            obj_model.setaName(name);
            obj_model.setDuration(duration);
            al_video.add(obj_model);

        }


        obj_adapter = new Adapter_VideoFolder(getApplicationContext(), al_video, VideoListActivity.this, new OnVideoItemClick() {
            @Override
            public void onVideooItemChecked(boolean isChecked, Model_Video video) {
                deleteBtn.setEnabled(true);
                deleteBtn.setBackgroundColor(ContextCompat.getColor(VideoListActivity.this, R.color.red));

                if (isChecked) {
                    currentSelectedItems.add(video);
                } else {
                    currentSelectedItems.remove(video);
                }
            }
        });

        recyclerView.setAdapter(obj_adapter);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        fn_video();
                    } else {
                        Toast.makeText(VideoListActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}

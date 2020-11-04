package com.softronix.cleaner.views.imagesFolder;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.Util.Storage;
import com.softronix.cleaner.adaptor.GridViewAdapter;
import com.softronix.cleaner.callback.OnImageItemCheckListner;
import com.softronix.cleaner.model.Model_images;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    // private RecyclerView recyclerView;
    GridViewAdapter adapter;
    private List<String> currentSelectedItems = new ArrayList<>();
    //private List<Model_images> currentSelectedItems = new ArrayList<>();
    Button deleteBtn;
    AdView mAdView;
    private Storage mStorage;
    List<Model_images> imagesList = new ArrayList<>();
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_recycler_layout);
        mStorage = new Storage(getApplicationContext());
        gridView = (GridView) findViewById(R.id.image_recycler_view);
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

        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, ImageListActivity.al_images, int_position, this, new OnImageItemCheckListner() {
            @Override
            public void onImageItemChecked(boolean isChecked, String images) {
                deleteBtn.setEnabled(true);
                deleteBtn.setBackgroundColor(ContextCompat.getColor(PhotosActivity.this, R.color.purple));
                if (isChecked) {
                    currentSelectedItems.add(images);
                } else {
                    currentSelectedItems.remove(images);
                }
            }
        });
        gridView.setAdapter(adapter);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < currentSelectedItems.size(); i++) {
                    File file = new File(currentSelectedItems.get(i));
                    if (file != null) {
                        deleteFileFromMediaStore(getApplicationContext().getContentResolver(), file);
                    }
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


    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }

}

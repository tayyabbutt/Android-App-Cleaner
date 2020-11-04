package com.softronix.cleaner.views.appmanager;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.softronix.cleaner.R;
import com.softronix.cleaner.adaptor.AppManagerAdaptor;
import com.softronix.cleaner.model.HelperForCheckBox;

import java.util.ArrayList;
import java.util.List;

import static com.softronix.cleaner.views.MainScreen.mainscreenloadtime;

public class ApplicationManager extends AppCompatActivity implements AppManagerAdaptor.Listner, View.OnClickListener {
    RecyclerView listapp;
    RecyclerView.LayoutManager manager;
    AdView mAdView;
    List<ResolveInfo> deletelist = new ArrayList<>();
    List<ResolveInfo> pkgAppsListnew;
    Button uninstalbtn;
    AppManagerAdaptor adaptor;
    List<HelperForCheckBox> helperForCheckBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_manager);
        listapp = findViewById(R.id.listapp);
        uninstalbtn = findViewById(R.id.uninstalbtn);
        uninstalbtn.setOnClickListener(this);
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
        getlist();
        second();
    }

    public void getlist() {
        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

    }

    public void second() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        int flags = PackageManager.GET_META_DATA;
        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, flags);
        pkgAppsListnew = new ArrayList<>();

        for (int i = 0; i < pkgAppsList.size(); i++) {
            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(pkgAppsList.get(i).activityInfo.packageName, 0);
                if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                } else {
                    pkgAppsListnew.add(pkgAppsList.get(i));
                    helperForCheckBoxes.add(new HelperForCheckBox(false, pkgAppsList.get(i)));

                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        adaptor = new AppManagerAdaptor(this, helperForCheckBoxes, this);
        manager = new LinearLayoutManager(this);
        listapp.setLayoutManager(manager);
        listapp.setAdapter(adaptor);
    }

    @Override
    public void clicked(int position, boolean isClicked) {
        if (isClicked == true) {
            deletelist.add(helperForCheckBoxes.get(position).getResolveInfo());
            uninstalbtn.setEnabled(true);
            uninstalbtn.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        } else {
            String packagename1 = helperForCheckBoxes.get(position).getResolveInfo().activityInfo.packageName;
            for (int i = 0; i < deletelist.size(); i++) {
                if (deletelist.get(i).activityInfo.packageName.equals(packagename1)) {
                    deletelist.remove(i);
                    break;
                }
            }
            if (deletelist.size() == 0) {
                uninstalbtn.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == uninstalbtn.getId()) {

            for (int j = 0; j < deletelist.size(); j++) {
                Uri uri = Uri.fromParts("package", deletelist.get(j).activityInfo.packageName, null);
                Intent it = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, uri);
                startActivity(it);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mainscreenloadtime++;
        super.onBackPressed();
    }
}

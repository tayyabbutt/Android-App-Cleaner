package com.softronix.cleaner.adaptor.memorybooster;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.softronix.cleaner.R;

import java.util.List;

public class MemoryBoosterAdaptor extends RecyclerView.Adapter<MemoryBoosterAdaptor.viewholder> {
    Context context;
    List<ApplicationInfo> packages;

    public MemoryBoosterAdaptor(Context context, List<ApplicationInfo> packages) {
        this.context = context;
        this.packages = packages;

    }

    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.memoryboosterrow, viewGroup, false);
        viewholder vh = new viewholder(mView);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, int i) {
        PackageManager pm = context.getPackageManager();
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            viewholder.getTextapp().setText(pm.getApplicationInfo(packages.get(i).packageName, 0).loadLabel(pm) + "\n" + packages.get(i).processName + "\n" + packages.get(i).uid);
            viewholder.getImageapp().setImageDrawable(pm.getApplicationInfo(packages.get(i).packageName, 0).loadIcon(pm));
            viewholder.getCheckBox().setChecked(true);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView textapp;

        public TextView getTextapp() {
            return textapp;
        }

        public void setTextapp(TextView textapp) {
            this.textapp = textapp;
        }

        public ImageView getImageapp() {
            return imageapp;
        }

        public void setImageapp(ImageView imageapp) {
            this.imageapp = imageapp;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        ImageView imageapp;
        CheckBox checkBox;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            textapp = itemView.findViewById(R.id.textapp);
            imageapp = itemView.findViewById(R.id.imageapp);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}

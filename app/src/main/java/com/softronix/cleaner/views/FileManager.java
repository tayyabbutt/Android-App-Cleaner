package com.softronix.cleaner.views;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;

import com.softronix.cleaner.R;
import com.softronix.cleaner.fragment.listmanagerfragment;

public class FileManager extends FragmentActivity {
    RecyclerView filemanagerlist;
    RecyclerView.LayoutManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        listmanagerfragment listmanagerfragment=new listmanagerfragment().listmanagerfragment(Environment.getExternalStorageDirectory().toString());
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment,listmanagerfragment).commit();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

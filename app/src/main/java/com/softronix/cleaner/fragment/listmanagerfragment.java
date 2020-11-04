package com.softronix.cleaner.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.softronix.cleaner.R;
import com.softronix.cleaner.adaptor.FileManagerAdaptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class listmanagerfragment extends Fragment {
    RecyclerView filemanagerlist;
    RecyclerView.LayoutManager manager;
    FragmentActivity fragmentActivity;
    String path;

    public listmanagerfragment listmanagerfragment(String path)
    {
        listmanagerfragment fragment=new listmanagerfragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        path=getArguments().getString("path");

        return LayoutInflater.from(getContext()).inflate(R.layout.listmanagerfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filemanagerlist=view.findViewById(R.id.filemanagerlist);
        List<File> files = getListFiles(new File(path));
        FileManagerAdaptor fileManagerAdaptor=new FileManagerAdaptor(getContext(),files);
        manager=new LinearLayoutManager(getContext());
        filemanagerlist.setLayoutManager(manager);
        filemanagerlist.setAdapter(fileManagerAdaptor);
    }
    private List<File> getListFiles(File parentDir)
    {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if(files !=null)
        {
            for (File file : files) {
                if(file.isFile())
                {
                    inFiles.add(file);
                }
                if (file.isDirectory())
                {
                    inFiles.add(file);
                }

            }
        }
        return inFiles;
    }

}

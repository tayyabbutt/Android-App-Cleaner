package com.softronix.cleaner.adaptor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.softronix.cleaner.R;
import com.softronix.cleaner.fragment.listmanagerfragment;

import java.io.File;
import java.util.List;

public class FileManagerAdaptor extends RecyclerView.Adapter<FileManagerAdaptor.viewholder> {

    List<File> files;
    Context context;

    public  FileManagerAdaptor(Context context, List<File> files)
    {
        this.context=context;
        this.files=files;
    }

    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filemanagerrow, viewGroup, false);
        viewholder vh = new viewholder(mView);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull viewholder viewholder, final int i) {
        viewholder.getText().setText(files.get(i).getName());
        viewholder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click item " + i, Toast.LENGTH_SHORT).show();
                if(files.get(i).isFile())
                {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(files.get(i)), getMimeType(files.get(i).getAbsolutePath()));
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // no Activity to handle this kind of files
                    }
                }
                else
                    {
                        listmanagerfragment listmanagerfragment=new listmanagerfragment().listmanagerfragment(files.get(i).getAbsolutePath());
                        FragmentTransaction ft=((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment,listmanagerfragment).addToBackStack("").commit();

                    }
                }
        });


    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
    @Override
    public int getItemCount() {
        return files.size();
    }

    public class viewholder extends RecyclerView.ViewHolder
    {
        TextView text;
        CardView cardView;

        public CardView getCardView() {
            return cardView;
        }

        public void setCardView(CardView cardView) {
            this.cardView = cardView;
        }

        public TextView getText() {
            return text;
        }

        public void setText(TextView text) {
            this.text = text;
        }

        public viewholder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.text);
            cardView=itemView.findViewById(R.id.card);
        }

    }
}

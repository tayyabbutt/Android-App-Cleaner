package com.softronix.cleaner.adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.softronix.cleaner.R;
import com.softronix.cleaner.callback.OnVideoItemClick;
import com.softronix.cleaner.model.Model_Video;
import com.softronix.cleaner.views.videoFolder.Activity_galleryview;

import java.util.ArrayList;


public class Adapter_VideoFolder extends RecyclerView.Adapter<Adapter_VideoFolder.ViewHolder> {

    ArrayList<Model_Video> al_video;
    Context context;
    Activity activity;
    OnVideoItemClick onItemCheckListenern;

    public Adapter_VideoFolder(Context context, ArrayList<Model_Video> al_video, Activity activity, @NonNull OnVideoItemClick onItemCheckListener) {

        this.al_video = al_video;
        this.context = context;
        this.activity = activity;
        this.onItemCheckListenern = onItemCheckListener;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        TextView title, detail, duration;
        RelativeLayout rl_select;
        LinearLayout song_recyclerView_item;
        CheckBox checkBox;

        public ViewHolder(View v) {

            super(v);
            song_recyclerView_item = (LinearLayout) v.findViewById(R.id.song_recyclerView_item);
            iv_image = (ImageView) v.findViewById(R.id.iv_image);
            title = (TextView) v.findViewById(R.id.songs_title_tv);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox_song);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    al_video.get(getAdapterPosition()).setBoolean_selected(isChecked);
                    if (onItemCheckListenern != null) {
                        onItemCheckListenern.onVideooItemChecked(isChecked, al_video.get(getAdapterPosition()));
                    }
                }

            });

        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);

        }
    }

    public void setFiles(ArrayList<Model_Video> files) {
        al_video = files;
    }

    @Override
    public Adapter_VideoFolder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_videos, parent, false);

        ViewHolder viewHolder1 = new ViewHolder(view);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder Vholder, final int position) {


        Glide.with(context).load("file://" + al_video.get(position).getStr_thumb())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.image_placeholder))
                .into(Vholder.iv_image);

        if (al_video.get(position).getaName() != null) {
            Vholder.title.setText(al_video.get(position).getaName());
        } else {
            Vholder.title.setText("Unknown");
        }
        Vholder.checkBox.setChecked(al_video.get(position).isBoolean_selected());


    }

    @Override
    public int getItemCount() {

        return al_video.size();
    }

}


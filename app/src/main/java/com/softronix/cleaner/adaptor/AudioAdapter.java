package com.softronix.cleaner.adaptor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.softronix.cleaner.R;
import com.softronix.cleaner.callback.OnItemCheckListener;
import com.softronix.cleaner.model.Model_Audio;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    public List<Model_Audio> audioSongs;
    Context context;
    Activity activity;
    OnItemCheckListener onItemCheckListener;

    public AudioAdapter(Context context, List<Model_Audio> audioSongs, Activity activity, @NonNull OnItemCheckListener onItemCheckListener) {

        this.audioSongs = audioSongs;
        this.context = context;
        this.activity = activity;
        this.onItemCheckListener = onItemCheckListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView song_title_tv, song_detail;

        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.audio_check);
            song_title_tv = (TextView) v.findViewById(R.id.song_title_tv);
            song_detail = (TextView) v.findViewById(R.id.song_detail);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    audioSongs.get(getAdapterPosition()).setSelected(isChecked);
                    if (onItemCheckListener != null) {
                        onItemCheckListener.onAudioItemChecked(isChecked, audioSongs.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);

        }
    }

    public void setFiles(List<Model_Audio> files) {
        audioSongs = files;
    }

    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item, parent, false);

        AudioAdapter.ViewHolder viewHolder1 = new AudioAdapter.ViewHolder(view);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final AudioAdapter.ViewHolder holder, final int position) {

        if (audioSongs.get(position).getaName() != null) {
            holder.song_title_tv.setText(audioSongs.get(position).getaName());
        } else {
            holder.song_title_tv.setText("");

        }

        if (audioSongs.get(position).getaArtist() != null && audioSongs.get(position).getaAlbum() != null) {
            holder.song_detail.setText(audioSongs.get(position).getaArtist() + ", " + audioSongs.get(position).getaAlbum());
        } else {
            holder.song_detail.setText("");
        }
        holder.checkBox.setChecked(audioSongs.get(position).isSelected());
    }

    @Override
    public int getItemCount() {

        return audioSongs.size();
    }

}

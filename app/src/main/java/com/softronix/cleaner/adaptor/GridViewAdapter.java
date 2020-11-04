package com.softronix.cleaner.adaptor;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.softronix.cleaner.R;
import com.softronix.cleaner.callback.OnImageItemCheckListner;
import com.softronix.cleaner.model.Model_images;

import java.util.ArrayList;
import java.util.List;


public class GridViewAdapter extends ArrayAdapter<Model_images> {

    Context context;
    ViewHolder viewHolder;
    List<Model_images> al_menu = new ArrayList<>();
    int int_position;
    Activity activity;
    OnImageItemCheckListner onItemCheckListener;

    public GridViewAdapter(Context context, List<Model_images> al_menu, int int_position, Activity activity, @NonNull OnImageItemCheckListner onItemCheckListener) {
        super(context, R.layout.image_item_adapter, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.activity = activity;
        this.int_position = int_position;
        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public int getCount() {
        return al_menu.get(int_position).getAl_imagepath().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.get(int_position).getAl_imagepath().size() > 0) {
            return al_menu.get(int_position).getAl_imagepath().size();
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item_adapter, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder1);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder21);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image1);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.imageCheckBox);

            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    al_menu.get(position).setSelected(isChecked);
                    if (onItemCheckListener != null) {
                        onItemCheckListener.onImageItemChecked(isChecked, al_menu.get(int_position).getAl_imagepath().get(position));
                    }
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setVisibility(View.GONE);
        viewHolder.tv_foldersize.setVisibility(View.GONE);

        Glide.with(context).load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(viewHolder.iv_image);
        return convertView;

    }

    private static class ViewHolder {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
        CheckBox checkBox;


    }


}












/*

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {
    Context context;
    List<Model_images> al_menu = new ArrayList<>();
    int int_position;
    Activity activity;
    OnImageItemCheckListner onItemCheckListener;

    public GridViewAdapter(Context context, List<Model_images> al_menu, int int_position, Activity activity, @NonNull OnImageItemCheckListner onItemCheckListener) {

        this.al_menu = al_menu;
        this.context = context;
        this.activity = activity;
        this.int_position = int_position;
        this.onItemCheckListener = onItemCheckListener;
    }

    public void setFiles(List<Model_images> al_images) {
        al_menu = al_images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;

        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.imageCheckBox);
            tv_foldern = (TextView) v.findViewById(R.id.tv_folder1);
            tv_foldersize = (TextView) v.findViewById(R.id.tv_folder21);
            iv_image = (ImageView) v.findViewById(R.id.iv_image1);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    al_menu.get(getAdapterPosition()).setSelected(isChecked);
                    if (onItemCheckListener != null) {
                        onItemCheckListener.onImageItemChecked(isChecked, al_menu.get(getAdapterPosition()));
                    }
                }
            });
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }

    @Override
    public GridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_adapter, parent, false);

        GridViewAdapter.ViewHolder viewHolder1 = new GridViewAdapter.ViewHolder(view);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final GridViewAdapter.ViewHolder holder, final int position) {

        Glide.with(context).load("file://" + al_menu.get(int_position).getAl_imagepath().get(position))
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(holder.iv_image);
        holder.checkBox.setChecked(al_menu.get(position).isSelected());

    }

    @Override
    public int getItemCount() {
        return al_menu.get(int_position).getAl_imagepath().size();
    }

}*/

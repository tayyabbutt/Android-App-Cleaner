package com.softronix.cleaner.adaptor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.softronix.cleaner.R;
import com.softronix.cleaner.model.HelperForCheckBox;

import java.util.List;

public class AppManagerAdaptor extends RecyclerView.Adapter<AppManagerAdaptor.ViewHolder> {

    private static final String SYSTEM_PACKAGE_NAME = "android";
    List<ApplicationInfo> packages;
    Context context;
    Listner listner;
    List<HelperForCheckBox> helperForCheckBoxes;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public AppManagerAdaptor(Context context, List<HelperForCheckBox> helperForCheckBoxes, Listner listner) {
        this.context = context;
        this.listner = listner;
        this.helperForCheckBoxes = helperForCheckBoxes;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.appmanagerrow, viewGroup, false);
        ViewHolder vh = new ViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        holder.textapp.setText(helperForCheckBoxes.get(i).getResolveInfo().loadLabel(context.getPackageManager()) + "\n" + helperForCheckBoxes.get(i).getResolveInfo().activityInfo.packageName);
        holder.imageapp.setImageDrawable(helperForCheckBoxes.get(i).getResolveInfo().loadIcon(context.getPackageManager()));
        holder.uninstal.setChecked(helperForCheckBoxes.get(i).getSelected());
    }

    @Override
    public int getItemCount() {
        return helperForCheckBoxes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textapp;
        ImageView imageapp;
        CardView appcard;
        CheckBox uninstal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textapp = itemView.findViewById(R.id.textapp);
            imageapp = itemView.findViewById(R.id.imageapp);
            appcard = itemView.findViewById(R.id.appcard);
            uninstal = itemView.findViewById(R.id.uninstal);
            uninstal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    helperForCheckBoxes.get(getAdapterPosition()).setSelected(isChecked);
                    listner.clicked(getAdapterPosition(), isChecked);
                }
            });

        }
    }

    public interface Listner {
        void clicked(int position, boolean isChecked);
    }
}

package com.softronix.cleaner.model;

import android.content.pm.ResolveInfo;

import java.io.Serializable;

public class HelperForCheckBox implements Serializable {

    private Boolean isSelected = false;
    private ResolveInfo resolveInfo;

    public HelperForCheckBox(Boolean isSelected, ResolveInfo resolveInfo) {
        this.isSelected = isSelected;
        this.resolveInfo = resolveInfo;
    }


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        this.resolveInfo = resolveInfo;
    }
}

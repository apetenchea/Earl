package com.earl;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

public class AppStatus {
    private int risk;
    private String label;
    private String packageName;
    private Drawable icon;

    AppStatus(int risk, String label, String packageName, Drawable icon) {
        this.risk = risk;
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
    }

    public int getRisk() {
        return risk;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public int getColor() {
        int color = Color.rgb(229, 229, 11);
        if (risk < 40) {
            color = Color.rgb(72, 244, 66);
        } else if (risk > 60) {
            color = Color.rgb(255, 0, 0);
        }
        return color;
    }
}

package com.earl.scan;

import android.graphics.Color;

public enum RiskLevel {
    LOW(Color.rgb(72, 244, 66)),
    MEDIUM(Color.rgb(229, 229, 11)),
    HIGH(Color.rgb(255, 0, 0));

    private int color;

    RiskLevel(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}

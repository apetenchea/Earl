package com.earl.scan;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.earl.apk.Apk;

public class Verdict implements Comparable<Verdict> {
    private Apk apk;
    private float risk;

    public Verdict(Apk apk, float risk) {
        this.apk = apk;
        this.risk = risk;
    }

    @Override
    public int compareTo(@NonNull Verdict o) {
        return -Float.compare(risk, o.risk);
    }

    public int getColor() {
        return getRiskLevel().getColor();
    }

    public Apk getApk() {
        return apk;
    }

    public String getApkPkgName() {
        return apk.getPackageName();
    }

    public int getRiskAsPercentage() {
        return Math.round(risk * 100f);
    }

    public Drawable getApkIcon() {
        return apk.getIcon();
    }

    public String getApkName() {
        String name = apk.getLabel();
        if (name == null) {
            name = apk.getPackageName();
        }
        return name;
    }

    float getRisk() {
        return risk;
    }

    String getApkMd5() {
        return apk.getMd5();
    }

    private RiskLevel getRiskLevel() {
        int riskPrc = getRiskAsPercentage();
        if (riskPrc < 40) {
            return RiskLevel.LOW;
        } else if (riskPrc > 60) {
            return RiskLevel.HIGH;
        }
        return RiskLevel.MEDIUM;
    }
}

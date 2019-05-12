package com.earl.ai;

import android.content.pm.PackageInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FeaturesExtractor {
    private static final String TAG = FeaturesExtractor.class.getName();
    private static FeaturesExtractor instance = null;
    private List<Feature> config;

    private FeaturesExtractor() {}

    public static FeaturesExtractor getInstance(AssetManager assetManager) {
        if (instance == null) {
            try {
                Gson gson = new Gson();
                AssetFileDescriptor featuresConfig = assetManager.openFd("features.json");
                InputStreamReader reader = new InputStreamReader(featuresConfig.createInputStream());
                instance = gson.fromJson(reader, FeaturesExtractor.class);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return instance;
    }

    public List<Float> extract(PackageInfo packageInfo) {
        List<Float> features = new ArrayList<>(config.size());
        for (Feature feature : config) {
            features.add(feature.getValue(packageInfo));
        }
        return features;
    }
}

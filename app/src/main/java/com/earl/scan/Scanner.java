package com.earl.scan;

import android.content.Context;
import android.util.Log;

import com.earl.ai.FeaturesExtractor;
import com.earl.ai.Model;
import com.earl.local.Cache;
import com.earl.local.Settings;
import java.util.List;

public class Scanner {
    private static final String TAG = Scanner.class.getName();

    private FeaturesExtractor featuresExtractor;
    private Model predictor;
    private Cache cache;
    private Settings settings;

    public Scanner(Context context) {
        featuresExtractor = FeaturesExtractor.getInstance(context.getAssets());
        if (featuresExtractor == null) {
            Log.e(TAG, "Unable to instantiate FeaturesExtractor!");
        }

        predictor = Model.getInstance(context.getAssets());
        if (predictor == null) {
            Log.e(TAG, "Unable to instantiate Model!");
        }

        cache = new Cache(context);
        settings = new Settings(context);
    }

    public Integer[] scan(Apk... apps) {
        Integer[] verdicts = new Integer[apps.length];
        boolean scanCache = settings.getUseScanCache();

        for (int index = 0; index < apps.length; ++index) {
            final String md5 = apps[index].getMd5();
            if (md5 == null) {
                verdicts[index] = 50;
                continue;
            }

            if (scanCache) {
                int cachedValue = cache.get(md5);
                if (cachedValue >= 0) {
                    verdicts[index] = cachedValue;
                    continue;
                }
            }

            List<Float> features = featuresExtractor.extract(apps[index].getPackageInfo());
            float[][] input = new float[1][features.size()];
            for (int feature_idx = 0; feature_idx < features.size(); ++feature_idx) {
                input[0][feature_idx] = features.get(feature_idx);
            }
            verdicts[index] = Math.round(predictor.predict(input) * 100f);
            cache.set(md5, verdicts[index]);
        }

        return verdicts;
    }
}

package com.earl.scan;

import android.content.Context;
import android.util.Log;

import com.earl.settings.Settings;
import com.earl.ai.FeaturesExtractor;
import com.earl.ai.Model;
import com.earl.apk.Apk;
import com.earl.db.VerdictCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scanner {
    private static final String TAG = Scanner.class.getName();

    private FeaturesExtractor featuresExtractor;
    private Model predictor;
    private VerdictCollection verdictCollection;
    private Cache cache;
    private Settings settings;

    public Scanner(Context context) {
        featuresExtractor = FeaturesExtractor.getInstance(context);
        if (featuresExtractor == null) {
            Log.e(TAG, "Unable to instantiate FeaturesExtractor!");
        }

        predictor = Model.getInstance(context);
        if (featuresExtractor == null) {
            Log.e(TAG, "Unable to instantiate Model!");
        }

        verdictCollection = new VerdictCollection();
        cache = new Cache(context);
        settings = new Settings(context);
    }

    public Verdict scan(Apk apk) {
        if (settings.getUseScanCache()) {
            Float cachedValue = cache.get(apk.getMd5());
            if (!Float.isNaN(cachedValue)) {
                return new Verdict(apk, cachedValue);
            }
        }

        if (settings.getUseScanCache()) {
            verdictCollection.clear();
            verdictCollection.submit(apk);
            Verdict verdict = verdictCollection.retrieve();
            if (verdict != null) {
                cache.set(verdict.getApkMd5(), verdict.getRisk());
                return verdict;
            }
        }

        List<Float> features = featuresExtractor.extract(apk.getPackageInfo());
        float[][] input = new float[1][featuresExtractor.getFeaturesCount()];
        for (int index = 0; index < features.size(); ++index) {
            input[0][index] = features.get(index);
        }

        float[][] output = predictor.predict(input);
        String md5 = apk.getMd5();
        if (md5 != null) {
            cache.set(md5, output[0][0]);
        }

        return new Verdict(apk, output[0][0]);
    }

    public Verdict[] scan(Apk[] apps) {
        List<Verdict> verdicts = new ArrayList<>(apps.length);
        Set<Apk> set = new HashSet<>(apps.length);

        if (settings.getUseScanCache()) {
            for (Apk apk : apps) {
                float cachedValue = cache.get(apk.getMd5());
                if (!Float.isNaN(cachedValue)) {
                    set.add(apk);
                    verdicts.add(new Verdict(apk, cachedValue));
                }
            }
        }

        if (settings.getUseVerdictsDb()) {
            verdictCollection.clear();
            for (Apk apk : apps) {
                if (!set.contains(apk)) {
                    verdictCollection.submit(apk);
                }
            }
            int queryCounter = verdictCollection.getQueryCounter();
            while (queryCounter > 0) {
                --queryCounter;
                Verdict verdict = verdictCollection.retrieve();
                if (verdict != null) {
                    set.add(verdict.getApk());
                    verdicts.add(verdict);
                }
            }
            for (Verdict verdict : verdictCollection.drain()) {
                set.add(verdict.getApk());
                verdicts.add(verdict);
            }
        }

        List<Apk> appsToScan = new ArrayList<>(apps.length);
        for (Apk apk : apps) {
            if (!set.contains(apk)) {
                appsToScan.add(apk);
            }
        }
        if (!appsToScan.isEmpty()) {
            float[][] input = new float[appsToScan.size()][featuresExtractor.getFeaturesCount()];
            for (int index = 0; index < appsToScan.size(); ++index) {
                List<Float> features = featuresExtractor.extract(appsToScan.get(index).getPackageInfo());
                for (int featureIndex = 0; featureIndex < features.size(); ++featureIndex) {
                    input[index][featureIndex] = features.get(featureIndex);
                }
            }

            float[][] output = predictor.predict(input);
            for (int index = 0; index < appsToScan.size(); ++index) {
                verdicts.add(new Verdict(appsToScan.get(index), output[index][0]));
            }
        }

        for (Verdict verdict : verdicts) {
            cache.set(verdict.getApkMd5(), verdict.getRisk());
        }

        return verdicts.toArray(new Verdict[0]);
    }
}

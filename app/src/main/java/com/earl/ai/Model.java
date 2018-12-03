package com.earl.ai;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.earl.MainActivity;
import com.earl.R;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Model {
    private static final String TAG = Model.class.getName();
    private static Model instance = null;

    private Interpreter interpreter;

    private Model(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public static Model getInstance(Context context) {
        if (instance == null) {
            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor modelFile = assetManager.openFd(context.getString(R.string.MODEL_FILE));
                Interpreter interpreter = new Interpreter(loadModel(modelFile));
                instance = new Model(interpreter);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return instance;
    }

    private static MappedByteBuffer loadModel(AssetFileDescriptor fileDescriptor) throws IOException {
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,
                fileDescriptor.getStartOffset(),
                fileDescriptor.getDeclaredLength());
    }


    public float[][] predict(float[][] input) {
        Log.d(TAG, "PREDICTION");
        float[][] output = new float[input.length][1];
        interpreter.run(input, output);
        return output;
    }

    public void close() {
        interpreter.close();
        interpreter = null;
        instance = null;
    }
}

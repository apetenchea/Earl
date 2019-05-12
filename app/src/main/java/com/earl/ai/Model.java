package com.earl.ai;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class Model {
    private static final String TAG = Model.class.getName();
    private static Model instance = null;

    private Interpreter interpreter;

    private Model(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public static Model getInstance(AssetManager assetManager) {
        if (instance == null) {
            try {
                AssetFileDescriptor modelFile = assetManager.openFd("model.lite");
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
        float[][] output = new float[input.length][1];
        interpreter.run(input, output);
        return output;
    }
}

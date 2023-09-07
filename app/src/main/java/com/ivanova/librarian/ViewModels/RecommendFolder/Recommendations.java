package com.ivanova.librarian.ViewModels.RecommendFolder;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Recommendations {

    private Interpreter tflite;

    public void getRecommendations(RecommendationsCallback recsCallback, ArrayList<Float> booksIds, float userId) {
        downloadModel(recsCallback, booksIds, userId);
    }

    private void downloadModel(RecommendationsCallback recsCallback, ArrayList<Float> booksIds, float userId) {
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("recommendations", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        Log.d(TAG, "Model was loaded");

                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            tflite = new Interpreter(modelFile);
                        }

                        ArrayList<Integer> ids = recommend(booksIds, userId);
                        recsCallback.onCallback(ids);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to download model");
                    }
                });
    }

    private ArrayList<Integer> recommend(ArrayList<Float> booksIds, float userId) {
        float[] userIdArray = new float[booksIds.size()];
        Arrays.fill(userIdArray, userId);

        float[] booksIdsFloat = new float[booksIds.size()];
        for (int i = 0; i < booksIds.size(); i++) {
            booksIdsFloat[i] = booksIds.get(i);
        }
        Arrays.sort(booksIdsFloat);

        Object[] inputs = {booksIdsFloat, userIdArray};
        Map<Integer, Object> outputs = new LinkedHashMap<>();
        outputs.put(0, new float[booksIds.size()][1]);
        tflite.runForMultipleInputsOutputs(inputs, outputs);

        Object[] outputsObjectsArray = (Object[]) outputs.get(0);
        ArrayList<Float> outputsArrayList = new ArrayList<>();
        for (int i = 0; i < booksIds.size(); i++) {
            float[] outputsFloatArray = (float[]) outputsObjectsArray[i];
            outputsArrayList.add(outputsFloatArray[0]);
        }

        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int largest = getIndexOfLargest(outputsArrayList);
            ids.add(largest + 1);
            outputsArrayList.set(largest, -1.0f);
        }
        return ids;
    }

    private int getIndexOfLargest(ArrayList<Float> array) {
        int largest = 0;
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i) > array.get(largest)) largest = i;
        }
        return largest;
    }
}

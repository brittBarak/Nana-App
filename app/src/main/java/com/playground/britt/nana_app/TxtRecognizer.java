package com.playground.britt.nana_app;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class TxtRecognizer {

    public static final int RESULTS_TO_SHOW = 3;
    public static final float CONFIDENCE_THRESHOLD = 0.75f;

    private static TxtRecognizer instance;
    private final Context appContext;
//    private LocalClassifier localClassifier;
//    private CloudClassifier cloudClassifier;

    private List<String> result = new ArrayList<>();

    private Comparator<FirebaseVisionLabel> localLabelComparator = new Comparator<FirebaseVisionLabel>() {
        @Override
        public int compare(FirebaseVisionLabel label1, FirebaseVisionLabel label2) {
            return (int) (label1.getConfidence() - label2.getConfidence());
        }
    };

    private Comparator<FirebaseVisionCloudLabel> cloudLabelComparator = new Comparator<FirebaseVisionCloudLabel>() {
        @Override
        public int compare(FirebaseVisionCloudLabel label1, FirebaseVisionCloudLabel label2) {
            return (int) (label1.getConfidence() - label2.getConfidence());
        }
    };

    static synchronized public TxtRecognizer getInstance(Context context) {
        if (instance == null) {
            instance = new TxtRecognizer(context.getApplicationContext());
        }
        return instance;
    }

    private TxtRecognizer(Context appContext) {
        this.appContext = appContext;
//        this.localClassifier = new LocalClassifier();
//        this.cloudClassifier = new CloudClassifier();
    }

    public void executeLocal(Bitmap bitmap, final ClassifierCallback callback) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        final long start = System.currentTimeMillis();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                result.clear();
                                result.add(texts.getText());
                                callback.onClassified("Local", result, System.currentTimeMillis() - start);
                            }
                        })
                .addOnFailureListener(callback);
    }

    private void processLocalResult(List<FirebaseVisionLabel> labels, ClassifierCallback callback, long start) {
        labels.sort(localLabelComparator);
        result.clear();

        FirebaseVisionLabel label;
        for (int i = 0; i < Math.min(RESULTS_TO_SHOW, labels.size()); ++i) {
            label = labels.get(i);
            result.add(label.getLabel() + ":" + label.getConfidence());
        }
        callback.onClassified("Local Model", result, System.currentTimeMillis() - start);
    }

    public void executeCloud(Bitmap bitmap, final ClassifierCallback callback) {
        final long start = System.currentTimeMillis();

        FirebaseVisionCloudDetectorOptions options =
                new FirebaseVisionCloudDetectorOptions.Builder()
                        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                        .setMaxResults(15)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                result.clear();
                                result.add(texts.getText());
                                callback.onClassified("Cloud", result, System.currentTimeMillis() - start);
                            }
                        })
                .addOnFailureListener(callback);
    }

    private synchronized void processCloudResult(List<FirebaseVisionCloudLabel> labels, ClassifierCallback callback, long start) {
        labels.sort(cloudLabelComparator);
        result.clear();

        FirebaseVisionCloudLabel label;
        for (int i = 0; i < Math.min(RESULTS_TO_SHOW, labels.size()); ++i) {
            label = labels.get(i);
            result.add(label.getLabel() + ":" + label.getConfidence());
        }
        callback.onClassified("Cloud Model", result, System.currentTimeMillis() - start);
    }


    private void processCustomResult(List<LabelAndProb> resultList, ClassifierCallback callback, long start) {
        Collections.sort(resultList);
        result.clear();

        for (int i = 0; i < Math.min(RESULTS_TO_SHOW, resultList.size()); i++) {
            LabelAndProb label = resultList.get(i);
            result.add(label.getLabel() + ":" + label.getProb());
        }
        callback.onClassified("Custom Model", result, System.currentTimeMillis() - start);
    }

}

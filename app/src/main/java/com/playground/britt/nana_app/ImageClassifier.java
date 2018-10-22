package com.playground.britt.nana_app;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class ImageClassifier {

    public static final int RESULTS_TO_SHOW = 5;
    public static final float CONFIDENCE_THRESHOLD = 0.75f;

    private static ImageClassifier instance;
    private final Context appContext;
    private LocalClassifier localClassifier;
    private CloudClassifier cloudClassifier;
//    private CustomClassifier customClassifier;

    private List<String> resultLabels = new ArrayList<>();


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

    static synchronized public ImageClassifier getInstance(Context context) {
        if (instance == null) {
            instance = new ImageClassifier(context.getApplicationContext());
        }
        return instance;
    }

    private ImageClassifier(Context appContext) {
        this.appContext = appContext;
        this.localClassifier = new LocalClassifier();
        this.cloudClassifier = new CloudClassifier();
//        this.customClassifier = new CustomClassifier(appContext, failureListener);
    }

    public void executeLocal(Bitmap bitmap, final ClassifierCallback callback) {
        final long start = System.currentTimeMillis();

        OnSuccessListener<List<FirebaseVisionLabel>> successListener = new OnSuccessListener<List<FirebaseVisionLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionLabel> labels) {
                //4.1. process output
                processLocalResult(labels, callback, start);
            }
        };

        localClassifier.execute(bitmap, successListener, callback);
    }

    private void processLocalResult(List<FirebaseVisionLabel> labels, ClassifierCallback callback, long start) {
        labels.sort(localLabelComparator);
        resultLabels.clear();

        FirebaseVisionLabel label;
        if (labels.size() > 0) {
            for (int i = 0; i < Math.min(RESULTS_TO_SHOW, labels.size()); ++i) {
                label = labels.get(i);
                resultLabels.add(label.getLabel() + ":" + label.getConfidence());
            }
        } else {
            resultLabels.add("Results not found");
        }
        callback.onClassified("Local Model", resultLabels, System.currentTimeMillis() - start);
    }

    public void executeCloud(Bitmap bitmap, final ClassifierCallback callback) {
        final long start = System.currentTimeMillis();

        OnSuccessListener<List<FirebaseVisionCloudLabel>> successListener = new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionCloudLabel> labels) {
                processCloudResult(labels, callback, start);

            }
        };

        cloudClassifier.execute(bitmap, successListener, callback);
    }

    private synchronized void processCloudResult(List<FirebaseVisionCloudLabel> labels, ClassifierCallback callback, long start) {
        labels.sort(cloudLabelComparator);
        resultLabels.clear();

        FirebaseVisionCloudLabel label;
        for (int i = 0; i < Math.min(RESULTS_TO_SHOW, labels.size()); ++i) {
            label = labels.get(i);
            resultLabels.add(label.getLabel() + ":" + label.getConfidence());
        }
        callback.onClassified("Cloud Model", resultLabels, System.currentTimeMillis() - start);
    }


//    public void executeCustom(Bitmap bitmap, final ClassifierCallback callback) {
//        final long start = System.currentTimeMillis();
//
//        OnSuccessListener<List<LabelAndProb>> successListener = new OnSuccessListener<List<LabelAndProb>>() {
//            @Override
//            public void onSuccess(List<LabelAndProb> labels) {
//                //4.3.2 process output (for ui)
//                processCustomResult(labels, callback, start);
//            }
//        };
//        customClassifier.execute(bitmap, successListener, failureListener);
//    }

    private void processCustomResult(List<LabelAndProb> resultList, ClassifierCallback callback, long start) {
        Collections.sort(resultList);
        resultLabels.clear();

        for (int i = 0; i < Math.min(RESULTS_TO_SHOW, resultList.size()); i++) {
            LabelAndProb label = resultList.get(i);
            resultLabels.add(label.getLabel() + ":" + label.getProb());
        }
        callback.onClassified("Custom Model", resultLabels, System.currentTimeMillis() - start);
    }


}

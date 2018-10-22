package com.playground.britt.nana_app;

import com.google.android.gms.tasks.OnFailureListener;

import java.util.List;

import androidx.annotation.NonNull;

interface ClassifierCallback extends OnFailureListener {
    void onClassified(String modelTitle, List<String> topLabels, long executionTime);

}

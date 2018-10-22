package com.playground.britt.nana_app

import android.content.Context
import android.graphics.Bitmap

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabel

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

internal class TxtRecognizerK private constructor(private val appContext: Context)//        this.localClassifier = new LocalClassifier();
//        this.cloudClassifier = new CloudClassifier();
{
    //    private LocalClassifier localClassifier;
    //    private CloudClassifier cloudClassifier;

    private val result = ArrayList<String>()

    private val localLabelComparator = Comparator<FirebaseVisionLabel> { label1, label2 -> (label1.confidence - label2.confidence).toInt() }

    private val cloudLabelComparator = Comparator<FirebaseVisionCloudLabel> { label1, label2 -> (label1.confidence - label2.confidence).toInt() }

    fun executeLocal(bitmap: Bitmap, callback: ClassifierCallback) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val start = System.currentTimeMillis()
        detector.processImage(image)
                .addOnSuccessListener { texts ->
                    result.clear()
                    result.add(texts.text)
                    callback.onClassified("Local", result, System.currentTimeMillis() - start)
                }
                .addOnFailureListener(callback)
    }

    private fun processLocalResult(labels: List<FirebaseVisionLabel>, callback: ClassifierCallback, start: Long) {
        Collections.sort(labels, localLabelComparator)
        result.clear()

        var label: FirebaseVisionLabel
        for (i in 0 until Math.min(RESULTS_TO_SHOW, labels.size)) {
            label = labels[i]
            result.add(label.label + ":" + label.confidence)
        }
        callback.onClassified("Local Model", result, System.currentTimeMillis() - start)
    }

    fun executeCloud(bitmap: Bitmap, callback: ClassifierCallback) {
        val start = System.currentTimeMillis()

        val options = FirebaseVisionCloudDetectorOptions.Builder()
                .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
                .setMaxResults(15)
                .build()
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().cloudTextRecognizer
        detector.processImage(image)
                .addOnSuccessListener { texts ->
                    result.clear()
                    result.add(texts.text)

                    callback.onClassified("Local", result, System.currentTimeMillis() - start)
                }
                .addOnFailureListener(callback)
    }

    @Synchronized
    private fun processCloudResult(labels: List<FirebaseVisionCloudLabel>, callback: ClassifierCallback, start: Long) {
        Collections.sort(labels, cloudLabelComparator)

        result.clear()

        var label: FirebaseVisionCloudLabel
        for (i in 0 until Math.min(RESULTS_TO_SHOW, labels.size)) {
            label = labels[i]
            result.add(label.label + ":" + label.confidence)
        }
        callback.onClassified("Cloud Model", result, System.currentTimeMillis() - start)
    }


    private fun processCustomResult(resultList: List<LabelAndProb>, callback: ClassifierCallback, start: Long) {
        Collections.sort(resultList)
        result.clear()

        for (i in 0 until Math.min(RESULTS_TO_SHOW, resultList.size)) {
            val label = resultList[i]
            result.add(label.getLabel() + ":" + label.getProb())
        }
        callback.onClassified("Custom Model", result, System.currentTimeMillis() - start)
    }

    companion object {

        val RESULTS_TO_SHOW = 3
        val CONFIDENCE_THRESHOLD = 0.75f

        private lateinit var instance: TxtRecognizerK

        @Synchronized
        fun getInstance(context: Context): TxtRecognizerK {
            if (instance == null) {
                instance = TxtRecognizerK(context.applicationContext)
            }
            return instance
        }
    }

}

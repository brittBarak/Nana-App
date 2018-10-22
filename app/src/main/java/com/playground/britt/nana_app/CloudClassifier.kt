package com.playground.britt.nana_app

import android.graphics.Bitmap

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class CloudClassifier {
    internal lateinit var image: FirebaseVisionImage

    private val cloudDetectorOptions = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.STABLE_MODEL)
            .setMaxResults(ImageClassifier.RESULTS_TO_SHOW)
            .build()


    //1.2. setup detector
    private val detector = FirebaseVision.getInstance().getVisionCloudLabelDetector()

    fun execute(bitmap: Bitmap, successListener: OnSuccessListener<List<FirebaseVisionCloudLabel>>, failureListener: OnFailureListener) {
        //2.2. process input
        image = FirebaseVisionImage.fromBitmap(bitmap)

        //3.2. run model
        detector.detectInImage(image)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
    }
}

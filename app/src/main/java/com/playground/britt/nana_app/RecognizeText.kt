package com.playground.britt.nana_app

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import java.util.*
import com.google.firebase.ml.vision.common.FirebaseVisionImage


class RecognizeText {
//    val textRecognizerLocal = FirebaseVision.getInstance().getOnDeviceTextRecognizer()
//
//
//    var textRecognizerCloud = FirebaseVision.getInstance()
//            .cloudTextRecognizer


    // Or, to provide language hints to assist with language detection:
    // See https://cloud.google.com/vision/docs/languages for supported languages
    var options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(Arrays.asList("en", "hi"))
            .build()

    var textRecognizer = FirebaseVision.getInstance().getCloudTextRecognizer(options)

//    fun execute(bitmap: Bitmap, context: Context?) {
//        val fromBitmap = FirebaseVisionImage.fromBitmap(bitmap)
//        textRecognizer.processImage(fromBitmap)
//                .addOnSuccessListener {
//                    // Task completed successfully
//                    // ...
//
//                    Toast.makeText(context, "SUCCESS", Toast.LENGTH_LONG).show()
//
//                }
//                .addOnFailureListener(callback);
//                }
//
//    }


}

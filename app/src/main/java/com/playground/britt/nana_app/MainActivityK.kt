package com.playground.britt.nana_app

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.text.style.StyleSpan
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseApp

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Collections
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivityK : AppCompatActivity(), AdapterView.OnItemSelectedListener, ClassifierCallback {
    private val filePaths = arrayOf("ice_cream1.jpg", "ice_cream2.png", "ice_cream3.jpg", "ice_cream4.jpeg", "ice_cream5.jpg", "ice_cream6.jpg", "ice_cream7.jpeg", "ice_cream8.jpeg", "ice_cream9.jpg", "ice_cream10.jpg", "ice_cream11.jpg", "ice_cream12.jpeg", "ice_cream13.jpeg", "ice_cream14.jpg")

    private var imageView: ImageView? = null
    private var selectedImage: Bitmap? = null
    private var labelsOverlay: TextView? = null

    internal lateinit var classifier: ImageClassifier
    internal lateinit var txtRecognizer: TxtRecognizer

    private val failureListener = OnFailureListener { e -> handleError(e) }

    private val imageNames: List<String>
        get() {
            val items = ArrayList<String>()
            for (i in filePaths.indices) {
                items.add("Image " + (i + 1))
            }
            return items
        }

    private fun handleError(e: Exception) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUI()

        FirebaseApp.initializeApp(this)

        classifier = ImageClassifier.getInstance(this)
        txtRecognizer = TxtRecognizer.getInstance(this)

        findViewById<View>(R.id.btn_label_local).setOnClickListener {
            labelsOverlay!!.text = null
            labelsOverlay!!.setBackgroundResource(0)
            classifier.executeLocal(selectedImage, this@MainActivityK)
        }

        findViewById<View>(R.id.btn_label_cloud).setOnClickListener {
            labelsOverlay!!.text = null
            labelsOverlay!!.setBackgroundResource(0)
            classifier.executeCloud(selectedImage, this@MainActivityK)
        }

        //        findViewById(R.id.btn_custom).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                labelsOverlay.setText("");
        //                classifier.executeCustom(selectedImage, MainActivity.this);
        //            }
        //        });

        findViewById<View>(R.id.btn_text_local).setOnClickListener {
            labelsOverlay!!.text = null
            labelsOverlay!!.setBackgroundResource(0)
            txtRecognizer.executeLocal(selectedImage, this@MainActivityK)
        }


        findViewById<View>(R.id.btn_text_cloud).setOnClickListener {
            labelsOverlay!!.text = null
            labelsOverlay!!.setBackgroundResource(0)
            txtRecognizer.executeCloud(selectedImage, this@MainActivityK)
        }

//        ViewCompat.setAccessibilityDelegate(spinner, object: AccessibilityDelegateCompat() {
//
//            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
//
//                super.onInitializeAccessibilityNodeInfo(host, info)
//
//                val clickActionId = AccessibilityNodeInfoCompat.ACTION_CLICK
//
//                info.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat(clickActionId, getString(R.string.abc_action_bar_home_description)))
//
//            }
//
//        })



    }

    private fun setUI() {
        labelsOverlay = findViewById(R.id.tv_result)
        labelsOverlay!!.movementMethod = ScrollingMovementMethod()

        imageView = findViewById(R.id.imageView)

        setSpinner()
    }

    private fun setSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner)

        val adapter = ArrayAdapter(this, android.R.layout
                .simple_spinner_dropdown_item, imageNames)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

    }


    override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
        labelsOverlay!!.text = null
        labelsOverlay!!.setBackgroundResource(0)
        selectedImage = getBitmapFromAsset(this, filePaths[position])
        if (selectedImage != null) {
            imageView!!.setImageBitmap(selectedImage)
        }

        spinner.contentDescription = "Image selected : image " + (position + 1)


    }

    override fun onClassified(modelTitle: String, topLabels: List<String>, executionTime: Long) {
        val builder = SpannableStringBuilder()
        val boldSpan = StyleSpan(android.graphics.Typeface.BOLD)
        builder.append("$modelTitle - time: $executionTime\n", boldSpan, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        if (topLabels === Collections.EMPTY_LIST || topLabels.size == 0) {
            builder.append("No results..")
        } else {
            for (s in topLabels) {
                builder.append(s)
                builder.append("\n")
            }

            labelsOverlay!!.text = builder
            labelsOverlay!!.setBackgroundResource(R.color.gray_transparent)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun onFailure(e: Exception) {
        handleError(e)
    }

    companion object {

        fun getBitmapFromAsset(context: Context, filePath: String): Bitmap? {
            val assetManager = context.assets

            val `is`: InputStream
            var bitmap: Bitmap? = null
            try {
                `is` = assetManager.open(filePath)
                bitmap = BitmapFactory.decodeStream(`is`)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return bitmap
        }
    }
}

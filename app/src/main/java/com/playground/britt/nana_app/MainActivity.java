package com.playground.britt.nana_app;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ClassifierCallback {
    private final String[] filePaths =
            new String[]{"ice_cream1.jpg", "ice_cream2.png", "ice_cream3.jpg", "ice_cream4.jpeg",
                    "ice_cream5.jpg", "ice_cream6.jpg", "ice_cream7.jpeg", "ice_cream8.jpeg",
                    "ice_cream9.jpg", "ice_cream10.jpg", "ice_cream11.jpg", "ice_cream12.jpeg",
                    "ice_cream13.jpeg", "ice_cream14.jpg"};

    private ImageView imageView;
    private Bitmap selectedImage;
    private TextView labelsOverlay;

    ImageClassifier classifier;
    TxtRecognizer txtRecognizer;

    private OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            handleError(e);
        }
    };

    private void handleError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();

        FirebaseApp.initializeApp(this);

        classifier = ImageClassifier.getInstance(this);
        txtRecognizer = TxtRecognizer.getInstance(this);

        findViewById(R.id.btn_label_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelsOverlay.setText(null);
                labelsOverlay.setBackgroundResource(0);
                classifier.executeLocal(selectedImage, MainActivity.this);
            }
        });

        findViewById(R.id.btn_label_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelsOverlay.setText(null);
                labelsOverlay.setBackgroundResource(0);
                classifier.executeCloud(selectedImage, MainActivity.this);
            }
        });

//        findViewById(R.id.btn_custom).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                labelsOverlay.setText("");
//                classifier.executeCustom(selectedImage, MainActivity.this);
//            }
//        });

        findViewById(R.id.btn_text_local).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelsOverlay.setText(null);
                labelsOverlay.setBackgroundResource(0);
                txtRecognizer.executeLocal(selectedImage, MainActivity.this);
            }
        });


        findViewById(R.id.btn_text_cloud).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                labelsOverlay.setText(null);
                labelsOverlay.setBackgroundResource(0);
                txtRecognizer.executeCloud(selectedImage, MainActivity.this);
            }
        });

    }

    private void setUI() {
        labelsOverlay = findViewById(R.id.tv_result);
        labelsOverlay.setMovementMethod(new ScrollingMovementMethod());

        imageView = findViewById(R.id.imageView);

        setSpinner();
    }

    private void setSpinner() {
        Spinner spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
                .simple_spinner_dropdown_item, getImageNames());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private List<String> getImageNames() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < filePaths.length; i++) {
            items.add("Image " + (i + 1));
        }
        return items;
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        labelsOverlay.setText(null);
        labelsOverlay.setBackgroundResource(0);
        selectedImage = getBitmapFromAsset(this, filePaths[position]);
        if (selectedImage != null) {
            imageView.setImageBitmap(selectedImage);
        }

        parent.setContentDescription( "Image selected : "+ filePaths[position]);


    }

    @Override
    public void onClassified(String modelTitle, List<String> topLabels, long executionTime) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        builder.append(modelTitle + " - time: " + executionTime + "\n", boldSpan, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        if (topLabels == Collections.EMPTY_LIST || topLabels.size() == 0) {
            builder.append("No results..");
        } else {
            for (String s : topLabels) {
                builder.append(s);
                builder.append("\n");
            }

            labelsOverlay.setText(builder);
            labelsOverlay.setBackgroundResource(R.color.gray_transparent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        handleError(e);
    }
}

package com.example.handwrittendigitrecognition;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Load TensorFlow Inference Library
    static {
        System.loadLibrary("tensorflow_inference");
    }

    // Define Variables for Communicating with the Frozen Model

    // Define Frozen Model Name with Path
    // Path starts with "file:///model_path"
    private static final String MODEL_NAME = "file:///android_asset/optimized_frozen_model.pb";

    // Define Model Input Node Name as defined while Saving the Model
    // Ref: https://github.com/anujdutt9/Android-Machine-Learning/blob/ad7dcbab6ff7cae5950e785b0ca74e26ef8bb972/Linear-Regression/freeze_graph.py#L50
    private static final String INPUT_NODE = "X";

    // Define Model Output Node Name as defined while Saving the Model
    // Ref: https://github.com/anujdutt9/Android-Machine-Learning/blob/ad7dcbab6ff7cae5950e785b0ca74e26ef8bb972/Linear-Regression/freeze_graph.py#L50
    private static final String OUTPUT_NODE = "y_actual";

    // Define Model Input Shape as defined while Saving the Model
    private static final int[] INPUT_SHAPE = {1,784};

    // Define TensorFlow Inference Interface
    private TensorFlowInferenceInterface inferenceInterface;

    // ImageView to show the Loaded Image
    ImageView imageView;
    // TextView to show the prediction results
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Image View and Text View
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        // Instantiate Inference Interface
        inferenceInterface = new TensorFlowInferenceInterface();
        // Initialize TF Model for Inference
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_NAME);
    }

    // On Click method to make Prediction on Image
    public void predictDigit(View view){

    }

    // On Click method to load Next Image
    public void loadNextImage(View view){

    }
}

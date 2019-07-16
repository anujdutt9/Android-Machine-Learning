package com.example.weather_prediction_app;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Load TensorFlow Android Library
    static {
        System.loadLibrary("tensorflow_inference");
    }

    // Define Model Name, I/O Nodes and Input Shape
    private static final String MODEL_FILE = "file:///android_asset/optimized_frozen_model.pb";
    private static final String INPUT_NODE = "input_features";
    private static final int[] INPUT_SHAPE = {1, 4};
    private static final String OUTPUT_NODE = "y_out";
    private TensorFlowInferenceInterface inferenceInterface;


    // Variables for Checkboxes
    CheckBox minTempCheckBox, maxTempCheckBox, meanTempCheckBox, totalPrecipCheckBox;
    TextView resultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maxTempCheckBox = findViewById(R.id.max_temp_checkbox);
        minTempCheckBox = findViewById(R.id.min_temp_checkbox);
        meanTempCheckBox = findViewById(R.id.mean_temp_checkbox);
        totalPrecipCheckBox = findViewById(R.id.total_precip_checkbox);
        resultsTextView = findViewById(R.id.textView);

        inferenceInterface = new TensorFlowInferenceInterface();
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_FILE);
    }

    // Function to make Prediction on Button Press
    public void predictTemperature(View view){
        // Check for Inputs from the Checkboxes
        float maxTempGreater = (maxTempCheckBox.isChecked()) ? 1 : 0;
        float minTempGreater = (minTempCheckBox.isChecked()) ? 1 : 0;
        float meanTempGreater = (meanTempCheckBox.isChecked()) ? 1 : 0;
        float precipTempGreater = (totalPrecipCheckBox.isChecked()) ? 1 : 0;
        // Defining  Input to the Model
        float[] input = {maxTempGreater, minTempGreater, meanTempGreater, precipTempGreater};
        // Run Inference on the Inputs
        float[] results = runInference(input);
        // Print the Results
        displayResults(results);
    }

    // Function to Perform Inference
    private float[] runInference(float[] input){
        Log.d("Input", Arrays.toString(input));
        // Pass in the Input data to Model
        inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SHAPE, input);
        Log.d("Fill Node Float", "Done........");
        // Run Inference on data
        inferenceInterface.runInference(new String[] {OUTPUT_NODE});
        Log.d("Run Inference", "Done........");
        float[] results = new float[2];
        Log.d("Results", Arrays.toString(results));
        // Read the predictions of model into a float array
        inferenceInterface.readNodeFloat(OUTPUT_NODE, results);
        return results;
    }

    // Function to display results
    private void displayResults(float[] results){
        if (results[0] > results[1]){
            resultsTextView.setText("Model predicts that the temperature will increase.");
        }
        else{
            resultsTextView.setText("Model predicts that the temperature will decrease.");
        }
    }
}

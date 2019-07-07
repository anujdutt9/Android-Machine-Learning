package com.example.linear_regression_app;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Load TensorFlow Inference Library
    static {
        System.loadLibrary("tensorflow_inference");
    }

    // Define Variables for Communicating with the Frozen Model

    // Define Model Name with Path
    // Path starts with "file:///model_path"
    private static final String MODEL_NAME = "file:///android_asset/optimized_frozen_model.pb";

    // Define Model Input Node Name as defined while Saving the Model
    // Ref: https://github.com/anujdutt9/Android-Machine-Learning/blob/ad7dcbab6ff7cae5950e785b0ca74e26ef8bb972/Linear-Regression/freeze_graph.py#L50
    private static final String INPUT_NODE = "input_features";

    // Define Model Output Node Name as defined while Saving the Model
    // Ref: https://github.com/anujdutt9/Android-Machine-Learning/blob/ad7dcbab6ff7cae5950e785b0ca74e26ef8bb972/Linear-Regression/freeze_graph.py#L50
    private static final String OUTPUT_NODE = "y_out";

    // Define Model Input Shape as defined while Saving the Model
    private static final int[] INPUT_SHAPE = {1,1};

    // Define TensorFlow Inference Interface
    private TensorFlowInferenceInterface inferenceInterface;

    // Edit Text and Text View
    EditText editText;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Edit Text and Text View
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);

        // Instantiate Inference Interface
        inferenceInterface = new TensorFlowInferenceInterface();
        // Initialize TF Model for Inference
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_NAME);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If edit Text is not empty
                if (!editText.getText().toString().equals("")) {
                    // Get the Input Value to Model and Parse as Float
                    float inputValue = Float.parseFloat(editText.getText().toString());
                    // Get the Predicted Result from the Model
                    String results = performInference(inputValue);
                    // Display the Result
                    textView.setText(results);
                }
            }
        });
    }


    // Function to take in a Float Value as Input and Return an Output as String
    private String performInference(float input){
        // Define the input value as an array as our model takes in Tensors as Input
        float[] floatArray = {input};
        // Params: input node name, input size, input values
        // Defines the Input Node with shape Input_Shape and populates with "floatArray" Values
        inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SHAPE, floatArray);
        // Perform Inference on Input Data
        // Params: String "OUTPUT_NODE name"
        inferenceInterface.runInference(new String[] {OUTPUT_NODE});
        // Array to store Model Predictions, as it's a Tensor, hence the array
        float[] results = {0.0f};
        // Read Predicted Output Values from OUTPUT_NODE into Results array
        inferenceInterface.readNodeFloat(OUTPUT_NODE, results);
        // Get the first value from Results Tensor and convert to String
        String finalResult = String.valueOf(results[0]);
        // Return the Prediction as String
        return finalResult;
    }
}

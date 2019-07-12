package com.example.handwrittendigitrecognition;

import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

    // Image List Index
    private int imageListIdx = 9;

    // Array containig the list of digit images
    private int[] imageListID = {
            R.drawable.digit0,
            R.drawable.digit1,
            R.drawable.digit2,
            R.drawable.digit3,
            R.drawable.digit4,
            R.drawable.digit5,
            R.drawable.digit6,
            R.drawable.digit7,
            R.drawable.digit8,
            R.drawable.digit9,
    };

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

    // Function to load in the image bitmap
    // reshape the bitmap
    //return the bitmap values as a float array as our model takes in float array
    private float[] processImage(){
        // Get Bitmap from Selected Image.
        // selected image: imageListID[imageListIdx]
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), imageListID[imageListIdx]);

        // Check for the image size or reshape/resize to make it appropriate size for our model Input
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 28, 28, true);

        // Show re-scaled image on the screen
        imageView.setImageBitmap(imageBitmap);

        // Int array to hold the image pixel values returned from the getPixels function
        int[] imageIntArray = new int[784];

        // Float array to hold the image values for passing as input to the ML Model
        float[] imagesFloatArray = new float[784];

        // Call on imageBitmap to get pixel values for selected image
        imageBitmap.getPixels(imageIntArray, 0, 28, 0, 0, 28, 28);

        // Convert IntArray values to Float and in range from 0 to 1
        for (int i=0; i<784; i++){
            imagesFloatArray[i] = imageIntArray[i] / -16777216;
        }

        // Log.d("Image Int Array", Arrays.toString(imageIntArray));
        // Log.d("Image Float Array", Arrays.toString(imagesFloatArray));

        return imagesFloatArray;
    }

    // On Click method to make Prediction on Image
    public void predictDigit(View view){
        // Process Selected Image
        float[] pixelbuffer = processImage();

        // Make the Predictions on the Selected Image Pixel Buffer Values shaped as [784].
        float[] results = makePrediction(pixelbuffer);

        // Log.d("Result", Arrays.toString(results));
        printResults(results);
    }


    // Function to Load the ML Model and Make Predictions on Image Pixel Values
    private float[] makePrediction(float[] pixelBuffer){
        // Take the model's Input Node and fill it with pixelBuffer floating point values of selected image
        inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SHAPE, pixelBuffer);

        // Loads the Model, runs the Inference, Loads prediction into "Output_Node"
        inferenceInterface.runInference(new String[] {OUTPUT_NODE});

        // Read results of "OUTPUT_NODE" into results array
        float[] results = {0,0,0,0,0,0,0,0,0,0};

        // Read "OUTPUT_NODE" into "results" float array
        inferenceInterface.readNodeFloat(OUTPUT_NODE, results);

        // Return the Results floatArray
        return results;
    }

    // Function to find Max Value in Array and Return it's Index
    private void printResults(float[] results){
        float maxVal = 0;
        float secondMaxVal = 0;
        int maxValIdx = 0;
        int secondMaxValIdx = 0;

        for (int i=0; i<10; i++){
            if (results[i] > maxVal){
                secondMaxVal = maxVal;
                secondMaxValIdx = maxValIdx;
                maxVal = results[i];
                maxValIdx = i;
            }
            else if (results[i] < maxVal && results[i] > secondMaxVal){
                secondMaxVal = results[i];
                secondMaxValIdx = i;
            }
        }

        String output = "Model Prediction: " + String.valueOf(maxValIdx)  +
                ", Second Prediction: " + String.valueOf(secondMaxValIdx);
        textView.setText(output);
    }


    // On Click method to load Next Image
    public void loadNextImage(View view){
        // If Image Index = 9, reset to 0 else increment by 1
        imageListIdx = (imageListIdx >= 9) ? 0 : imageListIdx + 1;

        // Set imageView to image at Index "imageListIdx"
        imageView.setImageDrawable(getDrawable(imageListID[imageListIdx]));
    }
}

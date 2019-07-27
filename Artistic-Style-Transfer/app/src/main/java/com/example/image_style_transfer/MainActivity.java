package com.example.image_style_transfer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Instantiate Button
    Button button;
    // Instantiate ImageView
    ImageView contentImageView;
    // String containing path to Captured Image
    String pathToFile;
    // Uri for the captured image
    Uri photoURI;


    // Number of Styles the Model supports
    private static final int NUM_STYLES = 26;
    // Optimized ML Model file
    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";
    // ML Model Input Node
    private static final String INPUT_NODE = "input";
    // ML Model Output Node
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";
    private TensorFlowInferenceInterface inferenceInterface;

    // Model Input Image Width
    private static final int IMAGE_WIDTH = 420;
    // Model Input Image Height
    private static final int IMAGE_HEIGHT = 560;

    // Array to contain captured Image Bitmap Values
    private int[] intValues = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
    // Array to contain processed [rotate + scale] Image Bitmap Values
    private float[] floatValues = new float[IMAGE_WIDTH * IMAGE_HEIGHT * 3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Content ImageView
        contentImageView = findViewById(R.id.imageView);

        // Button to Stylize Image
        button = findViewById(R.id.button);

        // Check App Permissions based on API Level
        if (Build.VERSION.SDK_INT >= 23){
            // Request Permissions
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        // On button click, open the camera to take a picture
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Function to open camera and take a picture
                dispatchPictureCaptureAction();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check of result code is OK
        if (resultCode == RESULT_OK){
            // If request code matches our request code
            if (requestCode == 1){
                // Decode the file at filePath
                //Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);

                // If image is shown as rotated, correct is using this function
                RotateBitmap rotateBitmap = new RotateBitmap();
                Bitmap bitmap = null;
                try {
                    bitmap = rotateBitmap.HandleSamplingAndRotationBitmap(this, photoURI);
                } catch (IOException e) {
                    Log.e("Error", e.toString());
                }

                // Scaled & Rotated Image Bitmap
                Bitmap scaledBitmap = null;

                try {
                    // Rescaled Image Bitmap as per Model Input Requirements
                    scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                            IMAGE_WIDTH,
                            IMAGE_HEIGHT,
                            true);

                    // Get Bitmap Values for Rescaled Image into intValues array
                    scaledBitmap.getPixels(intValues, 0,
                            scaledBitmap.getWidth(),
                            0,
                            0,
                            scaledBitmap.getWidth(),
                            scaledBitmap.getHeight());

                    // Normalizing BitMap value between range of 0 to 255.
                    // Ref.: https://developer.android.com/reference/android/graphics/Color.html
                    for (int i = 0; i < intValues.length; ++i) {
                        final int val = intValues[i];
                        floatValues[i * 3 + 0] = ((val >> 16) & 0x00FF);
                        floatValues[i * 3 + 1] = ((val >> 8) & 0x00FF);
                        floatValues[i * 3 + 2] = (val & 0x00FF);
                    }
                }
                catch (Exception e){
                    Log.e("Error", e.toString());
                }

                // Show captured image
                contentImageView.setImageBitmap(scaledBitmap);
                Toast.makeText(this, "Processing Image...", Toast.LENGTH_LONG);

                // Stylized Image Bitmap
                Bitmap stylizedBitmap = null;

                try{
                    // Run Model Inference to Stylize Image
                    stylizeImage();

                    // Define outputBitmap
                    Bitmap outputBitmap = scaledBitmap.copy( scaledBitmap.getConfig() , true);

                    // Set De-Normalized, stylized image value to outputBitmap
                    outputBitmap.setPixels(intValues,
                            0,
                            outputBitmap.getWidth(),
                            0,
                            0,
                            outputBitmap.getWidth(),
                            outputBitmap.getHeight());

                    // Scale the stylized image back to original size
                    stylizedBitmap = Bitmap.createScaledBitmap(outputBitmap,
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            true);
                }
                catch (Exception e){
                    Log.e("Error", e.toString());
                }

                // Show Stylized image
                contentImageView.setImageBitmap(stylizedBitmap);
                Toast.makeText(this, "Processing Completed...", Toast.LENGTH_LONG);
            }
        }
    }


    // Functon to get Style Image Values
    public float[] getStyleValues(){
        // Style Values for Transfer
        float[] styleVals = new float[NUM_STYLES];

        // ------------------ TEST ----------------
        for (int i = 0; i < NUM_STYLES; ++i) {
            styleVals[i] = 1.0f / NUM_STYLES;
        }

        // Sample Float Values for a Specific Style
        // Each style has a different set of values as an array
        //float[] vals = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};

        //for (int i = 0; i<vals.length-1; i++){
            //styleVals[i] = vals[i];
        //}
        // ----------------------------------------
        return styleVals;
    }


    // Function to run the ML Model and return Stylized Image Bitmap
    public void stylizeImage(){
        // Array to contain predicted values
        float[] outputValues = new float[IMAGE_WIDTH * IMAGE_HEIGHT * 3];

        // Style Values for Transfer
        float[] styleVals = getStyleValues();

        // Initialize inference variables to use our model
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);

        // Feed in the Content Image to the ML Model Input Node
        inferenceInterface.feed(INPUT_NODE, floatValues, 1, IMAGE_HEIGHT, IMAGE_WIDTH, 3);

        // Feed in the Style Image to the ML Model
        inferenceInterface.feed("style_num", styleVals, NUM_STYLES);

        // Run inference and get the results in Output Node
        inferenceInterface.run(new String[] {OUTPUT_NODE}, false);

        // Fetch the results from Output Node into the outputValues Array
        inferenceInterface.fetch(OUTPUT_NODE, outputValues);

        // Take the Output Values, De-normalize them, and put into intValues Array
        try{
            for (int i = 0; i < intValues.length; ++i) {
                intValues[i] = 0xFF000000
                        | (((int) (outputValues[i * 3] * 255)) << 16)
                        | (((int) (outputValues[i * 3 + 1] * 255)) << 8)
                        | ((int) (outputValues[i * 3 + 2] * 255));
            }
        }
        catch (Exception e){
            Log.e("Error", e.toString());
        }
    }


    // Function to capture an image on button press
    public void dispatchPictureCaptureAction(){
        // Setup image capture intent
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // If app can handle our intent to capture the image
        if (takePicture.resolveActivity(getPackageManager()) != null){
            // Create a temporary file to contain our captured image
            File photoFile = createPhotoFile();

            // If file has been created successfully, fire the image capture intent
            if (photoFile != null){
                // Get path to file storage location
                pathToFile = photoFile.getAbsolutePath();
                // Get URI for the photo
                photoURI = FileProvider.getUriForFile(MainActivity.this, "com.artistic-style-transfer.fileprovider", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // Start image capture activity
                startActivityForResult(takePicture, 1);
            }
        }
    }


    // Function to create a captured image file location
    private File createPhotoFile(){
        // Define name for the file i.e. current date and time
        String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Write the captured image to a directory
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Create and store the image with .jpg in directory
        File image = null;
        try {
            image = File.createTempFile(name, ".jpg", storageDir);
        } catch (IOException e) {
            Log.e("Error", e.toString());
        }
        // return the image file
        return image;
    }
}

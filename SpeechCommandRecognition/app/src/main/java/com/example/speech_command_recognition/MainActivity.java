package com.example.speech_command_recognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable{

    // ------------------ Audio Recorder Variables ---------------------
    // Input Audio Sample Rate
    private static final int RECORDER_SAMPLE_RATE = 16000;
    // Audio Recording Length for Classification
    private static final int RECORDING_LENGTH = RECORDER_SAMPLE_RATE * 1;
    // Request to Record Audio
    private static final int REQUEST_RECORD_AUDIO = 13;
    // Set Number of Channels to Use for Audio Input
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    // Set Audio Encoding to Use for Audio Input
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // Initialize Audio Recorder
    private AudioRecord recorder = null;

    // --------------------- ML Model Variables -----------------------
    // Define Frozen Model Name with Path
    // Path starts with "file:///model_path"
    private static final String MODEL_NAME = "file:///android_asset/speech_commands_graph.pb";
    // Labels File Name
    private static final String LABEL_FILENAME = "file:///android_asset/labels.txt";
    // Define Model Input Node Name as defined while Saving the Model
    private static final String INPUT_NODE = "decoded_sample_data:0";
    // Input Audio Sample Rate Node Name
    private static final String INPUT_SAMPLE_RATE_NAME = "decoded_sample_data:1";
    // Define Model Output Node Name as defined while Saving the Model
    private static final String OUTPUT_NODE = "labels_softmax";
    // Define Model Input Shape as defined while Saving the Model
    private static final int[] INPUT_SHAPE = {RECORDING_LENGTH, 1};
    // Define TensorFlow Inference Interface
    private TensorFlowInferenceInterface inferenceInterface;

    // Array to contain Probabilities for Each Label
    private List<String> labels = new ArrayList<String>();

    // Start/Stop Button
    private Button startSpeechButton;
    // Result Text View
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start/Stop Button
        startSpeechButton = findViewById(R.id.speechButton);
        // Result Text View
        resultTextView = findViewById(R.id.resultTextView);
        // On Button Click, start recording and analyze Audio
        startSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // record 1 second of audio then send it to the model for recognition
                startSpeechButton.setText("Listening...");
                Thread thread = new Thread(MainActivity.this);
                thread.start();
            }
        });

        // Get Model Class Labels
        String actualFilename = LABEL_FILENAME.split("file:///android_asset/")[1];
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(actualFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Problem reading label file!", e);
        }

        // Request Microphone Permission
        requestMicrophonePermission();
    }

    // Function to Request Microphone Recording Permission
    private void requestMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
        }
    }


    // Function to Record Audio
//    private float[] recordAudio(){
//        // Set Android Priority Thread
//        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
//        // Get the minimum buffer size required for the successful creation of an AudioRecord object, in byte units
//        // args: int sampleRateInHz, int channelConfig, int audioFormat
//        int audioBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
//        // Create Audio Recorder
//        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
//                                                RECORDER_SAMPLE_RATE,
//                                                RECORDER_CHANNELS,
//                                                RECORDER_AUDIO_ENCODING,
//                                                audioBufferSize);
//        // Check Audio Recorder State
//        if (recorder.getState() != AudioRecord.STATE_INITIALIZED){
//            Log.e("Error", "Audio Recorder cannot be initialized !!");
//            return new float[0];
//        }
//
//        // Start Audio Recorder
//        recorder.startRecording();
//        Log.e("Status", "Recording Started !!");
//
//        // Short Array to store the audio data read from phone audio hardware
//        long shortsAudioRead = 0;
//        int recordingOffset = 0;
//        // Stereo to Mono
//        short[] audioBuffer = new short[audioBufferSize / 2];
//        // Audio Recording Buffer to store audio data
//        short[] recordingBuffer = new short[RECORDING_LENGTH];
//        // While the Recording Audio Buffer is not full, keep recording the Audio
//        while (shortsAudioRead < RECORDING_LENGTH) {
//            // Reads audio data from the audio hardware for recording into a short array.
//            int numberOfShorts = recorder.read(audioBuffer, 0, audioBuffer.length);
//            shortsAudioRead += numberOfShorts;
//            System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, numberOfShorts);
//            recordingOffset += numberOfShorts;
//        }
//
//        // Stop Recording
//        recorder.stop();
//        //Release Audio Pipe from Recorder
//        recorder.release();
//        Log.v("Status", String.format("Recording stopped. total read: %d", shortsAudioRead));
//        // Convert Recording Buffer Values to Floating Values as Model take Floating Point Values as Input
//        float[] floatInputBuffer = new float[RECORDING_LENGTH];
//        // We need to feed in float values between -1.0f and 1.0f, so divide the signed 16-bit inputs.
//        for (int i = 0; i < RECORDING_LENGTH; ++i) {
//            floatInputBuffer[i] = recordingBuffer[i] / 32767.0f;
//        }
//        // Return Audio Buffer Data as Floating Point Values
//        return floatInputBuffer;
//    }

    @Override
    public void run() {
        // Get Audio Buffer Data as Floating Point Values
        //float[] floatInputBuffer = recordAudio();

        // ---------------- TEST ------------------
        // Set Android Priority Thread
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        // Get the minimum buffer size required for the successful creation of an AudioRecord object, in byte units
        // args: int sampleRateInHz, int channelConfig, int audioFormat
        int audioBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        // Create Audio Recorder
        recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                audioBufferSize);
        // Check Audio Recorder State
        if (recorder.getState() != AudioRecord.STATE_INITIALIZED){
            Log.e("Error", "Audio Recorder cannot be initialized !!");
            return;
        }

        // Start Audio Recorder
        recorder.startRecording();
        Log.e("Status", "Recording Started !!");

        // Short Array to store the audio data read from phone audio hardware
        long shortsAudioRead = 0;
        int recordingOffset = 0;
        // Stereo to Mono
        short[] audioBuffer = new short[audioBufferSize / 2];
        // Audio Recording Buffer to store audio data
        short[] recordingBuffer = new short[RECORDING_LENGTH];
        // While the Recording Audio Buffer is not full, keep recording the Audio
        while (shortsAudioRead < RECORDING_LENGTH) {
            // Reads audio data from the audio hardware for recording into a short array.
            int numberOfShorts = recorder.read(audioBuffer, 0, audioBuffer.length);
            shortsAudioRead += numberOfShorts;
            System.arraycopy(audioBuffer, 0, recordingBuffer, recordingOffset, numberOfShorts);
            recordingOffset += numberOfShorts;
        }

        // Stop Recording
        recorder.stop();
        //Release Audio Pipe from Recorder
        recorder.release();
        Log.v("Status", String.format("Recording stopped. total read: %d", shortsAudioRead));
        // Convert Recording Buffer Values to Floating Values as Model take Floating Point Values as Input
        float[] floatInputBuffer = new float[RECORDING_LENGTH];
        // We need to feed in float values between -1.0f and 1.0f, so divide the signed 16-bit inputs.
        for (int i = 0; i < RECORDING_LENGTH; ++i) {
            floatInputBuffer[i] = recordingBuffer[i] / 32767.0f;
        }
        // ----------------------------------------

        AssetManager assetManager = getAssets();

        // Setup Model for Inference
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_NAME);
        // Feed Input Sample Rate to Model
        int[] sampleRate = new int[] {RECORDER_SAMPLE_RATE};
        inferenceInterface.feed(INPUT_SAMPLE_RATE_NAME, sampleRate);
        // Feed Audio Data to the Model as Input
        //inferenceInterface.feed(INPUT_NODE, floatInputBuffer, RECORDING_LENGTH, 1);
        inferenceInterface.feed(INPUT_NODE, floatInputBuffer, INPUT_SHAPE[0], INPUT_SHAPE[1]);
        // Get Model Predictions
        String[] outputScoresName = new String[] {OUTPUT_NODE};
        inferenceInterface.run(outputScoresName);
        // Get Output Scores
        float[] outputScores = new float[labels.size()];
        inferenceInterface.fetch(OUTPUT_NODE, outputScores);

        // Get Label Corresponding to Maximum Probability Class
        float maxProb = outputScores[0];
        int idx = 0;
        for (int i=1; i<outputScores.length; i++) {
            if (outputScores[i] > maxProb) {
                maxProb = outputScores[i];
                idx = i;
            }
        }
        // Get label for Max Probability Class
        final String result = labels.get(idx);
        // Show result on the screen
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startSpeechButton.setText("Start");
                resultTextView.setText(result);
            }
        });
    }
}

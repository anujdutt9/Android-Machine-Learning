# Android-Machine-Learning

***This repository contains the Machine Learning Model training code as well as the trained model deployment to Android app code. The model deployment is done using TF-Mobile and TF-Lite.***

To load and test the TFLite model locally, use the following code:

```
import numpy as np
import tensorflow as tf

# Load TFLite model and allocate tensors.
interpreter = tf.lite.Interpreter(model_path="converted_model.tflite")
interpreter.allocate_tensors()

# Get input and output tensors.
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Test model on random input data.
input_shape = input_details[0]['shape']
input_data = np.array(np.random.random_sample(input_shape), dtype=np.float32)
interpreter.set_tensor(input_details[0]['index'], input_data)

interpreter.invoke()

# The function `get_tensor()` returns a copy of the tensor data.
# Use `tensor()` in order to get a pointer to the tensor.
output_data = interpreter.get_tensor(output_details[0]['index'])
print(output_data)
```

Source: https://www.tensorflow.org/lite/guide/inference#load_and_run_a_model_in_python

# Requirements

**1.** Tensorflow 1.13

**2.** Jupyter Notebook / PyCharm CE

**3.** Android Studio

**4.** Python 3+

# Project List

|     Name      |                                   Aim                                  |       Status        |
| ------------- | ---------------------------------------------------------------------- | ------------------- |
| Kotlin Basics | Basics of Kotlin language in Android Studio.                           |      Completed      |
|    BasicUI    | Just playing around app to get familiar with UI design in Android.     |      Completed      |
| TensorFlow Basics | Basics of TensorFlow in Python.                                    |      Completed      |
| TensorFlow Estimator API | Basics of TensorFlow Estimator API and creating a custom Estimator API. | Completed |
| Linear-Regression | Linear Regression model in TensorFlow with Android app code.       |      Completed      |
| Handwritten_Digit_Recognition | Linear Regression Model in TensorFlow for MNIST Image classification on Android. | Completed |
| Artistic-Style-Transfer | Artistic Style Transfer on Image on Android.                 |       Completed     |
| Weather-Prediction | Android app with TensorFlow code for making weather predictions.  |       Completed     |

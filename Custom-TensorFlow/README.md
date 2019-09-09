# ML-at-the-Edge

***This folder contains the custom TensorFlow pip wheel to install TensorFlow. This wheel is built from source with custom changes made to the original TensorFlow library Kernels to enable saving the MFCC Ops as a part of the TensorFlow Graph and easy conversion to TFLite models. The default version of TensorFlow available online does not allows saving the MFCC Ops in TFLite model.***

# Getting Started

1. Install Python 3+

2. Install TensorFlow using the whl file provided in this folder as follows:

```
pip3 install tensorflow-1.14.1-cp37-cp37m-macosx_10_7_x86_64.whl
```

# Usage

To test the TensorFlow installation and making sure that the MFCC Ops work with TFLite, run the Jupyter Notebook in the Experiment folder.

```
jupyter notebook Experiments/MFCC-TFLite-Test.ipynb
```

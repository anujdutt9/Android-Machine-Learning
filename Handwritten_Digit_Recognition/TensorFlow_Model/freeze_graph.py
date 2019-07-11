# Readup: https://medium.com/@prasadpal107/saving-freezing-optimizing-for-inference-restoring-of-tensorflow-models-b4146deb21b5

import tensorflow as tf

# Ref: https://www.tensorflow.org/guide/extend/model_files#freezing
# Ref: https://github.com/tensorflow/tensorflow/blob/master/tensorflow/python/tools/optimize_for_inference.py
# optimize_for_inference_lib: contains functions for creating optimized protobuf file
from tensorflow.python.tools import freeze_graph, optimize_for_inference_lib


# freeze_graph: takes a graph definition and a set of checkpoints and freezes them together into a single file
# input_graph: saved graph file, ".pbtxt" only
# input_binary: is input file saved as binary, which is True
# input_checkpoint: saved model checkpoint file, ".ckpt" only
# output_node_name: name of node that gives results during inference, not training.
# output_graph: frozen model file with graph and weights in a single file
freeze_graph.freeze_graph(input_graph='./saved_model/mnist_model.pbtxt',
                          input_saver='',
                          input_binary=True,
                          input_checkpoint='./saved_model/mnist_model.ckpt',
                          output_node_names='y_actual',
                          restore_op_name='save/restore_all',
                          filename_tensor_name='save/Const:0',
                          output_graph='./saved_model/frozen_mnist_model.pb',
                          clear_devices=True,
                          initializer_nodes='',
                          variable_names_blacklist='')

# Input Graph Definition
input_graph_def = tf.GraphDef()

# Optimize Frozen Model for Inference
# Open "frozen_linear_regression.pb" model
with tf.gfile.Open('./saved_model/frozen_mnist_model.pb', 'rb') as f:
    # Read data from frozen model file
    data = f.read()
    # Parse model data from "data"
    input_graph_def.ParseFromString(data)

# Output Graph Definition
# Save the .pb model as optimized model for inference

# Optimizing for inference does the following:
# > Removing operations used only for training like checkpoint saving.
# > Stripping out parts of the graph that are never reached.
# > Removing debug operations like CheckNumerics.

# input_node_names: name of node used as input during inference i.e. "X"
# output_node_names: name of node providing output during inference i.e. "y_actual"
output_graph_def = optimize_for_inference_lib.optimize_for_inference(input_graph_def=input_graph_def,
                                                                     input_node_names=["X"],
                                                                     output_node_names=['y_actual'],
                                                                     placeholder_type_enum=tf.float32.as_datatype_enum)

# FastGFile is same a GFile
# Ref: https://github.com/tensorflow/tensorflow/issues/12663
# Define the name and mode for the optimized frozen model file
file = tf.gfile.FastGFile(name="./saved_model/optimized_frozen_model.pb",
                          mode='w')

# Save the optimized graph def as an optimized frozen model
file.write(file_content=output_graph_def.SerializeToString())

print("Optimized Model Saved at ./saved_model/optimized_frozen_model.pb")


# ------------------------- Frozen Model to TFLite Model ------------------------------
# Convert Frozen Model to TFLite Model
converter = tf.lite.TFLiteConverter.from_frozen_graph(graph_def_file="./saved_model/frozen_mnist_model.pb",
                                                      input_arrays=["X"],
                                                      output_arrays=['y_actual'],
                                                      input_shapes={'X': [None, 784]})

# Quantize Trained Model
converter.post_training_quantize = True

# Convert Frozen Model to TFLite Model
tflite_model = converter.convert()

# Save TFLite Model
open("./saved_model/optimized_frozen_model.tflite", "wb").write(tflite_model)

print("Optimized TFLite Model Saved at ./saved_model/optimized_frozen_model.tflite")
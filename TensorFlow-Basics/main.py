import tensorflow as tf
#tf.enable_eager_execution()

print("TensorFlow Version: ", tf.__version__)

# TensorFlow Constant Node: Values never Change
# value: input data
# dtype: data type of input data
# shape: shape of input data
# name: save each node by its name
# verify_shape: verifies if given shape matches the input data shape
const_1 = tf.constant(value=[[1.0,2.0]],
                      dtype=tf.float32,
                      shape=(1,2),
                      name='const_1',
                      verify_shape=True)

# value: input data
# dtype: data type of input data
# shape: shape of input data
# name: save each node by its name
# verify_shape: verifies if given shape matches the input data shape
const_2 = tf.constant(value=[[3.0,4.0]],
                      dtype=tf.float32,
                      shape=(1,2),
                      name='const_2',
                      verify_shape=True)

# Create a Session
sess = tf.Session()
# Run the node in the session
print(sess.run(fetches=[const_1, const_2]))


# TensorFlow Variable Node: Values can change over time
# initial_value: initial value of variable
# trainable: check if variable is trainable or not. ex. updating weights
# vaidate_shape: verifies if given shape matches the input data shape
# name: save each node by its name
# dtype: data type of input data
# expected_shape: shape of input data
var_1 = tf.Variable(initial_value=[1.0],
                    trainable=True,
                    collections=None,
                    validate_shape=True,
                    caching_device=None,
                    name="var_1",
                    variable_def=None,
                    dtype=tf.float32,
                    expected_shape=(1, None))

# TF Variables Initializer
init = tf.global_variables_initializer()
sess.run(init)
print("var_1 Old Value: ", sess.run(fetches=var_1))

# Assign new value to variable
var_2 = var_1.assign(value=[2.0])
print("var_1 New Value: ", sess.run(fetches=var_2))


# TensorFlow Placeholder Nodes
# Used to assign Inputs for our Computation Graph @ run time
# dtype: type of Input Data
# shape: shape of Input Data @ run time
# name: name of the node in graph
placeholder_1 = tf.placeholder(dtype=tf.float32,
                               shape=(1,2),
                               name="placeholder_1")

# dtype: type of Input Data
# shape: shape of Input Data @ run time
# name: name of the node in graph
placeholder_2 = tf.placeholder(dtype=tf.float32,
                               shape=(1,4),
                               name="placeholder_2")

# Pass in data to placeholders as "array of array"
print(sess.run(fetches=[placeholder_1, placeholder_2], feed_dict={placeholder_1: [[1.0, 2.0]], placeholder_2: [[3.0, 4.0, 8.0, 9.0]]}))


# TensorFlow Operation Nodes
# Used to Build Computational Graph
result_1 = tf.add(const_1, const_2, name="result_1")
print("Addition 1 Result: ", sess.run(fetches=result_1))

result_2 = tf.add(placeholder_1, const_2, name="result_2")
print("Addition 2 Result: ", sess.run(fetches=result_2, feed_dict={placeholder_1: [[5.0, 6.0]]}))


# Linear Regression
# y = Wx + b

# Training Data
X_train = [1.0, 2.0, 3.0, 4.0]

y_train = [-1.0, -2.0, -3.0, -4.0]

# Weight: W
W = tf.Variable(initial_value=[1.0], dtype=tf.float32)

# Bias: b
b = tf.Variable(initial_value=[1.0], dtype=tf.float32)

# Input Features: x
X = tf.placeholder(dtype=tf.float32, name="input_features")
y = tf.placeholder(dtype=tf.float32, name="input_labels")

# Output: y
y_out = tf.add(tf.multiply(x=W, y=X), b)

# Loss Function
loss = tf.reduce_sum(input_tensor=tf.square(x=(y_out - y)))

# Optimizer and Training Step
train_step = tf.train.GradientDescentOptimizer(learning_rate=0.01).minimize(loss=loss)

# Create the Training Session
sess = tf.Session()
sess.run(tf.global_variables_initializer())

print("Loss before Training: ", sess.run(fetches=loss, feed_dict={X: X_train, y: y_train}))

# Epochs
epochs = 1000

for e in range(epochs):
    sess.run(fetches=train_step, feed_dict={X: X_train, y: y_train})

print("Loss after Training: ", sess.run(fetches=[loss, W, b], feed_dict={X: X_train, y: y_train}))

# Testing the Model
print("Input: [5.0, 10.0, 15.0] \tPrediction: {}".format(sess.run(fetches=y_out, feed_dict={X: [5.0, 10.0, 15.0]})))
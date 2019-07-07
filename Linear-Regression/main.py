import tensorflow as tf

# Linear Regression
# y = Wx + b

# Training Data
X_train = [1.0, 2.0, 3.0, 4.0]

y_train = [-1.0, -2.0, -3.0, -4.0]

# Weight: W
W = tf.Variable(initial_value=[1.0], dtype=tf.float32, name="weight")

# Bias: b
b = tf.Variable(initial_value=[1.0], dtype=tf.float32, name="bias")

# Input Features: x
X = tf.placeholder(dtype=tf.float32, name="input_features")
y = tf.placeholder(dtype=tf.float32, name="input_labels")

# Output Node: y_out
# Since, we get output from this for Inference, this is our Output Node in Graph
y_out = tf.add(x=tf.multiply(x=W, y=X, name="multiply"), y=b, name="y_out")

# Loss Function
loss = tf.reduce_sum(input_tensor=tf.square(x=(y_out - y)), name="loss")

# Optimizer and Training Step
optimizer = tf.train.GradientDescentOptimizer(learning_rate=0.01, name="optimizer")
train_step = optimizer.minimize(loss=loss, name="train_step")

# Model Saver
saver = tf.train.Saver()

# Create the Training Session
sess = tf.Session()

# Write Model Graph to a file
# as_text: False, saves model graph as binary file instead of a text file
tf.train.write_graph(graph_or_graph_def=sess.graph_def,
                     logdir='./saved_model/',
                     name="linear_regression.pbtxt",
                     as_text=False)

# Initialize all Variables
sess.run(tf.global_variables_initializer())

print("Loss before Training: ", sess.run(fetches=loss, feed_dict={X: X_train, y: y_train}))

# Epochs
epochs = 1000

# Train the Model
for e in range(epochs):
    sess.run(fetches=train_step, feed_dict={X: X_train, y: y_train})

# Save Trained Model
saver.save(sess=sess,
           save_path='./saved_model/linear_regression.ckpt')

print("Loss after Training: ", sess.run(fetches=[loss, W, b], feed_dict={X: X_train, y: y_train}))

# Testing the Model
print("Input: [5.0, 10.0, 15.0] \tPrediction: {}".format(sess.run(fetches=y_out, feed_dict={X: [5.0, 10.0, 15.0]})))
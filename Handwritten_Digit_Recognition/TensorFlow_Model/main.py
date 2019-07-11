import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data


# Load MNIST Dataset
mnist_data = input_data.read_data_sets(train_dir='./MNIST_Data',
                                       one_hot=True)

# Linear Regression Model
# y= Wx + b
X = tf.placeholder(dtype=tf.float32,
                   shape=[None, 784],
                   name='X')

# Weight
W = tf.Variable(initial_value=tf.zeros(shape=[784,10]),
                name='W')

# Bias
b = tf.Variable(initial_value=tf.zeros(shape=[10]),
                name='b')

# True Labels
y_actual = tf.add(x=tf.matmul(a=X, b=W, name='matmul'),
           y=b,
           name='y_actual')

# Predicted Output
y_expected = tf.placeholder(dtype=tf.float32,
                       shape=[None, 10],
                       name='y_expected')

# Cross Entropy loss
loss = tf.reduce_mean(input_tensor=tf.nn.softmax_cross_entropy_with_logits(labels=y_expected, logits=y_actual),
                      name='loss')

# Optimizer
optimizer = tf.train.AdamOptimizer(learning_rate=0.001,
                                   name='optimizer')

# Training Step
train_step = optimizer.minimize(loss=loss)

# Initialize All Variables
init = tf.global_variables_initializer()

# Model Saver
saver = tf.train.Saver()

# Define the Session
sess = tf.InteractiveSession()
sess.run(init)

# Write Model Graph to a file
# as_text: False, saves model graph as binary file instead of a text file
tf.train.write_graph(graph_or_graph_def=sess.graph_def,
                     logdir='./saved_model/',
                     name="mnist_model.pbtxt",
                     as_text=False)

# Epochs
epochs = 1000

# Train the Model
for e in range(epochs):

    # Divide images and labels into batches
    batch = mnist_data.train.next_batch(batch_size=100)

    # Train the Model
    train_step.run(feed_dict={X: batch[0], y_expected: batch[1]})

# Get number of correct predictions
correct_predictions = tf.equal(x=tf.arg_max(y_actual, 1),
                               y=tf.arg_max(y_expected, 1))

# Accuracy
accuracy = tf.reduce_mean(tf.cast(x=correct_predictions, dtype=tf.float32))

# Validation
print("Test Accuracy: ", accuracy.eval(feed_dict={X: mnist_data.test.images, y_expected: mnist_data.test.labels}))

print("Hyperparameters", sess.run(fetches=y_actual, feed_dict={X: [mnist_data.test.images[0]]}))

# Save Trained Model
saver.save(sess=sess,
           save_path='./saved_model/mnist_model.ckpt')

import tensorflow as tf
from data_retriever import buildDataSubset

# Function to Evaluate the Trained Model Predictions
def evaluateModel(actual, expected):
    correct = 0
    for i in range(len(actual)):
        actual_value = actual[i]
        expected_value = expected[i]
        if (actual_value[0] >= actual_value[1]) and (expected_value[0] >= expected_value[1]):
            correct += 1
        elif (actual_value[0] <= actual_value[1]) and (expected_value[0] <= expected_value[1]):
            correct += 1
    return correct/len(actual)*100

# Shape of Input Data
# [max_temp[i], min_temp[i], mean_temp[i], total_precip[i]]
input_shape = 4

X_train, y_train = buildDataSubset(file_path='Toronto_2019_Weather_Data.csv', start_row=1, num_rows=37)
X_test, y_test = buildDataSubset(file_path='Toronto_2019_Weather_Data.csv', start_row=38, num_rows=7)

# Input Features
X = tf.placeholder(dtype=tf.float32, shape=[None, input_shape], name='input_features')

# Actual Output
y = tf.placeholder(dtype=tf.float32, shape=[None, 2], name='input_labels')

# Weights
W = tf.Variable(initial_value=tf.ones(shape=[input_shape, 2]), name='weight')

# Bias
b = tf.Variable(initial_value=tf.ones(shape=2), name='bias')

# Predicted Output
y_out = tf.add(tf.matmul(a=X, b=W), b, name='y_out')

# Loss Function
loss = tf.reduce_sum(tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y, logits=y_out)), name='loss')

# Optimizer
optimizer = tf.train.AdamOptimizer(learning_rate=0.001, name='optimizer').minimize(loss)

init = tf.global_variables_initializer()

# Model Saver
saver = tf.train.Saver()

sess = tf.Session()

# Write Model Graph to a file
# as_text: False, saves model graph as binary file instead of a text file
tf.train.write_graph(graph_or_graph_def=sess.graph_def,
                     logdir='./saved_model/',
                     name="weather_prediction.pbtxt",
                     as_text=False)

sess.run(init)

# Epochs
epochs = 10000

# Train the Model
for _ in range(epochs):
    sess.run(fetches=optimizer, feed_dict={X: X_train, y: y_train})

print("Training Accuracy: ", evaluateModel(sess.run(fetches=y_out, feed_dict={X: X_train}), y_train))
print("Test Accuracy: ", evaluateModel(sess.run(fetches=y_out, feed_dict={X: X_test}), y_test))

# Save Trained Model
saver.save(sess=sess,
           save_path='./saved_model/weather_prediction.ckpt')
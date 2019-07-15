import pandas as pd

# Function to Read Data from CSV File
def readCSVData(file_path, start_row, num_rows):
    df = pd.read_csv(filepath_or_buffer=file_path,
                sep=',',
                names=['Max temp', 'Min temp', 'Mean temp', 'Total precip'],
                usecols=[5, 7, 9, 19],
                skiprows=start_row,
                nrows=num_rows)
    return df

# Check if next day temp. is higher or lower by calculating the difference
# 1: Increase
# 0: Decrease
def calculateLabels(temps):
    labels = []
    for i in range(len(temps) - 1):
        if (temps[i+1] >= temps[i]):
            labels.append([1, 0])
        else:
            labels.append([0, 1])
    return labels

# Calculate differences between temperatures between today and yesterday
def calculateDifferences(factors):
    differences = []
    for i in range(len(factors) - 1):
        if (factors[i + 1] >= factors[i]):
            differences.append(1)
        else:
            differences.append(0)
    return differences

# Function to build final dataset with OHE values
def buildDataSubset(file_path, start_row, num_rows):
    df = readCSVData(file_path=file_path, start_row=start_row, num_rows=num_rows)

    # Difference in Values between days
    max_temp = calculateDifferences(df['Max temp'])
    min_temp = calculateDifferences(df['Min temp'])
    mean_temp = calculateDifferences(df['Mean temp'])
    total_precip = calculateDifferences(df['Total precip'])
    labels = calculateLabels(df['Mean temp'])

    formatted_data = []
    for i in range(len(max_temp)):
        data_point = [max_temp[i], min_temp[i], mean_temp[i], total_precip[i]]
        formatted_data.append(data_point)

    return formatted_data, labels


# print(buildDataSubset(file_path='Toronto_2019_Weather_Data.csv', start_row=1, num_rows=5))
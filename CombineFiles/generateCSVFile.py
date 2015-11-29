'''
A script that should generate the CSV with the aggregations per array. Effectively finalizes 
the data into a form that R can parse. 

USAGE:
python generateCSVFile.py <input_directory> <output_directory>
'''
import os
import sys
import re
import numpy as np

def get_dataset(data_lines):
    '''
    Generates the pairs for each line of the dataset and then returns a list of those pairs
    as the dataset.

    Keyword Arguments:
    data_lines -- a list of lines in the input file.
    '''
    data_list = []
    for line in data_lines:
        if line.strip():
            pair = get_pair(line.strip())
            data_list.append(pair)
    return data_list

def get_pair(line):
    '''
    Generates a tuple of features and target for a specific line.
    These features will be the aggregated arrays and any of the other single numerical features.
    It should generate 138 features (as long as the aggregation techniques didn't change). The 
    target feature is hotness.

    Keyword Arguments:
    line -- the string to parse
    '''
    feature_array = []
    data = filter(None, line.strip().split('\t'))

    for i in range(6, 14):
        feature_array.extend(get_aggregations(np.fromstring(data[i].strip("[]"), sep=', ')))
    # 2D arrays
    timbre = data[14]
    pitch = data[15]
    # Convert to np.ndarray, first convert ], to ];. Then split on ;.
    # Once split, create 12 arrays for timbre (PCA coefficients )
    timbre_split = re.sub("\],", "\];", timbre).split(';')
    pitch_split = re.sub("\],", "\];", pitch).split(";")
    # Each one represents a PCA coefficient
    for item in timbre_split:
        item = np.fromstring(item.strip(" ").strip("[]"), sep=', ')
        feature_array.extend(get_aggregations(item))
    # Each one represents a note
    for item in pitch_split:
        item = np.fromstring(item.strip(" ").strip("[]"), sep=', ')
        feature_array.extend(get_aggregations(item))
    for i in range(16, 26):
        feature_array.append(float(data[i]))
    hotness = float(data[26]) # Target value

    # Will have 138 features (not including hotness) with an expected value of hotness.
    pair = (feature_array, hotness)
    return pair

def get_aggregations(array):
    '''
    Generates different aggregations for an array.
    Specifically, it generates the median, mean, variance, and range for the array.
    Returns these aggregations as a list.

    Keyword Arguments:
    array -- the data (should be a numpy array)
    '''
    median = np.median(array)
    mean = np.mean(array)
    variance = np.var(array)
    arr_range = np.amax(array) - np.amin(array)

    return median, mean, variance, arr_range

def output_data_csv(data, output):
    '''
    Outputs the dataset to a file in CSV format.

    Keyword Arguments:
    data -- the data that should be outputed (in a tuple format)
    output -- the file to write the data to
    '''
    add_header(output)
    for features, target in data:
        feature_string = construct_feature_string(features)
        output.write(feature_string + str(target) + "\n")

def add_header(output):
    '''
    Creates a header for all the datapoints in the dataset. The data points are hard coded for now.

    Keywords Arguments:
    output -- The file to print the header to.
    '''
    arrays = ["barsStart", "beatsStart", "sectionsStart", "segmentsMaxLoudness", "segmentsMaxLoudnessTime", "segmentsMaxLoudnessStart", "segmentsStart", "tatumsStart", "timbre0", "timbre1", "timebre2", "timbre3", "timbre4", "timbre5", "timbre6", "timbre7", "timbre8", "timbre9", "timbre10", "timbre11", "pitch0", "pitch1", "pitch2", "pitch3", "pitch4", "pitch5", "pitch6", "pitch7", "pitch8", "pitch9", "pitch10", "pitch11"]
    others = ["timeSignature", "songKey", "mode", "startOfFadeOut", "duration", "endOfFadeIn", "danceability", "energy", "loudness", "tempo"]

    for feature in arrays:
        output.write(feature + "Median" + ",")
        output.write(feature + "Mean" + ",")
        output.write(feature + "Variance" + ",")
        output.write(feature + "Range" + ",")

    for feature in others:
        output.write(feature + ",")

    output.write("hotness" + "\n")

def construct_feature_string(features):
    '''
    Creates a string representation of a list with specific formatting.

    Keyword Arguments:
    features -- the list to create a string for
    '''
    string = ""
    for feature in features:
        string += str(feature) + ","

    return string

if __name__ == '__main__':
    # TODO: add more sophisticated program options (use getopt)
    DATA_DIR = os.path.abspath(sys.argv[1])
    OUT_DIR = os.path.abspath(sys.argv[2])
    for inFile in os.listdir(DATA_DIR):
        if "Songs.txt" in inFile:
            newFeatures = []
            lines = open(os.path.join(DATA_DIR, inFile), 'r').readlines()
            country = lines[0].split("\t")[0]

            print "Parsing " + os.path.join(DATA_DIR, inFile)
            dataset = get_dataset(lines)

            output_file = open(os.path.join(OUT_DIR, country+".csv"), 'w')

            output_data_csv(dataset, output_file)

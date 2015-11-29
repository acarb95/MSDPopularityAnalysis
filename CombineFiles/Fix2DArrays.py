'''
Handles all 2D array conversion and transposition. Should be run like a script instead of being
used like an object.

USAGE:
python Fix2DArrays.py <input_directory> <output_directory>

-- or --
python Fix2DArrays.py <input_file> <output_file>
'''
import numpy as np
import re
import sys
import os

def convert_2D_array(array):
    '''
    Takes the 1D form of the 2D array and converts it back into 2D form, represented by
    a numpy array.

    Keyword arguments:
    array -- the array (in string form) to convert.
    '''
    array = array.strip(" ")

    array = re.sub("\[", "", array)
    array = re.sub("\]", "", array)

    data = array.split(", ")

    # Compute the row and column size from the total number.
    # size = row*col, col are fixed at 12 for both pitch and timbre
    size = len(data)
    row_size = size // 12
    col_size = 12

    # Create a blank numpy array with that row and column size
    new_2D_array = np.zeros((row_size, col_size))

    # Add all the points from the 1D form to the 2D form.
    counter = 0
    for row in range(0, row_size):
        for col in range(0, col_size):
            new_2D_array[row][col] = float(data[counter])
            counter += 1

    return new_2D_array

def convert_1D_array(array):
    '''
    Takes a string representation of an array and converts it into a 1D numpy array.

    Keyword Arguments:
    array -- the string representation of the array to convert
    '''
    array = array.strip(" ").strip("[]")
    return np.fromstring(array, sep=', ')

def get_2D_Array(array):
    '''
    Creates a numpy array with an already converted 2D array representation.

    Keyword Arguments:
    array -- a string representation of the array, must be in the form:
            [[0...n], [0...n], ..., [0...n]]
    '''
    new_2D_array = []

    # Split into 1D arrays
    arr_split = re.sub("\],", "\];", array).split(';')

    for item in arr_split:
        # Create 1D numpy array
        item = convert_1D_array(item)
        # Add it to the 2D array list.
        new_2D_array.append(item)

    # Convert to numpy array
    new_2D_array = np.array(new_2D_array)

    return new_2D_array

def convert_file(in_file, out_file):
    '''
    Converts the input file's 2D arrays into numpy ndarrays, then transposes them (switches rows
    and columns) to get the fixed size (12) as the rows instead of the columns. This is useful
    in the aggregation of the arrays.

    Here is where you can switch between converting the arrays, or just transposing them. If the
    array is still in 1D form, then you should use convert_2D_array, it will convert the array
    into 2D form. If the array is already in 2D form, get_2D_array will just return the numpy
    array representation of the string.

    Any method you use will need to return a numpy array because the code uses the numpy array
    transpose method.

    Keyword Arguments:
    in_file -- complete path to the input file
    out_file -- complete path to the output file
    '''
    lines = open(in_file, 'r').readlines()

    with open(out_file, 'w') as writer:
        for line in lines:
            split = line.strip().split("\t")
            for i in range(0, len(split)):
                if i != 1:
                    part = split[i]
                    # The 2D arrays exist at index 14 and 15. All other parts should be ignored
                    # and just written to the file.
                    if i == 14 or i == 15:
                        # Here, you can either use convert_2D_array or get_2D_array.
                        # That depends on whether or not the 2D arrays have already been converte
                        # to 2D form.
                        part = str(convert_2D_array(part).transpose().tolist())
                    writer.write(part+"\t")
            writer.write("\n")

def traverse_directory(in_dir, out_dir):
    '''
    Traverses the given input directory and puts all the output files in to the specified
    output directory.

    Keyword Arguments:
    in_dir -- the full path to the input directory
    out_dir -- the full path to the output directory
    '''
    print "Traversing " + in_dir
    for in_file in os.listdir(in_dir):
        if os.path.isfile(os.path.join(in_dir, in_file)):
            print "Converting " + in_file
            convert_file(os.path.join(in_dir, in_file), os.path.join(out_dir, in_file))

if __name__ == '__main__':
    # sys.argv[0] is the program name
    # TODO: create a more sophisticated program option (use getopt).
    INPUT_DIR = os.path.abspath(sys.argv[1])
    OUTPUT_DIR = os.path.abspath(sys.argv[2])

    # If directory, traverse and convert all files, else treat as one file.
    if os.path.isdir(INPUT_DIR):
        traverse_directory(INPUT_DIR, OUTPUT_DIR)
    else:
        print "Converting " + INPUT_DIR
        convert_file(INPUT_DIR, OUTPUT_DIR)
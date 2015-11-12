#! /bin/python

import os
import sys
import re
import sklearn
import math
import numpy as np
import matplotlib as plt

from itertools import izip
from sklearn import linear_model
from sklearn import metrics
from sklearn import preprocessing
from featureAnalysis import StatsAggregator

aggregator = None

def getDataset(lines, indexes = None):
	data = []
	index = 0
	for line in lines:
		if line.strip():
			pair = getPair(line.strip(), indexes)
			data.append(pair)
		index += 1
	return data

# Indexes and representations

# Identification Data
	# 0 country
	# 1 song_title
	# 2 artist
	# 3 latitude
	# 4 longitude
	# 5 country
# 1D Array
	# 6 barsStart
	# 7 beatsStart
	# 8 sectionsStart
	# 9 segmentsMaxLoudness
	# 10 segmentsMaxLoudnessTime
	# 11 segmentsMaxLoudnessStart
	# 12 segmentsStart
	# 13 tatumsStart
# 2D Array
	# 14 timbre
	# 15 pitch
# Integers
	# 16 timeSignature
	# 17 songKey
	# 18 mode
# Floats
	# 19 startOfFadeOut
	# 20 duration
	# 21 endOfFadeIn
	# 22 danceability
	# 23 energy
	# 24 loudness
	# 25 tempo

def getPair(line, indexes = None):
	featureArray = []
	data = filter(None, line.strip().split('\t'))
	print len(data)
	for i in range(6, 14):
		featureArray.extend(getAggregations(np.fromstring(data[i].strip("[]"), sep=', ')))
	# 2D arrays
	timbre = data[14]
	pitch = data[15]
	# Convert to np.ndarray, first convert ], to ];. Then split on ;.
	# Once split, create 12 arrays for timbre (PCA coefficients )
	timbreSplit = re.sub("\],", "\];", timbre).split(';')
	pitchSplit = re.sub("\],", "\];", pitch).split(";")
	# Each one represents a PCA coefficient
	for item in timbreSplit:
		item = np.fromstring(item.strip(" ").strip("[]"), sep=', ')
		featureArray.extend(getAggregations(item))
	# Each one represents a note
	for item in pitchSplit:
		item = np.fromstring(item.strip(" ").strip("[]"), sep=', ')
		featureArray.extend(getAggregations(item))
	for i in range(16, 26):
		featureArray.append(float(data[i]))
	hotness = float(data[26]) # Target value
	if indexes:
		newFeatureArray = []
		for i in indexes:
			newFeatureArray.append(featureArray[i])
		featureArray = newFeatureArray
	# Will have 138 features (not including hotness) with an expected value of hotness.
	pair = (featureArray, hotness)
	return pair

def getAggregations(array):
	median = np.median(array)
	mean = np.mean(array)
	variance = np.var(array)
	arrRange = np.amax(array) - np.amin(array)
	return median, mean, variance, arrRange

# def normalize2DArray(array):
# 	mean = np.mean(array)
# 	stddev = np.std(array)
# 	overall = []

# 	for i in range(len(array)):
# 		newArray = []
# 		for j in range(len(array[i])):
# 			if stddev == 0:
# 				newArray.append(array[i][j])
# 			else:
# 				newArray.append((array[i][j] - mean)/stddev)
# 		overall.append(newArray)
# 	return overall

# def normalize1DArray(array):
# 	mean = np.mean(array)
# 	stddev = np.std(array)
# 	overall = []
# 	for i in range(len(array)):
# 		if stddev == 0:
# 			overall.append(array[i])
# 		else:
# 			overall.append((array[i] - mean)/stddev)
# 	return overall

# def generateRegression(datas, solver='auto', normalize=True, alpha=1.0):
# 	# Test on small test case to see how to use --> ideally you have test, train sets and then the transformed one to compare with
# 	data = list(datas)
# 	eighty = int(math.floor(len(data)*.8))
# 	if eighty != 0:
# 		train = data[:eighty]
# 		test = data[eighty:]
# 	else:
# 		train = data
# 		test = data
# 	trainx, trainy = map(list, zip(*train))
# 	testx, testy = map(list, zip(*test))

# 	trainx = preprocessing.scale(trainx)
# 	testx = preprocessing.scale(testx)

# 	if normalize:
# 		trainMean = np.mean(trainy)
# 		trainStdDev = np.std(trainy)
# 		testMean = np.mean(testy)
# 		testStdDev = np.std(testy)
# 		trainy = normalize1DArray(trainy)
# 		testy = normalize1DArray(testy)

# 	aggregator.addStats(trainx, "trainx")
# 	aggregator.addStats(testx, "testx")
# 	aggregator.addStats(trainy, "trainy")
# 	aggregator.addStats(testy, "testy")
# 	aggregator.outputStats()

# 	clf = linear_model.Ridge(solver=solver, alpha=alpha)

# 	clf.fit(trainx, trainy)

# 	coefficients = clf.coef_

# 	n = len(data)
# 	m = len(trainx[0])

# 	results = (coefficients, getMetrics(clf, testx, testy, n, m))

# 	return results

# def generateGraph(alpha, dataxN, datayN, dataySD, datayMean):
# 	clf = linear_model.Ridge(alpha=alpha)
# 	fig, ax = plt.subplots()
# 	predicted = cross_val_predict(clf, dataxN, datayN, cv=10)
# 	predicted = denormalize(predicted, dataySD, datayMean)
# 	ax.scatter(datay, predicted)
# 	ax.plot([min(datay), max(datay)], [min(datay), max(datay)], 'k--', lw=4)
# 	ax.set_xlabel('Measured')
# 	ax.set_ylabel('Predicted')
# 	ax.set_title(alpha)
# 	fig.show()


# def denormalize(array, stddev, mean):
# 	newArray = []
# 	for i in range(len(array)):
# 		if stddev != 0:
# 			newArray.append(array[i]*stddev + mean)
# 		else:
# 			newArray.append(array[i])
# 	return newArray

# def getMetrics(clf, testx, testy, n, m):
# 	prediction = clf.predict(testx)
# 	#import pdb;pdb.set_trace()
# 	explainedVariance = metrics.explained_variance_score(testy, prediction)
# 	MAE = metrics.mean_absolute_error(testy, prediction)
# 	MSE = metrics.mean_squared_error(testy, prediction)
# 	rMSE = math.sqrt(MSE)
# 	MedianAE = metrics.median_absolute_error(testy, prediction)
# 	r2_score = metrics.r2_score(testy, prediction)
# 	if (n-m-1) == 0:
# 		aR2_score = "NaN"
# 	else:
# 		aR2_score = 1 - (1 - r2_score)* ((n-1)/(n-m-1))
# 	return (explainedVariance, MAE, MSE, rMSE, MedianAE, r2_score, aR2_score)

# def getCoefficients(coefficients):
# 	newFeatures = []

# 	i = 4
# 	while i <= 128:
# 		newFeatures.append(indexOfBest(coefficients, i - 4, i))
# 		i += 4

# 	return newFeatures

# def indexOfBest(coef, start, end):
# 	return np.where(coef == max(coef[start:end]))[0][0]

# def outputToFile(outFile, normalize, alpha, status, scores):
# 	nHeader = "Not Normalized"

# 	if normalize:
# 		nHeader = "Normalized"

# 	with open(outFile, 'a') as output:
# 		output.write("(" + str(alpha) + ", " + status + ", " + nHeader + "): \n")
# 		output.write("   EV: " + str(scores[0]) + "\n")
# 		output.write("   MAE: " + str(scores[1]) + "\n")
# 		output.write("   MSE: " + str(scores[2]) + "\n")
# 		output.write("   RMSE: " + str(scores[3]) + "\n")
# 		output.write("   MedAE: " + str(scores[4]) + "\n")
# 		output.write("   R^2: " + str(scores[5]) + "\n")
# 		output.write("   aR^2: " + str(scores[6]) + "\n")

# def writeOutput(outFile, data, country, normalize):
# 	global aggregator

# 	fileName = ""

# 	if normalize:
# 		print "Normalized: "
# 		fileName = "Normalized"

# 	for i in np.arange(0, 1.1, 0.1):
# 		print "\tChecking alpha of " + str(i)
# 		print "\t\tGetting full coefficients..."
# 		aggregator = StatsAggregator(country, True, normalize)
# 		coefficients, scores = generateRegression(data, solver='auto', alpha=i, normalize=normalize)
# 		outputToFile(outFile, normalize, i, "Full", scores)

# 		print "\t\tGetting partial coefficients..."
# 		newFeatures = getCoefficients(coefficients)
# 		newData = getDataset(lines, newFeatures)
# 		aggregator = StatsAggregator(country, False, normalize)
# 		coefficients, scores = generateRegression(newData, solver='auto', alpha = i, normalize=normalize)
# 		outputToFile(outFile, normalize, i, "Partial", scores)

# 		aggregator = None

def outputDataCSV(data, output):
	addHeader(output)
	for features, target in data:
		featureString = constructFeatureString(features)
		output.write(featureString + str(target) + "\n")

def addHeader(output):
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

def constructFeatureString(features):
	string = ""
	for feature in features:
		string += str(feature) + ","

	return string

if __name__ == '__main__':
	dataDir = os.path.abspath(sys.argv[1])
	outDir = os.path.abspath(sys.argv[2])
	for inFile in os.listdir(dataDir):
		if "Songs.txt" in inFile:
			newFeatures = []
			lines = open(os.path.join(dataDir, inFile), 'r').readlines()
			country = lines[0].split("\t")[0]

			#outFile = os.path.join(outDir, country + 'RegressionAnalysis.yaml')

			#if os.path.exists(outFile):
				#os.remove(outFile)

			print "Parsing " + os.path.join(dataDir, inFile)
			data = getDataset(lines)

			output = open(country+".csv", 'w')

			outputDataCSV(data, output)

			#print "Generating " + outFile
			#writeOutput(outFile, data, country, True)
			#writeOutput(outFile, data, country, False)

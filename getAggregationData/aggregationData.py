#! /bin/python 

import os
import re
import sklearn
import numpy as np

from itertools import izip
from mrjob.job import MRJob
from mrjob.protocol import RawProtocol, ReprProtocol
from sklearn import linear_model

class NgramNeighbors(MRJob):
	OUTPUT_PROTOCOL=RawProtocol

	# def mapper_init(self):

	def mapper(self, key, line):
		featureArray = []
		data = line.split('\t')

		country = data[0]
		song_title = data[1]
		artist = data[2]
		latitude = data[3]
		longitude = data[4]
		# Second country variable at index 5

		# 1D arrays --> get numpy version np.fromstring(barsStart.strip("[]"),  sep=', ')
		featureArray.extend(getAggregations(np.fromstring(data[6].strip("[]"),  sep=', '))) #barsStart
		featureArray.extend(getAggregations(np.fromstring(data[7].strip("[]"),  sep=', '))) #beatsStart
		featureArray.extend(getAggregations(np.fromstring(data[8].strip("[]"),  sep=', '))) #sectionsStart
		featureArray.extend(getAggregations(np.fromstring(data[9].strip("[]"),  sep=', '))) #segmentsMaxLoudness
		featureArray.extend(getAggregations(np.fromstring(data[10].strip("[]"),  sep=', '))) #segmentsMaxLoudnessTime
		featureArray.extend(getAggregations(np.fromstring(data[11].strip("[]"),  sep=', '))) #segmentsMaxLoudnessStart
		featureArray.extend(getAggregations(np.fromstring(data[12].strip("[]"),  sep=', '))) #segmentsStart
		featureArray.extend(getAggregations(np.fromstring(data[13].strip("[]"),  sep=', '))) #tatumsStart

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

		# Integers
		featureArray.append(data[16]) #timeSignature
		featureArray.append(data[17]) #songKey
		featureArray.append(data[18]) #mode

		# Floats
		featureArray.append(data[19]) #startOfFadeOut
		featureArray.append(data[20]) #duration
		featureArray.append(data[21]) #endOfFadeIn
		featureArray.append(data[22]) #danceability
		featureArray.append(data[23]) #energy
		featureArray.append(data[24]) #loudness
		featureArray.append(data[25]) #tempo

		hotness = data[26] # Target value

		# Will have 42 features (not including hotness) with an expected value of hotness. 
		
		pair = (featureArray, hotness)

		yield(country, pair)

	def getAggregations(array):
		median = np.median(array)
		mean = np.mean(array)
		variance = np.var(array)
		arrRange = np.amax(array) - np.amin(array)

		return median, mean, variance, arrRange

	def reducer(self, key, datas):
		# Test on small test case to see how to use --> ideally you have test, train sets and then the transformed one to compare with
		half = len(datas)//2
		train = datas[:half]
		test = data[half+1:]
		trainx, trainy = map(list, zip(*train))
		testx, testy = map(list, zip(*test))
		
		clf = linear_model.SGDRegressor()

		clf.fit_transform(trainx, trainy)

		clf.transform(trainx)

		coefficients = clf.coef_

		score = clf.score(testx, testy)

		results = (train, test, coefficients, score)

		yield key, str(results)

if __name__ == '__main__':
	NgramNeighbors.run()
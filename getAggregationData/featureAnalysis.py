import os
import sys
import re
import sklearn
import math
import numpy as np
np.set_printoptions(threshold=np.nan)

from itertools import izip

# np.corrcoef for features. 

# Must normalize features first, then correlate

class StatsAggregator():

	def __init__(self, country, full, normalized):
		self.stats = {}
		self.country = country
		self.full = full
		self.normalized = normalized

	def addStats(self, data, name):
		if name == 'trainx':
			#self.generateCorrelation(data)
			self.getNumberOfSongs(data)
			self.getNumberOfFeatures(data)
		self.generateRange(data, name)
	
	def generateRange(self, data, name):
		rangedata = (np.min(data), np.max(data))
		
		self.stats[name + " Range"] = rangedata

	def generateCorrelation(self, data):
		self.stats["Feature Correlation"] = np.corrcoef(data, rowvar=0)

	def getNumberOfSongs(self, data):
		self.stats["Number of Songs"] = len(data)

	def getNumberOfFeatures(self, data):
		self.stats["Number of Features"] = len(data[0])

	def outputStats(self):
		with open(os.path.join(os.path.abspath("./"), self.country+"FeatureAnalysis.yaml"), 'a') as output:
			output.write("(" + str(self.full) + ", " + str(self.normalized) + "):\n")
			for key in self.stats.keys():
				output.write("   " + key + ": " + str(self.stats[key]) + "\n")

if __name__ == '__main__':
	print "I should generate (per song and full dataset): "
	print "\tCorrelation/Covariance data for the coefficients (non-partial) used."
	print "\tRanges for the normalized data."
	print "\tNumber of songs per country."
	print "\tGraphs for all gathered data:"
	print "\t\tBox plots"
	print "\t\tCorrelation plots"
	print "\t\tDistribution graphs"
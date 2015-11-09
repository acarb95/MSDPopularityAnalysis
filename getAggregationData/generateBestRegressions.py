import os
import sys
import yaml

metricDict = {}
summaryDict = {}

def traverseDirectory(inDir, outDir):
	print "Traversing " + inDir
	for inFile in os.listdir(inDir):
		if os.path.isfile(os.path.join(inDir, inFile)):
			print "Parsing " + inFile
			country = inFile[:2]
			output = parseFile(os.path.join(inDir, inFile))
			addToSummaryDict(country, output)

			outFile = os.path.join(outDir, country + "Results.yaml")
			print "Generating " + outFile
			writeResult(outFile, output)
	print "Summary:"
	for k, v in summaryDict.iteritems():
		print "\t" + k + ": " + str(len(v))

	print "\tNegative R^2 Countries: " + str(summaryDict['Negative R^2'])

def parseFile(inFile):
	if os.path.splitext(inFile)[1] == '.yaml':
		dictionary = yaml.load(open(inFile, 'r'))
		for key, value in dictionary.iteritems():
			for k, v in value.iteritems():
				insertIntoDict(k, v, key)
		result = getBestResults()
		metricDict.clear()
		return result
	else:
		print "Error: file given is not a yaml."
		print "File given: " + inFile

def insertIntoDict(metric, value, combo):
	if not metric in metricDict:
		metricDict[metric] = {}
	metricDict[metric][value] = combo

def getBestResults():
	results = {}
	for metric in metricDict:
		values = metricDict[metric]
		comparer = min
		if 'R^2' in metric or metric == 'EV':
			comparer = max
		results[metric] = getBest(metric, values, comparer)

	return results

def getBest(metric, values, comparer):
	bestVal = comparer(values.keys())
	combo = metricDict[metric][bestVal]
	return bestVal, combo

def writeResult(outFile, results):
	with open(outFile, 'w') as output:
		for k, v in results.iteritems():
			output.write(k + ": \n")
			output.write("   Value: " + str(v[0]) + "\n")
			output.write("   Combo: " + str(v[1]) + "\n")

def addToSummaryDict(country, result):
	country = country.strip()
	if result['R^2'][0] >= 0:
		if not 'Positive R^2' in summaryDict:
			summaryDict['Positive R^2'] = []
		summaryDict['Positive R^2'].append(country)
	else:
		if not 'Negative R^2' in summaryDict:
			summaryDict['Negative R^2'] = []
		summaryDict['Negative R^2'].append(country)

if __name__ == '__main__':
	inDir = os.path.abspath(sys.argv[1])
	outDir = os.path.abspath(sys.argv[2])

	traverseDirectory(inDir, outDir)
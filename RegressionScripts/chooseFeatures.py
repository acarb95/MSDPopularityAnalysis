import os
import sys

def getFeatures(lines):
	features = {}
	newFeatures = []
	for line in lines:
		feature, weight = line.split()
		features[feature] = weight

	inv_features = dict(zip(features.values(), features.keys()))

	arrays = ["barsStart", "beatsStart", "sectionsStart", "segmentsMaxLoudness", "segmentsMaxLoudnessTime", "segmentsMaxLoudnessStart", "segmentsStart", "tatumsStart"]

	for i in range(12):
		arrays.append("pitch" + str(i))
		arrays.append("timbre" + str(i))

	for item in arrays:
		try:
			newFeatureValue = max(features[item + "Median"], features[item + "Mean"], features[item + "Variance"], features[item + "Range"])
			newFeature = inv_features[newFeatureValue]
			newFeatures.append(newFeature)
		except Exception:
			pass

	other_features = ["timeSignature", "songKey", "mode", "startOfFadeOut", "duration", "endOfFadeIn", "loudness", "tempo"]

	for feature in other_features:
		newFeatures.append(feature)

	return newFeatures

def outputFeatures(newFeatures, output):
	for feature in newFeatures:
		output.write(feature + "\n")

if __name__ == '__main__':
	dataDir = os.path.abspath(sys.argv[1])
	outDir = os.path.abspath(sys.argv[2])

	for inFile in os.listdir(dataDir):
		lines = open(os.path.join(dataDir, inFile), 'r').readlines()
		country = os.path.splitext(inFile)[0]

		outFile = country + "NewFeatures.txt"

		print "Generating output for " + outFile
		output = open(os.path.join(outDir, outFile), 'w')

		newFeatures = getFeatures(lines[1:])

		outputFeatures(newFeatures, output)

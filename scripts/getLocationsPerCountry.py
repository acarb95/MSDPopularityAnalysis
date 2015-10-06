import os
import sys

if __name__ == '__main__':
	countryCount = {}
	with open(os.path.abspath(sys.argv[1]), 'r') as inputFile:
		lines = inputFile.readlines()

	for line in lines:
		country = line.split("=")[1]
		if country in countryCount:
			countryCount[country] += 1
		else:
			countryCount[country] = 1

	with open(os.path.abspath(sys.argv[2]), 'w') as outputFile:
		for country, count in sorted(countryCount.items()):
			outputFile.write(country.strip().rstrip() + ": " + str(count) + "\n")

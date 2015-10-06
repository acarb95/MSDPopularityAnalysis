import itertools
import sys
import os

if __name__ == '__main__':
	n = int(sys.argv[2])
	r = int(sys.argv[1])

	with open(os.path.abspath("permutations" + str(n) + ".txt"), 'w') as outputFile:
		for p in itertools.product(range(0, n), repeat=r):
			outputFile.write("".join(map(str, p)) + "\n")
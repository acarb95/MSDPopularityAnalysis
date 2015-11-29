# Library imports
library(FSelector)
library(tools)

args <- commandArgs(trailingOnly = TRUE)
countryFile <- args[1]

countryCodeList <- readLines(countryFile)

for (i in 1:length(countryCodeList)) {
	country <- countryCodeList[i]
	files <- paste("./testOutput", paste(country, ".csv", sep=""), sep="/")
	dat <- read.csv(files, header=TRUE)
	attach(dat)

	scaled.dat <- data.frame(lapply(dat, function(x) scale(x)))
	scaled.dat <- scaled.dat[, colSums(is.na(scaled.dat)) < nrow(scaled.dat)]

	if (ncol(scaled.dat) == 0) stop("Only one data point")

	scaled.weights <- linear.correlation(hotness ~ ., data=scaled.dat)

	output <- paste(country, "weights.txt", sep="")

	print(paste("Generating ", output))

	sink(output)
	scaled.weights
	sink()

	detach(dat)
}

# Library imports
library(MASS)
library(mlbench)
library(FSelector)
library(tools)

args <- commandArgs(trailingOnly = TRUE)
countryFile <- args[1]

res <- readLines(countryFile)

for (i in 1:length(res)) {
	country <- res[i]
	files <- paste("./testOutput", paste(country, ".csv", sep=""), sep="/")

	print(paste("Generating summary for ", files))

	dat <- read.csv(files, header=TRUE)
	attach(dat)

	if (!exists("f")) f <- as.simple.formula(".", "hotness")

	scaled.dat <- data.frame(lapply(dat, function(x) scale(x)))
	scaled.dat <- scaled.dat[, colSums(is.na(scaled.dat)) < nrow(scaled.dat)]

	if (ncol(scaled.dat) == 0) scaled.dat <- dat

	fit <- lm(f, data=scaled.dat)

	# Try Catch
	result2 <- tryCatch({
		finalStep <- stepAIC(fit, direction="both", trace=FALSE)
	}, warning = function(war) {
		finalStep <- fit
	}, error = function(error) {
		finalStep <- fit
	})

	if (!exists("finalStep")) finalStep <- result2

	assign(country, rstudent(finalStep))

	output <- paste("./summaries", paste(country, "Summary.txt", sep=""), sep="/")

	sink(output)
	summary(finalStep)
	sink()
}

dataList <- lapply(res, get, envir=environment())
names(dataList) <- res

print(dataList)

boxplot(dataList)
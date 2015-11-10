# Library imports
library(MASS)
library(mlbench)
library(FSelector)
library(tools)

args <- commandArgs(trailingOnly = TRUE)
countryFile <- args[1]

res <- readLines(countryFile)

countries <- list()

for (i in 1:length(res)) {
	country <- res[i]
	files <- paste("./testOutput", paste(country, ".csv", sep=""), sep="/")

	print(paste("Generating summary for ", files))

	dat <- read.csv(files, header=TRUE)
	attach(dat)

	result1 <- tryCatch({
		newWeights <- readLines(paste("./newWeights", paste(country, "weightsNewFeatures.txt", sep=""), sep="/"))
		f <- as.simple.formula(newWeights, "hotness")
	}, warning = function(war) {
		f <- as.simple.formula(".", "hotness")
	}, error = function(error) {
		f <- as.simple.formula(".", "hotness")
	})

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

	print(finalStep$residuals)

	countries[[i]] <- finalStep$residuals

	#output <- paste("./summaries", paste(country, "Summary.txt", sep=""), sep="/")

	#sink(output)
	#summary(finalStep)
	#sink()
	detach(dat)
}

names(countries) <- res

print(countries)

boxplot(countries)

# Library imports
library(MASS)
library(FSelector)


SUMMARIES <- TRUE
RESIDUALS <- TRUE
ALLRESIDUALS <- TRUE

args <- commandArgs(trailingOnly = TRUE)
countryFile <- args[1]
countryNameFile <- args[2]

countryCodeList <- readLines(countryFile)
countryNameList <- readLines(countryNameFile)

countries <- list()
countryNames <- vector()
count <- 1

for (i in 1:length(countryCodeList)) {
	country <- countryCodeList[i]
	cName <- countryNameList[i]

	finalStep <- NULL
	f <- NULL
	files <- paste("./testOutput", paste(country, ".csv", sep=""), sep="/")

	print(paste("Generating Regression for ", files))

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

	if (!exists("f") | is.null(f)) f <- as.simple.formula(".", "hotness")

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

	if (!exists("finalStep") | is.null(finalStep)) finalStep <- result2

	#print(finalStep$residuals)
	nonZero <- FALSE

	for (i in 1:length(finalStep$residuals)) {
		if (finalStep$residuals[i] != 0){
			nonZero <- TRUE
		}
	}

	if (nonZero) {
		countries[[count]] <- finalStep$residuals
		countryNames[[count]] <- country
		count <- count+1
	}

	if (SUMMARIES) {
    	output <- paste("./summaries", paste(country, "Summary.txt", sep=""),     sep="/")
		print(paste("Printing summary to", output))

		sink(output)
		print(summary(finalStep))
		sink()
	}

	if (RESIDUALS) {
		print("Creating residuals plot and saving")
		title <- paste(paste(cName, paste(paste("(", country, sep=""), ")", sep="")), "Residual Plot")
		print(title)
		pdf(paste("./residuals", paste(country, "Residuals.pdf", sep=""), sep="/"), width = 11, height = 8.5)
		plot(predict(finalStep),finalStep$residuals,main=title,xlab="Y-hat",ylab="Studentized Residuals")
		abline(h=0,lty=2)
		lines(supsmu(predict(finalStep),finalStep$residuals),col=2)
		dev.off()
	}

	detach(dat)
}

names(countries) <- countryNames

#print(countries)

if (ALLRESIDUALS) {
	print("Creating overall residuals comparison plot")
	pdf("CountryResiduals.pdf", width = 11, height = 8.5)
	boxplot(countries, main="Country Residual Comparison Plot",xlab="Country",ylab="Studentized Residuals")
	dev.off()
}

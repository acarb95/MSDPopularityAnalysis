# Library imports
library(MASS)
library(mlbench)
library(FSelector)
library(tools)

args <- commandArgs(trailingOnly = TRUE)
files <- args[1]

dat <- read.csv(files, header=TRUE)
attach(dat)

scaled.dat <- data.frame(lapply(dat, function(x) scale(x)))
scaled.dat <- scaled.dat[, colSums(is.na(scaled.dat)) < nrow(scaled.dat)]

if (ncol(scaled.dat) == 0) stop("Only one data point")

scaled.weights <- linear.correlation(hotness ~ ., data=scaled.dat)

output <- paste(basename(file_path_sans_ext(files)), "weights.txt", sep="")

print(paste("Generating ", output))

sink(output)
scaled.weights
sink()


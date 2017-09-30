# MSDPopularityAnalysis
Hit Song Science is an interdisiplinary study of predicting song popularity. Previous work has used a variety of features, datasets, and labels for popularity as well as different machine learning techniques. I wanted to explore Hit Song Science in a broader view: worldwide. Using the Million Song Dataset, I investigated the correlation between a song's popularity and its features with a secondary goal of attempting to pick out specific song features a country prefers.

## Methodology
There are three stages to the analysis:
1. Generation of the top 100 songs per country
2. Preprocessing the data
3. Generation of the regression data

A mix of bash scripting, python scripting, R, and MapReduce programs were used throughout the methodology.

### Generation of top 100 songs
Ideally, the analysis would run on all songs per country, but during the final stages of reduce, the tasks would run out of heap space. Therefore, the dataset needed to be reduced significantly. The first MapReduce program (GetTop100PerCountry) sorted all the songs and outputed the top 100 songs' identifying data (Title, Artist, Latitude, Longitude, Country). The second MapReduce program loaded the song list into a HashSet and matches the song features to the songs in the HashSet (Get2DArraysPerSong).

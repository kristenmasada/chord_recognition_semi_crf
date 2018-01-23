# ChordRecognition
#### Kristen Masada and Razvan Bunescu

This repository contains code to run our semi-CRF system on the BaCh dataset using Radicioni and Esposito's original 10 folds.

# Instructions:
1. Generate fold files: `cd fold_gen`, then `python3 fold_gen.py <path to ChordRecognition directory>` (for example, `python3 fold_gen.py /home/km942412/ChordRecognition/`)
2. Run folds: `cd ..`, then `./run-folds.sh <starting fold number> <ending fold number> <number of folds running at once> breve_chords_in_dataset.txt &`. For example, if I want to run folds 1 through 10, with 3 folds running at a time, using all of the chords that appear in the BaCh dataset as possible labels, `./run-folds.sh 1 10 3 breve_chords_in_dataset.txt &`. This will generate feature_count files (which contain the name of each feature and a count of how many times it appears in the training data), output-c files (initial output files generated when counting the number of features in the training data), and output-e files (the output files that will contain the test results for each fold). It will also generate model.txt files (which contain the weights learned for each feature), .model files (the saved model trained on a given fold set), and .log files (which contain general output information similar to the output-e files).

# Running our system on another dataset:
* Make sure to change the capacity size of the network graph in WordWeakSemiCRFNetworkCompiler.java on line 39, as this graph is affected by the length of the training songs. I usually determine this number by trial and error. The size that it is set to right now works well for most songs of medium length. 
* If the system is running too slowly, this might be caused by the semi-CRF using a maximum segment length that is too long. Try making maxSegmentLength a smaller number (line 87 of CRMain.java).
* Also make sure that the labels in your dataset use the same kind of labels mentioned in our paper (3 possible modes, 4 possible added notes, and 12 possible root notes). Our system can be modified to use other chord labels, but this will also affect how the features are encoded.

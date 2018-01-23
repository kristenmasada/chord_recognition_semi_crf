# creates 10 folds for training/testing from 60 Bach chorales

import sys

# open file with names of Bach chorale files
file = 'all_songs.txt'		  # our folds = "our_folds/all_songs.txt"
                                  # their folds = "breve_folds/all_songs.txt"
folds_folder = '../folds'	  # output folder for folds (e.g. 'our_folds', 'breve_folds')
datapath = sys.argv[1]		  # datapath to ChordRecognition repo
# datapath = '/Users/kristenmasada/Documents/ChordRecognition/source/masada/fold_gen/'
with open(file, 'r') as chorales:
  chorale_names = chorales.read().splitlines()
  chorale_names = [c + "annotated_events.xml" for c in chorale_names]

  # create 10 folds
  for i in range(0, 60, 6):

    # create training file
    with open(folds_folder + '/train' + str(int((i / 6) + 1)) + '.txt', 'w') as training_fold:
      for chorale_name in (chorale_names[0:i] + chorale_names[i+6:60]):
        chorale_name_with_path = datapath + 'bach/' + chorale_name
        training_fold.write("%s\n" % chorale_name_with_path)

    # create testing file
    with open(folds_folder + '/test' + str(int((i / 6) + 1)) + '.txt', 'w') as testing_fold:
      for chorale_name in chorale_names[i:i+6]:
        chorale_name_with_path = datapath + 'bach/' + chorale_name
        testing_fold.write("%s\n" % chorale_name_with_path)  

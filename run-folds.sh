#!/bin/bash

# first argument - number of folds
# second argument - number of folds per iteration
# third argument - starting fold
# fourth argument - chords path

JAVA=java
CLASSPATH=bin:lib/statnlp-core-2015.1-SNAPSHOT.jar:lib/xom-1.2.10.jar:lib/commons-lang3-3.5.jar
CURRENTDIRECTORYPATH=${PWD##*/}/
FOLDSPATH=folds/
STARTINGFOLD=$1
NUMFOLDS=$2
NUMFOLDSPERIT=$3
CHORDSPATH=$4

# count features
for fold in $(seq $STARTINGFOLD $NUMFOLDS);
do
	echo $fold
	nice $JAVA -Xmx250000m -classpath $CLASSPATH cr.CRMain -currentDirectoryPath $CURRENTDIRECTORYPATH -foldsPath $FOLDSPATH -foldNum $fold -simplify generic_added_notes -writeModelText -normalizeEnharmonics -countFeatures -useAllChords -useAllChordsPath $CHORDSPATH &> output-c-${fold}.txt
done

# run folds
for fold in $(seq $STARTINGFOLD $NUMFOLDS);
do
	echo "Started fold $fold"
	nice $JAVA -Xmx250000m -classpath $CLASSPATH cr.CRMain -currentDirectoryPath $CURRENTDIRECTORYPATH -foldsPath $FOLDSPATH -foldNum $fold -simplify generic_added_notes -writeModelText -normalizeEnharmonics -useAllChords -useAllChordsPath $CHORDSPATH &> output-e-${fold}.txt &
	echo "$fold % $NUMFOLDSPERIT" | bc
	if [ `echo "$fold % $NUMFOLDSPERIT" | bc` -eq 0 ]
	then
		wait
		echo "Fold set finished"
	fi
done

echo "$NUMFOLDS % $NUMFOLDSPERIT" | bc
if [ `echo "$NUMFOLDS % $NUMFOLDSPERIT" | bc` -ne 0 ]
then
	wait
	echo "Fold set finished"
fi

exit 0



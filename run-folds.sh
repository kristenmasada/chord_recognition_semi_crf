#!/bin/bash 
# first argument - number of fold sets 
# second argument - number of folds per set 
# third argument - number of folds running simultaneously 
# fourth argument - chords path

JAVA=java
CLASSPATH=bin:lib/statnlp-core-2015.1-SNAPSHOT.jar:lib/xom-1.2.10.jar:lib/commons-lang3-3.5.jar
FOLDSPATH=folds/
STARTFOLD=$1
ENDFOLD=$2
NUMFOLDSPERIT=$3
CHORDSPATH=$4

# count features
for fold in $(seq $STARTFOLD $ENDFOLD);
do
	echo $fold
	nice $JAVA -Xmx250000m -classpath $CLASSPATH cr.CRMain -foldsPath $FOLDSPATH -foldNum $fold -simplify generic_added_notes -writeModelText -normalizeEnharmonics -countFeatures -useAllChords -useAllChordsPath $CHORDSPATH &> output-c-${fold}.txt
done

# run folds
for fold in $(seq $STARTFOLD $ENDFOLD);
do
	echo "started fold $fold"
	nice $JAVA -Xmx250000m -classpath $CLASSPATH cr.CRMain -foldsPath $FOLDSPATH -foldNum $fold -simplify generic_added_notes -writeModelText -normalizeEnharmonics -useAllChords -useAllChordsPath $CHORDSPATH &> output-e-${fold}.txt &
	echo "$fold % $NUMFOLDSPERIT" | bc
	if [ `echo "$fold % $NUMFOLDSPERIT" | bc` -eq 0 ]
	then
		wait
		echo "Fold set finished"
	fi
done

if [ `echo "$NUMFOLDSPERSET % $NUMFOLDSPERIT" | bc` -ne 0 ]
then
	wait
	echo "Fold set finished"
fi

exit 0



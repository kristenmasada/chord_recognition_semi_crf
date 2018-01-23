package cr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static cr.SongUtil.print;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import com.statnlp.commons.types.Instance;
import com.statnlp.hybridnetworks.DiscriminativeNetworkModel;
import com.statnlp.hybridnetworks.FeatureArray;
import com.statnlp.hybridnetworks.GenerativeNetworkModel;
import com.statnlp.hybridnetworks.NetworkConfig;

import com.statnlp.hybridnetworks.FeatureManager;
import com.statnlp.hybridnetworks.GlobalNetworkParam;
import com.statnlp.hybridnetworks.LocalNetworkParam;
import com.statnlp.hybridnetworks.Network;
import com.statnlp.hybridnetworks.NetworkCompiler;
import com.statnlp.hybridnetworks.NetworkModel;


public class CRMain {
	
	public enum Simplify {
		MODES,					// simplify chord labels to major/minor/diminished/augmented triads
		GENERIC_ADDED_NOTES, 	// include added notes in chord labels (4ths, 6ths, 7ths) + aug6 chords
		ADDED_NOTES,			// include added notes in chord labels (4ths, 6ths, 7ths) + additional
								// modes for 7ths (dom [i.e. 7], min-maj, hdim)
		NONE					// include everything in chord label (modes, added notes, inversions, 
								// missing notes)
		;
		
		public static String helpString() {
			return "Please specify the simplification from the following choices:\n"
					+ "\t-MODES\n"
					+ "\t-NONE\n";
		}
	}
	
	public static Simplify simplification = Simplify.valueOf("NONE");		// simplification of chord labels used for training/testing
																			// (see Simplify enum defined on line 19)

	public static void main(String[] args) throws IllegalArgumentException, InterruptedException, IOException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, SecurityException {
//		System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt"))));	// redirect System.out output to "outOutput.txt"
		String dataPath = null;					// prefix for training/testing files
		String foldsPath = null;
		String currentDirectoryPath = null;
		String trainFile = "";
		String testFile = "";
		String featureCountFile = "";
		String trainFilePath = null;			// filename for training data
		String testFilePath = null;			// filename for testing data
		String result_filename = null;			// filename to store results
		String timestamp = Calendar.getInstance().getTime().toString();
		String modelFile = "";
		String modelPath = null;
		String featureCountPath = null;
		String useAllChordsPath = "all_chords.txt";
		String logFile = "";
		String logPath = null;
		Song[] trainInstances = null;			// training data instances
		Song[] testInstances = null;			// testing data instances
		boolean findMaxSegmentLength = false;	// find maximum segment length?
		boolean writeModelText = false;
		boolean countFeatures = false;
		boolean normalizeEnharmonics = false;
		boolean useAllChords = false;
		boolean serializeModel = true;
		int maxLength = 207;					// maximum song length
		int maxSegmentLength = 17;				// maximum segment length
		int numExamplesPrinted = 10;
		int totalNumEvents = 0;
		int totalNumSegments = 0;
		int foldNum = 0;
		NetworkConfig._numThreads = 4;			// number of threads used
		NetworkConfig.L2_REGULARIZATION_CONSTANT = 0.125;
		NetworkConfig.objtol = 1e-6;
		String weightInit = "random";
		int maxNumIterations = 5000;			// maximum number of iterations for training
		String[] features = new String[0];		// list of features to be used
		NetworkCompiler compiler = null;
		NetworkModel model = null;
		
		// read in command line arguments
		int argIndex = 0;						// index of command line argument within args
		String[] moreArgs = new String[0];		// additional command line arguments for FeatureManager, etc.
		while(argIndex < args.length) {
			String arg = args[argIndex];
			if(arg.charAt(0) == '-') {
				switch(arg.substring(1)) {
					case "dataPath":
						dataPath = args[argIndex + 1];
						argIndex += 2;
						break;
					case "foldsPath":
						foldsPath = args[argIndex + 1];
						argIndex += 2;
						break;
					case "currentDirectoryPath":
						currentDirectoryPath = args[argIndex + 1];
						argIndex += 2;
						break;
					case "trainFile":
						trainFile = args[argIndex + 1];
						argIndex += 2;
						break;
					case "testFile":
						testFile = args[argIndex + 1];
						argIndex += 2;
						break;
					case "foldNum":
						foldNum = Integer.parseInt(args[argIndex + 1]);
						argIndex += 2;
						break;
					case "features":
						features = args[argIndex + 1].split(",");
						argIndex += 2;
						break;
					case "resultPath":
						result_filename = args[argIndex + 1];
						argIndex += 2;
						break;
					case "logPath":
						logPath = args[argIndex + 1];
						argIndex += 2;
						break;
//					case "featureCountPath":
//						featureCountPath = args[argIndex + 1];
//						argIndex += 2;
//						break;
					case "useAllChordsPath":
						useAllChordsPath = args[argIndex + 1];
						argIndex += 2;
						break;
					case "simplify":
						try {
							simplification = Simplify.valueOf(args[argIndex+1].toUpperCase());
						} catch (IllegalArgumentException e) {
							throw new IllegalArgumentException("Unrecognized simplification: " + args[argIndex + 1] + "\n" + Simplify.helpString());
						} 
						argIndex += 2;
						break;
					case "writeModelText":
						writeModelText = true;
						argIndex += 1;
						break;
					case "nThreads":
						NetworkConfig._numThreads = Integer.parseInt(args[argIndex+1]);
						argIndex += 2;
						break;
					case "l2":
						NetworkConfig.L2_REGULARIZATION_CONSTANT = Double.parseDouble(args[argIndex+1]);
						argIndex += 2;
						break;
					case "weightInit":
						weightInit = args[argIndex+1];
						if(weightInit.equals("random")){
							NetworkConfig.RANDOM_INIT_WEIGHT = true;
						} else {
							NetworkConfig.RANDOM_INIT_WEIGHT = false;
							NetworkConfig.FEATURE_INIT_WEIGHT = Double.parseDouble(weightInit);
						}
						argIndex += 2;
						break;
					case "numExamplesPrinted":
						numExamplesPrinted = Integer.parseInt(args[argIndex+1]);
						argIndex += 2;
						break;
					case "objtol":
						NetworkConfig.objtol = Double.parseDouble(args[argIndex+1]);
						argIndex += 2;
						break;
					case "countFeatures":
						countFeatures = true;
						argIndex += 1;
						break;
					case "normalizeEnharmonics":
						normalizeEnharmonics = true;
						argIndex += 1;
						break;
					case "useAllChords":
						useAllChords = true;
						argIndex += 1;
						break;
					case "-":
						moreArgs = Arrays.copyOfRange(args, argIndex+1, args.length);
						argIndex = args.length;
						break;
					default:
						throw new IllegalArgumentException("Unrecognized argument: " + arg);
				}
			} else {
				throw new IllegalArgumentException("Error while parsing: " + arg);
			}
		}
		

		
		if(trainFile.isEmpty()) {
			trainFile = "train" + foldNum + ".txt";
		}
		
		if(testFile.isEmpty()) {
			testFile = "test" + foldNum + ".txt";
		}
		
		if(featureCountFile.isEmpty()) {
			featureCountFile = "feature_count" + foldNum + ".txt";
		}
		
		if(modelFile.isEmpty()) {
			modelFile = timestamp + " Fold " + foldNum + ".model";
		}
		
		if(logFile.isEmpty()) {
			logFile = timestamp + " Fold " + foldNum + ".log"; 
		}
		
		
		// create train, test, feature count, model, and log paths
		trainFilePath = foldsPath + trainFile;
		System.out.println(trainFilePath);
		testFilePath = foldsPath + testFile;
		System.out.println(testFilePath);
		featureCountPath = featureCountFile;
		modelPath = modelFile;
		logPath = logFile;
		
		PrintStream outstream = null;
		if(logPath != null) {
			outstream = new PrintStream(logPath, "UTF-8");
		}

		WordWeakSemiCRFFeatureManager fm = null;
		if(trainFilePath != null) {
			List<Song> trainInstancesList;	// list of songs used as training data
			
			// read in songs
			trainInstancesList = Arrays.asList(SongUtil.readData(trainFilePath, true, simplification, normalizeEnharmonics));
			
			// use all chord labels option
			if(useAllChords) {
				SongUtil.addAllLabels(useAllChordsPath);
			}
			
			// labels
			SpanLabel[] labels = SpanLabel.LABELS.values().toArray(new SpanLabel[SpanLabel.LABELS.size()]);
			
			String max_song_length = "";
			int max_song_onset = 0;
			for(int instanceIdx = trainInstancesList.size()-1; instanceIdx >= 0; instanceIdx--) { 
				Song instance = trainInstancesList.get(instanceIdx);
				
				// find max song length
				int size = instance.size();
				maxLength = Math.max(maxLength, size);
				totalNumEvents += size;
				totalNumSegments += instance.output.size();
				
				// find max segment length
				if(findMaxSegmentLength) {
					List<Span> output = instance.output;
					for(Span span: output) {
						double maxSegmentLengthTmp = maxSegmentLength;
						maxSegmentLength = Math.max(maxSegmentLength, span.stop - span.start);
						if(maxSegmentLengthTmp < maxSegmentLength) {
							max_song_length = instance.title;
							max_song_onset = span.start;
						}
					}
				}
			}
			System.out.println("Max song length: " + maxLength + " Max segment length: " + maxSegmentLength + " Title: " + max_song_length + " Onset: " + max_song_onset);
			System.out.println("Total num events: " + totalNumEvents + ", Total num segments: " + totalNumSegments);
			
			trainInstances = trainInstancesList.toArray(new Song[trainInstancesList.size()]);		
			
			NetworkConfig.TRAIN_MODE_IS_GENERATIVE = false;
			NetworkConfig._CACHE_FEATURES_DURING_TRAINING = true;

			int size = trainInstances.length;
			
			print("Read..."+size+" instances.", true, outstream, System.err);

			compiler = new WordWeakSemiCRFNetworkCompiler(labels, maxLength, maxSegmentLength);
			
			if(countFeatures) {
				fm = new WordWeakSemiCRFFeatureManager(new GlobalNetworkParam(), features, countFeatures, featureCountPath, moreArgs);
				List<Integer> counts = new ArrayList<Integer>(Collections.nCopies(10000, 0));
				
				for(Instance trainInstance : trainInstances) {
					Network network = compiler.compile(-1, trainInstance, new LocalNetworkParam(-1, fm, -1));
					for(int k = 0; k<network.countNodes(); k++) {
						int[][] childrenList_k = network.getChildren(k);
						
						for(int children_k_index = 0; children_k_index < childrenList_k.length; children_k_index++) {
							int[] children_k = childrenList_k[children_k_index];
							FeatureArray fa = fm.extract_helper(network, k, children_k);
							while(fa != null) {
								for(int f : fa.getCurrent()) {
									counts.set(f, counts.get(f) + 1);
								}
								fa = fa.getNext();
							}
						}
					}
				}
				
				PrintStream featureCountTextWriter = new PrintStream(featureCountPath);
				for(int i = 0; i < counts.size(); i++) {
					String featureName = fm.featureIDToName.get(i);
					if(!featureName.isEmpty()) {
						String featureNameAndCount = String.format("%-30s %-5s", featureName, counts.get(i));
						featureCountTextWriter.println(featureNameAndCount);
					}
				}
				featureCountTextWriter.close();
			}
			else {
				fm = new WordWeakSemiCRFFeatureManager(new GlobalNetworkParam(), features, countFeatures, featureCountPath, moreArgs);
				model = NetworkConfig.TRAIN_MODE_IS_GENERATIVE ? GenerativeNetworkModel.create(fm, compiler) : DiscriminativeNetworkModel.create(fm, compiler);
				model.train(trainInstances, maxNumIterations);
				
				if(serializeModel) {
					print("Writing object...", false, outstream, System.out);
					long startTime = System.currentTimeMillis();
					ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelPath));
					oos.writeObject(model);
					oos.close();
					long endTime = System.currentTimeMillis();
					print(String.format("Done in %.3fs", (endTime-startTime)/1000.0), true, outstream, System.out);
				}
		
				if(writeModelText) {
					PrintStream modelTextWriter = new PrintStream(modelPath+".txt");
					modelTextWriter.println("Model path: "+modelPath);
					modelTextWriter.println("Train path: "+trainFilePath);
					modelTextWriter.println("Test path: "+testFilePath);
					modelTextWriter.println("Max length: "+maxLength);
					modelTextWriter.println("Max span: "+maxSegmentLength);
					modelTextWriter.println("#Threads: "+NetworkConfig._numThreads);
					modelTextWriter.println("L2 param: "+NetworkConfig.L2_REGULARIZATION_CONSTANT);
					modelTextWriter.println("Weight init: "+weightInit);
					modelTextWriter.println("objtol: "+NetworkConfig.objtol);
					modelTextWriter.println("Max iter: "+maxNumIterations);
					modelTextWriter.println("Features: "+Arrays.asList(features));
					modelTextWriter.println();
					modelTextWriter.println("Labels:");
					List<?> labelsUsed = new ArrayList<Object>();
			
					labelsUsed = Arrays.asList(((WordWeakSemiCRFNetworkCompiler)compiler).labels);
		
					for(Object obj: labelsUsed){
						modelTextWriter.println(obj);
					}
					GlobalNetworkParam paramG = fm.getParam_G();
					modelTextWriter.println("Num features: "+paramG.countFeatures());
					modelTextWriter.println("Features:");
					HashMap<String, HashMap<String, HashMap<String, Integer>>> featureIntMap = paramG.getFeatureIntMap();
					for(String featureType: sorted(featureIntMap.keySet())){
						modelTextWriter.println(featureType);
						HashMap<String, HashMap<String, Integer>> outputInputMap = featureIntMap.get(featureType);
						for(String output: sorted(outputInputMap.keySet())){
							modelTextWriter.println("\t"+output);
							HashMap<String, Integer> inputMap = outputInputMap.get(output);
							for(String input: sorted(inputMap.keySet())){
								int featureId = inputMap.get(input);
								modelTextWriter.println("\t\t"+input+" "+featureId+" "+fm.getParam_G().getWeight(featureId));
							}
						}
					}
					modelTextWriter.close();
				}
			}
		} else {
			print("Reading object...", false, outstream, System.out);
			long startTime = System.currentTimeMillis();
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath));
			model = (NetworkModel)ois.readObject();
			ois.close();
			Field _fm = NetworkModel.class.getDeclaredField("_fm");
			_fm.setAccessible(true);
			fm = (WordWeakSemiCRFFeatureManager)_fm.get(model);
			Field _compiler = NetworkModel.class.getDeclaredField("_compiler");
			_compiler.setAccessible(true);
			compiler = (NetworkCompiler)_compiler.get(model);

			long endTime = System.currentTimeMillis();
			print(String.format("Done in %.3fs", (endTime-startTime)/1000.0), true, outstream, System.out);
		}

		if(testFilePath != null && !countFeatures) {
			testInstances = SongUtil.readData(testFilePath, false, simplification, normalizeEnharmonics);
			
			Instance[] predictions = null;
			
			predictions = model.decode(testInstances);
			
			List<Song> predictionsList = new ArrayList<Song>();
			for(Instance instance: predictions){
				predictionsList.add((Song)instance);
			}
			
			predictionsList.sort(Comparator.comparing(Instance::getInstanceId));
			if(result_filename == null){
				result_filename = testFilePath+".result";
			}
			
			PrintStream result = new PrintStream(result_filename);
			for(Song instance: predictionsList){
				result.println(instance.toString());
			}
			
			result.close();
			
			numExamplesPrinted = testInstances.length;
			SongEvaluator.evaluate(predictions, outstream, numExamplesPrinted);
		}
	}
	
	private static List<String> sorted(Set<String> coll){
		List<String> result = new ArrayList<String>(coll);
		Collections.sort(result);
		return result;
	}
}

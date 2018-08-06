package cr;

import static cr.SongUtil.listToArray;
import static cr.SongUtil.setupFeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import com.statnlp.hybridnetworks.FeatureArray;
import com.statnlp.hybridnetworks.FeatureManager;
import com.statnlp.hybridnetworks.GlobalNetworkParam;
import com.statnlp.hybridnetworks.Network;

import cr.WordWeakSemiCRFNetworkCompiler.NodeType;

import org.apache.commons.lang3.StringUtils;
//
///**
// * The class that defines the features to be extracted<br>
// * This is based on StatNLP framework for CRF on acyclic graphs
// * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
// *
// */
public class WordWeakSemiCRFFeatureManager extends FeatureManager {

	private static final long serialVersionUID = 6510131496948610905L;
	
	// weight options for harmonic and segment consistency features
	public enum Weight {
		ACCENT,				// take accent info into account
		DURATION,
		NONE				
		;
	}
	
	public static enum FeatureType implements IFeatureType {
		CHEAT(false),
		
		// segment features
		
		PURITY(true),
		ACCENTED_PURITY(true),
		DURATION_PURITY(true),
		FIG_PURITY(true),
		FIG_ACCENTED_PURITY(true),
		FIG_DURATION_PURITY(true),
		ROOT_COVERED(true),	
		THIRD_COVERED(true),
		FIFTH_COVERED(true),
		ADDED_NOTE_COVERED(true),
		ADDED_NOTE_NOT_COVERED(true),
		SUS_OR_POW_ROOT_COVERED(false),
		SUS_OR_POW_2ND_OR_4TH_COVERED(false),
		SUS_OR_POW_5TH_COVERED(false),
		DOM7SUS4_7TH_COVERED(false),
		DOM7SUS4_7TH_NOT_COVERED(false),
		AUG6_BASS_COVERED(false),
		AUG6_3RD_COVERED(false),
		AUG6_6TH_COVERED(false),
		AUG6_5TH_COVERED(false),
		ALL_NOTES_COVERED(true),
		DURATION_ADDED_NOTE_GREATER_THAN_ROOT(true),
		DURATION_7TH_OF_7SUS4_GREATER_THAN_ROOT(false),
		DURATION_ROOT_COVERED(true),
		FIG_DURATION_ROOT_COVERED(true),
		SEGMENT_DURATION_ROOT_COVERED(true),
		DURATION_THIRD_COVERED(true),
		FIG_DURATION_THIRD_COVERED(true),
		SEGMENT_DURATION_THIRD_COVERED(true),
		DURATION_FIFTH_COVERED(true),
		FIG_DURATION_FIFTH_COVERED(true),
		SEGMENT_DURATION_FIFTH_COVERED(true),
		ACCENT_ROOT_COVERED(true),
		FIG_ACCENT_ROOT_COVERED(true),
		ACCENT_THIRD_COVERED(true),
		FIG_ACCENT_THIRD_COVERED(true),
		ACCENT_FIFTH_COVERED(true),
		FIG_ACCENT_FIFTH_COVERED(true),
		DURATION_ADDED_NOTE_COVERED(true),
		FIG_DURATION_ADDED_NOTE_COVERED(true),
		SEGMENT_DURATION_ADDED_NOTE_COVERED(true),
		ACCENT_ADDED_NOTE_COVERED(true),
		FIG_ACCENT_ADDED_NOTE_COVERED(true),
		DURATION_SUS_POW_ROOT_COVERED(false),
		FIG_DURATION_SUS_POW_ROOT_COVERED(false),
		SEGMENT_DURATION_SUS_POW_ROOT_COVERED(false),
		DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED(false),
		FIG_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED(false),
		SEGMENT_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED(false),
		DURATION_SUS_POW_FIFTH_COVERED(false),
		FIG_DURATION_SUS_POW_FIFTH_COVERED(false),
		SEGMENT_DURATION_SUS_POW_FIFTH_COVERED(false),
		ACCENT_SUS_POW_ROOT_COVERED(false),
		FIG_ACCENT_SUS_POW_ROOT_COVERED(false),
		ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED(false),
		FIG_ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED(false),
		ACCENT_SUS_POW_FIFTH_COVERED(false),
		FIG_ACCENT_SUS_POW_FIFTH_COVERED(false),
		DURATION_SUS_POW_7SUS4_SEVENTH_COVERED(false),
		FIG_DURATION_SUS_POW_7SUS4_SEVENTH_COVERED(false),
		SEGMENT_DURATION_SUS_POW_7SUS4_SEVENTH_COVERED(false),
		ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED(false),
		FIG_ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED(false),
		DURATION_AUG6_BASS_COVERED(false),
		FIG_DURATION_AUG6_BASS_COVERED(false),
		SEGMENT_DURATION_AUG6_BASS_COVERED(false),
		DURATION_AUG6_3RD_COVERED(false),
		FIG_DURATION_AUG6_3RD_COVERED(false),
		SEGMENT_DURATION_AUG6_3RD_COVERED(false),
		DURATION_AUG6_6TH_COVERED(false),
		FIG_DURATION_AUG6_6TH_COVERED(false),
		SEGMENT_DURATION_AUG6_6TH_COVERED(false),
		DURATION_AUG6_5TH_COVERED(false),
		FIG_DURATION_AUG6_5TH_COVERED(false),
		SEGMENT_DURATION_AUG6_5TH_COVERED(false),
		ACCENT_AUG6_BASS_COVERED(false),
		FIG_ACCENT_AUG6_BASS_COVERED(false),
		ACCENT_AUG6_3RD_COVERED(false),
		FIG_ACCENT_AUG6_3RD_COVERED(false),
		ACCENT_AUG6_6TH_COVERED(false),
		FIG_ACCENT_AUG6_6TH_COVERED(false),
		ACCENT_AUG6_5TH_COVERED(false),
		FIG_ACCENT_AUG6_5TH_COVERED(false),
		BEGINNING_ACCENTED(true),			
		FIRST_BASS_IS_ROOT(true),
		FIRST_BASS_IS_THIRD(true),
		FIRST_BASS_IS_FIFTH(true),
		FIRST_BASS_IS_ADDED_NOTE(true),
		FIRST_BASS_IS_SUS_POW_ROOT(false),
		FIRST_BASS_IS_SUS_POW_2ND_OR_4TH(false),
		FIRST_BASS_IS_SUS_POW_5TH(false),
		FIRST_BASS_IS_SUS_POW_7SUS4_7TH(false),
		FIRST_BASS_IS_AUG6_BASS(false),
		FIRST_BASS_IS_AUG6_3RD(false),
		FIRST_BASS_IS_AUG6_6TH(false),
		FIRST_BASS_IS_AUG6_5TH(false),
		FIG_FIRST_BASS_IS_ROOT(true),
		FIG_FIRST_BASS_IS_THIRD(true),
		FIG_FIRST_BASS_IS_FIFTH(true),
		FIG_FIRST_BASS_IS_ADDED_NOTE(true),
		FIG_FIRST_BASS_IS_SUS_POW_ROOT(false),
		FIG_FIRST_BASS_IS_SUS_POW_2ND_OR_4TH(false),
		FIG_FIRST_BASS_IS_SUS_POW_5TH(false),
		FIG_FIRST_BASS_IS_SUS_POW_7SUS4_7TH(false),
		FIG_FIRST_BASS_IS_AUG6_BASS(false),
		FIG_FIRST_BASS_IS_AUG6_3RD(false),
		FIG_FIRST_BASS_IS_AUG6_6TH(false),
		FIG_FIRST_BASS_IS_AUG6_5TH(false),
		SEGMENT_BASS_IS_ROOT(true),
		SEGMENT_BASS_IS_THIRD(true),
		SEGMENT_BASS_IS_FIFTH(true),
		SEGMENT_BASS_IS_ADDED_NOTE(true),
		SEGMENT_BASS_IS_SUS_POW_ROOT(false),
		SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH(false),
		SEGMENT_BASS_IS_SUS_POW_5TH(false),
		SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH(false),
		SEGMENT_BASS_IS_AUG6_BASS(false),
		SEGMENT_BASS_IS_AUG6_3RD(false),
		SEGMENT_BASS_IS_AUG6_6TH(false),
		SEGMENT_BASS_IS_AUG6_5TH(false),
		FIG_SEGMENT_BASS_IS_ROOT(true),
		FIG_SEGMENT_BASS_IS_THIRD(true),
		FIG_SEGMENT_BASS_IS_FIFTH(true),
		FIG_SEGMENT_BASS_IS_ADDED_NOTE(true),
		FIG_SEGMENT_BASS_IS_SUS_POW_ROOT(false),
		FIG_SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH(false),
		FIG_SEGMENT_BASS_IS_SUS_POW_5TH(false),
		FIG_SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH(false),
		FIG_SEGMENT_BASS_IS_AUG6_BASS(false),
		FIG_SEGMENT_BASS_IS_AUG6_3RD(false),
		FIG_SEGMENT_BASS_IS_AUG6_6TH(false),
		FIG_SEGMENT_BASS_IS_AUG6_5TH(false),
		DURATION_BASS_IS_ROOT(true),
		DURATION_BASS_IS_THIRD(true),
		DURATION_BASS_IS_FIFTH(true),
		DURATION_BASS_IS_ADDED_NOTE(true),
		DURATION_SUS_POW_BASS_IS_ROOT(false),
		DURATION_SUS_POW_BASS_IS_2ND_OR_4TH(false),
		DURATION_SUS_POW_BASS_IS_5TH(false),
		DURATION_SUS_POW_BASS_IS_7SUS4_7TH(false),
		DURATION_BASS_IS_AUG6_BASS(false),
		DURATION_BASS_IS_AUG6_3RD(false),
		DURATION_BASS_IS_AUG6_6TH(false),
		DURATION_BASS_IS_AUG6_5TH(false),
		ACCENT_BASS_IS_ROOT(true),
		ACCENT_BASS_IS_THIRD(true),
		ACCENT_BASS_IS_FIFTH(true),
		ACCENT_BASS_IS_ADDED_NOTE(true),
		ACCENT_SUS_POW_BASS_IS_ROOT(false),
		ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH(false),
		ACCENT_SUS_POW_BASS_IS_5TH(false),
		ACCENT_SUS_POW_BASS_IS_7SUS4_7TH(false),		
		ACCENT_BASS_IS_AUG6_BASS(false),
		ACCENT_BASS_IS_AUG6_3RD(false),
		ACCENT_BASS_IS_AUG6_6TH(false),
		ACCENT_BASS_IS_AUG6_5TH(false),
		FIG_DURATION_BASS_IS_ROOT(true),
		FIG_DURATION_BASS_IS_THIRD(true),
		FIG_DURATION_BASS_IS_FIFTH(true),
		FIG_DURATION_BASS_IS_ADDED_NOTE(true),
		FIG_DURATION_SUS_POW_BASS_IS_ROOT(false),
		FIG_DURATION_SUS_POW_BASS_IS_2ND_OR_4TH(false),
		FIG_DURATION_SUS_POW_BASS_IS_5TH(false),
		FIG_DURATION_SUS_POW_BASS_IS_7SUS4_7TH(false),
		FIG_DURATION_BASS_IS_AUG6_BASS(false),
		FIG_DURATION_BASS_IS_AUG6_3RD(false),
		FIG_DURATION_BASS_IS_AUG6_6TH(false),
		FIG_DURATION_BASS_IS_AUG6_5TH(false),
		FIG_ACCENT_BASS_IS_ROOT(true),
		FIG_ACCENT_BASS_IS_THIRD(true),
		FIG_ACCENT_BASS_IS_FIFTH(true),
		FIG_ACCENT_BASS_IS_ADDED_NOTE(true),
		FIG_ACCENT_SUS_POW_BASS_IS_ROOT(false),
		FIG_ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH(false),
		FIG_ACCENT_SUS_POW_BASS_IS_5TH(false),
		FIG_ACCENT_SUS_POW_BASS_IS_7SUS4_7TH(false),
		FIG_ACCENT_BASS_IS_AUG6_BASS(false),
		FIG_ACCENT_BASS_IS_AUG6_3RD(false),
		FIG_ACCENT_BASS_IS_AUG6_6TH(false),
		FIG_ACCENT_BASS_IS_AUG6_5TH(false),
		
		// transition features
		CHORD_BIGRAM(true)
		;
		
		private boolean isEnabled;
		
		private FeatureType() {
			this(false);
		}
		
		private FeatureType(boolean isEnabled) {
			this.isEnabled = isEnabled;
		}
		
		public void enable() {
			isEnabled = true;
		}
		
		public void disable() {
			isEnabled = false;
		}
		
		public boolean enabled() {
			return isEnabled;
		}
		
		public boolean disabled() {
			return !isEnabled;
		}
	}

	private static enum Argument {
		HELP(0,
				"Print this help message",
				"h,help"),
		;
		
		final private int numArgs;
		final private String[] argNames;
		final private String[] names;
		final private String help;
		private Argument(int numArgs, String help, String names, String... argNames) {
			this.numArgs = numArgs;
			this.argNames = argNames;
			this.names = names.split(",");
			this.help = help;
		}
		
		/**
		 * Return the Argument which has the specified name
		 * @param name
		 * @return
		 */
		public static Argument argWithName(String name){
			for(Argument argument: Argument.values()){
				for(String argName: argument.names){
					if(argName.equals(name)){
						return argument;
					}
				}
			}
			throw new IllegalArgumentException("Unrecognized argument: "+name);
		}
		
		/**
		 * Print help message
		 */
		private static void printHelp() {
			StringBuilder result = new StringBuilder();
			result.append("Options:\n");
			for(Argument argument: Argument.values()){
				result.append("-"+StringUtils.join(argument.names, " -"));
				result.append(" "+StringUtils.join(argument.argNames, " "));
				result.append("\n");
				if(argument.help != null && argument.help.length() > 0){
					result.append("\t"+argument.help.replaceAll("\n","\n\t")+"\n");
				}
			}
//			System.out.println(result.toString());
		}
	}
	
	private boolean countFeatures = false;
	public HashMap<String, Integer> featureNameToID = new HashMap<String, Integer>();
	public List<String> featureIDToName = new ArrayList<String>(Collections.nCopies(10000, ""));
	public static HashMap<String, Integer> enharmonicNotesToID = new HashMap<String, Integer>();
	public static List<String> enharmonicIDToNotes = new ArrayList<>(Arrays.asList("A Bbb", "A# Bb", "B Cb", "B# C", "C# Db", "C## D Ebb", "D# Eb", "E Fb", "E# F", "F# Gb", "F## G Abb", "G# Ab"));
	private static HashMap<String, Integer> enharmonicNotesToOctaveID = new HashMap<String, Integer>();
	
	static {
		// fill enharmonicNotesToID HashMap
		enharmonicNotesToID.put("A", 0);
		enharmonicNotesToID.put("Bbb", 0);
		enharmonicNotesToID.put("A#", 1);
		enharmonicNotesToID.put("Bb", 1);
		enharmonicNotesToID.put("B", 2);
		enharmonicNotesToID.put("Cb", 2);
		enharmonicNotesToID.put("B#", 3);
		enharmonicNotesToID.put("C", 3);
		enharmonicNotesToID.put("C#", 4);
		enharmonicNotesToID.put("Db", 4);
		enharmonicNotesToID.put("C##", 5);
		enharmonicNotesToID.put("D", 5);
		enharmonicNotesToID.put("Ebb", 5);
		enharmonicNotesToID.put("D#", 6);
		enharmonicNotesToID.put("Eb", 6);
		enharmonicNotesToID.put("E", 7);
		enharmonicNotesToID.put("Fb", 7);
		enharmonicNotesToID.put("E#", 8);
		enharmonicNotesToID.put("F", 8);
		enharmonicNotesToID.put("F#", 9);
		enharmonicNotesToID.put("Gb", 9);
		enharmonicNotesToID.put("F##", 10);
		enharmonicNotesToID.put("G", 10);
		enharmonicNotesToID.put("Abb", 10);
		enharmonicNotesToID.put("G#", 11);
		enharmonicNotesToID.put("Ab", 11);
		
		// fill enharmonicNotesToOctaveID HashMap
		enharmonicNotesToOctaveID.put("B#", 0);
		enharmonicNotesToOctaveID.put("C", 0);
		enharmonicNotesToOctaveID.put("C#", 1);
		enharmonicNotesToOctaveID.put("Db", 1);
		enharmonicNotesToOctaveID.put("C##", 2);
		enharmonicNotesToOctaveID.put("D", 2);
		enharmonicNotesToOctaveID.put("Ebb", 2);
		enharmonicNotesToOctaveID.put("D#", 3);
		enharmonicNotesToOctaveID.put("Eb", 3);
		enharmonicNotesToOctaveID.put("E", 4);
		enharmonicNotesToOctaveID.put("Fb", 4);
		enharmonicNotesToOctaveID.put("E#", 5);
		enharmonicNotesToOctaveID.put("F", 5);
		enharmonicNotesToOctaveID.put("F#", 6);
		enharmonicNotesToOctaveID.put("Gb", 6);
		enharmonicNotesToOctaveID.put("F##", 7);
		enharmonicNotesToOctaveID.put("G", 7);
		enharmonicNotesToOctaveID.put("Abb", 7);
		enharmonicNotesToOctaveID.put("G#", 8);
		enharmonicNotesToOctaveID.put("Ab", 8);
		enharmonicNotesToOctaveID.put("A", 9);
		enharmonicNotesToOctaveID.put("Bbb", 9);
		enharmonicNotesToOctaveID.put("A#", 10);
		enharmonicNotesToOctaveID.put("Bb", 10);
		enharmonicNotesToOctaveID.put("B", 11);
		enharmonicNotesToOctaveID.put("Cb", 11);
	}
	
	public WordWeakSemiCRFFeatureManager(GlobalNetworkParam param_g, String[] features) throws IOException{
		this(param_g, features, false, (String) null, (String[]) null);
	}

	public WordWeakSemiCRFFeatureManager(GlobalNetworkParam param_g, String[] features, boolean countFeatures, String featureCountPath, String... args) throws IOException {
		super(param_g);
		setupFeatures(FeatureType.class, features);
		this.countFeatures = countFeatures;
		if(!countFeatures) {
			File f = new File(featureCountPath);
			Scanner s = new Scanner(f);
			
			while(s.hasNext()) {
				String featureName = s.next();
				
				if(s.hasNextInt()) {
					int count = s.nextInt();
					featureNameToID.put(featureName, count);
				}
			}
			
			s.close();
		}
		
		int argIndex = 0;
		while(argIndex < args.length){
			String arg = args[argIndex];
			if(arg.length() > 0 && arg.charAt(0) == '-'){
				Argument argument = Argument.argWithName(args[argIndex].substring(1));
				switch(argument) {
				case HELP:
					Argument.printHelp();
					System.exit(0);
				}
				argIndex += argument.numArgs+1;
			} else {
				throw new IllegalArgumentException("Error while parsing: "+arg);
			}
		}
	}
	
	@Override
	protected FeatureArray extract_helper(Network net, int parent_k, int[] children_k) {
		SongNetwork network = (SongNetwork)net;
		Song instance = (Song)network.getInstance();
		int MIN_FEATURE_COUNT = 4;
		boolean overlappedConsistency = false;
		int[] overlappedBins = {90, 80, 70, 60, 50, 40, 30, 20, 10, 0};
		
		// get event-based input
		List<Event> inputTokenized = instance.getInputTokenized();
		int length = inputTokenized.size();
		
		// get position, node type, and label id for parent
		int[] parent_arr = network.getNodeArray(parent_k);
		int parentPos = parent_arr[0]-1;
		NodeType parentType = NodeType.values()[parent_arr[1]];
		int parentLabelId = parent_arr[2]-1;
		
		if(parentType == NodeType.LEAF || (parentType == NodeType.ROOT && parentPos < length)){
			return FeatureArray.EMPTY;
		}
		
		// get position, node type, and label id for child
		// NOTE: in our case, each parent should have only one child
		// (see WordWeakSemiCRFNetworkCompiler.compileLabeled()),
		// which is why we only get this info for the first child.
		int[] child_arr = network.getNodeArray(children_k[0]);
		int childPos = child_arr[0]-1;
		NodeType childType = NodeType.values()[child_arr[1]];
		int childLabelId = child_arr[2]-1;

		// initialize parameters
		GlobalNetworkParam param_g = this._param_g;
		
		if(FeatureType.CHEAT.enabled()) {
			int instanceId = Math.abs(instance.getInstanceId());
			int cheatFeature = param_g.toFeature(FeatureType.CHEAT.name(), "", instanceId+" "+parentPos+" "+childPos+" "+parentLabelId+" "+childLabelId);
			return new FeatureArray(new int[]{cheatFeature});
		}
		
		// get events between BEGIN/END or END/BEGIN nodes
		int beginningSegmentIndex = (childType == NodeType.BEGIN) ? childPos : childPos + 1;
		int endSegmentIndex = (parentType == NodeType.END) ? parentPos + 1 : parentPos;
		List<Event> eventsInside = inputTokenized.subList(beginningSegmentIndex, endSegmentIndex);
		Event previousEvent = (childPos > 0) ? inputTokenized.get(beginningSegmentIndex - 1) : new Event();
		Event nextEvent = (parentPos < (inputTokenized.size() - 1)) ? inputTokenized.get(endSegmentIndex) : new Event();
		
		// initialize purity weight to none
		Weight featuresWeight = Weight.valueOf("NONE");
	
		// common features
		List<Integer> commonFeatures = new ArrayList<Integer>();
		
		// no common features yet
		
		FeatureArray features = new FeatureArray(listToArray(commonFeatures));
		
		// segment features (begin to end)
		if(parentType == NodeType.END) {
			List<Integer> segmentFeatures = new ArrayList<Integer>();
			
			// get parent label name
			String parentLabel = SpanLabel.get(parentLabelId).form;
//			System.out.println("");
//			System.out.println("Measure: " + eventsInside.get(0).measureNumber);
//			System.out.println("Segment bounds: " + beginningSegmentIndex + ", " + endSegmentIndex);
//			System.out.println("[" + childPos + ", " + childType.toString() + ", " + (childLabelId) + "]");
//			System.out.println("[" + parentPos + ", " + parentType.toString() + ", " + (parentLabelId) + "]");
			
			String root = getRoot(parentLabel);
			String mode = getMode(parentLabel);
			String addedNote = getAddedNote(parentLabel);
			
//			System.out.println(instance.title);
//			System.out.println("Parent label: " + parentLabel);
			
			List<Integer> parentNotes = getChordNotes(root, mode, addedNote);
			List<Note> segmentNotes = getNotesInSegment(eventsInside);
			List<Note> nonFigSegmentNotes = getNonFigurationNotesInSegment(eventsInside, segmentNotes, previousEvent, nextEvent, parentNotes);
	
			if(FeatureType.PURITY.enabled()) {
				int purityValue = purity(featuresWeight, root, parentNotes, segmentNotes, eventsInside, parentLabel, overlappedConsistency);
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(purityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(purityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {
					String purityFeatureName = FeatureType.PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int purityFeature = param_g.toFeature(FeatureType.PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(purityFeature, purityFeatureName);
						segmentFeatures.add(purityFeature);
					}
					else {
						Integer count = featureNameToID.get(purityFeatureName) ;
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int purityFeature = param_g.toFeature(FeatureType.PURITY.name(), "", overlappedPurityValue + "");
							segmentFeatures.add(purityFeature);
						}
					}
				}
			}
			
			if(FeatureType.ACCENTED_PURITY.enabled()) {
				featuresWeight = Weight.valueOf("ACCENT");
				int purityValue = purity(featuresWeight, root, parentNotes, segmentNotes, eventsInside, parentLabel, overlappedConsistency);
				
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(purityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(purityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {
					String accentedPurityFeatureName = FeatureType.ACCENTED_PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int accentedPurityFeature = param_g.toFeature(FeatureType.ACCENTED_PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(accentedPurityFeature, accentedPurityFeatureName);
						segmentFeatures.add(accentedPurityFeature);
					}
					else {
						Integer count = featureNameToID.get(accentedPurityFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int accentedPurityFeature = param_g.toFeature(FeatureType.ACCENTED_PURITY.name(), "", overlappedPurityValue + "");
	//						System.out.println(accentedPurityFeatureName);
							segmentFeatures.add(accentedPurityFeature);
						}
					}
				}
			}
			
			if(FeatureType.DURATION_PURITY.enabled()) {
				featuresWeight = Weight.valueOf("DURATION");
				int purityValue = purity(featuresWeight, root, parentNotes, segmentNotes, eventsInside, parentLabel, overlappedConsistency);
				
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(purityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(purityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {
					String durationPurityFeatureName = FeatureType.DURATION_PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int durationPurityFeature = param_g.toFeature(FeatureType.DURATION_PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(durationPurityFeature, durationPurityFeatureName);
						segmentFeatures.add(durationPurityFeature);
					}
					else {
						Integer count = featureNameToID.get(durationPurityFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int durationPurityFeature = param_g.toFeature(FeatureType.DURATION_PURITY.name(), "", overlappedPurityValue + "");
	//						System.out.println(durationPurityFeatureName);
							segmentFeatures.add(durationPurityFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_PURITY.enabled()) {
				int figPurityValue = purity(featuresWeight, root, parentNotes, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency);
				
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(figPurityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(figPurityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {
					String figPurityFeatureName = FeatureType.FIG_PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int figPurityFeature = param_g.toFeature(FeatureType.FIG_PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(figPurityFeature, figPurityFeatureName);
						segmentFeatures.add(figPurityFeature);
					}
					else {
						Integer count = featureNameToID.get(figPurityFeatureName) ;
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figPurityFeature = param_g.toFeature(FeatureType.FIG_PURITY.name(), "", overlappedPurityValue + "");
	//						System.out.println(purityFeatureName + " " + count);
							segmentFeatures.add(figPurityFeature);
						}
					}	
				}
			}
			
			if(FeatureType.FIG_ACCENTED_PURITY.enabled()) {
				featuresWeight = Weight.valueOf("ACCENT");
				int figAccentedPurityValue = purity(featuresWeight, root, parentNotes, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency);
				
				
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(figAccentedPurityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(figAccentedPurityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {
					String figAccentedPurityFeatureName = FeatureType.FIG_ACCENTED_PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int figAccentedPurityFeature = param_g.toFeature(FeatureType.FIG_ACCENTED_PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(figAccentedPurityFeature, figAccentedPurityFeatureName);
						segmentFeatures.add(figAccentedPurityFeature);
					}
					else {
						Integer count = featureNameToID.get(figAccentedPurityFeatureName) ;
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figAccentedPurityFeature = param_g.toFeature(FeatureType.FIG_ACCENTED_PURITY.name(), "", overlappedPurityValue + "");
	//						System.out.println(purityFeatureName + " " + count);
							segmentFeatures.add(figAccentedPurityFeature);
						}
					}	
				}
			}
			
			if(FeatureType.FIG_DURATION_PURITY.enabled()) {
				featuresWeight = Weight.valueOf("DURATION");
				int figDurationPurityValue = purity(featuresWeight, root, parentNotes, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency);
				
				
				List<Integer> overlappedPurityValues = new ArrayList<Integer>();
				overlappedPurityValues.add(figDurationPurityValue);
				
				if(overlappedConsistency) {
					for(int bin : overlappedBins) {
						if(figDurationPurityValue > bin) {
							overlappedPurityValues.add(bin);
						}
					}
				}
				
				for(int overlappedPurityValue : overlappedPurityValues) {		
					String figDurationPurityFeatureName = FeatureType.FIG_DURATION_PURITY.name() + "_" + overlappedPurityValue;
					if(countFeatures) {
						int figDurationPurityFeature = param_g.toFeature(FeatureType.FIG_DURATION_PURITY.name(), "", overlappedPurityValue + "");
						featureIDToName.set(figDurationPurityFeature, figDurationPurityFeatureName);
						segmentFeatures.add(figDurationPurityFeature);
					}
					else {
						Integer count = featureNameToID.get(figDurationPurityFeatureName) ;
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figDurationPurityFeature = param_g.toFeature(FeatureType.FIG_DURATION_PURITY.name(), "", overlappedPurityValue + "");
	//						System.out.println(purityFeatureName + " " + count);
							segmentFeatures.add(figDurationPurityFeature);
						}
					}	
				}
			}
			
			// PURITY FEATURES
			boolean is_aug6_chord = isAug6Chord(parentLabel);
			boolean is_fr_or_ger_chord = isFrOrGerChord(parentLabel);
			boolean is_sus_or_pow_chord = isSusOrPowChord(parentLabel);
			boolean is_sus_chord = isSusChord(parentLabel);
			boolean is_pow_chord = isPowChord(parentLabel);
			boolean is_7sus4_chord = mode.equals("7sus4");
			boolean is_reg_chord = !is_aug6_chord && !is_sus_or_pow_chord;
			
			int interval1 = 0;
			boolean root_covered = (is_aug6_chord || is_sus_or_pow_chord) ? false : coverage(interval1, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord);
			if(FeatureType.ROOT_COVERED.enabled()) {
				if(root_covered) {	
					String rootCoveredFeatureName = FeatureType.ROOT_COVERED.name();
					if(countFeatures) {
						int rootCoveredFeature = param_g.toFeature(FeatureType.ROOT_COVERED.name(), "", "");
						featureIDToName.set(rootCoveredFeature, rootCoveredFeatureName);
						segmentFeatures.add(rootCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(rootCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int rootCoveredFeature = param_g.toFeature(FeatureType.ROOT_COVERED.name(), "", "");
//							System.out.println(rootCoveredFeatureName);
							segmentFeatures.add(rootCoveredFeature);
						}
					}
				}
			}
			
			int interval2 = 1;
			boolean third_covered = (is_aug6_chord || is_sus_or_pow_chord) ? false : coverage(interval2, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord);
			if(FeatureType.THIRD_COVERED.enabled()) {
				if(third_covered) {		
					String thirdCoveredFeatureName = FeatureType.THIRD_COVERED.name();
					if(countFeatures) {
						int thirdCoveredFeature = param_g.toFeature(FeatureType.THIRD_COVERED.name(), "", "");
						featureIDToName.set(thirdCoveredFeature, thirdCoveredFeatureName);
						segmentFeatures.add(thirdCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(thirdCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int thirdCoveredFeature = param_g.toFeature(FeatureType.THIRD_COVERED.name(), "", "");
//							System.out.println(thirdCoveredFeatureName);
							segmentFeatures.add(thirdCoveredFeature);
						}
					}
				}
			}
			
			int interval3 = 2;
			boolean fifth_covered = (is_aug6_chord || is_sus_or_pow_chord) ? false : coverage(interval3, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord);
			if(FeatureType.FIFTH_COVERED.enabled()) {
				if(fifth_covered) {
					String fifthCoveredFeatureName = FeatureType.FIFTH_COVERED.name();
					if(countFeatures) {
						int fifthCoveredFeature = param_g.toFeature(FeatureType.FIFTH_COVERED.name(), "",  "");
						featureIDToName.set(fifthCoveredFeature, fifthCoveredFeatureName);
						segmentFeatures.add(fifthCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(fifthCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int fifthCoveredFeature = param_g.toFeature(FeatureType.FIFTH_COVERED.name(), "",  "");
//							System.out.println(fifthCoveredFeatureName);
							segmentFeatures.add(fifthCoveredFeature);
						}
					}
				}
			}
			
			int interval4 = 3;
			boolean added_note_covered = (is_aug6_chord || is_sus_or_pow_chord) ? false : coverage(interval4, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord);
			if(FeatureType.ADDED_NOTE_COVERED.enabled()) {
				if(added_note_covered) {
					String addedNoteCoveredFeatureName = FeatureType.ADDED_NOTE_COVERED.name();
					if(countFeatures) {
						int addedNoteCoveredFeature = param_g.toFeature(FeatureType.ADDED_NOTE_COVERED.name(), "", "");
						featureIDToName.set(addedNoteCoveredFeature, addedNoteCoveredFeatureName);
						segmentFeatures.add(addedNoteCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(addedNoteCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int addedNoteCoveredFeature = param_g.toFeature(FeatureType.ADDED_NOTE_COVERED.name(), "", "");
//							System.out.println(addedNoteCoveredFeatureName);
							segmentFeatures.add(addedNoteCoveredFeature);
						}
					}
				}
			}
			
			if(FeatureType.ADDED_NOTE_NOT_COVERED.enabled()) {
				if(!addedNote.isEmpty() && !added_note_covered) {
					String addedNoteNotCoveredFeatureName = FeatureType.ADDED_NOTE_NOT_COVERED.name();
					if(countFeatures) {
						int addedNoteNotCoveredFeature = param_g.toFeature(FeatureType.ADDED_NOTE_NOT_COVERED.name(), "", "");
						featureIDToName.set(addedNoteNotCoveredFeature, addedNoteNotCoveredFeatureName);
						segmentFeatures.add(addedNoteNotCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(addedNoteNotCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int addedNoteNotCoveredFeature = param_g.toFeature(FeatureType.ADDED_NOTE_NOT_COVERED.name(), "", "");
//							System.out.println(addedNoteNotCoveredFeatureName);
							segmentFeatures.add(addedNoteNotCoveredFeature);
						}
					}
				}
			}
			
			boolean sus_or_pow_root_covered = is_sus_or_pow_chord ? coverage(interval1, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.SUS_OR_POW_ROOT_COVERED.enabled()) {
				if(sus_or_pow_root_covered) {
					String susOrPowRootCoveredFeatureName = FeatureType.SUS_OR_POW_ROOT_COVERED.name();
					if(countFeatures) {
						int susOrPowRootCoveredFeature = param_g.toFeature(susOrPowRootCoveredFeatureName, "", "");
						featureIDToName.set(susOrPowRootCoveredFeature, susOrPowRootCoveredFeatureName);
						segmentFeatures.add(susOrPowRootCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(susOrPowRootCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int susOrPowRootCoveredFeature = param_g.toFeature(susOrPowRootCoveredFeatureName, "", "");
//							System.out.println(susOrPowRootCoveredFeatureName);
							segmentFeatures.add(susOrPowRootCoveredFeature);
						}
					}
				}
			}
			
			boolean sus_or_pow_2nd_or_4th_covered = is_sus_chord ? coverage(interval2, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.SUS_OR_POW_2ND_OR_4TH_COVERED.enabled()) {
				if(sus_or_pow_2nd_or_4th_covered) {
					String susOrPow2ndOr4thCoveredFeatureName = FeatureType.SUS_OR_POW_2ND_OR_4TH_COVERED.name();
					if(countFeatures) {
						int susOrPow2ndOr4thCoveredFeature = param_g.toFeature(susOrPow2ndOr4thCoveredFeatureName, "", "");
						featureIDToName.set(susOrPow2ndOr4thCoveredFeature, susOrPow2ndOr4thCoveredFeatureName);
						segmentFeatures.add(susOrPow2ndOr4thCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(susOrPow2ndOr4thCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int susOrPow2ndOr4thCoveredFeature = param_g.toFeature(susOrPow2ndOr4thCoveredFeatureName, "", "");
//							System.out.println(susOrPow2ndOr4thCoveredFeatureName);
							segmentFeatures.add(susOrPow2ndOr4thCoveredFeature);
						}
					}
				}
			}
			
			boolean sus_or_pow_5th_covered = is_sus_or_pow_chord ? coverage(interval3, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.SUS_OR_POW_5TH_COVERED.enabled()) {
				if(sus_or_pow_5th_covered) {
					String susOrPow5thCoveredFeatureName = FeatureType.SUS_OR_POW_5TH_COVERED.name();
					if(countFeatures) {
						int susOrPow5thCoveredFeature = param_g.toFeature(susOrPow5thCoveredFeatureName, "", "");
						featureIDToName.set(susOrPow5thCoveredFeature, susOrPow5thCoveredFeatureName);
						segmentFeatures.add(susOrPow5thCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(susOrPow5thCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int susOrPow5thCoveredFeature = param_g.toFeature(susOrPow5thCoveredFeatureName, "", "");
//							System.out.println(susOrPow5thCoveredFeatureName);
							segmentFeatures.add(susOrPow5thCoveredFeature);
						}
					}
				}
			}
			
			boolean dom7sus4_7th_covered = is_7sus4_chord ? coverage(interval4, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.DOM7SUS4_7TH_COVERED.enabled()) {
				if(dom7sus4_7th_covered) {
//					System.out.println("7th of 7sus4 covered.");
					String dom7sus4_7thCoveredFeatureName = FeatureType.DOM7SUS4_7TH_COVERED.name();
					if(countFeatures) {
						int dom7sus4_7thCoveredFeature = param_g.toFeature(dom7sus4_7thCoveredFeatureName, "", "");
						featureIDToName.set(dom7sus4_7thCoveredFeature, dom7sus4_7thCoveredFeatureName);
						segmentFeatures.add(dom7sus4_7thCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(dom7sus4_7thCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int dom7sus4_7thCoveredFeature = param_g.toFeature(dom7sus4_7thCoveredFeatureName, "", "");
//							System.out.println(dom7sus4_7thCoveredFeatureName);
							segmentFeatures.add(dom7sus4_7thCoveredFeature);
						}
					}
				}
			}
			
			if(FeatureType.DOM7SUS4_7TH_NOT_COVERED.enabled()) {
				if(is_7sus4_chord && !dom7sus4_7th_covered) {
					String dom7sus4_7thNotCoveredFeatureName = FeatureType.DOM7SUS4_7TH_NOT_COVERED.name();
					if(countFeatures) {
						int dom7sus4_7thNotCoveredFeature = param_g.toFeature(dom7sus4_7thNotCoveredFeatureName, "", "");
						featureIDToName.set(dom7sus4_7thNotCoveredFeature, dom7sus4_7thNotCoveredFeatureName);
						segmentFeatures.add(dom7sus4_7thNotCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(dom7sus4_7thNotCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int dom7sus4_7thNotCoveredFeature = param_g.toFeature(dom7sus4_7thNotCoveredFeatureName, "", "");
//							System.out.println(dom7sus4_7thNotCoveredFeatureName);
							segmentFeatures.add(dom7sus4_7thNotCoveredFeature);
						}
					}
				}
			}
			
			boolean aug6_bass_covered = is_aug6_chord ? coverage(interval1, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.AUG6_BASS_COVERED.enabled()) {
				if(aug6_bass_covered) {
					String aug6BassCoveredFeatureName = FeatureType.AUG6_BASS_COVERED.name();
					if(countFeatures) {
						int aug6BassCoveredFeature = param_g.toFeature(aug6BassCoveredFeatureName, "", "");
						featureIDToName.set(aug6BassCoveredFeature, aug6BassCoveredFeatureName);
						segmentFeatures.add(aug6BassCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(aug6BassCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int aug6BassCoveredFeature = param_g.toFeature(aug6BassCoveredFeatureName, "", "");
//							System.out.println(aug6BassCoveredFeatureName);
							segmentFeatures.add(aug6BassCoveredFeature);
						}
					}
				}
			}
			
			boolean aug6_3rd_covered = is_aug6_chord ? coverage(interval2, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.AUG6_3RD_COVERED.enabled()) {
				if(aug6_3rd_covered) {
					String aug6_3rdCoveredFeatureName = FeatureType.AUG6_3RD_COVERED.name();
					if(countFeatures) {
						int aug6_3rdCoveredFeature = param_g.toFeature(aug6_3rdCoveredFeatureName, "", "");
						featureIDToName.set(aug6_3rdCoveredFeature, aug6_3rdCoveredFeatureName);
						segmentFeatures.add(aug6_3rdCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(aug6_3rdCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int aug6_3rdCoveredFeature = param_g.toFeature(aug6_3rdCoveredFeatureName, "", "");
//							System.out.println(aug6_3rdCoveredFeatureName);
							segmentFeatures.add(aug6_3rdCoveredFeature);
						}
					}
				}
			}
			
			boolean aug6_6th_covered = is_aug6_chord ? coverage(interval3, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.AUG6_6TH_COVERED.enabled()) {
				if(aug6_6th_covered) {
					String aug6_6thCoveredFeatureName = FeatureType.AUG6_6TH_COVERED.name();
					if(countFeatures) {
						int aug6_6thCoveredFeature = param_g.toFeature(aug6_6thCoveredFeatureName, "", "");
						featureIDToName.set(aug6_6thCoveredFeature, aug6_6thCoveredFeatureName);
						segmentFeatures.add(aug6_6thCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(aug6_6thCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int aug6_6thCoveredFeature = param_g.toFeature(aug6_6thCoveredFeatureName, "", "");
//							System.out.println(aug6_6thCoveredFeatureName);
							segmentFeatures.add(aug6_6thCoveredFeature);
						}
					}
				}
			}
			
			boolean aug6_5th_covered = is_fr_or_ger_chord ? coverage(interval4, addedNote, parentNotes, segmentNotes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord) : false;
			if(FeatureType.AUG6_5TH_COVERED.enabled()) {
				if(aug6_5th_covered) {
					String aug6_5thCoveredFeatureName = FeatureType.AUG6_5TH_COVERED.name();
					if(countFeatures) {
						int aug6_5thCoveredFeature = param_g.toFeature(aug6_5thCoveredFeatureName, "", "");
						featureIDToName.set(aug6_5thCoveredFeature, aug6_5thCoveredFeatureName);
						segmentFeatures.add(aug6_5thCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(aug6_5thCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int aug6_5thCoveredFeature = param_g.toFeature(aug6_5thCoveredFeatureName, "", "");
//							System.out.println(aug6_5thCoveredFeatureName);
							segmentFeatures.add(aug6_5thCoveredFeature);
						}
					}
				}
			}
			
			if(FeatureType.ALL_NOTES_COVERED.enabled()) {
				boolean triad_covered = addedNote.isEmpty() && root_covered && third_covered && fifth_covered;
				boolean added_note_chord_covered = !addedNote.isEmpty() && root_covered && third_covered && fifth_covered && added_note_covered;
				boolean pow_covered = is_pow_chord && sus_or_pow_root_covered && sus_or_pow_5th_covered;
				boolean sus2_or_sus4_covered = !is_7sus4_chord && sus_or_pow_root_covered && sus_or_pow_2nd_or_4th_covered && sus_or_pow_5th_covered;
				boolean dom7sus4_covered = sus_or_pow_root_covered && sus_or_pow_2nd_or_4th_covered && sus_or_pow_5th_covered && dom7sus4_7th_covered;
				boolean aug6_covered = aug6_bass_covered && aug6_3rd_covered && aug6_6th_covered;
				if(is_fr_or_ger_chord && !aug6_5th_covered) {
					aug6_covered = false;
				}
				boolean all_notes_covered = (triad_covered || added_note_chord_covered || aug6_covered || pow_covered || sus2_or_sus4_covered || dom7sus4_covered);
				
				if(all_notes_covered) {
					String allNotesCoveredFeatureName = FeatureType.ALL_NOTES_COVERED.name();
					if(countFeatures) {
						int allNotesCoveredFeature = param_g.toFeature(allNotesCoveredFeatureName, "", "");
						featureIDToName.set(allNotesCoveredFeature, allNotesCoveredFeatureName);
						segmentFeatures.add(allNotesCoveredFeature);
					}
					else {
						Integer count = featureNameToID.get(allNotesCoveredFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int allNotesCoveredFeature = param_g.toFeature(allNotesCoveredFeatureName, "", "");
							segmentFeatures.add(allNotesCoveredFeature);
						}
					}
				}
			}
			
			if(FeatureType.DURATION_ADDED_NOTE_GREATER_THAN_ROOT.enabled()) {
				if(!is_aug6_chord && !is_sus_or_pow_chord && !addedNote.isEmpty() && durationAddedNoteGreaterThanRoot(parentNotes, segmentNotes)) {
//					System.out.println("Duration added note greater than root");
					String durationAddedNoteGreaterThanRootFeatureName = FeatureType.DURATION_ADDED_NOTE_GREATER_THAN_ROOT.name();
					if(countFeatures) {
						int durationAddedNoteGreaterThanRootFeature = param_g.toFeature(durationAddedNoteGreaterThanRootFeatureName, "", "");
						featureIDToName.set(durationAddedNoteGreaterThanRootFeature, durationAddedNoteGreaterThanRootFeatureName);
						segmentFeatures.add(durationAddedNoteGreaterThanRootFeature);
					}
					else {
						Integer count = featureNameToID.get(durationAddedNoteGreaterThanRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int durationAddedNoteGreaterThanRootFeature = param_g.toFeature(durationAddedNoteGreaterThanRootFeatureName, "", "");
							segmentFeatures.add(durationAddedNoteGreaterThanRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.DURATION_7TH_OF_7SUS4_GREATER_THAN_ROOT.enabled()) {
				if(is_7sus4_chord && durationAddedNoteGreaterThanRoot(parentNotes, segmentNotes)) {
//					System.out.println("Duration 7th of 7sus4 greater than root");
					String duration7thOf7sus4GreaterThanRootFeatureName = FeatureType.DURATION_7TH_OF_7SUS4_GREATER_THAN_ROOT.name();
					if(countFeatures) {
						int duration7thOf7sus4GreaterThanRootFeature = param_g.toFeature(duration7thOf7sus4GreaterThanRootFeatureName, "", "");
						featureIDToName.set(duration7thOf7sus4GreaterThanRootFeature, duration7thOf7sus4GreaterThanRootFeatureName);
						segmentFeatures.add(duration7thOf7sus4GreaterThanRootFeature);
					}
					else {
						Integer count = featureNameToID.get(duration7thOf7sus4GreaterThanRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int durationAddedNoteGreaterThanRootFeature = param_g.toFeature(duration7thOf7sus4GreaterThanRootFeatureName, "", "");
							segmentFeatures.add(durationAddedNoteGreaterThanRootFeature);
						}
					}
				}
			}
			
			
			if(FeatureType.DURATION_ROOT_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationRootCoveredFeatureName = FeatureType.DURATION_ROOT_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationRootCoveredFeature = param_g.toFeature(FeatureType.DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationRootCoveredFeature, durationRootCoveredFeatureName);
							segmentFeatures.add(durationRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationRootCoveredFeature = param_g.toFeature(FeatureType.DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationRootCoveredFeatureName);
								segmentFeatures.add(durationRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_ROOT_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationRootCoveredFeatureName = FeatureType.FIG_DURATION_ROOT_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationRootCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationRootCoveredFeature, figDurationRootCoveredFeatureName);
							segmentFeatures.add(figDurationRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationRootCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationRootCoveredFeatureName);
								segmentFeatures.add(figDurationRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_ROOT_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationRootCoveredFeatureName = FeatureType.SEGMENT_DURATION_ROOT_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationRootCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationRootCoveredFeature, segmentDurationRootCoveredFeatureName);
							segmentFeatures.add(segmentDurationRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationRootCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_ROOT_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationRootCoveredFeatureName);
								segmentFeatures.add(segmentDurationRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_THIRD_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationThirdCoveredFeatureName = FeatureType.DURATION_THIRD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationThirdCoveredFeature = param_g.toFeature(FeatureType.DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationThirdCoveredFeature, durationThirdCoveredFeatureName);
							segmentFeatures.add(durationThirdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationThirdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationThirdCoveredFeature = param_g.toFeature(FeatureType.DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationThirdCoveredFeatureName);
								segmentFeatures.add(durationThirdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_THIRD_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationThirdCoveredFeatureName = FeatureType.FIG_DURATION_THIRD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationThirdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationThirdCoveredFeature, figDurationThirdCoveredFeatureName);
							segmentFeatures.add(figDurationThirdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationThirdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationThirdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationRootCoveredFeatureName);
								segmentFeatures.add(figDurationThirdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_THIRD_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationThirdCoveredFeatureName = FeatureType.SEGMENT_DURATION_THIRD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationThirdCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationThirdCoveredFeature, segmentDurationThirdCoveredFeatureName);
							segmentFeatures.add(segmentDurationThirdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationThirdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationThirdCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_THIRD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationThirdCoveredFeatureName);
								segmentFeatures.add(segmentDurationThirdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_FIFTH_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationFifthCoveredFeatureName = FeatureType.DURATION_FIFTH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationFifthCoveredFeature = param_g.toFeature(FeatureType.DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationFifthCoveredFeature, durationFifthCoveredFeatureName);
							segmentFeatures.add(durationFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationFifthCoveredFeature = param_g.toFeature(FeatureType.DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationFifthCoveredFeatureName);
								segmentFeatures.add(durationFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_FIFTH_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String figDurationFifthCoveredFeatureName = FeatureType.FIG_DURATION_FIFTH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationFifthCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationFifthCoveredFeature, figDurationFifthCoveredFeatureName);
							segmentFeatures.add(figDurationFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationFifthCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationRootCoveredFeatureName);
								segmentFeatures.add(figDurationFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_FIFTH_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationFifthCoveredFeatureName = FeatureType.SEGMENT_DURATION_FIFTH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationFifthCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationFifthCoveredFeature, segmentDurationFifthCoveredFeatureName);
							segmentFeatures.add(segmentDurationFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationFifthCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_FIFTH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationFifthCoveredFeatureName);
								segmentFeatures.add(segmentDurationFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_ROOT_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentRootCoveredFeatureName = FeatureType.ACCENT_ROOT_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentRootCoveredFeature = param_g.toFeature(FeatureType.ACCENT_ROOT_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentRootCoveredFeature, accentRootCoveredFeatureName);
							segmentFeatures.add(accentRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentRootCoveredFeature = param_g.toFeature(FeatureType.ACCENT_ROOT_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentRootCoveredFeatureName);
								segmentFeatures.add(accentRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_ROOT_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {	
						String figAccentRootCoveredFeatureName = FeatureType.FIG_ACCENT_ROOT_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentRootCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_ROOT_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(figAccentRootCoveredFeature, figAccentRootCoveredFeatureName);
							segmentFeatures.add(figAccentRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentRootCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_ROOT_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(figAccentRootCoveredFeatureName);
								segmentFeatures.add(figAccentRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_THIRD_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentThirdCoveredFeatureName = FeatureType.ACCENT_THIRD_COVERED.name() + "_" + overlappedAccentValue;				
						if(countFeatures) {
							int accentThirdCoveredFeature = param_g.toFeature(FeatureType.ACCENT_THIRD_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentThirdCoveredFeature, accentThirdCoveredFeatureName);
							segmentFeatures.add(accentThirdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentThirdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentThirdCoveredFeature = param_g.toFeature(FeatureType.ACCENT_THIRD_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentThirdCoveredFeatureName);
								segmentFeatures.add(accentThirdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_THIRD_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentThirdCoveredFeatureName = FeatureType.FIG_ACCENT_THIRD_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentThirdCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_THIRD_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(figAccentThirdCoveredFeature, figAccentThirdCoveredFeatureName);
							segmentFeatures.add(figAccentThirdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentThirdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentThirdCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_THIRD_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(figAccentThirdCoveredFeatureName);
								segmentFeatures.add(figAccentThirdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_FIFTH_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentFifthCoveredFeatureName = FeatureType.ACCENT_FIFTH_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentFifthCoveredFeature = param_g.toFeature(FeatureType.ACCENT_FIFTH_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentFifthCoveredFeature, accentFifthCoveredFeatureName);
							segmentFeatures.add(accentFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentFifthCoveredFeature = param_g.toFeature(FeatureType.ACCENT_FIFTH_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentFifthCoveredFeatureName);
								segmentFeatures.add(accentFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_FIFTH_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentFifthCoveredFeatureName = FeatureType.FIG_ACCENT_FIFTH_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentFifthCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_FIFTH_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(figAccentFifthCoveredFeature, figAccentFifthCoveredFeatureName);
							segmentFeatures.add(figAccentFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentFifthCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_FIFTH_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(figAccentFifthCoveredFeatureName);
								segmentFeatures.add(figAccentFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_ADDED_NOTE_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String durationAddedNoteCoveredFeatureName = FeatureType.DURATION_ADDED_NOTE_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationAddedNoteCoveredFeature, durationAddedNoteCoveredFeatureName);
							segmentFeatures.add(durationAddedNoteCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationAddedNoteCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationAddedNoteCoveredFeatureName);
								segmentFeatures.add(durationAddedNoteCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_ADDED_NOTE_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String figDurationAddedNoteCoveredFeatureName = FeatureType.FIG_DURATION_ADDED_NOTE_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAddedNoteCoveredFeature, figDurationAddedNoteCoveredFeatureName);
							segmentFeatures.add(figDurationAddedNoteCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAddedNoteCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAddedNoteCoveredFeatureName);
								segmentFeatures.add(figDurationAddedNoteCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_ADDED_NOTE_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String segmentDurationAddedNoteCoveredFeatureName = FeatureType.SEGMENT_DURATION_ADDED_NOTE_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationAddedNoteCoveredFeature, segmentDurationAddedNoteCoveredFeatureName);
							segmentFeatures.add(segmentDurationAddedNoteCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationAddedNoteCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationAddedNoteCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_ADDED_NOTE_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationAddedNoteCoveredFeatureName);
								segmentFeatures.add(segmentDurationAddedNoteCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_ADDED_NOTE_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentAddedNoteCoveredFeatureName = FeatureType.ACCENT_ADDED_NOTE_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentAddedNoteCoveredFeature = param_g.toFeature(FeatureType.ACCENT_ADDED_NOTE_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentAddedNoteCoveredFeature, accentAddedNoteCoveredFeatureName);
							segmentFeatures.add(accentAddedNoteCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentAddedNoteCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentAddedNoteCoveredFeature = param_g.toFeature(FeatureType.ACCENT_ADDED_NOTE_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentAddedNoteCoveredFeatureName);
								segmentFeatures.add(accentAddedNoteCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_ADDED_NOTE_COVERED.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentAddedNoteCoveredFeatureName = FeatureType.FIG_ACCENT_ADDED_NOTE_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentAddedNoteCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_ADDED_NOTE_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(figAccentAddedNoteCoveredFeature, figAccentAddedNoteCoveredFeatureName);
							segmentFeatures.add(figAccentAddedNoteCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentAddedNoteCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentAddedNoteCoveredFeature = param_g.toFeature(FeatureType.FIG_ACCENT_ADDED_NOTE_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(figAccentAddedNoteCoveredFeatureName);
								segmentFeatures.add(figAccentAddedNoteCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_ROOT_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationSusPowRootCoveredFeatureNameGeneric = FeatureType.DURATION_SUS_POW_ROOT_COVERED.name();
						String durationSusPowRootCoveredFeatureName = durationSusPowRootCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationSusPowRootCoveredFeature = param_g.toFeature(durationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(durationSusPowRootCoveredFeature, durationSusPowRootCoveredFeatureName);
							segmentFeatures.add(durationSusPowRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowRootCoveredFeature = param_g.toFeature(durationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(durationSusPowRootCoveredFeatureName);
								segmentFeatures.add(durationSusPowRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_ROOT_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationSusPowRootCoveredFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_ROOT_COVERED.name();
						String figDurationSusPowRootCoveredFeatureName = figDurationSusPowRootCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationSusPowRootCoveredFeature = param_g.toFeature(figDurationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(figDurationSusPowRootCoveredFeature, figDurationSusPowRootCoveredFeatureName);
							segmentFeatures.add(figDurationSusPowRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowRootCoveredFeature = param_g.toFeature(figDurationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(figDurationSusPowRootCoveredFeatureName);
								segmentFeatures.add(figDurationSusPowRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_SUS_POW_ROOT_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationSusPowRootCoveredFeatureNameGeneric = FeatureType.SEGMENT_DURATION_SUS_POW_ROOT_COVERED.name();
						String segmentDurationSusPowRootCoveredFeatureName = segmentDurationSusPowRootCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationSusPowRootCoveredFeature = param_g.toFeature(segmentDurationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationSusPowRootCoveredFeature, segmentDurationSusPowRootCoveredFeatureName);
							segmentFeatures.add(segmentDurationSusPowRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationSusPowRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationSusPowRootCoveredFeature = param_g.toFeature(segmentDurationSusPowRootCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationSusPowRootCoveredFeatureName);
								segmentFeatures.add(segmentDurationSusPowRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String durationSusPowSecondOrFourthCoveredFeatureName = durationSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(durationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(durationSusPowSecondOrFourthCoveredFeature, durationSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(durationSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(durationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(durationSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(durationSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String figDurationSusPowSecondOrFourthCoveredFeatureName = figDurationSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(figDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(figDurationSusPowSecondOrFourthCoveredFeature, figDurationSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(figDurationSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(figDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(figDurationSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(figDurationSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.SEGMENT_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String segmentDurationSusPowSecondOrFourthCoveredFeatureName = segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationSusPowSecondOrFourthCoveredFeature, segmentDurationSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(segmentDurationSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(segmentDurationSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_FIFTH_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationSusPowFifthCoveredFeatureNameGeneric = FeatureType.DURATION_SUS_POW_FIFTH_COVERED.name();
						String durationSusPowFifthCoveredFeatureName = durationSusPowFifthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationSusPowFifthCoveredFeature = param_g.toFeature(durationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(durationSusPowFifthCoveredFeature, durationSusPowFifthCoveredFeatureName);
							segmentFeatures.add(durationSusPowFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowFifthCoveredFeature = param_g.toFeature(durationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(durationSusPowFifthCoveredFeatureName);
								segmentFeatures.add(durationSusPowFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_FIFTH_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationSusPowFifthCoveredFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_FIFTH_COVERED.name();
						String figDurationSusPowFifthCoveredFeatureName = figDurationSusPowFifthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationSusPowFifthCoveredFeature = param_g.toFeature(figDurationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(figDurationSusPowFifthCoveredFeature, figDurationSusPowFifthCoveredFeatureName);
							segmentFeatures.add(figDurationSusPowFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowFifthCoveredFeature = param_g.toFeature(figDurationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(figDurationSusPowFifthCoveredFeatureName);
								segmentFeatures.add(figDurationSusPowFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_SUS_POW_FIFTH_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationSusPowFifthCoveredFeatureNameGeneric = FeatureType.SEGMENT_DURATION_SUS_POW_FIFTH_COVERED.name();
						String segmentDurationSusPowFifthCoveredFeatureName = segmentDurationSusPowFifthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationSusPowFifthCoveredFeature = param_g.toFeature(segmentDurationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationSusPowFifthCoveredFeature, segmentDurationSusPowFifthCoveredFeatureName);
							segmentFeatures.add(segmentDurationSusPowFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationSusPowFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationSusPowFifthCoveredFeature = param_g.toFeature(segmentDurationSusPowFifthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationSusPowFifthCoveredFeatureName);
								segmentFeatures.add(segmentDurationSusPowFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_ROOT_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentSusPowRootCoveredFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_ROOT_COVERED.name();
						String accentSusPowRootCoveredFeatureName = accentSusPowRootCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentSusPowRootCoveredFeature = param_g.toFeature(accentSusPowRootCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(accentSusPowRootCoveredFeature, accentSusPowRootCoveredFeatureName);
							segmentFeatures.add(accentSusPowRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowRootCoveredFeature = param_g.toFeature(accentSusPowRootCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(accentSusPowRootCoveredFeatureName);
								segmentFeatures.add(accentSusPowRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_ROOT_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentSusPowRootCoveredFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_ROOT_COVERED.name();
						String figAccentSusPowRootCoveredFeatureName = figAccentSusPowRootCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentSusPowRootCoveredFeature = param_g.toFeature(figAccentSusPowRootCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(figAccentSusPowRootCoveredFeature, figAccentSusPowRootCoveredFeatureName);
							segmentFeatures.add(figAccentSusPowRootCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowRootCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowRootCoveredFeature = param_g.toFeature(figAccentSusPowRootCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(figAccentSusPowRootCoveredFeatureName);
								segmentFeatures.add(figAccentSusPowRootCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String accentSusPowSecondOrFourthCoveredFeatureName = accentSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentSusPowSecondOrFourthCoveredFeature = param_g.toFeature(accentSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(accentSusPowSecondOrFourthCoveredFeature, accentSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(accentSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowSecondOrFourthCoveredFeature = param_g.toFeature(accentSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(accentSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(accentSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String figAccentSusPowSecondOrFourthCoveredFeatureName = figAccentSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentSusPowSecondOrFourthCoveredFeature = param_g.toFeature(figAccentSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(figAccentSusPowSecondOrFourthCoveredFeature, figAccentSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(figAccentSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowSecondOrFourthCoveredFeature = param_g.toFeature(figAccentSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(figAccentSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(figAccentSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_FIFTH_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentSusPowFifthCoveredFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_FIFTH_COVERED.name();
						String accentSusPowFifthCoveredFeatureName = accentSusPowFifthCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentSusPowFifthCoveredFeature = param_g.toFeature(accentSusPowFifthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(accentSusPowFifthCoveredFeature, accentSusPowFifthCoveredFeatureName);
							segmentFeatures.add(accentSusPowFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowFifthCoveredFeature = param_g.toFeature(accentSusPowFifthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(accentSusPowFifthCoveredFeatureName);
								segmentFeatures.add(accentSusPowFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_FIFTH_COVERED.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String figAccentSusPowFifthCoveredFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_FIFTH_COVERED.name();
						String figAccentSusPowFifthCoveredFeatureName = figAccentSusPowFifthCoveredFeatureNameGeneric + "_" + overlappedAccentValue;
						if(countFeatures) {
							int figAccentSusPowFifthCoveredFeature = param_g.toFeature(figAccentSusPowFifthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
							featureIDToName.set(figAccentSusPowFifthCoveredFeature, figAccentSusPowFifthCoveredFeatureName);
							segmentFeatures.add(figAccentSusPowFifthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowFifthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowFifthCoveredFeature = param_g.toFeature(figAccentSusPowFifthCoveredFeatureNameGeneric, "", overlappedAccentValue + "");
		//						System.out.println(figAccentSusPowFifthCoveredFeatureName);
								segmentFeatures.add(figAccentSusPowFifthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_7SUS4_SEVENTH_COVERED.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String durationSusPow7Sus4SeventhCoveredFeatureNameGeneric = FeatureType.DURATION_SUS_POW_7SUS4_SEVENTH_COVERED.name();
						String durationSusPow7Sus4SeventhCoveredFeatureName = durationSusPow7Sus4SeventhCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(durationSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(durationSusPow7Sus4SeventhCoveredFeature, durationSusPow7Sus4SeventhCoveredFeatureName);
							segmentFeatures.add(durationSusPow7Sus4SeventhCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPow7Sus4SeventhCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(durationSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(durationSusPow7Sus4SeventhCoveredFeatureName);
								segmentFeatures.add(durationSusPow7Sus4SeventhCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_7SUS4_SEVENTH_COVERED.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figDurationSusPow7Sus4SeventhCoveredFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_7SUS4_SEVENTH_COVERED.name();
						String figDurationSusPow7Sus4SeventhCoveredFeatureName = figDurationSusPow7Sus4SeventhCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(figDurationSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(figDurationSusPow7Sus4SeventhCoveredFeature, figDurationSusPow7Sus4SeventhCoveredFeatureName);
							segmentFeatures.add(figDurationSusPow7Sus4SeventhCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPow7Sus4SeventhCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(figDurationSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(figDurationSusPow7Sus4SeventhCoveredFeatureName);
								segmentFeatures.add(figDurationSusPow7Sus4SeventhCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_SUS_POW_7SUS4_SEVENTH_COVERED.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric = FeatureType.SEGMENT_DURATION_SUS_POW_SECOND_OR_FOURTH_COVERED.name();
						String segmentDurationSusPowSecondOrFourthCoveredFeatureName = segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationSusPowSecondOrFourthCoveredFeature, segmentDurationSusPowSecondOrFourthCoveredFeatureName);
							segmentFeatures.add(segmentDurationSusPowSecondOrFourthCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationSusPowSecondOrFourthCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationSusPowSecondOrFourthCoveredFeature = param_g.toFeature(segmentDurationSusPowSecondOrFourthCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationSusPowSecondOrFourthCoveredFeatureName);
								segmentFeatures.add(segmentDurationSusPowSecondOrFourthCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String accentSusPow7Sus4SeventhCoveredFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED.name();
						String accentSusPow7Sus4SeventhCoveredFeatureName = accentSusPow7Sus4SeventhCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int accentSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(accentSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(accentSusPow7Sus4SeventhCoveredFeature, accentSusPow7Sus4SeventhCoveredFeatureName);
							segmentFeatures.add(accentSusPow7Sus4SeventhCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPow7Sus4SeventhCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(accentSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(accentSusPow7Sus4SeventhCoveredFeatureName);
								segmentFeatures.add(accentSusPow7Sus4SeventhCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {
						String figAccentSusPow7Sus4SeventhCoveredFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_7SUS4_SEVENTH_COVERED.name();
						String figAccentSusPow7Sus4SeventhCoveredFeatureName = figAccentSusPow7Sus4SeventhCoveredFeatureNameGeneric + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figAccentSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(figAccentSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
							featureIDToName.set(figAccentSusPow7Sus4SeventhCoveredFeature, figAccentSusPow7Sus4SeventhCoveredFeatureName);
							segmentFeatures.add(figAccentSusPow7Sus4SeventhCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPow7Sus4SeventhCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPow7Sus4SeventhCoveredFeature = param_g.toFeature(figAccentSusPow7Sus4SeventhCoveredFeatureNameGeneric, "", overlappedDurationValue + "");
		//						System.out.println(figAccentSusPow7Sus4SeventhCoveredFeatureName);
								segmentFeatures.add(figAccentSusPow7Sus4SeventhCoveredFeature);
							}
						}
					}
				}
			}
			
			
			if(FeatureType.DURATION_AUG6_BASS_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String durationAug6BassCoveredFeatureName = FeatureType.DURATION_AUG6_BASS_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationAug6BassCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationAug6BassCoveredFeature, durationAug6BassCoveredFeatureName);
							segmentFeatures.add(durationAug6BassCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationAug6BassCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationAug6BassCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationAug6BassCoveredFeatureName);
								segmentFeatures.add(durationAug6BassCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_AUG6_BASS_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String figDurationAug6BassCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6BassCoveredFeature, figDurationAug6BassCoveredFeatureName);
							segmentFeatures.add(figDurationAug6BassCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6BassCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6BassCoveredFeatureName);
								segmentFeatures.add(figDurationAug6BassCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_DURATION_AUG6_BASS_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String segmentDurationAug6BassCoveredFeatureName = FeatureType.SEGMENT_DURATION_AUG6_BASS_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationAug6BassCoveredFeature, segmentDurationAug6BassCoveredFeatureName);
							segmentFeatures.add(segmentDurationAug6BassCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationAug6BassCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationAug6BassCoveredFeatureName);
								segmentFeatures.add(segmentDurationAug6BassCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_AUG6_3RD_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String durationAug6_3rdCoveredFeatureName = FeatureType.DURATION_AUG6_3RD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationAug6_3rdCoveredFeature, durationAug6_3rdCoveredFeatureName);
							segmentFeatures.add(durationAug6_3rdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationAug6_3rdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationAug6_3rdCoveredFeatureName);
								segmentFeatures.add(durationAug6_3rdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_AUG6_3RD_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String figDurationAug6_3rdCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_3rdCoveredFeature, figDurationAug6_3rdCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_3rdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_3rdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_3rdCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_3rdCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.SEGMENT_DURATION_AUG6_3RD_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String segmentDurationAug6_3rdCoveredFeatureName = FeatureType.SEGMENT_DURATION_AUG6_3RD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationAug6_3rdCoveredFeature, segmentDurationAug6_3rdCoveredFeatureName);
							segmentFeatures.add(segmentDurationAug6_3rdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationAug6_3rdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationAug6_3rdCoveredFeatureName);
								segmentFeatures.add(segmentDurationAug6_3rdCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.DURATION_AUG6_6TH_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String durationAug6_6thCoveredFeatureName = FeatureType.DURATION_AUG6_6TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationAug6_6thCoveredFeature, durationAug6_6thCoveredFeatureName);
							segmentFeatures.add(durationAug6_6thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationAug6_6thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationAug6_6thCoveredFeatureName);
								segmentFeatures.add(durationAug6_6thCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_AUG6_6TH_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String figDurationAug6_6thCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_6thCoveredFeature, figDurationAug6_6thCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_6thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_6thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_6thCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_6thCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.SEGMENT_DURATION_AUG6_6TH_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String segmentDurationAug6_6thCoveredFeatureName = FeatureType.SEGMENT_DURATION_AUG6_6TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationAug6_6thCoveredFeature, segmentDurationAug6_6thCoveredFeatureName);
							segmentFeatures.add(segmentDurationAug6_6thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationAug6_6thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationAug6_6thCoveredFeatureName);
								segmentFeatures.add(segmentDurationAug6_6thCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.DURATION_AUG6_5TH_COVERED.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String durationAug6_5thCoveredFeatureName = FeatureType.DURATION_AUG6_5TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int durationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(durationAug6_5thCoveredFeature, durationAug6_5thCoveredFeatureName);
							segmentFeatures.add(durationAug6_5thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(durationAug6_5thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(durationAug6_5thCoveredFeatureName);
								segmentFeatures.add(durationAug6_5thCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_AUG6_5TH_COVERED.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {	
						String figDurationAug6_5thCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_5thCoveredFeature, figDurationAug6_5thCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_5thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_5thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_5thCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_5thCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.SEGMENT_DURATION_AUG6_5TH_COVERED.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int segmentDurationValue = segment_weighted_duration_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(segmentDurationValue);
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(segmentDurationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String segmentDurationAug6_5thCoveredFeatureName = FeatureType.SEGMENT_DURATION_AUG6_5TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int segmentDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(segmentDurationAug6_5thCoveredFeature, segmentDurationAug6_5thCoveredFeatureName);
							segmentFeatures.add(segmentDurationAug6_5thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(segmentDurationAug6_5thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int segmentDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.SEGMENT_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(segmentDurationAug6_5thCoveredFeatureName);
								segmentFeatures.add(segmentDurationAug6_5thCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.ACCENT_AUG6_BASS_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentAug6BassCoveredFeatureName = FeatureType.ACCENT_AUG6_BASS_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentAug6BassCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_BASS_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentAug6BassCoveredFeature, accentAug6BassCoveredFeatureName);
							segmentFeatures.add(accentAug6BassCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentAug6BassCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentAug6BassCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_BASS_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentAug6BassCoveredFeatureName);
								segmentFeatures.add(accentAug6BassCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_AUG6_BASS_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String figDurationAug6BassCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6BassCoveredFeature, figDurationAug6BassCoveredFeatureName);
							segmentFeatures.add(figDurationAug6BassCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6BassCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6BassCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_BASS_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6BassCoveredFeatureName);
								segmentFeatures.add(figDurationAug6BassCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.ACCENT_AUG6_3RD_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentAug6_3rdCoveredFeatureName = FeatureType.ACCENT_AUG6_3RD_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_3RD_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentAug6_3rdCoveredFeature, accentAug6_3rdCoveredFeatureName);
							segmentFeatures.add(accentAug6_3rdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentAug6_3rdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_3RD_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentAug6_3rdCoveredFeatureName);
								segmentFeatures.add(accentAug6_3rdCoveredFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.FIG_ACCENT_AUG6_3RD_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String figDurationAug6_3rdCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_3rdCoveredFeature, figDurationAug6_3rdCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_3rdCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_3rdCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_3rdCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_3RD_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_3rdCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_3rdCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_AUG6_6TH_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentAug6_6thCoveredFeatureName = FeatureType.ACCENT_AUG6_6TH_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentAug6_6thCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_6TH_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentAug6_6thCoveredFeature, accentAug6_6thCoveredFeatureName);
							segmentFeatures.add(accentAug6_6thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentAug6_6thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentAug6_6thCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_6TH_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentAug6_6thCoveredFeatureName);
								segmentFeatures.add(accentAug6_6thCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_AUG6_6TH_COVERED.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String figDurationAug6_6thCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_6thCoveredFeature, figDurationAug6_6thCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_6thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_6thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_6thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_6TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_6thCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_6thCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_AUG6_5TH_COVERED.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int accentValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, segmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedAccentValues = new ArrayList<Integer>();
					overlappedAccentValues.add(accentValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(accentValue > bin) {
								overlappedAccentValues.add(bin);
							}
						}
					}
					
					for(int overlappedAccentValue : overlappedAccentValues) {
						String accentAug6_5thCoveredFeatureName = FeatureType.ACCENT_AUG6_5TH_COVERED.name() + "_" + overlappedAccentValue;
						if(countFeatures) {
							int accentAug6_5thCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_5TH_COVERED.name(), "", overlappedAccentValue + "");
							featureIDToName.set(accentAug6_5thCoveredFeature, accentAug6_5thCoveredFeatureName);
							segmentFeatures.add(accentAug6_5thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(accentAug6_5thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentAug6_5thCoveredFeature = param_g.toFeature(FeatureType.ACCENT_AUG6_5TH_COVERED.name(), "", overlappedAccentValue + "");
		//						System.out.println(accentAug6_5thCoveredFeatureName);
								segmentFeatures.add(accentAug6_5thCoveredFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_AUG6_5TH_COVERED.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int durationValue = weighted_coverage(featuresWeight, addedNote, parentNotes, interval, nonFigSegmentNotes, eventsInside, parentLabel, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedDurationValues = new ArrayList<Integer>();
					overlappedDurationValues.add(durationValue);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(durationValue > bin) {
								overlappedDurationValues.add(bin);
							}
						}
					}
					
					for(int overlappedDurationValue : overlappedDurationValues) {		
						String figDurationAug6_5thCoveredFeatureName = FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name() + "_" + overlappedDurationValue;
						if(countFeatures) {
							int figDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
							featureIDToName.set(figDurationAug6_5thCoveredFeature, figDurationAug6_5thCoveredFeatureName);
							segmentFeatures.add(figDurationAug6_5thCoveredFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationAug6_5thCoveredFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationAug6_5thCoveredFeature = param_g.toFeature(FeatureType.FIG_DURATION_AUG6_5TH_COVERED.name(), "", overlappedDurationValue + "");
		//						System.out.println(figDurationAug6_5thCoveredFeatureName);
								segmentFeatures.add(figDurationAug6_5thCoveredFeature);
							}
						}
					}
				}
			}
	
			if(FeatureType.BEGINNING_ACCENTED.enabled()) {
				String beginningAccentedFeatureName = FeatureType.BEGINNING_ACCENTED.name() + "_" + beginningAccented(eventsInside);
				if(countFeatures) {
					int beginningAccentedFeature = param_g.toFeature(FeatureType.BEGINNING_ACCENTED.name(), "", beginningAccented(eventsInside) + "");
					featureIDToName.set(beginningAccentedFeature, beginningAccentedFeatureName);
					segmentFeatures.add(beginningAccentedFeature);
				}
				else {
					Integer count = featureNameToID.get(beginningAccentedFeatureName);
					if((count != null) && (count > MIN_FEATURE_COUNT)) {
						int beginningAccentedFeature = param_g.toFeature(FeatureType.BEGINNING_ACCENTED.name(), "", beginningAccented(eventsInside) + "");
//						System.out.println(beginningAccentedFeatureName);
						segmentFeatures.add(beginningAccentedFeature);
					}
				}
			}
			
			// BASS
			
			// (first event)
			Event firstEvent = eventsInside.get(0);
			List<Note> notesInFirstEvent = firstEvent.notes;
			Note firstBass = findBassNote(notesInFirstEvent);
			
			if(FeatureType.FIRST_BASS_IS_ROOT.enabled()) {
				int interval = 0;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsRootFeatureName = FeatureType.FIRST_BASS_IS_ROOT.name();
					if(countFeatures) {
						int firstBassIsRootFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_ROOT.name(), "", "");
						featureIDToName.set(firstBassIsRootFeature, firstBassIsRootFeatureName);
						segmentFeatures.add(firstBassIsRootFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsRootFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_ROOT.name(), "", "");
//							System.out.println(firstBassIsRootFeatureName);
							segmentFeatures.add(firstBassIsRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_THIRD.enabled()) {
				int interval = 1;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsThirdFeatureName = FeatureType.FIRST_BASS_IS_THIRD.name();
					if(countFeatures) {
						int firstBassIsThirdFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_THIRD.name(), "", "");
						featureIDToName.set(firstBassIsThirdFeature, firstBassIsThirdFeatureName);
						segmentFeatures.add(firstBassIsThirdFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsThirdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsThirdFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_THIRD.name(), "", "");
//							System.out.println(firstBassIsThirdFeatureName);
							segmentFeatures.add(firstBassIsThirdFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_FIFTH.enabled()) {
				int interval = 2;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + firstBassIsFifthFeature);
					String firstBassIsFifthFeatureName = FeatureType.FIRST_BASS_IS_FIFTH.name();
					if(countFeatures) {
						int firstBassIsFifthFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_FIFTH.name(), "", "");
						featureIDToName.set(firstBassIsFifthFeature, firstBassIsFifthFeatureName);
						segmentFeatures.add(firstBassIsFifthFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsFifthFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsFifthFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_FIFTH.name(), "", "");
//							System.out.println(firstBassIsFifthFeatureName);
							segmentFeatures.add(firstBassIsFifthFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_ADDED_NOTE.enabled()) {
				int interval = 3;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
					String firstBassIsAddedNoteFeatureName = FeatureType.FIRST_BASS_IS_ADDED_NOTE.name();
					if(countFeatures) {
						int firstBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_ADDED_NOTE.name(), "", "");
						featureIDToName.set(firstBassIsAddedNoteFeature, firstBassIsAddedNoteFeatureName);
						segmentFeatures.add(firstBassIsAddedNoteFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsAddedNoteFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_ADDED_NOTE.name(), "", "");
//							System.out.println(firstBassIsAddedNoteFeatureName);
							segmentFeatures.add(firstBassIsAddedNoteFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_SUS_POW_ROOT.enabled()) {
				int interval = 0;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsSusPowRootFeatureName = FeatureType.FIRST_BASS_IS_SUS_POW_ROOT.name();
					if(countFeatures) {
						int firstBassIsSusPowRootFeature = param_g.toFeature(firstBassIsSusPowRootFeatureName, "", "");
						featureIDToName.set(firstBassIsSusPowRootFeature, firstBassIsSusPowRootFeatureName);
						segmentFeatures.add(firstBassIsSusPowRootFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsSusPowRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsSusPowRootFeature = param_g.toFeature(firstBassIsSusPowRootFeatureName, "", "");
//							System.out.println(firstBassIsSusPowRootFeatureName);
							segmentFeatures.add(firstBassIsSusPowRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_SUS_POW_2ND_OR_4TH.enabled()) {
				int interval = 1;
				if(is_sus_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsSusPow2ndOr4thFeatureName = FeatureType.FIRST_BASS_IS_SUS_POW_2ND_OR_4TH.name();
					if(countFeatures) {
						int firstBassIsSusPow2ndOr4thFeature = param_g.toFeature(firstBassIsSusPow2ndOr4thFeatureName, "", "");
						featureIDToName.set(firstBassIsSusPow2ndOr4thFeature, firstBassIsSusPow2ndOr4thFeatureName);
						segmentFeatures.add(firstBassIsSusPow2ndOr4thFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsSusPow2ndOr4thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsSusPow2ndOr4thFeature = param_g.toFeature(firstBassIsSusPow2ndOr4thFeatureName, "", "");
//							System.out.println(firstBassIsSusPow2ndOr4thFeatureName);
							segmentFeatures.add(firstBassIsSusPow2ndOr4thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_SUS_POW_5TH.enabled()) {
				int interval = 2;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsSusPow5thFeatureName = FeatureType.FIRST_BASS_IS_SUS_POW_5TH.name();
					if(countFeatures) {
						int firstBassIsSusPow5thFeature = param_g.toFeature(firstBassIsSusPow5thFeatureName, "", "");
						featureIDToName.set(firstBassIsSusPow5thFeature, firstBassIsSusPow5thFeatureName);
						segmentFeatures.add(firstBassIsSusPow5thFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsSusPow5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsSusPow5thFeature = param_g.toFeature(firstBassIsSusPow5thFeatureName, "", "");
//							System.out.println(firstBassIsSusPow5thFeatureName);
							segmentFeatures.add(firstBassIsSusPow5thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_SUS_POW_7SUS4_7TH.enabled()) {
				int interval = 3;
				if(is_7sus4_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String firstBassIsSusPow7Sus4_7thFeatureName = FeatureType.FIRST_BASS_IS_SUS_POW_7SUS4_7TH.name();
					if(countFeatures) {
						int firstBassIsSusPow7Sus4_7thFeature = param_g.toFeature(firstBassIsSusPow7Sus4_7thFeatureName, "", "");
						featureIDToName.set(firstBassIsSusPow7Sus4_7thFeature, firstBassIsSusPow7Sus4_7thFeatureName);
						segmentFeatures.add(firstBassIsSusPow7Sus4_7thFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsSusPow7Sus4_7thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsSusPow7Sus4_7thFeature = param_g.toFeature(firstBassIsSusPow7Sus4_7thFeatureName, "", "");
//							System.out.println(firstBassIsSusPow7Sus4_7thFeatureName);
							segmentFeatures.add(firstBassIsSusPow7Sus4_7thFeature);
						}
					}
				}
			}	
			
			if(FeatureType.FIRST_BASS_IS_AUG6_BASS.enabled()) {
				int interval = 0;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + firstBassIsAug6BassFeature);
					String firstBassIsAug6BassFeatureName = FeatureType.FIRST_BASS_IS_AUG6_BASS.name();
					if(countFeatures) {
						int firstBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_BASS.name(), "", "");
						featureIDToName.set(firstBassIsAug6BassFeature, firstBassIsAug6BassFeatureName);
						segmentFeatures.add(firstBassIsAug6BassFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsAug6BassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_BASS.name(), "", "");
//							System.out.println(firstBassIsAug6BassFeatureName);
							segmentFeatures.add(firstBassIsAug6BassFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_AUG6_3RD.enabled()) {
				int interval = 1;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + firstBassIsAug6_3rdFeature);
					String firstBassIsAug6_3rdFeatureName = FeatureType.FIRST_BASS_IS_AUG6_3RD.name();
					if(countFeatures) {
						int firstBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_3RD.name(), "", "");
						featureIDToName.set(firstBassIsAug6_3rdFeature, firstBassIsAug6_3rdFeatureName);
						segmentFeatures.add(firstBassIsAug6_3rdFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsAug6_3rdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_3RD.name(), "", "");
//							System.out.println(firstBassIsAug6_3rdFeatureName);
							segmentFeatures.add(firstBassIsAug6_3rdFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_AUG6_6TH.enabled()) {
				int interval = 2;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + firstBassIsAug6_6thBassFeature);
					String firstBassIsAug6_6thBassFeatureName = FeatureType.FIRST_BASS_IS_AUG6_6TH.name();
					if(countFeatures) {
						int firstBassIsAug6_6thBassFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_6TH.name(), "", "");
						featureIDToName.set(firstBassIsAug6_6thBassFeature, firstBassIsAug6_6thBassFeatureName);
						segmentFeatures.add(firstBassIsAug6_6thBassFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsAug6_6thBassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsAug6_6thBassFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_6TH.name(), "", "");
//							System.out.println(firstBassIsAug6_6thBassFeatureName);
							segmentFeatures.add(firstBassIsAug6_6thBassFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIRST_BASS_IS_AUG6_5TH.enabled()) {
				int interval = 3;
				if(is_fr_or_ger_chord && bassIsInterval(interval, addedNote, parentNotes, firstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + firstBassIsAug6_5thFeature);
					String firstBassIsAug6_5thFeatureName = FeatureType.FIRST_BASS_IS_AUG6_5TH.name();
					if(countFeatures) {
						int firstBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_5TH.name(), "", "");
						featureIDToName.set(firstBassIsAug6_5thFeature, firstBassIsAug6_5thFeatureName);
						segmentFeatures.add(firstBassIsAug6_5thFeature);
					}
					else {
						Integer count = featureNameToID.get(firstBassIsAug6_5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int firstBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIRST_BASS_IS_AUG6_5TH.name(), "", "");
//							System.out.println(firstBassIsAug6_5thFeatureName);
							segmentFeatures.add(firstBassIsAug6_5thFeature);
						}
					}
				}
			}
			
			Event secondEvent = ((childPos + 1) < (inputTokenized.size() - 1)) ? inputTokenized.get(childPos + 1) : new Event();
			List<Note> nonFigNotesInFirstEvent = getNonFigurationNotesInSegment(eventsInside.subList(0, 1), notesInFirstEvent, previousEvent, secondEvent, parentNotes);
			Note nonFigFirstBass = findBassNote(nonFigNotesInFirstEvent);
			
			if(FeatureType.FIG_FIRST_BASS_IS_ROOT.enabled()) {
				int interval = 0;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsRootFeatureName = FeatureType.FIG_FIRST_BASS_IS_ROOT.name();
					if(countFeatures) {
						int figFirstBassIsRootFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_ROOT.name(), "", "");
						featureIDToName.set(figFirstBassIsRootFeature, figFirstBassIsRootFeatureName);
						segmentFeatures.add(figFirstBassIsRootFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsRootFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_ROOT.name(), "", "");
//							System.out.println(figFirstBassIsRootFeatureName);
							segmentFeatures.add(figFirstBassIsRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_THIRD.enabled()) {
				int interval = 1;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsThirdFeatureName = FeatureType.FIG_FIRST_BASS_IS_THIRD.name();
					if(countFeatures) {
						int figFirstBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_THIRD.name(), "", "");
						featureIDToName.set(figFirstBassIsThirdFeature, figFirstBassIsThirdFeatureName);
						segmentFeatures.add(figFirstBassIsThirdFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsThirdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_THIRD.name(), "", "");
//							System.out.println(figFirstBassIsThirdFeatureName);
							segmentFeatures.add(figFirstBassIsThirdFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_FIFTH.enabled()) {
				int interval = 2;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsFifthFeatureName = FeatureType.FIG_FIRST_BASS_IS_FIFTH.name();
					if(countFeatures) {
						int figFirstBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_FIFTH.name(), "", "");
						featureIDToName.set(figFirstBassIsFifthFeature, figFirstBassIsFifthFeatureName);
						segmentFeatures.add(figFirstBassIsFifthFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsFifthFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_FIFTH.name(), "", "");
//							System.out.println(figFirstBassIsFifthFeatureName);
							segmentFeatures.add(figFirstBassIsFifthFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_ADDED_NOTE.enabled()) {
				int interval = 3;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
//					System.out.println("Feature id:" + firstBassIsRootFeature);
					String figFirstBassIsAddedNoteFeatureName = FeatureType.FIG_FIRST_BASS_IS_ADDED_NOTE.name();
					if(countFeatures) {
						int figFirstBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_ADDED_NOTE.name(), "", "");
						featureIDToName.set(figFirstBassIsAddedNoteFeature, figFirstBassIsAddedNoteFeatureName);
						segmentFeatures.add(figFirstBassIsAddedNoteFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsAddedNoteFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_ADDED_NOTE.name(), "", "");
//							System.out.println(figFirstBassIsAddedNoteFeatureName);
							segmentFeatures.add(figFirstBassIsAddedNoteFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_SUS_POW_ROOT.enabled()) {
				int interval = 0;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsSusPowRootFeatureName = FeatureType.FIG_FIRST_BASS_IS_SUS_POW_ROOT.name();
					if(countFeatures) {
						int figFirstBassIsSusPowRootFeature = param_g.toFeature(figFirstBassIsSusPowRootFeatureName, "", "");
						featureIDToName.set(figFirstBassIsSusPowRootFeature, figFirstBassIsSusPowRootFeatureName);
						segmentFeatures.add(figFirstBassIsSusPowRootFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsSusPowRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsSusPowRootFeature = param_g.toFeature(figFirstBassIsSusPowRootFeatureName, "", "");
//							System.out.println(figFirstBassIsSusPowRootFeatureName);
							segmentFeatures.add(figFirstBassIsSusPowRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_SUS_POW_2ND_OR_4TH.enabled()) {
				int interval = 1;
				if(is_sus_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsSusPow2ndOr4thFeatureName = FeatureType.FIG_FIRST_BASS_IS_SUS_POW_2ND_OR_4TH.name();
					if(countFeatures) {
						int figFirstBassIsSusPow2ndOr4thFeature = param_g.toFeature(figFirstBassIsSusPow2ndOr4thFeatureName, "", "");
						featureIDToName.set(figFirstBassIsSusPow2ndOr4thFeature, figFirstBassIsSusPow2ndOr4thFeatureName);
						segmentFeatures.add(figFirstBassIsSusPow2ndOr4thFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsSusPow2ndOr4thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsSusPow2ndOr4thFeature = param_g.toFeature(figFirstBassIsSusPow2ndOr4thFeatureName, "", "");
//							System.out.println(figFirstBassIsSusPow2ndOr4thFeatureName);
							segmentFeatures.add(figFirstBassIsSusPow2ndOr4thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_SUS_POW_5TH.enabled()) {
				int interval = 2;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsSusPow5thFeatureName = FeatureType.FIG_FIRST_BASS_IS_SUS_POW_5TH.name();
					if(countFeatures) {
						int figFirstBassIsSusPow5thFeature = param_g.toFeature(figFirstBassIsSusPow5thFeatureName, "", "");
						featureIDToName.set(figFirstBassIsSusPow5thFeature, figFirstBassIsSusPow5thFeatureName);
						segmentFeatures.add(figFirstBassIsSusPow5thFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsSusPow5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsSusPow5thFeature = param_g.toFeature(figFirstBassIsSusPow5thFeatureName, "", "");
//							System.out.println(figFirstBassIsSusPow5thFeatureName);
							segmentFeatures.add(figFirstBassIsSusPow5thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_SUS_POW_7SUS4_7TH.enabled()) {
				int interval = 3;
				if(is_7sus4_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figFirstBassIsSusPow7Sus4_7thFeatureName = FeatureType.FIG_FIRST_BASS_IS_SUS_POW_7SUS4_7TH.name();
					if(countFeatures) {
						int figFirstBassIsSusPow7Sus4_7thFeature = param_g.toFeature(figFirstBassIsSusPow7Sus4_7thFeatureName, "", "");
						featureIDToName.set(figFirstBassIsSusPow7Sus4_7thFeature, figFirstBassIsSusPow7Sus4_7thFeatureName);
						segmentFeatures.add(figFirstBassIsSusPow7Sus4_7thFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsSusPow7Sus4_7thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsSusPow7Sus4_7thFeature = param_g.toFeature(figFirstBassIsSusPow7Sus4_7thFeatureName, "", "");
//							System.out.println(figFirstBassIsSusPow7Sus4_7thFeatureName);
							segmentFeatures.add(figFirstBassIsSusPow7Sus4_7thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_AUG6_BASS.enabled()) {
				int interval = 0;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + figFirstBassIsAug6Bass_BassFeature);
					String figFirstBassIsAug6Bass_BassFeatureName = FeatureType.FIG_FIRST_BASS_IS_AUG6_BASS.name();
					if(countFeatures) {
						int figFirstBassIsAug6Bass_BassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_BASS.name(), "", "");
						featureIDToName.set(figFirstBassIsAug6Bass_BassFeature, figFirstBassIsAug6Bass_BassFeatureName);
						segmentFeatures.add(figFirstBassIsAug6Bass_BassFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsAug6Bass_BassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsAug6Bass_BassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_BASS.name(), "", "");
//							System.out.println(figFirstBassIsAug6Bass_BassFeatureName);
							segmentFeatures.add(figFirstBassIsAug6Bass_BassFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_AUG6_3RD.enabled()) {
				int interval = 1;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + figFirstBassIsAug6_3rdBassFeature);
					String figFirstBassIsAug6_3rdBassFeatureName = FeatureType.FIG_FIRST_BASS_IS_AUG6_3RD.name();
					if(countFeatures) {
						int figFirstBassIsAug6_3rdBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_3RD.name(), "", "");
						featureIDToName.set(figFirstBassIsAug6_3rdBassFeature, figFirstBassIsAug6_3rdBassFeatureName);
						segmentFeatures.add(figFirstBassIsAug6_3rdBassFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsAug6_3rdBassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsAug6_3rdBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_3RD.name(), "", "");
//							System.out.println(figFirstBassIsAug6_3rdBassFeatureName);
							segmentFeatures.add(figFirstBassIsAug6_3rdBassFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_AUG6_6TH.enabled()) {
				int interval = 2;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + figFirstBassIsAug6_6thBassFeature);
					String figFirstBassIsAug6_6thBassFeatureName = FeatureType.FIG_FIRST_BASS_IS_AUG6_6TH.name();
					if(countFeatures) {
						int figFirstBassIsAug6_6thBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_6TH.name(), "", "");
						featureIDToName.set(figFirstBassIsAug6_6thBassFeature, figFirstBassIsAug6_6thBassFeatureName);
						segmentFeatures.add(figFirstBassIsAug6_6thBassFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsAug6_6thBassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsAug6_6thBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_6TH.name(), "", "");
//							System.out.println(figFirstBassIsAug6_6thBassFeatureName);
							segmentFeatures.add(figFirstBassIsAug6_6thBassFeature);
						}
					}
				}				
			}
			
			if(FeatureType.FIG_FIRST_BASS_IS_AUG6_5TH.enabled()) {
				int interval = 3;
				if(is_fr_or_ger_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigFirstBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) { 
//					System.out.println("Feature id:" + figFirstBassIsAug6_5thBassFeature);
					String figFirstBassIsAug6_5thBassFeatureName = FeatureType.FIG_FIRST_BASS_IS_AUG6_5TH.name();
					if(countFeatures) {
						int figFirstBassIsAug6_5thBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_5TH.name(), "", "");
						featureIDToName.set(figFirstBassIsAug6_5thBassFeature, figFirstBassIsAug6_5thBassFeatureName);
						segmentFeatures.add(figFirstBassIsAug6_5thBassFeature);
					}
					else {
						Integer count = featureNameToID.get(figFirstBassIsAug6_5thBassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figFirstBassIsAug6_5thBassFeature = param_g.toFeature(FeatureType.FIG_FIRST_BASS_IS_AUG6_5TH.name(), "", "");
//							System.out.println(figFirstBassIsAug6_5thBassFeatureName);
							segmentFeatures.add(figFirstBassIsAug6_5thBassFeature);
						}
					}
				}
			}
			
			Note segmentBass = findBassNote(segmentNotes);

			if(FeatureType.SEGMENT_BASS_IS_ROOT.enabled()) {
				int interval = 0;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsRootFeatureName = FeatureType.SEGMENT_BASS_IS_ROOT.name();
					if(countFeatures) {
						int segmentBassIsRootFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_ROOT.name(), "", "");
						featureIDToName.set(segmentBassIsRootFeature, segmentBassIsRootFeatureName);
						segmentFeatures.add(segmentBassIsRootFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsRootFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_ROOT.name(), "", "");
//							System.out.println(segmentBassIsRootFeatureName);
							segmentFeatures.add(segmentBassIsRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_THIRD.enabled()) {
				int interval = 1;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsThirdFeatureName = FeatureType.SEGMENT_BASS_IS_THIRD.name();
					if(countFeatures) {
						int segmentBassIsThirdFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_THIRD.name(), "", "");
						featureIDToName.set(segmentBassIsThirdFeature, segmentBassIsThirdFeatureName);
						segmentFeatures.add(segmentBassIsThirdFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsThirdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsThirdFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_THIRD.name(), "", "");
//							System.out.println(segmentBassIsThirdFeatureName);
							segmentFeatures.add(segmentBassIsThirdFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_FIFTH.enabled()) {
				int interval = 2;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsFifthFeatureName = FeatureType.SEGMENT_BASS_IS_FIFTH.name();
					if(countFeatures) {
						int segmentBassIsFifthFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_FIFTH.name(), "", "");
						featureIDToName.set(segmentBassIsFifthFeature, segmentBassIsFifthFeatureName);
						segmentFeatures.add(segmentBassIsFifthFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsFifthFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsFifthFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_FIFTH.name(), "", "");
//							System.out.println(segmentBassIsFifthFeatureName);
							segmentFeatures.add(segmentBassIsFifthFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_ADDED_NOTE.enabled()) {
				int interval = 3;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsAddedNoteFeatureName = FeatureType.SEGMENT_BASS_IS_ADDED_NOTE.name();
					if(countFeatures) {
						int segmentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_ADDED_NOTE.name(), "", "");
						featureIDToName.set(segmentBassIsAddedNoteFeature, segmentBassIsAddedNoteFeatureName);
						segmentFeatures.add(segmentBassIsAddedNoteFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsAddedNoteFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_ADDED_NOTE.name(), "", "");
//							System.out.println(segmentBassIsAddedNoteFeatureName);
							segmentFeatures.add(segmentBassIsAddedNoteFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_SUS_POW_ROOT.enabled()) {
				int interval = 0;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsSusPowRootFeatureName = FeatureType.SEGMENT_BASS_IS_SUS_POW_ROOT.name();
					if(countFeatures) {
						int segmentBassIsSusPowRootFeature = param_g.toFeature(segmentBassIsSusPowRootFeatureName, "", "");
						featureIDToName.set(segmentBassIsSusPowRootFeature, segmentBassIsSusPowRootFeatureName);
						segmentFeatures.add(segmentBassIsSusPowRootFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsSusPowRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsSusPowRootFeature = param_g.toFeature(segmentBassIsSusPowRootFeatureName, "", "");
//							System.out.println(segmentBassIsSusPowRootFeatureName);
							segmentFeatures.add(segmentBassIsSusPowRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH.enabled()) {
				int interval = 1;
				if(is_sus_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsSusPow2ndOr4thFeatureName = FeatureType.SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH.name();
					if(countFeatures) {
						int segmentBassIsSusPow2ndOr4thFeature = param_g.toFeature(segmentBassIsSusPow2ndOr4thFeatureName, "", "");
						featureIDToName.set(segmentBassIsSusPow2ndOr4thFeature, segmentBassIsSusPow2ndOr4thFeatureName);
						segmentFeatures.add(segmentBassIsSusPow2ndOr4thFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsSusPow2ndOr4thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsSusPow2ndOr4thFeature = param_g.toFeature(segmentBassIsSusPow2ndOr4thFeatureName, "", "");
//							System.out.println(segmentBassIsSusPow2ndOr4thFeatureName);
							segmentFeatures.add(segmentBassIsSusPow2ndOr4thFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_SUS_POW_5TH.enabled()) {
				int interval = 2;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsSusPow5thFeatureName = FeatureType.SEGMENT_BASS_IS_SUS_POW_5TH.name();
					if(countFeatures) {
						int segmentBassIsSusPow5thFeature = param_g.toFeature(segmentBassIsSusPow5thFeatureName, "", "");
						featureIDToName.set(segmentBassIsSusPow5thFeature, segmentBassIsSusPow5thFeatureName);
						segmentFeatures.add(segmentBassIsSusPow5thFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsSusPow5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsSusPow5thFeature = param_g.toFeature(segmentBassIsSusPow5thFeatureName, "", "");
//							System.out.println(segmentBassIsSusPow5thFeatureName);
							segmentFeatures.add(segmentBassIsSusPow5thFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH.enabled()) {
				int interval = 3;
				if(is_7sus4_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsSusPow7Sus4_7thFeatureName = FeatureType.SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH.name();
					if(countFeatures) {
						int segmentBassIsSusPow7Sus4_7thFeature = param_g.toFeature(segmentBassIsSusPow7Sus4_7thFeatureName, "", "");
						featureIDToName.set(segmentBassIsSusPow7Sus4_7thFeature, segmentBassIsSusPow7Sus4_7thFeatureName);
						segmentFeatures.add(segmentBassIsSusPow7Sus4_7thFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsSusPow7Sus4_7thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsSusPow7Sus4_7thFeature = param_g.toFeature(segmentBassIsSusPow7Sus4_7thFeatureName, "", "");
//							System.out.println(segmentBassIsSusPow7Sus4_7thFeatureName);
							segmentFeatures.add(segmentBassIsSusPow7Sus4_7thFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_AUG6_BASS.enabled()) {
				int interval = 0;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsAug6BassFeatureName = FeatureType.SEGMENT_BASS_IS_AUG6_BASS.name();
					if(countFeatures) {
						int segmentBassIsAug6BassFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_BASS.name(), "", "");
						featureIDToName.set(segmentBassIsAug6BassFeature, segmentBassIsAug6BassFeatureName);
						segmentFeatures.add(segmentBassIsAug6BassFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsAug6BassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsAug6BassFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_BASS.name(), "", "");
//							System.out.println(segmentBassIsAug6BassFeatureName);
							segmentFeatures.add(segmentBassIsAug6BassFeature);
						}
					}
				}	
			}
			
			if(FeatureType.SEGMENT_BASS_IS_AUG6_3RD.enabled()) {
				int interval = 1;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsAug6_3rdFeatureName = FeatureType.SEGMENT_BASS_IS_AUG6_3RD.name();
					if(countFeatures) {
						int segmentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_3RD.name(), "", "");
						featureIDToName.set(segmentBassIsAug6_3rdFeature, segmentBassIsAug6_3rdFeatureName);
						segmentFeatures.add(segmentBassIsAug6_3rdFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsAug6_3rdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_3RD.name(), "", "");
//							System.out.println(segmentBassIsAug6_3rdFeatureName);
							segmentFeatures.add(segmentBassIsAug6_3rdFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_AUG6_6TH.enabled()) {
				int interval = 2;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsAug6_6thFeatureName = FeatureType.SEGMENT_BASS_IS_AUG6_6TH.name();
					if(countFeatures) {
						int segmentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_6TH.name(), "", "");
						featureIDToName.set(segmentBassIsAug6_6thFeature, segmentBassIsAug6_6thFeatureName);
						segmentFeatures.add(segmentBassIsAug6_6thFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsAug6_6thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_6TH.name(), "", "");
//							System.out.println(segmentBassIsAug6_6thFeatureName);
							segmentFeatures.add(segmentBassIsAug6_6thFeature);
						}
					}
				}
			}
			
			if(FeatureType.SEGMENT_BASS_IS_AUG6_5TH.enabled()) {
				int interval = 3;
				if(is_fr_or_ger_chord && bassIsInterval(interval, addedNote, parentNotes, segmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String segmentBassIsAug6_5thFeatureName = FeatureType.SEGMENT_BASS_IS_AUG6_5TH.name();
					if(countFeatures) {
						int segmentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_5TH.name(), "", "");
						featureIDToName.set(segmentBassIsAug6_5thFeature, segmentBassIsAug6_5thFeatureName);
						segmentFeatures.add(segmentBassIsAug6_5thFeature);
					}
					else {
						Integer count = featureNameToID.get(segmentBassIsAug6_5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int segmentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.SEGMENT_BASS_IS_AUG6_5TH.name(), "", "");
//							System.out.println(segmentBassIsAug6_5thFeatureName);
							segmentFeatures.add(segmentBassIsAug6_5thFeature);
						}
					}
				}				
			}
			
			Note nonFigSegmentBass = findBassNote(nonFigSegmentNotes);
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_ROOT.enabled()) {
				int interval = 0;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsRootFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_ROOT.name();
					if(countFeatures) {
						int figSegmentBassIsRootFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_ROOT.name(), "", "");
						featureIDToName.set(figSegmentBassIsRootFeature, figSegmentBassIsRootFeatureName);
						segmentFeatures.add(figSegmentBassIsRootFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsRootFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_ROOT.name(), "", "");
//							System.out.println(figSegmentBassIsRootFeatureName);
							segmentFeatures.add(figSegmentBassIsRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_THIRD.enabled()) {
				int interval = 1;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsThirdFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_THIRD.name();
					if(countFeatures) {
						int figSegmentBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_THIRD.name(), "", "");
						featureIDToName.set(figSegmentBassIsThirdFeature, figSegmentBassIsThirdFeatureName);
						segmentFeatures.add(figSegmentBassIsThirdFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsThirdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_THIRD.name(), "", "");
//							System.out.println(figSegmentBassIsThirdFeatureName);
							segmentFeatures.add(figSegmentBassIsThirdFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_FIFTH.enabled()) {
				int interval = 2;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsFifthFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_FIFTH.name();
					if(countFeatures) {
						int figSegmentBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_FIFTH.name(), "", "");
						featureIDToName.set(figSegmentBassIsFifthFeature, figSegmentBassIsFifthFeatureName);
						segmentFeatures.add(figSegmentBassIsFifthFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsFifthFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_FIFTH.name(), "", "");
//							System.out.println(figSegmentBassIsFifthFeatureName);
							segmentFeatures.add(figSegmentBassIsFifthFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_ADDED_NOTE.enabled()) {
				int interval = 3;
				if(is_reg_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsAddedNoteFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_ADDED_NOTE.name();
					if(countFeatures) {
						int figSegmentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_ADDED_NOTE.name(), "", "");
						featureIDToName.set(figSegmentBassIsAddedNoteFeature, figSegmentBassIsAddedNoteFeatureName);
						segmentFeatures.add(figSegmentBassIsAddedNoteFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsAddedNoteFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_ADDED_NOTE.name(), "", "");
//							System.out.println(figSegmentBassIsAddedNoteFeatureName);
							segmentFeatures.add(figSegmentBassIsAddedNoteFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_ROOT.enabled()) {
				int interval = 0;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsSusPowRootFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_ROOT.name();
					if(countFeatures) {
						int figSegmentBassIsSusPowRootFeature = param_g.toFeature(figSegmentBassIsSusPowRootFeatureName, "", "");
						featureIDToName.set(figSegmentBassIsSusPowRootFeature, figSegmentBassIsSusPowRootFeatureName);
						segmentFeatures.add(figSegmentBassIsSusPowRootFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsSusPowRootFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsSusPowRootFeature = param_g.toFeature(figSegmentBassIsSusPowRootFeatureName, "", "");
//							System.out.println(figSegmentBassIsSusPowRootFeatureName);
							segmentFeatures.add(figSegmentBassIsSusPowRootFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH.enabled()) {
				int interval = 1;
				if(is_sus_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsSusPow2ndOr4thFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_2ND_OR_4TH.name();
					if(countFeatures) {
						int figSegmentBassIsSusPow2ndOr4thFeature = param_g.toFeature(figSegmentBassIsSusPow2ndOr4thFeatureName, "", "");
						featureIDToName.set(figSegmentBassIsSusPow2ndOr4thFeature, figSegmentBassIsSusPow2ndOr4thFeatureName);
						segmentFeatures.add(figSegmentBassIsSusPow2ndOr4thFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsSusPow2ndOr4thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsSusPow2ndOr4thFeature = param_g.toFeature(figSegmentBassIsSusPow2ndOr4thFeatureName, "", "");
//							System.out.println(figSegmentBassIsSusPow2ndOr4thFeatureName);
							segmentFeatures.add(figSegmentBassIsSusPow2ndOr4thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_5TH.enabled()) {
				int interval = 2;
				if(is_sus_or_pow_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsSusPow5thFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_5TH.name();
					if(countFeatures) {
						int figSegmentBassIsSusPow5thFeature = param_g.toFeature(figSegmentBassIsSusPow5thFeatureName, "", "");
						featureIDToName.set(figSegmentBassIsSusPow5thFeature, figSegmentBassIsSusPow5thFeatureName);
						segmentFeatures.add(figSegmentBassIsSusPow5thFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsSusPow5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsSusPow5thFeature = param_g.toFeature(figSegmentBassIsSusPow5thFeatureName, "", "");
//							System.out.println(figSegmentBassIsSusPow5thFeatureName);
							segmentFeatures.add(figSegmentBassIsSusPow5thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH.enabled()) {
				int interval = 3;
				if(is_7sus4_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsSusPow7Sus4_7thFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_SUS_POW_7SUS4_7TH.name();
					if(countFeatures) {
						int figSegmentBassIsSusPow7Sus4_7thFeature = param_g.toFeature(figSegmentBassIsSusPow7Sus4_7thFeatureName, "", "");
						featureIDToName.set(figSegmentBassIsSusPow7Sus4_7thFeature, figSegmentBassIsSusPow7Sus4_7thFeatureName);
						segmentFeatures.add(figSegmentBassIsSusPow7Sus4_7thFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsSusPow7Sus4_7thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsSusPow7Sus4_7thFeature = param_g.toFeature(figSegmentBassIsSusPow7Sus4_7thFeatureName, "", "");
//							System.out.println(figSegmentBassIsSusPow7Sus4_7thFeatureName);
							segmentFeatures.add(figSegmentBassIsSusPow7Sus4_7thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_BASS.enabled()) {
				int interval = 0;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsAug6BassFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_AUG6_BASS.name();
					if(countFeatures) {
						int figSegmentBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_BASS.name(), "", "");
						featureIDToName.set(figSegmentBassIsAug6BassFeature, figSegmentBassIsAug6BassFeatureName);
						segmentFeatures.add(figSegmentBassIsAug6BassFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsAug6BassFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_BASS.name(), "", "");
//							System.out.println(figSegmentBassIsAug6BassFeatureName);
							segmentFeatures.add(figSegmentBassIsAug6BassFeature);
						}
					}
				}	
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_3RD.enabled()) {
				int interval = 1;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsAug6_3rdFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_AUG6_3RD.name();
					if(countFeatures) {
						int figSegmentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_3RD.name(), "", "");
						featureIDToName.set(figSegmentBassIsAug6_3rdFeature, figSegmentBassIsAug6_3rdFeatureName);
						segmentFeatures.add(figSegmentBassIsAug6_3rdFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsAug6_3rdFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_3RD.name(), "", "");
//							System.out.println(figSegmentBassIsAug6_3rdFeatureName);
							segmentFeatures.add(figSegmentBassIsAug6_3rdFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_6TH.enabled()) {
				int interval = 2;
				if(is_aug6_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsAug6_6thFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_AUG6_6TH.name();
					if(countFeatures) {
						int figSegmentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_6TH.name(), "", "");
						featureIDToName.set(figSegmentBassIsAug6_6thFeature, figSegmentBassIsAug6_6thFeatureName);
						segmentFeatures.add(figSegmentBassIsAug6_6thFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsAug6_6thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_6TH.name(), "", "");
//							System.out.println(figSegmentBassIsAug6_6thFeatureName);
							segmentFeatures.add(figSegmentBassIsAug6_6thFeature);
						}
					}
				}
			}
			
			if(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_5TH.enabled()) {
				int interval = 3;
				if(is_fr_or_ger_chord && bassIsInterval(interval, addedNote, parentNotes, nonFigSegmentBass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					String figSegmentBassIsAug6_5thFeatureName = FeatureType.FIG_SEGMENT_BASS_IS_AUG6_5TH.name();
					if(countFeatures) {
						int figSegmentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_5TH.name(), "", "");
						featureIDToName.set(figSegmentBassIsAug6_5thFeature, figSegmentBassIsAug6_5thFeatureName);
						segmentFeatures.add(figSegmentBassIsAug6_5thFeature);
					}
					else {
						Integer count = featureNameToID.get(figSegmentBassIsAug6_5thFeatureName);
						if((count != null) && (count > MIN_FEATURE_COUNT)) {
							int figSegmentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_SEGMENT_BASS_IS_AUG6_5TH.name(), "", "");
//							System.out.println(figSegmentBassIsAug6_5thFeatureName);
							segmentFeatures.add(figSegmentBassIsAug6_5thFeature);
						}
					}
				}				
			}
			
			List<Note> bassNotes = findBassNotes(eventsInside);
			
			if(FeatureType.DURATION_BASS_IS_ROOT.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsRootFeatureName = FeatureType.DURATION_BASS_IS_ROOT.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsRootFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_ROOT.name(), "", overlappedBassValue+ "");
							featureIDToName.set(durationBassIsRootFeature, durationBassIsRootFeatureName);
							segmentFeatures.add(durationBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsRootFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_ROOT.name(), "", overlappedBassValue+ "");
		//						System.out.println(durationBassIsRootFeatureName);
								segmentFeatures.add(durationBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_BASS_IS_THIRD.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsThirdFeatureName = FeatureType.DURATION_BASS_IS_THIRD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsThirdFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
							featureIDToName.set(durationBassIsThirdFeature, durationBassIsThirdFeatureName);	
							segmentFeatures.add(durationBassIsThirdFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsThirdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsThirdFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_THIRD.name(), "",overlappedBassValue + "");
		//						System.out.println(durationBassIsThirdFeatureName);
								segmentFeatures.add(durationBassIsThirdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_BASS_IS_FIFTH.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsFifthFeatureName = FeatureType.DURATION_BASS_IS_FIFTH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsFifthFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
							featureIDToName.set(durationBassIsFifthFeature, durationBassIsFifthFeatureName);	
							segmentFeatures.add(durationBassIsFifthFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsFifthFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsFifthFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
		//						System.out.println(durationBassIsFifthFeatureName);
								segmentFeatures.add(durationBassIsFifthFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_BASS_IS_ADDED_NOTE.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsAddedNoteFeatureName = FeatureType.DURATION_BASS_IS_ADDED_NOTE.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsAddedNoteFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
							featureIDToName.set(durationBassIsAddedNoteFeature, durationBassIsAddedNoteFeatureName);	
							segmentFeatures.add(durationBassIsAddedNoteFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsAddedNoteFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsAddedNoteFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
		//						System.out.println(durationBassIsAddedNoteFeatureName);
								segmentFeatures.add(durationBassIsAddedNoteFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_BASS_IS_ROOT.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationSusPowBassIsRootFeatureNameGeneric = FeatureType.DURATION_SUS_POW_BASS_IS_ROOT.name();
						String durationSusPowBassIsRootFeatureName = durationSusPowBassIsRootFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationSusPowBassIsRootFeature = param_g.toFeature(durationSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue+ "");
							featureIDToName.set(durationSusPowBassIsRootFeature, durationSusPowBassIsRootFeatureName);
							segmentFeatures.add(durationSusPowBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowBassIsRootFeature = param_g.toFeature(durationSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue+ "");
		//						System.out.println(durationSusPowBassIsRootFeatureName);
								segmentFeatures.add(durationSusPowBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_BASS_IS_2ND_OR_4TH.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationSusPowBassIs2ndOr4thFeatureNameGeneric = FeatureType.DURATION_SUS_POW_BASS_IS_2ND_OR_4TH.name();
						String durationSusPowBassIs2ndOr4thFeatureName = durationSusPowBassIs2ndOr4thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationSusPowBassIs2ndOr4thFeature = param_g.toFeature(durationSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue+ "");
							featureIDToName.set(durationSusPowBassIs2ndOr4thFeature, durationSusPowBassIs2ndOr4thFeatureName);
							segmentFeatures.add(durationSusPowBassIs2ndOr4thFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowBassIs2ndOr4thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowBassIs2ndOr4thFeature = param_g.toFeature(durationSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue+ "");
		//						System.out.println(durationSusPowBassIs2ndOr4thFeatureName);
								segmentFeatures.add(durationSusPowBassIs2ndOr4thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_BASS_IS_5TH.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationSusPowBassIs5thFeatureNameGeneric = FeatureType.DURATION_SUS_POW_BASS_IS_5TH.name();
						String durationSusPowBassIs5thFeatureName = durationSusPowBassIs5thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationSusPowBassIs5thFeature = param_g.toFeature(durationSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue+ "");
							featureIDToName.set(durationSusPowBassIs5thFeature, durationSusPowBassIs5thFeatureName);
							segmentFeatures.add(durationSusPowBassIs5thFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowBassIs5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowBassIs5thFeature = param_g.toFeature(durationSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue+ "");
		//						System.out.println(durationSusPowBassIs5thFeatureName);
								segmentFeatures.add(durationSusPowBassIs5thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_SUS_POW_BASS_IS_7SUS4_7TH.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationSusPowBassIs7Sus4_7thFeatureNameGeneric = FeatureType.DURATION_SUS_POW_BASS_IS_7SUS4_7TH.name();
						String durationSusPowBassIs7Sus4_7thFeatureName = durationSusPowBassIs7Sus4_7thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationSusPowBassIs7Sus4_7thFeature = param_g.toFeature(durationSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue+ "");
							featureIDToName.set(durationSusPowBassIs7Sus4_7thFeature, durationSusPowBassIs7Sus4_7thFeatureName);
							segmentFeatures.add(durationSusPowBassIs7Sus4_7thFeature);
						}
						else {
							Integer count = featureNameToID.get(durationSusPowBassIs7Sus4_7thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationSusPowBassIs7Sus4_7thFeature = param_g.toFeature(durationSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue+ "");
		//						System.out.println(durationSusPowBassIs7Sus4_7thFeatureName);
								segmentFeatures.add(durationSusPowBassIs7Sus4_7thFeature);
							}
						}
					}
				}
			}
			
			
			
			if(FeatureType.DURATION_BASS_IS_AUG6_BASS.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsAug6BassFeatureName = FeatureType.DURATION_BASS_IS_AUG6_BASS.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsAug6BassFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue+ "");
							featureIDToName.set(durationBassIsAug6BassFeature, durationBassIsAug6BassFeatureName);
							segmentFeatures.add(durationBassIsAug6BassFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsAug6BassFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsAug6BassFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue+ "");
		//						System.out.println(durationBassIsAug6BassFeatureName);
								segmentFeatures.add(durationBassIsAug6BassFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.DURATION_BASS_IS_AUG6_3RD.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsAug6_3rdFeatureName = FeatureType.DURATION_BASS_IS_AUG6_3RD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue+ "");
							featureIDToName.set(durationBassIsAug6_3rdFeature, durationBassIsAug6_3rdFeatureName);
							segmentFeatures.add(durationBassIsAug6_3rdFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsAug6_3rdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue+ "");
		//						System.out.println(durationBassIsAug6_3rdFeatureName);
								segmentFeatures.add(durationBassIsAug6_3rdFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.DURATION_BASS_IS_AUG6_6TH.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsAug6_6thFeatureName = FeatureType.DURATION_BASS_IS_AUG6_6TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsAug6_6thFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue+ "");
							featureIDToName.set(durationBassIsAug6_6thFeature, durationBassIsAug6_6thFeatureName);
							segmentFeatures.add(durationBassIsAug6_6thFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsAug6_6thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsAug6_6thFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue+ "");
		//						System.out.println(durationBassIsAug6_6thFeatureName);
								segmentFeatures.add(durationBassIsAug6_6thFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.DURATION_BASS_IS_AUG6_5TH.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String durationBassIsAug6_5thFeatureName = FeatureType.DURATION_BASS_IS_AUG6_5TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int durationBassIsAug6_5thFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue+ "");
							featureIDToName.set(durationBassIsAug6_5thFeature, durationBassIsAug6_5thFeatureName);
							segmentFeatures.add(durationBassIsAug6_5thFeature);
						}
						else {
							Integer count = featureNameToID.get(durationBassIsAug6_5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int durationBassIsAug6_5thFeature = param_g.toFeature(FeatureType.DURATION_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue+ "");
		//						System.out.println(durationBassIsAug6_5thFeatureName);
								segmentFeatures.add(durationBassIsAug6_5thFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.ACCENT_BASS_IS_ROOT.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsRootFeatureName = FeatureType.ACCENT_BASS_IS_ROOT.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsRootFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsRootFeature, accentBassIsRootFeatureName);	
							segmentFeatures.add(accentBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsRootFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsRootFeatureName);
								segmentFeatures.add(accentBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_THIRD.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsThirdFeatureName = FeatureType.ACCENT_BASS_IS_THIRD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsThirdFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsThirdFeature, accentBassIsThirdFeatureName);	
							segmentFeatures.add(accentBassIsThirdFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsThirdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsThirdFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsThirdFeatureName);
								segmentFeatures.add(accentBassIsThirdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_FIFTH.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsFifthFeatureName = FeatureType.ACCENT_BASS_IS_FIFTH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsFifthFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsFifthFeature, accentBassIsFifthFeatureName);
							segmentFeatures.add(accentBassIsFifthFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsFifthFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsFifthFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsFifthFeatureName);
								segmentFeatures.add(accentBassIsFifthFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_ADDED_NOTE.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsAddedNoteFeatureName = FeatureType.ACCENT_BASS_IS_ADDED_NOTE.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsAddedNoteFeature, accentBassIsAddedNoteFeatureName);
							segmentFeatures.add(accentBassIsAddedNoteFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsAddedNoteFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsAddedNoteFeatureName);
								segmentFeatures.add(accentBassIsAddedNoteFeature);
							}
						}
					}
				}
			}
		
			if(FeatureType.ACCENT_SUS_POW_BASS_IS_ROOT.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentSusPowBassIsRootFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_BASS_IS_ROOT.name();
						String accentSusPowBassIsRootFeatureName = accentSusPowBassIsRootFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentSusPowBassIsRootFeature = param_g.toFeature(accentSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(accentSusPowBassIsRootFeature, accentSusPowBassIsRootFeatureName);	
							segmentFeatures.add(accentSusPowBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowBassIsRootFeature = param_g.toFeature(accentSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(accentSusPowBassIsRootFeatureName);
								segmentFeatures.add(accentSusPowBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentSusPowBassIs2ndOr4thFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH.name();
						String accentSusPowBassIs2ndOr4thFeatureName = accentSusPowBassIs2ndOr4thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentSusPowBassIs2ndOr4thFeature = param_g.toFeature(accentSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(accentSusPowBassIs2ndOr4thFeature, accentSusPowBassIs2ndOr4thFeatureName);	
							segmentFeatures.add(accentSusPowBassIs2ndOr4thFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowBassIs2ndOr4thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowBassIs2ndOr4thFeature = param_g.toFeature(accentSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(accentSusPowBassIs2ndOr4thFeatureName);
								segmentFeatures.add(accentSusPowBassIs2ndOr4thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_BASS_IS_5TH.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentSusPowBassIs5thFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_BASS_IS_5TH.name();
						String accentSusPowBassIs5thFeatureName = accentSusPowBassIs5thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentSusPowBassIs5thFeature = param_g.toFeature(accentSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(accentSusPowBassIs5thFeature, accentSusPowBassIs5thFeatureName);	
							segmentFeatures.add(accentSusPowBassIs5thFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowBassIs5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowBassIs5thFeature = param_g.toFeature(accentSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(accentSusPowBassIs5thFeatureName);
								segmentFeatures.add(accentSusPowBassIs5thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_SUS_POW_BASS_IS_7SUS4_7TH.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentSusPowBassIs7Sus4_7thFeatureNameGeneric = FeatureType.ACCENT_SUS_POW_BASS_IS_7SUS4_7TH.name();
						String accentSusPowBassIs7Sus4_7thFeatureName = accentSusPowBassIs7Sus4_7thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentSusPowBassIs7Sus4_7thFeature = param_g.toFeature(accentSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(accentSusPowBassIs7Sus4_7thFeature, accentSusPowBassIs7Sus4_7thFeatureName);	
							segmentFeatures.add(accentSusPowBassIs7Sus4_7thFeature);
						}
						else {
							Integer count = featureNameToID.get(accentSusPowBassIs7Sus4_7thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentSusPowBassIs7Sus4_7thFeature = param_g.toFeature(accentSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(accentSusPowBassIs7Sus4_7thFeatureName);
								segmentFeatures.add(accentSusPowBassIs7Sus4_7thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_AUG6_BASS.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsAug6BassFeatureName = FeatureType.ACCENT_BASS_IS_AUG6_BASS.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsAug6BassFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsAug6BassFeature, accentBassIsAug6BassFeatureName);	
							segmentFeatures.add(accentBassIsAug6BassFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsAug6BassFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsAug6BassFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsAug6BassFeatureName);
								segmentFeatures.add(accentBassIsAug6BassFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_AUG6_3RD.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsAug6_3rdFeatureName = FeatureType.ACCENT_BASS_IS_AUG6_3RD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsAug6_3rdFeature, accentBassIsAug6_3rdFeatureName);	
							segmentFeatures.add(accentBassIsAug6_3rdFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsAug6_3rdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsAug6_3rdFeatureName);
								segmentFeatures.add(accentBassIsAug6_3rdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.ACCENT_BASS_IS_AUG6_6TH.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsAug6_6thFeatureName = FeatureType.ACCENT_BASS_IS_AUG6_6TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsAug6_6thFeature, accentBassIsAug6_6thFeatureName);	
							segmentFeatures.add(accentBassIsAug6_6thFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsAug6_6thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsAug6_6thFeatureName);
								segmentFeatures.add(accentBassIsAug6_6thFeature);
							}
						}
					}
				}	
			}
			
			if(FeatureType.ACCENT_BASS_IS_AUG6_5TH.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, bassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String accentBassIsAug6_5thFeatureName = FeatureType.ACCENT_BASS_IS_AUG6_5TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int accentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue + "");
							featureIDToName.set(accentBassIsAug6_5thFeature, accentBassIsAug6_5thFeatureName);	
							segmentFeatures.add(accentBassIsAug6_5thFeature);
						}
						else {
							Integer count = featureNameToID.get(accentBassIsAug6_5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int accentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.ACCENT_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue + "");
		//						System.out.println(accentBassIsAug6_5thFeatureName);
								segmentFeatures.add(accentBassIsAug6_5thFeature);
							}
						}
					}
				}
			}
			
			List<Note> nonFigBassNotes = findNonFigBassNotes(eventsInside, previousEvent, nextEvent, parentNotes);
			
			if(FeatureType.FIG_DURATION_BASS_IS_ROOT.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsRootFeatureName = FeatureType.FIG_DURATION_BASS_IS_ROOT.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsRootFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
							featureIDToName.set(figDurationBassIsRootFeature, figDurationBassIsRootFeatureName);
							segmentFeatures.add(figDurationBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsRootFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
		//						System.out.println(figDurationBassIsRootFeatureName);
								segmentFeatures.add(figDurationBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_THIRD.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsThirdFeatureName = FeatureType.FIG_DURATION_BASS_IS_THIRD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
							featureIDToName.set(figDurationBassIsThirdFeature, figDurationBassIsThirdFeatureName);
							segmentFeatures.add(figDurationBassIsThirdFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsThirdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
		//						System.out.println(figDurationBassIsThirdFeatureName);
								segmentFeatures.add(figDurationBassIsThirdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_FIFTH.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsFifthFeatureName = FeatureType.FIG_DURATION_BASS_IS_FIFTH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
							featureIDToName.set(figDurationBassIsFifthFeature, figDurationBassIsFifthFeatureName);
							segmentFeatures.add(figDurationBassIsFifthFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsFifthFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
		//						System.out.println(figDurationBassIsFifthFeatureName);
								segmentFeatures.add(figDurationBassIsFifthFeature);
							}
						}	
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_ADDED_NOTE.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsAddedNoteFeatureName = FeatureType.FIG_DURATION_BASS_IS_ADDED_NOTE.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
							featureIDToName.set(figDurationBassIsAddedNoteFeature, figDurationBassIsAddedNoteFeatureName);
							segmentFeatures.add(figDurationBassIsAddedNoteFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsAddedNoteFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
		//						System.out.println(figDurationBassIsAddedNoteFeatureName);
								segmentFeatures.add(figDurationBassIsAddedNoteFeature);
							}
						}	
					}
				}
			}
			
			
			if(FeatureType.FIG_DURATION_SUS_POW_BASS_IS_ROOT.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationSusPowBassIsRootFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_BASS_IS_ROOT.name();
						String figDurationSusPowBassIsRootFeatureName = figDurationSusPowBassIsRootFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationSusPowBassIsRootFeature = param_g.toFeature(figDurationSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figDurationSusPowBassIsRootFeature, figDurationSusPowBassIsRootFeatureName);
							segmentFeatures.add(figDurationSusPowBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowBassIsRootFeature = param_g.toFeature(figDurationSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figDurationSusPowBassIsRootFeatureName);
								segmentFeatures.add(figDurationSusPowBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_BASS_IS_2ND_OR_4TH.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationSusPowBassIs2ndOr4thFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_BASS_IS_2ND_OR_4TH.name();
						String figDurationSusPowBassIs2ndOr4thFeatureName = figDurationSusPowBassIs2ndOr4thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationSusPowBassIs2ndOr4thFeature = param_g.toFeature(figDurationSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figDurationSusPowBassIs2ndOr4thFeature, figDurationSusPowBassIs2ndOr4thFeatureName);
							segmentFeatures.add(figDurationSusPowBassIs2ndOr4thFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowBassIs2ndOr4thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowBassIs2ndOr4thFeature = param_g.toFeature(figDurationSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figDurationSusPowBassIs2ndOr4thFeatureName);
								segmentFeatures.add(figDurationSusPowBassIs2ndOr4thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_BASS_IS_5TH.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationSusPowBassIs5thFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_BASS_IS_5TH.name();
						String figDurationSusPowBassIs5thFeatureName = figDurationSusPowBassIs5thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationSusPowBassIs5thFeature = param_g.toFeature(figDurationSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figDurationSusPowBassIs5thFeature, figDurationSusPowBassIs5thFeatureName);
							segmentFeatures.add(figDurationSusPowBassIs5thFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowBassIs5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowBassIs5thFeature = param_g.toFeature(figDurationSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figDurationSusPowBassIs5thFeatureName);
								segmentFeatures.add(figDurationSusPowBassIs5thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_SUS_POW_BASS_IS_7SUS4_7TH.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationSusPowBassIs7Sus4_7thFeatureNameGeneric = FeatureType.FIG_DURATION_SUS_POW_BASS_IS_7SUS4_7TH.name();
						String figDurationSusPowBassIs7Sus4_7thFeatureName = figDurationSusPowBassIs7Sus4_7thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationSusPowBassIs7Sus4_7thFeature = param_g.toFeature(figDurationSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figDurationSusPowBassIs7Sus4_7thFeature, figDurationSusPowBassIs7Sus4_7thFeatureName);
							segmentFeatures.add(figDurationSusPowBassIs7Sus4_7thFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationSusPowBassIs7Sus4_7thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationSusPowBassIs7Sus4_7thFeature = param_g.toFeature(figDurationSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figDurationSusPowBassIs7Sus4_7thFeatureName);
								segmentFeatures.add(figDurationSusPowBassIs7Sus4_7thFeature);
							}
						}
					}
				}
			}			
			
			if(FeatureType.FIG_DURATION_BASS_IS_AUG6_BASS.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsAug6BassFeatureName = FeatureType.FIG_DURATION_BASS_IS_AUG6_BASS.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue+ "");
							featureIDToName.set(figDurationBassIsAug6BassFeature, figDurationBassIsAug6BassFeatureName);
							segmentFeatures.add(figDurationBassIsAug6BassFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsAug6BassFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue+ "");
		//						System.out.println(figDurationBassIsAug6BassFeatureName);
								segmentFeatures.add(figDurationBassIsAug6BassFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_AUG6_3RD.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsAug6_3rdFeatureName = FeatureType.FIG_DURATION_BASS_IS_AUG6_3RD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue+ "");
							featureIDToName.set(figDurationBassIsAug6_3rdFeature, figDurationBassIsAug6_3rdFeatureName);
							segmentFeatures.add(figDurationBassIsAug6_3rdFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsAug6_3rdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue+ "");
		//						System.out.println(figDurationBassIsAug6_3rdFeatureName);
								segmentFeatures.add(figDurationBassIsAug6_3rdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_AUG6_6TH.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsAug6_6thFeatureName = FeatureType.FIG_DURATION_BASS_IS_AUG6_6TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue+ "");
							featureIDToName.set(figDurationBassIsAug6_6thFeature, figDurationBassIsAug6_6thFeatureName);
							segmentFeatures.add(figDurationBassIsAug6_6thFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsAug6_6thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue+ "");
		//						System.out.println(figDurationBassIsAug6_6thFeatureName);
								segmentFeatures.add(figDurationBassIsAug6_6thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_DURATION_BASS_IS_AUG6_5TH.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("DURATION");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figDurationBassIsAug6_5thFeatureName = FeatureType.FIG_DURATION_BASS_IS_AUG6_5TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figDurationBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue+ "");
							featureIDToName.set(figDurationBassIsAug6_5thFeature, figDurationBassIsAug6_5thFeatureName);
							segmentFeatures.add(figDurationBassIsAug6_5thFeature);
						}
						else {
							Integer count = featureNameToID.get(figDurationBassIsAug6_5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figDurationBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_DURATION_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue+ "");
		//						System.out.println(figDurationBassIsAug6_5thFeatureName);
								segmentFeatures.add(figDurationBassIsAug6_5thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_ROOT.enabled()) {
				if(is_reg_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsRootFeatureName = FeatureType.FIG_ACCENT_BASS_IS_ROOT.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsRootFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsRootFeature, figAccentBassIsRootFeatureName);	
							segmentFeatures.add(figAccentBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsRootFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_ROOT.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsRootFeatureName);
								segmentFeatures.add(figAccentBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_THIRD.enabled()) {
				if(is_reg_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsThirdFeatureName = FeatureType.FIG_ACCENT_BASS_IS_THIRD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsThirdFeature, figAccentBassIsThirdFeatureName);	
							segmentFeatures.add(figAccentBassIsThirdFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsThirdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsThirdFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_THIRD.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsThirdFeatureName);
								segmentFeatures.add(figAccentBassIsThirdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_FIFTH.enabled()) {
				if(is_reg_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsFifthFeatureName = FeatureType.FIG_ACCENT_BASS_IS_FIFTH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsFifthFeature, figAccentBassIsFifthFeatureName);	
							segmentFeatures.add(figAccentBassIsFifthFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsFifthFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsFifthFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_FIFTH.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsFifthFeatureName);
								segmentFeatures.add(figAccentBassIsFifthFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_ADDED_NOTE.enabled()) {
				if(is_reg_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsAddedNoteFeatureName = FeatureType.FIG_ACCENT_BASS_IS_ADDED_NOTE.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsAddedNoteFeature, figAccentBassIsAddedNoteFeatureName);	
							segmentFeatures.add(figAccentBassIsAddedNoteFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsAddedNoteFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsAddedNoteFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_ADDED_NOTE.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsAddedNoteFeatureName);
								segmentFeatures.add(figAccentBassIsAddedNoteFeature);
							}
						}
					}
				}
			}
			
			
			if(FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_ROOT.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentSusPowBassIsRootFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_ROOT.name();
						String figAccentSusPowBassIsRootFeatureName = figAccentSusPowBassIsRootFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentSusPowBassIsRootFeature = param_g.toFeature(figAccentSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figAccentSusPowBassIsRootFeature, figAccentSusPowBassIsRootFeatureName);	
							segmentFeatures.add(figAccentSusPowBassIsRootFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowBassIsRootFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowBassIsRootFeature = param_g.toFeature(figAccentSusPowBassIsRootFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figAccentSusPowBassIsRootFeatureName);
								segmentFeatures.add(figAccentSusPowBassIsRootFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH.enabled()) {
				if(is_sus_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentSusPowBassIs2ndOr4thFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_2ND_OR_4TH.name();
						String figAccentSusPowBassIs2ndOr4thFeatureName = figAccentSusPowBassIs2ndOr4thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentSusPowBassIs2ndOr4thFeature = param_g.toFeature(figAccentSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figAccentSusPowBassIs2ndOr4thFeature, figAccentSusPowBassIs2ndOr4thFeatureName);	
							segmentFeatures.add(figAccentSusPowBassIs2ndOr4thFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowBassIs2ndOr4thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowBassIs2ndOr4thFeature = param_g.toFeature(figAccentSusPowBassIs2ndOr4thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figAccentSusPowBassIs2ndOr4thFeatureName);
								segmentFeatures.add(figAccentSusPowBassIs2ndOr4thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_5TH.enabled()) {
				if(is_sus_or_pow_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentSusPowBassIs5thFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_5TH.name();
						String figAccentSusPowBassIs5thFeatureName = figAccentSusPowBassIs5thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentSusPowBassIs5thFeature = param_g.toFeature(figAccentSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figAccentSusPowBassIs5thFeature, figAccentSusPowBassIs5thFeatureName);	
							segmentFeatures.add(figAccentSusPowBassIs5thFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowBassIs5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowBassIs5thFeature = param_g.toFeature(figAccentSusPowBassIs5thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figAccentSusPowBassIs5thFeatureName);
								segmentFeatures.add(figAccentSusPowBassIs5thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_7SUS4_7TH.enabled()) {
				if(is_7sus4_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentSusPowBassIs7Sus4_7thFeatureNameGeneric = FeatureType.FIG_ACCENT_SUS_POW_BASS_IS_7SUS4_7TH.name();
						String figAccentSusPowBassIs7Sus4_7thFeatureName = figAccentSusPowBassIs7Sus4_7thFeatureNameGeneric + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentSusPowBassIs7Sus4_7thFeature = param_g.toFeature(figAccentSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
							featureIDToName.set(figAccentSusPowBassIs7Sus4_7thFeature, figAccentSusPowBassIs7Sus4_7thFeatureName);	
							segmentFeatures.add(figAccentSusPowBassIs7Sus4_7thFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentSusPowBassIs7Sus4_7thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentSusPowBassIs7Sus4_7thFeature = param_g.toFeature(figAccentSusPowBassIs7Sus4_7thFeatureNameGeneric, "", overlappedBassValue + "");
		//						System.out.println(figAccentSusPowBassIs7Sus4_7thFeatureName);
								segmentFeatures.add(figAccentSusPowBassIs7Sus4_7thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_AUG6_BASS.enabled()) {
				if(is_aug6_chord) {
					int interval = 0;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsAug6BassFeatureName = FeatureType.FIG_ACCENT_BASS_IS_AUG6_BASS.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsAug6BassFeature, figAccentBassIsAug6BassFeatureName);	
							segmentFeatures.add(figAccentBassIsAug6BassFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsAug6BassFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsAug6BassFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_BASS.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsAug6BassFeatureName);
								segmentFeatures.add(figAccentBassIsAug6BassFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_AUG6_3RD.enabled()) {
				if(is_aug6_chord) {
					int interval = 1;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsAug6_3rdFeatureName = FeatureType.FIG_ACCENT_BASS_IS_AUG6_3RD.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsAug6_3rdFeature, figAccentBassIsAug6_3rdFeatureName);	
							segmentFeatures.add(figAccentBassIsAug6_3rdFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsAug6_3rdFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsAug6_3rdFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_3RD.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsAug6_3rdFeatureName);
								segmentFeatures.add(figAccentBassIsAug6_3rdFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_AUG6_6TH.enabled()) {
				if(is_aug6_chord) {
					int interval = 2;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight =  weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsAug6_6thFeatureName = FeatureType.FIG_ACCENT_BASS_IS_AUG6_6TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsAug6_6thFeature, figAccentBassIsAug6_6thFeatureName);	
							segmentFeatures.add(figAccentBassIsAug6_6thFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsAug6_6thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsAug6_6thFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_6TH.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsAug6_6thFeatureName);
								segmentFeatures.add(figAccentBassIsAug6_6thFeature);
							}
						}
					}
				}
			}
			
			if(FeatureType.FIG_ACCENT_BASS_IS_AUG6_5TH.enabled()) {
				if(is_fr_or_ger_chord) {
					int interval = 3;
					featuresWeight = Weight.valueOf("ACCENT");
					int bassWeight = weightedBass(featuresWeight, addedNote, parentNotes, interval, eventsInside, nonFigBassNotes, overlappedConsistency, is_reg_chord, is_pow_chord, is_7sus4_chord);
					
					List<Integer> overlappedBassValues = new ArrayList<Integer>();
					overlappedBassValues.add(bassWeight);
					
					if(overlappedConsistency) {
						for(int bin : overlappedBins) {
							if(bassWeight > bin) {
								overlappedBassValues.add(bin);
							}
						}
					}
					
					for(int overlappedBassValue : overlappedBassValues) {
						String figAccentBassIsAug6_5thFeatureName = FeatureType.FIG_ACCENT_BASS_IS_AUG6_5TH.name() + "_" + overlappedBassValue;
						if(countFeatures) {
							int figAccentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue + "");
							featureIDToName.set(figAccentBassIsAug6_5thFeature, figAccentBassIsAug6_5thFeatureName);	
							segmentFeatures.add(figAccentBassIsAug6_5thFeature);
						}
						else {
							Integer count = featureNameToID.get(figAccentBassIsAug6_5thFeatureName);
							if((count != null) && (count > MIN_FEATURE_COUNT)) {
								int figAccentBassIsAug6_5thFeature = param_g.toFeature(FeatureType.FIG_ACCENT_BASS_IS_AUG6_5TH.name(), "", overlappedBassValue + "");
		//						System.out.println(figAccentBassIsAug6_5thFeatureName);
								segmentFeatures.add(figAccentBassIsAug6_5thFeature);
							}
						}
					}
				}
			}
			
			features = new FeatureArray(listToArray(segmentFeatures), features);
		}
		
		// transition features (end to begin)
		if(parentType == NodeType.BEGIN && childType == NodeType.END) {
			List<Integer> transitionFeatures = new ArrayList<Integer>();
//			System.out.println("");
//			System.out.println(instance.title);
			// get parent label name
			String parentLabel = SpanLabel.get(parentLabelId).form;
//			System.out.println("Parent label: " + parentLabel);
			
			// get child label name
			String childLabel = SpanLabel.get(childLabelId).form;
//			System.out.println("Child label: " + childLabel);
			
			if(FeatureType.CHORD_BIGRAM.enabled()) {
				boolean is_parent_aug6_chord = isAug6Chord(parentLabel);
				String parentMode = is_parent_aug6_chord ? getMode(parentLabel) + "6" : getMode(parentLabel);
				boolean is_child_aug6_chord = isAug6Chord(childLabel);
				String childMode = is_child_aug6_chord ? getMode(childLabel) + "6": getMode(childLabel);
				
				String transition = parentMode + getAddedNote(parentLabel) + "_" + childMode + getAddedNote(childLabel) + "_" + findInterval(parentLabel, childLabel) + "";
//				System.out.println(transition);
				String chordBigramFeatureName = FeatureType.CHORD_BIGRAM.name() + "_" + transition;
				if(countFeatures) {
					int chordBigramFeature = param_g.toFeature(FeatureType.CHORD_BIGRAM.name(), "", transition);
					featureIDToName.set(chordBigramFeature, chordBigramFeatureName);
					transitionFeatures.add(chordBigramFeature);
				}
				else {
					Integer count = featureNameToID.get(chordBigramFeatureName);
					if((count != null) && (count > MIN_FEATURE_COUNT)) {
						int chordBigramFeature = param_g.toFeature(FeatureType.CHORD_BIGRAM.name(), "", transition);
//						System.out.println(chordBigramFeatureName);
						transitionFeatures.add(chordBigramFeature);
					}
				}	
			}
//			
			features = new FeatureArray(listToArray(transitionFeatures), features);
		}
		
		return features;
	}
	
	private static String getRoot(String parentLabel) {
		// get root note
		String pattern = "[A-G][#b]?";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		String root = m.find() ? m.group(0) : "";
		
		return root;
	}
	
	private static String getMode(String parentLabel) {
		String pattern = "(7sus4|sus2|sus4|maj\\(\\*3\\)|maj|min|dim|ger|it|fr)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		String mode = m.find() ? m.group(0) : "";
//		System.out.println("Mode: " + mode);	
		return mode;
	}
	
	private static String getAddedNote(String parentLabel) {
		if(isAug6Chord(parentLabel) || isSusOrPowChord(parentLabel)) {
			return "";
		}
		String pattern = "(4|6|7)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel.substring(parentLabel.length() - 1));
		String added_note = m.find() ? m.group(0) : "";
//		System.out.println("Added note: " + added_note);	
		return added_note;
	}
	
	private static int getOctaveNum(String pitch) {
		return Integer.parseInt(pitch.substring(pitch.length() - 1));
	}
	
	private static int getAccidentalNum(Note note) {
		String pattern = "(b|#)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(note.pitch);
		String accidental = m.find()? m.group(0) : "";
		
		if(accidental.equals("#")) {
			return 2;
		}
		else if(accidental.equals("")) {
			return 1;
		}
		else {
			return 0;
		}
	}
	
	private static List<Note> getNotesInSegment(List<Event> events) {
		List<Note> notesInSegment = new ArrayList<Note>();
		
//		System.out.println("Notes in segment:");
		for(Event event : events) {
			for(Note note : event.notes) {
				if((events.indexOf(event) == 0) || note.fromPrevious == false) {
					notesInSegment.add(note);
//					System.out.println(note.pitch);
				}
			}
		}
		
		return notesInSegment;
	}
	
	private static boolean doubleEquals(double num1, double num2) {
		double epsilon = 0.0000001d;
		return (Math.abs(num1 - num2) < epsilon);
	}
	
	private static boolean harmonic(Note curNote, List<Note> curEventNotes) {
		String curNoteWithoutOctaveNum = getNoteWithoutOctaveNum(curNote);
		int curNoteIndex = enharmonicNotesToID.get(curNoteWithoutOctaveNum);
		int harmonicCount = 0;
		int MIN_HARMONIC = 2;
		int numPreviousNotes = curEventNotes.size() - 1;
		List<String> notesSeen = new ArrayList<String>(Collections.emptyList());
		int NUM_NOTES = 12;
		int MAJOR_THIRD = 4;
		int MINOR_THIRD = 3;
		int PERFECT_FIFTH = 7;
		
		for(Note note : curEventNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			if((note != curNote) && !notesSeen.contains(noteWithoutOctaveNum)) {
				int noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
				
				// find interval both ways
				int interval = noteIndex < curNoteIndex ? (noteIndex + NUM_NOTES) - curNoteIndex : noteIndex - curNoteIndex;
				int reverseInterval = curNoteIndex < noteIndex ? (curNoteIndex + NUM_NOTES) - noteIndex : curNoteIndex - noteIndex;
				
				// check if either interval is a harmonic interval
				if((interval == MAJOR_THIRD) || (interval == MINOR_THIRD) || (interval == PERFECT_FIFTH)) {
//					System.out.println("(Harmonic) Note: " + note.pitch + " curNote: " + curNote.pitch); 
					notesSeen.add(noteWithoutOctaveNum);
					harmonicCount++;
				}
				else if((reverseInterval == MAJOR_THIRD) || (reverseInterval == MINOR_THIRD) || (reverseInterval == PERFECT_FIFTH)) {
//					System.out.println("(Harmonic) Note: " + note.pitch + " curNote: " + curNote.pitch);
					notesSeen.add(noteWithoutOctaveNum);
					harmonicCount++;
				}
				
				if((numPreviousNotes > 2 && harmonicCount == MIN_HARMONIC)) {
					return true;
				}
				else if(harmonicCount == numPreviousNotes) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static List<Note> getNonFigurationNotesInSegment(List<Event> events, List<Note> segmentNotes, Event previousEvent, Event nextEvent, List<Integer> parentNotes) {
		List<Note> nonFigSegmentNotes = new ArrayList<Note>(segmentNotes.size());
		
		for(Note segmentNote : segmentNotes) {
			nonFigSegmentNotes.add(new Note(segmentNote));
		}
		
//		System.out.println("w/ fig:");
//		for(Note note : nonFigSegmentNotes) {
//			System.out.println(note.pitch);
//		}
		
		// check for suspensions 
		for(Note firstNote : events.get(0).notes) {
			for(Note prevNote : previousEvent.notes) {
				double prevNote_offset = (prevNote.onset + prevNote.duration);
				if(firstNote.pitch.equals(prevNote.pitch) && doubleEquals(firstNote.onset, prevNote_offset)) {
//					System.out.println("Suspension...");
//					System.out.println("First note: " + firstNote.pitch);
//					System.out.println("Previous note: " + prevNote.pitch);
//					System.out.println("Held over: " + firstNote.pitch);
				
					// check if suspension
					String firstNoteWithoutOctaveNum = getNoteWithoutOctaveNum(firstNote);
					int firstIndex = enharmonicNotesToID.get(firstNoteWithoutOctaveNum);
					if(!parentNotes.contains(firstIndex) && ((firstNote.duration < prevNote.duration) || (doubleEquals(firstNote.duration, prevNote.duration))) && harmonic(prevNote, previousEvent.notes)) {
//						System.out.println("Possible suspension: " + firstNote.pitch);			
//						System.out.println(firstNote);
						nonFigSegmentNotes.remove(firstNote);
					}
				}
			}
		}
		
		// check for anticipations
		int num_events = events.size();
		for(Note lastNote : events.get(num_events - 1).notes) {
			for(Note nextNote : nextEvent.notes) {
				double lastNote_offset = (lastNote.onset + lastNote.duration);
				if(lastNote.pitch.equals(nextNote.pitch) && doubleEquals(lastNote_offset, nextNote.onset)) {
					String lastNoteWithoutOctaveNum = getNoteWithoutOctaveNum(lastNote);
					int lastIndex = enharmonicNotesToID.get(lastNoteWithoutOctaveNum);
//					System.out.println("Anticipation...");
//					System.out.println("Last note: " + lastNote.pitch);
//					System.out.println("Next note: " + nextNote.pitch);
//					System.out.println("Held over: " + lastNote.pitch);
//					System.out.println("Last note nonharmonic? " + !parentNotes.contains(lastIndex));
//					System.out.println("Next note harmonic? : " + harmonic(nextNote, nextEvent.notes));
					if(!parentNotes.contains(lastIndex) && ((lastNote.duration < nextNote.duration) || (doubleEquals(lastNote.duration, nextNote.duration))) && harmonic(nextNote, nextEvent.notes)) {
//						System.out.println("Possible anticipation: " + lastNote.pitch);
//						System.out.println(lastNote);
						nonFigSegmentNotes.remove(lastNote);
					}
				}
			}
		}
		
		// check for passing and neighbor notes
		int NUM_NOTES = 12;
		int LAST_TWO_EVENTS = 2;
		List<Event> allEvents = new ArrayList<Event>();
		allEvents.add(previousEvent);
		allEvents.addAll(events);
		allEvents.add(nextEvent);
		
		for(int i = 0; i < (allEvents.size() - 2); i++) {
			Event event = allEvents.get(i);
			for(Note note : event.notes) {	
				int harmonicCount = 0;
				int belongsToSegment = 0;
				String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
				int noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
				int noteOctaveIndex = enharmonicNotesToOctaveID.get(noteWithoutOctaveNum);
				int noteNum = getOctaveNum(note.pitch) * NUM_NOTES + noteOctaveIndex;
				double noteOffset = note.onset + note.duration;
				
				if(event != previousEvent && parentNotes.contains(noteIndex)) {
					belongsToSegment++;
					harmonicCount++;
				}
				else if(harmonic(note, event.notes)) {
					harmonicCount++;
				}
				
				for(Note nextNote : allEvents.get(i + 1).notes) {
					boolean up = false;
					boolean down = false;
					String nextNoteWithoutOctaveNum = getNoteWithoutOctaveNum(nextNote);
					int nextNoteIndex = enharmonicNotesToID.get(nextNoteWithoutOctaveNum);
					int nextNoteOctaveIndex = enharmonicNotesToOctaveID.get(nextNoteWithoutOctaveNum);
					int nextNoteNum = getOctaveNum(nextNote.pitch) * NUM_NOTES + nextNoteOctaveIndex;
					double nextNoteOffset = nextNote.onset + nextNote.duration;
					int interval = nextNoteNum - noteNum;
					int reverseInterval = noteNum - nextNoteNum;
//					System.out.println("Note: " + note.pitch + " Next note: " + nextNote.pitch + " Note num: " + noteNum + " Next note num: " + nextNoteNum + " interval: " + interval + "reverse interval: " + reverseInterval);
					if((note.accent > nextNote.accent) && !parentNotes.contains(nextNoteIndex) && doubleEquals(noteOffset, nextNote.onset) && (doubleEquals(note.duration, nextNote.duration) || (nextNote.duration < note.duration)) && (((interval <= 3) && interval > 0) || ((reverseInterval <= 3) && (reverseInterval > 0)))) {
						if((interval <= 3) && (interval > 0)) {
							up = true;
						}
						else if((reverseInterval <= 3) && (reverseInterval > 0)) {
							down = true;
						}	
						
						for(Note nextNextNote : allEvents.get(i + 2).notes) {
							boolean up2 = false;
							boolean down2 = false;
							String nextNextNoteWithoutOctaveNum = getNoteWithoutOctaveNum(nextNextNote);
							int nextNextNoteIndex = enharmonicNotesToID.get(nextNextNoteWithoutOctaveNum);
							int nextNextNoteOctaveIndex = enharmonicNotesToOctaveID.get(nextNextNoteWithoutOctaveNum);
							int nextNextNoteNum = getOctaveNum(nextNextNote.pitch) * NUM_NOTES + nextNextNoteOctaveIndex;
							int interval2 = nextNextNoteNum - nextNoteNum;
							int reverseInterval2 = nextNoteNum - nextNextNoteNum;
							
							if(doubleEquals(nextNoteOffset, nextNextNote.onset) && ((doubleEquals(nextNote.duration, nextNextNote.duration)) || (nextNote.duration < nextNextNote.duration)) && (((interval2 <= 3) && (interval2 > 0)) || ((reverseInterval2 <= 3) && (reverseInterval2 > 0)))) {
								if((interval2 <= 3) && (interval2 > 0)) {
									up2 = true;
								}
								else if((reverseInterval2 <= 3) && (reverseInterval2 > 0)) {
									down2 = true;
								}
								
								if(allEvents.get(i + 2) != nextEvent && parentNotes.contains(nextNextNoteIndex)) {
									belongsToSegment++;
									harmonicCount++;
								}
								else if(harmonic(nextNextNote, allEvents.get(i + 2).notes)) {
									harmonicCount++;
								}
								
								if(harmonicCount > 1 && belongsToSegment >= 1) {
									// find neighboring note
									if(note.pitch.equals(nextNextNote.pitch)) {
//										System.out.println("");
//										System.out.println("Possible neighbor tone: " + note.pitch + " " + nextNote.pitch + " " + nextNextNote.pitch);
//										System.out.println("Interval: " + interval + " Reverse interval: " + reverseInterval + " Interval2: " + interval2 + " Reverse interval 2: " + reverseInterval2);
//										System.out.println("Note: " + noteOctaveIndex + " Next Note: " + nextNoteIndex + " Next Next Note: " + nextNextNoteIndex);
//										System.out.println(nextNote);
										nonFigSegmentNotes.remove(nextNote);
									}	
									// find passing note
									else if((up == true && up2 == true) || (down == true && down2 == true)) {
//										System.out.println("");
//										System.out.println("Possible passing tone: " + note.pitch + " " + nextNote.pitch + " " + nextNextNote.pitch);
//										System.out.println("Interval: " + interval + " Reverse interval: " + reverseInterval + " Interval2: " + interval2 + " Reverse interval 2: " + reverseInterval2);
//										System.out.println("Note: " + noteOctaveIndex + " Next Note: " + nextNoteIndex + " Next Next Note: " + nextNextNoteIndex);
//										System.out.println(nextNote);
										nonFigSegmentNotes.remove(nextNote);
									}
								}
							}
						}
					}
				}
			}
		}
		
//		System.out.println("w/o fig:");
//		for(Note note : nonFigSegmentNotes) {
//			System.out.println(note.pitch);
//		}
		
		return nonFigSegmentNotes;
	}
	
	private static List<Integer> getChordNotes(String root, String mode, String addedNote) {
		List<Integer> chordNotes = new ArrayList<Integer>();	// list of indices of notes in chord
		
		// add root note to list
		
		int rootIndex = enharmonicNotesToID.get(root);
		chordNotes.add(rootIndex);

		// interval distances
		int MAJOR_SECOND = 2;		// interval of major second
		int MAJOR_THIRD = 4;		// " major third
		int MINOR_THIRD = 3;		// " minor third
		int PERFECT_FOURTH = 5;		// " major fourth
		int PERFECT_FIFTH = 7;		// " perfect fifth
		int DIM_FIFTH = 6;			// " minor fifth
		int AUG_FIFTH = 8;			// " augmented fifth
		int MINOR_SIXTH = 8;		// " minor sixth
		int MAJOR_SIXTH = 9;		// " major sixth
		int AUG_SIXTH = 10;			// " augmented sixth
		int MAJOR_SEVENTH = 11;		// " major seventh
		int MINOR_SEVENTH = 10;		// " minor seventh
		int DIM_SEVENTH = 9;		// " diminished seventh
		int NUM_KEYS = 12;			// number of keys included in notesWithAccidentals
		
		// find index of third, fifth, and added note in notesWithAccidentals for given parent chord
		switch(mode) {
		case "maj":
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MAJOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MAJOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MAJOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			
			break;
		case "7":
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
			break;
		case "min":
			chordNotes.add((rootIndex + MINOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MAJOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MAJOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			
			break;
		case "dim":
			chordNotes.add((rootIndex + MINOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + DIM_FIFTH) % NUM_KEYS);
			
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + DIM_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
					chordNotes.add((rootIndex + DIM_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			case ADDED_NOTES:
				switch(addedNote) {
				case "4":
					chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
					break;
				case "6":
					chordNotes.add((rootIndex + MAJOR_SIXTH) % NUM_KEYS);
					break;
				case "7":
					chordNotes.add((rootIndex + DIM_SEVENTH) % NUM_KEYS);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			
			break;
		case "maj(*3)":
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			chordNotes.add((rootIndex + DIM_FIFTH) % NUM_KEYS);
			break;
		case "sus2":
			chordNotes.add((rootIndex + MAJOR_SECOND) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			break;
		case "sus4":
			chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			break;
		case "7sus4":
			chordNotes.add((rootIndex + PERFECT_FOURTH) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			chordNotes.add((rootIndex + MINOR_SEVENTH) % NUM_KEYS);
			break;
		case "it":
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + AUG_SIXTH) % NUM_KEYS);
			break;
		case "fr":
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + AUG_SIXTH) % NUM_KEYS);
			chordNotes.add((rootIndex + DIM_FIFTH) % NUM_KEYS);
			break;
		case "ger":
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + AUG_SIXTH) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
			break;
		default:	// major triad
			chordNotes.add((rootIndex + MAJOR_THIRD) % NUM_KEYS);
			chordNotes.add((rootIndex + PERFECT_FIFTH) % NUM_KEYS);
		}
		
//		System.out.println("Notes in chord:");
		int noteNum = 0;
		for(int note: chordNotes) {
//			System.out.println(enharmonicIDToNotes.get(note));
			noteNum++;
		}
		
		return chordNotes;
	}

	
//	private static List<String> getNotesInChord(String root, String mode, String addedNote) {
//		List<String> notes = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));
//		List<String> notesWithAccidentals = new ArrayList<>(Arrays.asList("A Bbb", "A# Bb", "B Cb", "B# C", "C# Db", "D Ebb", "D# Eb", "E Fb", "E# F", "F# Gb", "F## G Abb", "G# Ab"));
//		List<String> notesInChord = new ArrayList<String>();			// list of notes in chord
//		
//		// add root note to list
//		notesInChord.add(root);	
//		int rootIndex = notes.indexOf(root.substring(0,1)); // strip root of accidental
//		
//		// find index for root in notesWithAccidentals
//		int rootIndexWithAccidental = -1;
//		String pattern = root + "($| )";
//		Pattern r = Pattern.compile(pattern);
//		
//		for(String note : notesWithAccidentals) {
//			Matcher m = r.matcher(note);
//			if (m.find()) {
//				rootIndexWithAccidental = notesWithAccidentals.indexOf(note);
//			}
//		}
//		
////		System.out.println("Root index: " + rootIndex);
////		System.out.println("Accidental index: " + rootIndexWithAccidental);
//		
//		int thirdIndex = -1;		// index of third of parent chord
//		int fifthIndex = -1;		// index of fifth of parent chord
//		int addedNoteIndex = -1;	// index of added not of parent chord
//		
//		int THIRD = 2;			// interval of generic third
//		int FOURTH = 3;			// " generic fourth
//		int FIFTH = 4;			// " generic fifth
//		int SIXTH = 5; 			// " generic sixth
//		int SEVENTH = 6;		// " generic seventh
//		int NUM_NOTES = 7;		// number of notes included in notes
//		
//		int MAJOR_THIRD = 4;		// interval of major third
//		int MINOR_THIRD = 3;		// " minor third
//		int PERFECT_FOURTH = 5;		// " major fourth
//		int PERFECT_FIFTH = 7;		// " perfect fifth
//		int DIM_FIFTH = 6;			// " minor fifth
//		int AUG_FIFTH = 8;			// " augmented fifth
//		int MAJOR_SIXTH = 9;		// " major sixth
//		int MAJOR_SEVENTH = 11;		// " major seventh
//		int MINOR_SEVENTH = 10;		// " minor seventh
//		int DIM_SEVENTH = 9;		// " diminished seventh
//		int NUM_KEYS = 12;			// number of keys included in notesWithAccidentals
//		
//		// find index of third, fifth, and added note in notesWithAccidentals for given parent chord
//		switch(mode) {
//		case "maj":
//			thirdIndex = (rootIndexWithAccidental + MAJOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + PERFECT_FIFTH) % NUM_KEYS;
//			
//			switch(CRMain.simplification) {
//			case GENERIC_ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndex + FOURTH) % NUM_NOTES;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndex + SIXTH) % NUM_NOTES;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndex + SEVENTH) % NUM_NOTES;
//					break;
//				default:
//					break;
//				}
//				break;
//			case ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndexWithAccidental + PERFECT_FOURTH) % NUM_KEYS;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndexWithAccidental + MAJOR_SIXTH) % NUM_KEYS;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndexWithAccidental + MAJOR_SEVENTH) % NUM_KEYS;
//					break;
//				default:
//					break;
//				}
//				break;
//			default:
//				break;
//			}
//			
//			break;
//		case "7":
//			thirdIndex = (rootIndexWithAccidental + MAJOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + PERFECT_FIFTH) % NUM_KEYS;
//			addedNoteIndex = (rootIndexWithAccidental + MINOR_SEVENTH) % NUM_KEYS;
//			break;
//		case "min":
//			thirdIndex = (rootIndexWithAccidental + MINOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + PERFECT_FIFTH) % NUM_KEYS;
//			
//			switch(CRMain.simplification) {
//			case GENERIC_ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndex + FOURTH) % NUM_NOTES;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndex + SIXTH) % NUM_NOTES;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndex + SEVENTH) % NUM_NOTES;
//					break;
//				default:
//					break;
//				}
//				break;
//			case ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndexWithAccidental + PERFECT_FOURTH) % NUM_KEYS;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndexWithAccidental + MAJOR_SIXTH) % NUM_KEYS;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndexWithAccidental + MINOR_SEVENTH) % NUM_KEYS;
//					break;
//				default:
//					break;
//				}
//				break;
//			default:
//				break;
//			}
//			
//			break;
//		case "minmaj":
//			thirdIndex = (rootIndexWithAccidental + MINOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + PERFECT_FIFTH) % NUM_KEYS;
//			addedNoteIndex = (rootIndexWithAccidental + MAJOR_SEVENTH) % NUM_KEYS;
//			break;
//		case "dim":
//			thirdIndex = (rootIndexWithAccidental + MINOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + DIM_FIFTH) % NUM_KEYS;
//			
//			switch(CRMain.simplification) {
//			case GENERIC_ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndex + FOURTH) % NUM_NOTES;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndex + SIXTH) % NUM_NOTES;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndex + SEVENTH) % NUM_NOTES;
//					break;
//				default:
//					break;
//				}
//				break;
//			case ADDED_NOTES:
//				switch(addedNote) {
//				case "4":
//					addedNoteIndex = (rootIndexWithAccidental + PERFECT_FOURTH) % NUM_KEYS;
//					break;
//				case "6":
//					addedNoteIndex = (rootIndexWithAccidental + MAJOR_SIXTH) % NUM_KEYS;
//					break;
//				case "7":
//					addedNoteIndex = (rootIndexWithAccidental + DIM_SEVENTH) % NUM_KEYS;
//					break;
//				default:
//					break;
//				}
//				break;
//			default:
//				break;
//			}
//			
//			break;
//		case "hdim":
//			thirdIndex = (rootIndexWithAccidental + MINOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + DIM_FIFTH) % NUM_KEYS;
//			addedNoteIndex = (rootIndexWithAccidental + MINOR_SEVENTH) % NUM_KEYS;
//			break;
//		case "aug":
//			thirdIndex = (rootIndexWithAccidental + MAJOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + AUG_FIFTH) % NUM_KEYS;
//			break;
//		default:	// major triad
//			thirdIndex = (rootIndexWithAccidental + MAJOR_THIRD) % NUM_KEYS;
//			fifthIndex = (rootIndexWithAccidental + PERFECT_FIFTH) % NUM_KEYS;
//		}
//		
//		// add third to list
//		pattern = notes.get((rootIndex + THIRD) % NUM_NOTES) + "[^ ]*";	// get correct enharmonic note
//		r = Pattern.compile(pattern);
//		Matcher m = r.matcher(notesWithAccidentals.get(thirdIndex));
//		if(m.find()) {
//			notesInChord.add(m.group(0));
//		}
//		
//		// add fifth to list
//		pattern = notes.get((rootIndex + FIFTH) % NUM_NOTES) + "[^ ]*";	
//		r = Pattern.compile(pattern);
//		m = r.matcher(notesWithAccidentals.get(fifthIndex));
//		if(m.find()) {
//			notesInChord.add(m.group(0));
//		}
//		
//		// add added note to list
//		switch(CRMain.simplification) {
//		case GENERIC_ADDED_NOTES:
//			switch(addedNote) {
//			case "4":
//				notesInChord.add(notes.get(addedNoteIndex));
//				break;
//			case "6":
//				notesInChord.add(notes.get(addedNoteIndex));
//				break;
//			case "7":
//				notesInChord.add(notes.get(addedNoteIndex));
//			default:
//				break;
//			}
//			break;
//		case ADDED_NOTES:
//			switch(addedNote) {
//			case "4":
//				pattern = notes.get((rootIndex + FOURTH) % NUM_NOTES) + "[^ ]*";	
//				r = Pattern.compile(pattern);
//				m = r.matcher(notesWithAccidentals.get(addedNoteIndex));
//				if(m.find()) {
//					notesInChord.add(m.group(0));
//				}
//				break;
//			case "6":
//				pattern = notes.get((rootIndex + SIXTH) % NUM_NOTES) + "[^ ]*";	
//				r = Pattern.compile(pattern);
//				m = r.matcher(notesWithAccidentals.get(addedNoteIndex));
//				if(m.find()) {
//					notesInChord.add(m.group(0));
//				}
//				break;
//			case "7":
//				pattern = notes.get((rootIndex + SEVENTH) % NUM_NOTES) + "[^ ]*";	
//				r = Pattern.compile(pattern);
//				m = r.matcher(notesWithAccidentals.get(addedNoteIndex));
//				if(m.find()) {
//					notesInChord.add(m.group(0));
//				}
//				break;
//			default:
//				break;
//			}
//			break;
//		default:
//			break;
//		}
//		
////		System.out.println("Notes in chord:");
////		for(String note: notesInChord) {
////			System.out.println(note);
////		}
//		
//		return notesInChord;
//	}
	
	private static String getNoteWithoutOctaveNum(Note note) {
		String pattern = "[A-G][#b]?";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(note.pitch);
		if(m.find()) {
			return m.group(0);
		}
		else {
			return "";
		}
	}
	
	private static String getNoteWithoutAccidental(Note note) {
		String pattern = "[A-G]";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(note.pitch);
		if(m.find()) {
			return m.group(0);
		}
		else {
			return "";
		}
	}
	
	private static boolean isAug6Chord(String parentLabel) {
		String pattern = "(it|ger|fr)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		
		if(m.find()) {
			return true;
		}
		else {
			return false;
		}		
	}
	
	private static boolean isFrOrGerChord(String parentLabel) {
		String pattern = "(ger|fr)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		
		if(m.find()) {
			return true;
		}
		else {
			return false;
		}	
	}
	
	private static boolean isSusOrPowChord(String parentLabel) {
		String pattern = "(7sus4|sus4|sus2|maj\\(\\*3\\))";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		
		if(m.find()) {
			return true;
		}
		else {
			return false;
		}		
	}
	
	private static boolean isSusChord(String parentLabel) {
		String pattern = "sus";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		
		if(m.find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static boolean isPowChord(String parentLabel) {
		String pattern = "maj\\(\\*3\\)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		
		if (m.find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private static int countParentNotes(List<Note> segmentNotes, String parentLabel, List<Integer> parentNotes) {
		int numParentNotesInSegment = 0;
		
		// find number of notes in segment that belong to parent chord
		for(Note note : segmentNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			int noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
			
			 if(parentNotes.contains(noteIndex)) {
				numParentNotesInSegment += 1;	
			}
		}
	
		return numParentNotesInSegment;
	}
	
	private static double countParentNotesWeighted(Weight featuresWeight, List<Note> segmentNotes, List<Event> events, String parentLabel, List<Integer> parentNotes, String root) {
		double numParentNotesInSegmentWithWeight = 0.0;
		double featuresWeightValue = 0.0;
		
		// find number of notes in segment that belong to parent chord
		for(Note note : segmentNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			int noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
			
			if(parentNotes.contains(noteIndex)) {
				switch(featuresWeight) {
				case ACCENT:
					if(!note.fromPrevious) {
						featuresWeightValue = note.accent;
//						System.out.println("(Parent) Note: " + note.pitch + " Accent: " + note.accent);
					}
					else {
						featuresWeightValue = 0.0;
//						System.out.println("(Segment) Note: " + note.pitch + " Accent: " + featuresWeightValue);
					}
					break;
				case DURATION:
					if(note.fromPrevious) {
//						System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
						featuresWeightValue = note.duration - (events.get(0).onset - note.onset);
					}
					else {
						featuresWeightValue = note.duration;
					}
//					System.out.println("(Parent) Note: " + note.pitch + " Duration: " + note.duration);
					break;
				case NONE:
					featuresWeightValue = 1.0;
//					System.out.println("(Parent) Note: " + noteWithoutOctaveNum);
					break;
				default:
					featuresWeightValue = 1.0;
				}
				
				numParentNotesInSegmentWithWeight += featuresWeightValue;	
			}
		}
	
		return numParentNotesInSegmentWithWeight;
	}
	
	private static double countSegmentNotes(Weight featuresWeight, List<Note> segmentNotes, List<Event> events) {
		double numNotesInSegmentWithWeight = 0.0;
		double featuresWeightValue = 0.0;
		
		for(Note note : segmentNotes) {
			switch(featuresWeight) {
			case ACCENT:
				if(!note.fromPrevious) {
					featuresWeightValue = note.accent;
//					System.out.println("(Segment) Note: " + note.pitch + " Accent: " + note.accent);
				}
				else {
					featuresWeightValue = 0.0;
//					System.out.println("(Segment) Note: " + note.pitch + " Accent: " + featuresWeightValue);
				}
				break;
			case DURATION:
				if(note.fromPrevious) {
//					System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
					featuresWeightValue = note.duration - (events.get(0).onset - note.onset);
				}
				else {
//					System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
					featuresWeightValue = note.duration;
				}
//				System.out.println("(Segment) Note: " + note.pitch + " Duration: " + note.duration);
				break;
			case NONE:
				featuresWeightValue = 1.0;
//				System.out.println("(Segment) Note: " + note.pitch);
				break;
			default:
				featuresWeightValue = 1.0;
			}
			
			numNotesInSegmentWithWeight += featuresWeightValue;
		}
		
	
		return numNotesInSegmentWithWeight;
	}
	
	private static int findConsistencyLevel(double [] bins, double percentage, boolean none, boolean all) {
		if(none == true) {
			return 0;
		}
		else if(all == true) {
			return 101;
		}
		
		for(double bin : bins) {
			if(percentage <= bin) {
				return (int) (bin * 100);
			}
		}
		
		return 0;
	}
	
	private static int findOverlappedConsistencyLevel(double [] bins, double percentage, boolean none, boolean all) {
		if(none == true) {
			return -1;
		}
		else if(all == true) {
			return 101;
		}
		
		for(double bin : bins) {
			if(percentage > bin) {
				return (int) (bin * 100);
			}
		}
		
		return 0;
	}
	
	private static int purity(Weight featuresWeight, String root, List<Integer> parentNotes, List<Note> segmentNotes, List<Event> events, String parentLabel, boolean overlappedConsistency) {
		double numNotesInSegment = countSegmentNotes(featuresWeight, segmentNotes, events);
		int numParentNotesInSegment = countParentNotes(segmentNotes, parentLabel, parentNotes);
		double numParentNotesInSegmentWithWeight = countParentNotesWeighted(featuresWeight, segmentNotes, events, parentLabel, parentNotes, root);
		double percentage = 0.0;
		double[] bins = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		double[] overlappedBins = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1, 0.0};
		boolean none = false;	// segment has 0% purity
		boolean all = false;	// segment has 100% purity
		
		
//		System.out.println("numParentNotesInSegment: " + numParentNotesInSegment + " numParentNotesInSegmentWithWeight: " + numParentNotesInSegmentWithWeight + " numNotesInSegment: " + numNotesInSegment);
		if(numParentNotesInSegment == 0) {
			none = true;
		}
		else if(numParentNotesInSegment == segmentNotes.size()) {
			all = true;
		}
		else {
			percentage = numParentNotesInSegmentWithWeight / (double) numNotesInSegment;
		}
//		System.out.println("Percentage: " + percentage);
//		System.out.println("Consistency: " + findConsistencyLevel(bins, percentage, none, all));
		if(overlappedConsistency) {
			return findOverlappedConsistencyLevel(overlappedBins, percentage, none, all);
		}
		else {
			return findConsistencyLevel(bins, percentage, none, all);
		}
	}
	
	private static boolean coverage(int interval, String addedNote, List<Integer> parentNotes, List<Note> segmentNotes, String parentLabel, boolean is_reg_chord, boolean is_pow_chord, boolean is_7sus4_chord) {
		int FIFTH_INTERVAL = 2;
		int ADDED_NOTE_INTERVAL = 3;
		int NUM_NOTES_IN_ADDED_NOTE_CHORD = 4;
		// check if this is an added note chord
		if(is_reg_chord && (interval == ADDED_NOTE_INTERVAL) && (parentNotes.size() < NUM_NOTES_IN_ADDED_NOTE_CHORD)) {
//			System.out.println("Not an added note chord");
			return false;
		}
		
		for(Note note : segmentNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			Integer noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
//			System.out.println("Parent note: " + parentNotes.get(interval) + " Segment note: " + noteWithoutOctaveNum);
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
				if(interval == ADDED_NOTE_INTERVAL) { 
					if((parentNotes.size() > NUM_NOTES_IN_ADDED_NOTE_CHORD) && (noteIndex.equals(parentNotes.get(interval)) || (noteIndex.equals(parentNotes.get(interval + 1))))) {
//						System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						return true;
					}
					else if(noteIndex.equals(parentNotes.get(interval))) {
//						 System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						return true;
					}
				}
				else if((interval != ADDED_NOTE_INTERVAL) && noteIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					return true;
				}
				break;
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				if(is_pow_chord && (interval == FIFTH_INTERVAL) && (noteIndex.equals(parentNotes.get(interval)) || (noteIndex.equals(parentNotes.get(interval - 1))))) {
//					System.out.println("(Power chord) Interval covered: " + interval + " Note: " + note.pitch);
					return true;
				}
				else if((interval != ADDED_NOTE_INTERVAL) && noteIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					return true;
				}
				else if(is_7sus4_chord && noteIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					return true;
				}
				else if(interval == ADDED_NOTE_INTERVAL) {
					if((parentNotes.size() > NUM_NOTES_IN_ADDED_NOTE_CHORD) && (noteIndex.equals(parentNotes.get(interval)) || (noteIndex.equals(parentNotes.get(interval + 1))))) {
//						System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						return true;
					}
					else if(noteIndex.equals(parentNotes.get(interval))) {
//						 System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						return true;
					}
				}
			case ADDED_NOTES:
			case MODES:
				if(noteWithoutOctaveNum.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					return true;
				}
				break;
			default:
				break;
			}
		}
		 
		return false;
	}
	
	private static boolean durationAddedNoteGreaterThanRoot(List<Integer> parentNotes, List<Note> segmentNotes) {
		double rootDuration = 0.0;
		double addedNoteDuration = 0.0;
		int NUM_NOTES_IN_ADDED_NOTE_CHORD = 4;
		int rootIndex = 0;
		int addedNoteIndex = 3;
		
		for(Note note : segmentNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			Integer noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				if((parentNotes.size() > NUM_NOTES_IN_ADDED_NOTE_CHORD) && ((noteIndex.equals(parentNotes.get(addedNoteIndex)) || (noteIndex.equals(parentNotes.get(addedNoteIndex + 1)))))) {
//					System.out.println("(Added Note) Note: " + note.pitch + " Duration: " + note.duration);
					addedNoteDuration += note.duration;
				}
				else if(noteIndex.equals(parentNotes.get(addedNoteIndex))) {
//					System.out.println("(Added/7sus4 Note) Note: " + note.pitch + " Duration: " + note.duration); 
					addedNoteDuration += note.duration;
				}
				else if(noteIndex.equals(parentNotes.get(rootIndex))) {
//					System.out.println("(Root) Note: " + note.pitch + " Duration: " + note.duration); 
					rootDuration += note.duration;
				}
				break;
			case ADDED_NOTES:
			case MODES:
				if(noteIndex.equals(parentNotes.get(addedNoteIndex))) {
//					System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
					addedNoteDuration += note.duration;
				}
				else if(noteIndex.equals(parentNotes.get(rootIndex))) {
					rootDuration += note.duration;
				}
				break;
			default:
				break;
			}
		}
		
//		System.out.println("Root duration: " + rootDuration);
//		System.out.println("Added note duration: " + addedNoteDuration);
//		System.out.println("addedNoteDuration > rootDuration ? " + (addedNoteDuration > rootDuration) );
		
		return (addedNoteDuration > rootDuration); 
	}
	
	private static int weighted_coverage(Weight featuresWeight, String addedNote, List<Integer> parentNotes, int interval, List<Note> segmentNotes, List<Event> events, String parentLabel, boolean overlappedConsistency, boolean is_reg_chord, boolean is_pow_chord, boolean is_7sus4_chord) {
		double numNotesInSegment = countSegmentNotes(featuresWeight, segmentNotes, events);
		int numParentNotesInSegment = 0;
		int NUM_NOTES_IN_ADDED_NOTE_CHORD = 4;
		boolean none = false;
		boolean all = false;
		double weighted_interval = 0.0;
		int ADDED_NOTE_INTERVAL = 3;
		int FIFTH_INTERVAL = 2;
		double[] bins = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		double[] overlappedBins = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
		
//		System.out.println("--------------------");
		
		// check if this is an added note chord
		if(is_reg_chord && (interval == ADDED_NOTE_INTERVAL) && (addedNote.isEmpty())) {
//			System.out.println("Not an added note chord");
			return 0;
		}
		
		for(Note note : segmentNotes) {
			String noteWithoutOctaveNum = getNoteWithoutOctaveNum(note);
			Integer noteIndex = enharmonicNotesToID.get(noteWithoutOctaveNum);
			switch(CRMain.simplification) {
			case GENERIC_ADDED_NOTES:
			case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
				if(is_pow_chord && (interval == FIFTH_INTERVAL) && (noteIndex.equals(parentNotes.get(interval)) || (noteIndex.equals(parentNotes.get(interval - 1))))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					switch(featuresWeight) {
					case ACCENT:
						if(!note.fromPrevious) {
//							System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
							weighted_interval += note.accent;
						}
						break;
					case DURATION:
						if(note.fromPrevious) {
//							System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
							weighted_interval += note.duration - (events.get(0).onset - note.onset);
						}
						else {
//							System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
							weighted_interval += note.duration;
						}
						break;
					default:
						weighted_interval += 1.0;
					}
					numParentNotesInSegment += 1;
					break;
				}
				else if((interval != ADDED_NOTE_INTERVAL) && noteIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					switch(featuresWeight) {
					case ACCENT:
						if(!note.fromPrevious) {
//							System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
							weighted_interval += note.accent;
						}
						break;
					case DURATION:
						if(note.fromPrevious) {
//							System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
							weighted_interval += note.duration - (events.get(0).onset - note.onset);
						}
						else {
//							System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
							weighted_interval += note.duration;
						}
						break;
					default:
						weighted_interval += 1.0;
					}
					numParentNotesInSegment += 1;
					break;
				}
				else if(is_7sus4_chord && noteIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval covered: " + interval + " Note: " + note.pitch);
					switch(featuresWeight) {
					case ACCENT:
						if(!note.fromPrevious) {
//							System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
							weighted_interval += note.accent;
						}
						break;
					case DURATION:
						if(note.fromPrevious) {
//							System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
							weighted_interval += note.duration - (events.get(0).onset - note.onset);
						}
						else {
//							System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
							weighted_interval += note.duration;
						}
						break;
					default:
						weighted_interval += 1.0;
					}
					numParentNotesInSegment += 1;
					break;
				}
				else if(interval == ADDED_NOTE_INTERVAL) { 
					if((parentNotes.size() > NUM_NOTES_IN_ADDED_NOTE_CHORD) && ((noteIndex.equals(parentNotes.get(interval)) || (noteIndex.equals(parentNotes.get(interval + 1)))))) {
//						System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						switch(featuresWeight) {
						case ACCENT:
							if(!note.fromPrevious) {
//								System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
								weighted_interval += note.accent;
							}
							break;
						case DURATION:
							if(note.fromPrevious) {
//								System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
								weighted_interval += note.duration - (events.get(0).onset - note.onset);
							}
							else {
//								System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
								weighted_interval += note.duration;
							}
							break;
						default:
							weighted_interval += 1.0;
						}
						numParentNotesInSegment += 1;
						break;
					}
					else if (noteIndex.equals(parentNotes.get(interval))) {
//						System.out.println("ADDED NOTE Interval covered: " + interval + " Note: " + note.pitch);
						switch(featuresWeight) {
						case ACCENT:
							if(!note.fromPrevious) {
//								System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
								weighted_interval += note.accent;
							}
							break;
						case DURATION:
							if(note.fromPrevious) {
//								System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
								weighted_interval += note.duration - (events.get(0).onset - note.onset);
							}
							else {
//								System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
								weighted_interval += note.duration;
							}
							break;
						default:
							weighted_interval += 1.0;
						}
						numParentNotesInSegment += 1;
						break;
					}
				}
				break;
			case ADDED_NOTES:
			case MODES:
				if(noteIndex.equals(parentNotes.get(interval))) {
					switch(featuresWeight) {
					case ACCENT:
						if(!note.fromPrevious) {
//							System.out.println("Note: " + note.pitch + " Accent: " + note.accent);
							weighted_interval += note.accent;
						}
						break;
					case DURATION:
						if(note.fromPrevious) {
//							System.out.println("(fromPrevious) Note: " + note.pitch + " Duration: " + (note.duration - (events.get(0).onset - note.onset)) + " Event onset: " + events.get(0).onset + " Note onset: " + note.onset);
							weighted_interval += note.duration - (events.get(0).onset - note.onset);
						}
						else {
//							System.out.println("Note: " + note.pitch + " Duration: " + note.duration);
							weighted_interval += note.duration;
						}
						break;
					default:
						weighted_interval += 1.0;
					}
					numParentNotesInSegment += 1;
				}
				break;
			default:
				break;
			}
		}
		
		if(numParentNotesInSegment == 0) {
			none = true;
		}
		else if (numParentNotesInSegment == segmentNotes.size()) {
			all = true;
		}
		
		double percentage = (weighted_interval / numNotesInSegment);
		
		
//		System.out.println("Weighted interval: " + weighted_interval);
//		System.out.println("Weighted segment: " + numNotesInSegment);
//		System.out.println("Ratio: " + (weighted_interval / numNotesInSegment));
//		System.out.println("Consistency: " + findConsistencyLevel(bins, weighted_interval / numNotesInSegment, none, all));
		if(overlappedConsistency) {
			return findOverlappedConsistencyLevel(overlappedBins, percentage, none, all);
		}
		else {
			return findConsistencyLevel(bins, percentage, none, all);
		}
	}	
	
	private static int segment_weighted_duration_coverage(Weight featuresWeight, String addedNote, List<Integer> parentNotes, int interval, List<Note> segmentNotes, List<Event> events, String parentLabel, boolean overlappedConsistency, boolean is_reg_chord, boolean is_pow_chord, boolean is_7sus4_chord) {
		double weighted_interval = 0.0;
		double weighted_segment = 0.0;
		int eventCounter = 0;
		int intervalCounter = 0;
		boolean none = false;
		boolean all = false;
		double[] bins = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		double[] overlappedBins = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
		
		for(Event event : events) {	
			if(coverage(interval, addedNote, parentNotes, event.notes, parentLabel, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
//				System.out.println("Event measure: " + event.measureNumber + " Duration: " + event.duration);
				weighted_interval += event.duration;
				intervalCounter++;
			}
			weighted_segment += event.duration;
			eventCounter++;
		}
		
		if(intervalCounter == 0) {
			none = true;
		}
		else if(intervalCounter == eventCounter) {
			all = true;
		}
		
		double percentage = (weighted_interval / weighted_segment);
		
//		System.out.println("Interval: " + interval);
//		System.out.println("Weighted interval: " + weighted_interval);
//		System.out.println("Weighted segment: " + weighted_segment);
//		System.out.println("Ratio: " + (weighted_interval / weighted_segment));
//		System.out.println("Consistency: " + findConsistencyLevel(bins, weighted_interval / weighted_segment, none, all));
		if(overlappedConsistency) {
			return findOverlappedConsistencyLevel(overlappedBins, percentage, none, all);
		}
		else {
			return findConsistencyLevel(bins, percentage, none, all);
		}
	}

	
	private static double beginningAccented(List<Event> eventsInside) {
//		System.out.println("Accent of first event of segment: " + eventsInside.get(0).accent);
		return eventsInside.get(0).accent;
	}
	
	private static Note findBassNote(List<Note> eventNotes) {
		List<String> notes = new ArrayList<>(Arrays.asList("C", "D", "E", "F", "G", "A", "B"));
		Note bass = eventNotes.isEmpty() ? new Note() : eventNotes.get(0);
		
		// find bass note of segment (lowest note of list of notes)
		for(Note note : eventNotes) {
			int noteOctave = Integer.parseInt(note.pitch.substring(note.pitch.length() - 1));
			int bassOctave = Integer.parseInt(bass.pitch.substring(bass.pitch.length() - 1));
			if(noteOctave < bassOctave) {
				bass = note;
			}
			else if(noteOctave == bassOctave) {
				String noteWithoutAccidentalAndOctaveNum = getNoteWithoutAccidental(note);
				String bassWithoutAccidentalAndOctaveNum = getNoteWithoutAccidental(bass);
				int noteIndex = notes.indexOf(noteWithoutAccidentalAndOctaveNum);
				int bassIndex = notes.indexOf(bassWithoutAccidentalAndOctaveNum);
				
				if(noteIndex < bassIndex) {
					bass = note;
				}
				else if(noteIndex == bassIndex) {
					int noteAccidental = getAccidentalNum(note);
					int bassAccidental = getAccidentalNum(bass);
					
					if(noteAccidental < bassAccidental) {
						bass = note;
					}
				}
			}
		}
		
		return bass;
	}
	
	private static List<Note> findBassNotes(List<Event> events) {
		List<Note> bassNotes = new ArrayList<Note>();
		
		for(Event event : events) {
			bassNotes.add(findBassNote(event.notes));
		}
		
		return bassNotes;
	}
	
	private static List<Note> findNonFigBassNotes(List<Event> events, Event previousEvent, Event nextEvent, List<Integer> parentNotes) {
		List<Note> bassNotes = new ArrayList<Note>();
		
		for(int i = 0; i < events.size(); i++) {
			Event eventBefore = (i == 0) ? previousEvent : events.get(i - 1);
			Event eventAfter = (i == (events.size() - 1)) ? nextEvent : events.get(i + 1);
			List<Note> nonFigEventNotes = getNonFigurationNotesInSegment(events.subList(i, i + 1), events.get(i).notes, eventBefore, eventAfter, parentNotes);
			Note bassNote = findBassNote(nonFigEventNotes);
//			System.out.println("Event num: " + i + " Bass note: " + bassNote);
			bassNotes.add(bassNote);
		}
		
		return bassNotes;
	}
	
	
	private static boolean bassIsInterval(int interval, String addedNote, List<Integer> parentNotes, Note bass, boolean is_reg_chord, boolean is_pow_chord, boolean is_7sus4_chord) {
		int ADDED_NOTE_INTERVAL = 3;
		int FIFTH_INTERVAL = 2;
		int NUM_NOTES_IN_ADDED_NOTE_CHORD = 4;
		
		// check if this is an added note chord
		if(is_reg_chord && (interval == ADDED_NOTE_INTERVAL) && (addedNote.isEmpty())) {
//			System.out.println("Not an added note chord");
			return false;
		}
		
		if(bass.pitch == "") {
			return false;
		}
		
		// find name of bass note without octave number
		String bassNote = getNoteWithoutOctaveNum(bass);
		Integer bassIndex = enharmonicNotesToID.get(bassNote);
		
		switch(CRMain.simplification) {
		case GENERIC_ADDED_NOTES:
		case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
			if(is_pow_chord && (interval == FIFTH_INTERVAL) && (bassIndex.equals(parentNotes.get(interval)) || (bassIndex.equals(parentNotes.get(interval - 1))))) {
//				System.out.println("Interval note: " + enharmonicIDToNotes.get(parentNotes.get(interval)) + " Bass note: " + bass.pitch + " (bassNote == interval)");
				return true;
			}
			else if(interval != ADDED_NOTE_INTERVAL && bassIndex.equals(parentNotes.get(interval))){
//				System.out.println("Interval note: " + enharmonicIDToNotes.get(parentNotes.get(interval)) + " Bass note: " + bass.pitch + " (bassNote == interval)");
				return true;
			}
			else if(is_7sus4_chord && bassIndex.equals(parentNotes.get(interval))) {
//				System.out.println("Interval note: " + enharmonicIDToNotes.get(parentNotes.get(interval)) + " Bass note: " + bass.pitch + " (bassNote == interval)");
				return true;
			}
			else if(interval == ADDED_NOTE_INTERVAL) {
				if((parentNotes.size() > NUM_NOTES_IN_ADDED_NOTE_CHORD) && ((bassIndex.equals(parentNotes.get(interval)) || (bassIndex.equals(parentNotes.get(interval + 1)))))) {
//					System.out.println("Interval note: " + enharmonicIDToNotes.get(parentNotes.get(interval)) + " Bass note: " + bass.pitch + " (bassNote == interval)");
					return true;
				}
				else if(bassIndex.equals(parentNotes.get(interval))) {
//					System.out.println("Interval note: " + enharmonicIDToNotes.get(parentNotes.get(interval)) + " Bass note: " + bass.pitch + " (bassNote == interval)");
					return true;
				}
			}
			break;
		case ADDED_NOTES:
		case MODES:		
//			System.out.println("Interval note: " + parentNotes.get(interval) + " Bass note: " + bass.pitch + " (bassNote == interval): " + (bassNote.equals(parentNotes.get(interval))));
			return (bassIndex.equals(parentNotes.get(interval)));
		default:
			return false;
		}
		
		return false;
	}
	
	private static int weightedBass(Weight featuresWeight, String addedNote, List<Integer> parentNotes, int interval, List<Event> eventsInside, List<Note> bassNotes, boolean overlappedConsistency, boolean is_reg_chord, boolean is_pow_chord, boolean is_7sus4_chord) {
		double intervalAsBassWeight = 0.0;
		double segmentWeight = 0.0;
		double[] bins = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		double[] overlappedBins = {0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1};
		int eventCounter = 0;
		int bassCounter = 0;
		boolean none = false;
		boolean all = false;
		
		for(Note bass : bassNotes) {
			Event event = eventsInside.get(eventCounter);
			switch(featuresWeight) {
			case DURATION:
				if(bassIsInterval(interval, addedNote, parentNotes, bass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					intervalAsBassWeight += event.duration;
					bassCounter++;
//					System.out.println("Interval: " + interval + " Event duration: " + event.duration);
				}
				segmentWeight += event.duration;
				break;
			case ACCENT:
				if(bassIsInterval(interval, addedNote, parentNotes, bass, is_reg_chord, is_pow_chord, is_7sus4_chord)) {
					intervalAsBassWeight += event.accent;
					bassCounter++;
//					System.out.println("Interval: " + interval + " Event accent: " + event.accent);
				}
				segmentWeight += event.accent;
				break;
			default:
				break;
			}
			eventCounter++;
		}
		

		if(bassCounter == 0) {
			none = true;
		}
		else if (bassCounter == eventCounter) {
			all = true;
		}
		
		double percentage = (intervalAsBassWeight / segmentWeight);
		
//		System.out.println("Weighted segment: " + segmentWeight);
//		System.out.println("Weighted bass: " + intervalAsBassWeight);
//		System.out.println("Interval / Segment: " + (intervalAsBassWeight / segmentWeight));
//		System.out.println("Consistency level: " + findConsistencyLevel(bins, (intervalAsBassWeight / segmentWeight), none, all));
		
		if(overlappedConsistency) {
			return findOverlappedConsistencyLevel(overlappedBins, percentage, none, all);
		}
		else {
			return findConsistencyLevel(bins, percentage, none, all);
		}
	}	
	
    private static int findInterval(String parentLabel, String childLabel) {
    	String parentRoot = getRoot(parentLabel);
        String childRoot = getRoot(childLabel);
        
		// find index for root in notesWithAccidentals
        int parentRootIndex = enharmonicNotesToID.get(parentRoot);
		
		int childRootIndex = enharmonicNotesToID.get(childRoot);
		        
        int NUM_NOTES = 12;
        int interval = childRootIndex <= parentRootIndex ? (parentRootIndex - childRootIndex) : ((parentRootIndex + NUM_NOTES) - childRootIndex);
//        System.out.println("Interval: " + interval);
        return interval;
    }
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		for(FeatureType featureType: FeatureType.values()){
			oos.writeBoolean(featureType.isEnabled);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		for(FeatureType featureType: FeatureType.values()){
			featureType.isEnabled = ois.readBoolean();
		}
	}

}


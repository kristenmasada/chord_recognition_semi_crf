package cr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.statnlp.hybridnetworks.GlobalNetworkParam;

import cr.CRMain.Simplify;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class SongUtil {
	public static List<String> enharmonicIDToMajChord = new ArrayList<>(Arrays.asList("A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"));
	public static List<String> enharmonicIDToMinOrDimChord = new ArrayList<>(Arrays.asList("A", "Bb", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"));
	public static List<String> enharmonicIDToAug6Chord = new ArrayList<>(Arrays.asList("A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"));
	public static List<String> enharmonicIDToSus4 = new ArrayList<>(Arrays.asList("A", "Bb", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"));
	public static List<String> enharmonicIDToSus2 = new ArrayList<>(Arrays.asList("A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab"));
	public static HashMap<String, Integer> enharmonicNotesToID = new HashMap<String, Integer>();
	static {
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
	}
	
	public static void print(String message, boolean printEndline, PrintStream... outstream){
		if(outstream.length == 0){
			outstream = new PrintStream[]{System.out};
		}
		for(PrintStream stream: outstream){
			if(stream != null){
				if(printEndline){
					stream.println(message);
				} else {
					stream.print(message);
				}
			}
		}
	}
	
	public static void addAllLabels(String filename) throws IOException {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		
		while(br.ready()) {
			String chordLabel = br.readLine().trim();
			SpanLabel.get(chordLabel);
			WordLabel.get("B-" + chordLabel);
			WordLabel.get("I-" + chordLabel);
		}
		
		br.close();
		
		return;
	}
	
	public static Song[] readData(String filename, boolean setLabeled, Simplify simplification, boolean normalizeEnharmonics) throws IOException {
		ArrayList<String> songFiles = new ArrayList<String>();
		ArrayList<Song> result = new ArrayList<Song>();
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		
		// read in names of song files
		while(br.ready()) {
			String songFilename = br.readLine();
			songFiles.add(songFilename);
		}
		
		br.close();
		
		File directory = new File("./");
		System.out.println(directory.getAbsolutePath());
		
		try {
			Builder parser = new Builder();
			
			// begin instance id at 1
			int instanceId = 1;
			
			for(int i = 0; i < songFiles.size(); i++) {
				Document doc = parser.build(songFiles.get(i));
				
				// for one song---fix to iterate over multiple songs
				Element root = doc.getRootElement();
			
				// read in a song instance
				Song song = Song.parseXML(root, instanceId, simplification, normalizeEnharmonics);
				
				// if true, guarantees that this song instance will be considered
				// during training (see Instance.java)
				if(setLabeled) {
					song.setLabeled();
				}
				else {
					song.setUnlabeled();
				}
				
				// predictions?
				
				// output song info
				System.out.println(song.title);
				
				// add song instance to result
				result.add(song);
				instanceId += 1;
			}
		} catch (ParsingException e) {
			System.err.println("Cafe con Leche is malformed today. How embarrassing!");
		} catch (IOException ex) {
			System.err.println("Could not connect to Cafe con Leche. The site may be down.");
		}
		
		return result.toArray(new Song[result.size()]);
	}
	
	public static String simplifyLabel(String label, Simplify simplification) {
		switch(simplification) {
		case MODES:
			String pattern = "([BI]-)?[A-G][#b]?:(maj|min|dim|aug)";	// only match chord modes
			Pattern r = Pattern.compile(pattern);						// create pattern object
			Matcher m = r.matcher(label);								// simplify label
			if(m.find()) {
				return m.group(0);
			}
			else {
				return label;
			}
		case GENERIC_ADDED_NOTES:
			pattern = "([BI]-)?[A-G][#b]?:(maj|min|dim|aug|ger|it|fr)(4|6|7)?";				// match added notes
			r = Pattern.compile(pattern);													// create pattern object
			m = r.matcher(label);															// simplify label
			if(m.find()) {
				return m.group(0);
			}
			else {
				return label;
			}
		case GENERIC_ADDED_NOTES_PLUS_SUS_AND_POW:
			pattern = "([BI]-)?[A-G][#b]?:(maj|7sus|dim|min|sus)(2|4|6|7)?(\\(\\*3\\))?";				// match added notes
			r = Pattern.compile(pattern);													// create pattern object
			m = r.matcher(label);															// simplify label
			if(m.find()) {
				return m.group(0);
			}
			else {
				return label;
			}
		case ADDED_NOTES:
			pattern = "([BI]-)?[A-G][#b]?:(7|minmaj|maj|min|dim|hdim|aug)(4|6|7)?";	// match added notes and
																					// chord modes
			r = Pattern.compile(pattern);											// create pattern object
			m = r.matcher(label);													// simplify label
			if(m.find()) {
				return m.group(0);
			}
			else {
				return label;
			}
		case NONE:
			return label;
		default:
			return label;
		}
	}
	
	public static String getMode(String parentLabel) {
		String pattern = "(7sus4|sus4|sus2|maj\\(\\*3\\)|maj|min|dim|aug|ger|it|fr)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		String mode = m.find() ? m.group(0) : "";
//		System.out.println("Mode: " + mode);	
		return mode;
	}
	
	public static String getRoot(String parentLabel) {
		// get root note
		String pattern = "[A-G][#b]?";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(parentLabel);
		String root = m.find() ? m.group(0) : "";
		
		return root;
	}
	
	public static String normalizeEnharmonicChords(String label, boolean normalizeEnharmonics) throws IOException {
		if(!normalizeEnharmonics) {
			return label;
		}
		
		String prefix = new String();
		String root = new String();
		String suffix = new String();
		String pattern = "([BI]-)?(.*)(:(7sus|maj|min|dim|aug|ger|fr|it|sus)(2|4|6|7)?(\\(\\*3\\))?)";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(label);
		
		if(m.find()) {
			prefix = m.group(1);
			root = m.group(2);
			suffix = m.group(3);
		}
		String mode = getMode(label);
		String correctRoot = new String();
		int enharmonicRootID = root != "" ? enharmonicNotesToID.get(root) : 0;
		
		switch(mode) {
		case "maj":
		case "sus2":
		case "maj(*3)":
			correctRoot = enharmonicIDToMajChord.get(enharmonicRootID);
			break;
		case "min":
		case "dim":
		case "sus4":
		case "7sus4":
			correctRoot = enharmonicIDToMinOrDimChord.get(enharmonicRootID);
			break;
		case "ger":
		case "it":
		case "fr":
			correctRoot = enharmonicIDToAug6Chord.get(enharmonicRootID);
			break;
		default:
			correctRoot = enharmonicIDToMajChord.get(enharmonicRootID);
			break;
		}
		
//		System.out.println("");
//		System.out.println("Original label: " + label);
//		System.out.println("enharmonicRootID: " + enharmonicRootID + " original root: " + root + " correct root: " + correctRoot);
		
		if(!root.equals(correctRoot)) {
//			System.out.println("Original label: " + label);
			if(prefix != null) {
				label = prefix + correctRoot + suffix;
			}
			else {
				label = correctRoot + suffix;
			}
//			System.out.println("New label: " + label);
		}
		
		return label;
	}
	
	public static List<Span> labelsToSpans(List<WordLabel> labels, List<Event> input, double length) {
		List<Span> result = new ArrayList<Span>();
		double onset = 0.0;
		double offset = 0.0;
		int startIndx = 0;
		int stopIndx = 0;
		SpanLabel label = null;
		
		for(int i = 0; i <= labels.size(); i++) {
			if (i == labels.size()) {
				stopIndx = i;
				offset = length; 
				result.add(new Span(onset, offset, startIndx, stopIndx, label));
			} 
			else {
				String form = labels.get(i).form;
				Event curEvent = input.get(i);
				if(form.startsWith("B")) {
					if(i != 0) {
						offset = curEvent.onset;
						stopIndx = i;
						result.add(new Span(onset, offset, startIndx, stopIndx, label));
					}
					onset = curEvent.onset;
					startIndx = i;
					label = SpanLabel.get(form.substring(form.indexOf("-")+1));
				}
			}
		}
		return result;
	}
	
	
	public static void setupFeatures(Class<? extends IFeatureType> featureTypeClass, String[] features){
		try {
			Method valueOf = featureTypeClass.getMethod("valueOf", String.class);
			IFeatureType[] featureTypes = (IFeatureType[])featureTypeClass.getMethod("values").invoke(null);
			if(features != null && features.length > 0){
				for(IFeatureType feature: featureTypes){
					feature.disable();
				}
				for(String feature: features){
					((IFeatureType)valueOf.invoke(null, feature.toUpperCase())).enable();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int[] listToArray(List<Integer> list){
		for(int i = list.size() - 1; i >= 0; i--){
			if(list.get(i) == -1){
				list.remove(i);
			}
		}
		int[] result = new int[list.size()];
		for(int i = 0; i < list.size(); i++){
			result[i] = list.get(i);
		}
		return result;
	}

	
}
package cr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.statnlp.example.base.BaseInstance;

import static cr.SongUtil.simplifyLabel;
import static cr.SongUtil.normalizeEnharmonicChords;

import cr.CRMain.Simplify;
import nu.xom.Element;
import nu.xom.Elements;

public class Song extends BaseInstance<Song, List<Event>, List<Span>> {

	private static final long serialVersionUID = -6338401873189645364L;

	public String title; 						// title of song
	public List<WordLabel> outputTokenized;		// tokenized output
	public List<WordLabel> predictionTokenized; // tokenized predictions
	public double length;						// total length of song in quarterLength
	
	public Song() {
		this(1, 1.0, new ArrayList<Event>(), new ArrayList<Span>(), "", 0.0);
	}

	public Song(int instanceId) {
		this(instanceId, 1.0, new ArrayList<Event>(), new ArrayList<Span>(), "", 0.0, new ArrayList<WordLabel>());
	}

	public Song(int instanceId, double weight) {
		this(instanceId, weight, (List<Event>) null, (List<Span>) null, (String) null, 0.0, (List<WordLabel>) null);
	}

	public Song(int instanceId, double weight, List<Event> input, List<Span> output, String title, double length) {
		super(instanceId, weight);
		this.input = input;
		this.output = output;
		this.prediction = Collections.emptyList();
		this.title = title;
		this.length = length;
		this.outputTokenized = null;
		this.predictionTokenized = null;
	}
	
	public Song(int instanceId, double weight, List<Event> input, List<Span> output, String title, double length, List<WordLabel> outputTokenized) {
		super(instanceId, weight);
		this.input = input;
		this.output = output;
		this.prediction = Collections.emptyList();
		this.title = title;
		this.length = length;
		this.outputTokenized = outputTokenized;
		this.predictionTokenized = null;
	}
	
	public Song duplicate(){
		Song result = super.duplicate();
		result.title = this.title;
		result.length = this.length;
		result.outputTokenized = this.outputTokenized == null ? null : new ArrayList<WordLabel>(this.outputTokenized);
		result.predictionTokenized = this.predictionTokenized == null ? null : new ArrayList<WordLabel>(this.predictionTokenized);
		return result;
	}
	
	public static Song parseXML(Element element, int instanceId, Simplify simplification, boolean normalizeEnharmonics) throws IOException {
		Song song = new Song(instanceId);
		String tag;
		
		// weight as 1.0?
		song.title = element.getFirstChildElement("title").getValue();
		song.length = Double.parseDouble(element.getFirstChildElement("length").getValue());
//		System.out.println("Title: " + song.title + ", Length: " + song.length);
		
		Elements events = element.getFirstChildElement("events").getChildElements();
		for (int i = 0; i < events.size(); i++) {
			if(i == 0) {
				song.input.add(Event.parseXML(events.get(i), null));
			}
			else {
				song.input.add(Event.parseXML(events.get(i), song.input.get(i - 1)));
			}
			tag = events.get(i).getFirstChildElement("tag").getValue().trim();
			song.outputTokenized.add(WordLabel.get(normalizeEnharmonicChords(simplifyLabel(tag, simplification), normalizeEnharmonics)));
		}
		
		Elements segments = element.getFirstChildElement("segments").getChildElements();
		for (int i = 0; i < segments.size(); i++) {
			song.output.add(Span.parseXML(segments.get(i), simplification, normalizeEnharmonics));
		}
		
		return song;
	}
	
	public void setPredictionTokenized(List<WordLabel> predictionTokenized){
		this.predictionTokenized = predictionTokenized;
		if(predictionTokenized == null){
			this.prediction = null;
		} else {
			this.prediction = SongUtil.labelsToSpans(predictionTokenized, input, length);
		}
	}
	
	public String toString() {
		String text = "Title: " + this.title + "\n";
		text += "Length: " + this.length + "\n";
		for (Event event : this.input) {
			int eventIndex = this.input.indexOf(event);
			text += event.toString();
			text += "- Tag: " + this.outputTokenized.get(eventIndex).toString() + "\n";
		}
		
		text += "Output:\n";
		
		for (Span span : this.output) {
			text += span.toString();
		}
		
		text += "Predictions:\n";
		
		for (Span span : this.prediction) {
			text += span.toString();
		}
		
		return text;
	}
	
	public List<Event> getInputTokenized(){
		if(input == null){
			throw new RuntimeException("Input not available.");
		}
		return input;
	}
	
	public List<WordLabel> getOutputTokenized(){
		if(outputTokenized == null){
			throw new RuntimeException("Output not yet tokenized.");
		}
		return outputTokenized;
	}

	public List<Event> duplicateInput() {
		return input == null ? null : new ArrayList<Event>(input);
	}

	public List<Span> duplicateOutput() {
		return output == null ? null : new ArrayList<Span>(output);
	}

	public List<Span> duplicatePrediction() {
		return prediction == null ? null : new ArrayList<Span>(prediction);
	}

	@Override
	public int size() {
		return getInput().size();
	}
}
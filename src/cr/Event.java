package cr;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;

public class Event {

	// Should I include a serialVersionUID?
	
	public double onset;			// onset of event (measured in quarter length)
	public double duration;			// duration of event (measured in quarter length)
	public int measureNumber;		// number of measure that event occurs in
	public double accent;			// accent of event (based on Music21's beatStrength() method)
	public List<Note> notes;		// list of notes within event
	
	public Event() {
		notes = new ArrayList<Note>();
	}

	public Event(double onset, double duration, int measureNumber, double accent, List<Note> notes) {
		this.onset = onset;
		this.duration = duration;
		this.measureNumber = measureNumber;
		this.accent = accent;
		this.notes = notes;
	}
	
	public static Event parseXML(Element element, Event previousEvent) {
		Event event = new Event();
		
		event.onset = Double.parseDouble(element.getFirstChildElement("onset").getValue());
		event.duration = Double.parseDouble(element.getFirstChildElement("duration").getValue());
		event.measureNumber = Integer.parseInt(element.getFirstChildElement("measureNumber").getValue());
		event.accent = Double.parseDouble(element.getFirstChildElement("accent").getValue());
		
		Elements notes = element.getFirstChildElement("notes").getChildElements();
		for (int i = 0; i < notes.size(); i++) {
			event.notes.add(Note.parseXML(notes.get(i), previousEvent));
		}
		
//		System.out.println(event.toString());
		
		return event;
	}
	
	public String toString() {
		String text = new String("Event:\n"
				+ "- Onset: " + this.onset + "\n"
				+ "- Duration: " + this.duration + "\n"
				+ "- Measure Number: " + this.measureNumber + "\n"
				+ "- Accent: " + this.accent + "\n"
				+ "- Notes:\n");
		for (Note note : notes) {
			text += note.toString();
		}
		return text;
	}
}
package cr;

import nu.xom.Element;

public class Note {

	// Should I include a serialVersionUID?
	
	public String pitch;
	public double duration;
	public boolean fromPrevious;
	public double accent;			// accent of event (based on Music21's beatStrength() method)
	public double onset;
	
	public Note() {
		pitch = "";
	}
	
	public Note(Note oldNote) {
		this.pitch = oldNote.pitch;
		this.duration = oldNote.duration;
		this.fromPrevious = oldNote.fromPrevious;
		this.accent = oldNote.accent;
		this.onset = oldNote.onset;
	}

	public Note(String pitch, double duration, Boolean fromPrevious, double accent, double onset) {
		this.pitch = pitch;
		this.duration = duration;
		this.fromPrevious = fromPrevious;
		this.accent = accent;
		this.onset = onset;
	}
	
	public static Note parseXML(Element element, Event previousEvent) {
		Note note = new Note();
		
		note.pitch = element.getFirstChildElement("pitch").getValue();
		note.duration = Double.parseDouble(element.getFirstChildElement("duration").getValue());
		note.fromPrevious = element.getFirstChildElement("fromPrevious").getValue().equals("True") ? true : false;
		note.accent = Double.parseDouble(element.getFirstChildElement("accent").getValue());
		if(!note.fromPrevious) {
			note.onset = Double.parseDouble(element.getFirstChildElement("onset").getValue());
		}
		else {
			for(Note previousNote : previousEvent.notes) {
				if(note.pitch.equals(previousNote.pitch)) {
					note.onset = previousNote.onset;
				}
			}
		}
		return note;
	}
	
	private static boolean doubleEquals(double num1, double num2) {
		double epsilon = 0.0000001d;
		return (Math.abs(num1 - num2) < epsilon);
	}
	
	@Override
	public boolean equals(Object note) {
		if(note == null) {
			return false;
		}
		else if(getClass() != note.getClass()) {
			return false;
		}
		final Note compNote = (Note) note;
		if((pitch == compNote.pitch) && doubleEquals(duration, compNote.duration) && (fromPrevious == compNote.fromPrevious) && doubleEquals(accent, compNote.accent) && doubleEquals(onset, compNote.onset)) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		return new String("-- Note:\n"
						+ "--- Pitch: " + this.pitch + "\n"
						+ "--- Duration: " + this.duration + "\n"
						+ "--- From Previous: " + this.fromPrevious + "\n"
						+ "--- Accent: " + this.accent + "\n"
						+ "--- Onset: " + this.onset + "\n"
						+ ""); 
	}
}
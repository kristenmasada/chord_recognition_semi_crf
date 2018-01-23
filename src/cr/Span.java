package cr;

import java.io.IOException;
import java.io.Serializable;

import cr.CRMain.Simplify;
import static cr.SongUtil.simplifyLabel;
import static cr.SongUtil.normalizeEnharmonicChords;
import nu.xom.Element;

public class Span implements Comparable<Span>, Serializable{
	
//	private static final long serialVersionUID = 1849557517361796614L; ?!
	public SpanLabel label;
	public double onset;
	public double offset;
	public int start;
	public int stop;
	
	public Span() {
		
	}

	public Span(double onset, double offset, int start, int stop, SpanLabel label) {
		this.onset = onset;
		this.offset = offset;
		this.start = start;
		this.stop = stop;
		this.label = label;
	}
	
	public static Span parseXML(Element element, Simplify simplification, boolean normalizeEnharmonics) throws IOException {
		Span span = new Span();
		
		span.label = SpanLabel.get(normalizeEnharmonicChords(simplifyLabel(element.getFirstChildElement("chordLabel").getValue(), simplification), normalizeEnharmonics));	
		span.onset = Double.parseDouble(element.getFirstChildElement("onset").getValue());
		span.offset = Double.parseDouble(element.getFirstChildElement("offset").getValue());
		span.start = Integer.parseInt(element.getFirstChildElement("eventStart").getValue());
		span.stop = Integer.parseInt(element.getFirstChildElement("eventStop").getValue());
		
//		System.out.println(span.toString());
		
		return span;
	}
	
	public boolean equals(Object o){
		if(o instanceof Span){
			Span s = (Span)o;
//			if(onset != s.onset) return false;
//			if(offset != s.offset) return false;
			if(start != s.start) return false;
			if(stop != s.stop) return false;
			return label.equals(s.label);
		}
		return false;
	}

	@Override
	public int compareTo(Span o) {
		if(onset < o.onset) return -1;
		if(onset > o.onset) return 1;
		if(offset < o.onset) return -1;
		if(offset > o.offset) return 1;
		if (start < o.start) return -1;
		if (stop > o.stop) return 1;
		if(label == null){
			if(o.label == null){
				return 0;
			}
			return -1;
		} else {
			if(o.label == null){
				return 1;
			}
			return label.compareTo(o.label); 
		}
	}
	
	public String toString(){
		String text = new String("Segment:\n"
				+ "- Label: " + this.label.toString() + "\n"
				+ "- Onset: " + this.onset + "\n"
				+ "- Offset: " + this.offset + "\n"
				+ "- Event Start: " + this.start + "\n"
				+ "- Event Stop: " + this.stop + "\n");
		return text;
	}

}

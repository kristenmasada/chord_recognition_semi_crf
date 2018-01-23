package cr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SpanLabel implements Comparable<SpanLabel>, Serializable {
	
	private static final long serialVersionUID = -4455671752828268868L;
	//	private static final long serialVersionUID = -2821034438335023157L;	?
	public static final Map<String, SpanLabel> LABELS = new HashMap<String, SpanLabel>();
	public static final Map<Integer, SpanLabel> LABELS_INDEX = new HashMap<Integer, SpanLabel>();
	
	public static SpanLabel get(String form){
		if(!LABELS.containsKey(form)){
			SpanLabel label = new SpanLabel(form, LABELS.size());
			LABELS.put(form, label);
			LABELS_INDEX.put(label.id, label);
		}
		return LABELS.get(form);
	}
	
	public static SpanLabel get(int id){
		return LABELS_INDEX.get(id);
	}
	
	public String form;
	public int id;
	
	private SpanLabel(String form, int id) {
		this.form = form;
		this.id = id;
	}

	@Override
	public int hashCode() {
		return form.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SpanLabel))
			return false;
		SpanLabel other = (SpanLabel) obj;
		if (form == null) {
			if (other.form != null)
				return false;
		} else if (!form.equals(other.form))
			return false;
		return true;
	}
	
	public String toString(){
		return String.format("%s (id: %d)", form, id);
	}

	@Override
	public int compareTo(SpanLabel o) {
		return Integer.compare(id, o.id);
	}
	
	public static int compare(SpanLabel o1, SpanLabel o2){
		if(o1 == null){
			if(o2 == null) return 0;
			else return -1;
		} else {
			if(o2 == null) return 1;
			else return o1.compareTo(o2);
		}
	}
}

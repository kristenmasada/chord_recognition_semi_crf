package cr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The class representing a label for a word/token<br>
 * This class stores singleton objects, which can be retrieved by its ID or name.
 * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
 *
 */
public class WordLabel implements Comparable<WordLabel>, Serializable{
	
	private static final long serialVersionUID = 3941855494601533166L;
	public static final Map<String, WordLabel> LABELS = new HashMap<String, WordLabel>();
	public static final Map<Integer, WordLabel> LABELS_INDEX = new HashMap<Integer, WordLabel>();
	
	public static WordLabel get(String form){
		if(!LABELS.containsKey(form)){
			WordLabel label = new WordLabel(form, LABELS.size());
			LABELS.put(form, label);
			LABELS_INDEX.put(label.id, label);
		}
		return LABELS.get(form);
	}
	
	public static WordLabel get(int id){
		return LABELS_INDEX.get(id);
	}
	
	public String form;
	public int id;
	
	private WordLabel(String form, int id) {
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
		if (!(obj instanceof WordLabel))
			return false;
		WordLabel other = (WordLabel) obj;
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
	public int compareTo(WordLabel o) {
		return Integer.compare(id, o.id);
	}
	
	public static int compare(WordLabel o1, WordLabel o2){
		if(o1 == null){
			if(o2 == null) return 0;
			else return -1;
		} else {
			if(o2 == null) return 1;
			else return o1.compareTo(o2);
		}
	}
}


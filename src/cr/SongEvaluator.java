package cr;

import static cr.SongUtil.print;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.statnlp.commons.types.Instance;




public class SongEvaluator {
	private static class Statistics {
		public int correct = 0;
		public int totalPred = 0;
		public int totalGold = 0;
		
		public void add(Statistics s){
			this.correct += s.correct;
			this.totalPred += s.totalPred;
			this.totalGold += s.totalGold;
		}
		
		public double calculatePrecision(){
			if(totalPred == 0){
				return 0;
			}
			return 1.0*correct/totalPred;
		}
		
		public double calculateRecall(){
			if(totalGold == 0){
				return 0;
			}
			return 1.0*correct/totalGold;
		}
		
		public double calculateF1(){
			double precision = calculatePrecision();
			double recall = calculateRecall();
			double f1 = precision*recall;
			if(f1 == 0){
				return 0;
			}
			f1 = 2*f1/(precision+recall);
			return f1;
		}
		
		public void printScore(PrintStream... outstreams){
			double precision = calculatePrecision();
			double recall = calculateRecall();
			double f1 = calculateF1();
			print(String.format("(Segment-Based) Correct: %1$d, Predicted: %2$d, Gold: %3$d ", correct, totalPred, totalGold), true, outstreams);
			print(String.format("(Segment-Based) Overall P: %#5.2f%%, R: %#5.2f%%, F: %#5.2f%%", 100*precision, 100*recall, 100*f1), true, outstreams);
		}
		
		public void printEventScore(PrintStream... outstreams) {
			double accuracy = calculateRecall();
			print(String.format("(Event-Based) Correct: %1$d, Predicted: %2$d, Gold: %3$d ", correct, totalPred, totalGold), true, outstreams);
			print(String.format("(Event-Based) Overall A: %#5.2f%%", 100*accuracy), true, outstreams);
		}
	}

	public static void evaluate(Instance[] predictions, PrintStream outstream, int printLimit){
		int count = 0;
		PrintStream[] outstreams = new PrintStream[]{outstream, System.out};
		Statistics finalSpanResult = new Statistics();
		Statistics finalEventResult = new Statistics();
		for(Instance inst: predictions){
			if(count >= printLimit){
				outstreams = new PrintStream[]{outstream};
			}
			Song instance = (Song)inst;
			print("Title:", true, outstreams);
			print(instance.title.toString(), true, outstreams);
			print("Input:", true, outstreams);
			print(instance.input.toString(), true, outstreams);
			print("Gold:", true, outstreams);
			print(instance.output.toString(), true, outstreams);
			print("Prediction:", true, outstreams);
			print(instance.prediction.toString(), true, outstreams);
			Statistics[] spanScores = getSpanScore(new Instance[]{instance});
			Statistics[] eventScores = getEventScore(new Instance[]{instance});
			Statistics spanOverall = sum(spanScores);
			Statistics eventOverall = sum(eventScores);
			finalSpanResult.add(spanOverall);
			finalEventResult.add(eventOverall);
			spanOverall.printScore(outstreams);
			eventOverall.printEventScore(outstreams);
			print("", true, outstreams);
//			printDetailedScore(spanScores, outstreams);
			print("", true, outstreams);
			count += 1;
		}
		if(printLimit > 0){
			print("", true, outstream, System.out);
		} else {
			print("", true, outstreams);
		}
		outstreams = new PrintStream[]{outstream, System.out};
		print("### Overall score ###", true, outstream, System.out);
		finalSpanResult.printScore(outstreams);
		finalEventResult.printEventScore(outstreams);
		print("", true, outstream, System.out);
//		Statistics[] scores = getSpanScore(predictions);
//		printDetailedScore(scores, outstream, System.out);
	}
	
	private static Statistics sum(Statistics[] scores){
		Statistics result = new Statistics();
		for(Statistics score: scores){
			result.add(score);
		}
		return result;
	}
	
	private static Statistics[] getSpanScore(Instance[] instances) {
		int size = SpanLabel.LABELS.size();
		Statistics[] result = createStatistics(size);
		for(Instance inst: instances) {
			Song instance = (Song)inst;
			List<Span> predicted;
			List<Span> actual;
			predicted = duplicate(instance.getPrediction());
			actual = duplicate(instance.getOutput());
			for(Span span : actual) {
				if(predicted.contains(span)) {
					predicted.remove(span);
					SpanLabel label = span.label;
					result[label.id].correct += 1;
					result[label.id].totalPred += 1;
				}
				result[span.label.id].totalGold += 1;
			}
			for(Span span: predicted){
				result[span.label.id].totalPred += 1;
			}
		}
		return result;
	}
	
	private static Statistics[] getEventScore(Instance[] instances) {
		int size = SpanLabel.LABELS.size();
		Statistics[] result = createStatistics(size);
		for(Instance inst: instances) {
			Song instance = (Song)inst;
			List<Span> predicted;
			List<Span> actual;
			predicted = duplicate(instance.getPrediction());
			actual = duplicate(instance.getOutput());
			ListIterator<Span> it_act = actual.listIterator();
			ListIterator<Span> it_pred = predicted.listIterator();
			Span span_act = new Span();
			Span span_pred = new Span();
			Event e_act = new Event();
			Event e_pred = new Event();
			int numPredEvents = span_pred.stop - span_pred.start;
			int ip = 0;
			while(it_act.hasNext()) {
				 span_act = it_act.next();
				 int numActualEvents = span_act.stop - span_act.start;
				 for(int i = 0; i < numActualEvents; i++) {
					 System.out.println("Event Num: " + (span_act.start + i) + ", Measure Num: " + instance.input.get(span_act.start).measureNumber);
					 if(ip < numPredEvents) {
						 ip++;
					 }
					 else {
						 span_pred = it_pred.next();
						 ip = 0;
						 numPredEvents = span_pred.stop - span_pred.start;
						 ip++;
					 }
					 
					 if(span_pred.label.id == span_act.label.id) {
						 result[span_act.label.id].correct += 1;
					 }
					 System.out.println("Actual: " + span_act.label + " Predicted: " + span_pred.label);
					 result[span_act.label.id].totalGold += 1;
					 result[span_pred.label.id].totalPred += 1;
				 }
			}
		}
		return result;
	}
	
	private static Statistics[] createStatistics(int size){
		Statistics[] result = new Statistics[size];
		for(int i=0; i<size; i++){
			result[i] = new Statistics();
		}
		return result;
	}
	
	private static void printDetailedScore(Statistics[] result, PrintStream... outstreams){
		double avgF1 = 0;
		for(int i=0; i<result.length; i++){
			double precision = result[i].calculatePrecision();
			double recall = result[i].calculateRecall();
			double f1 = result[i].calculateF1();
			avgF1 += f1;
			print(String.format("%7s: #Corr:%2$3d, #Pred:%3$3d, #Gold:%4$3d, Pr=%5$#5.2f%% Rc=%6$#5.2f%% F1=%7$#5.2f%%", SpanLabel.get(i).form, result[i].correct, result[i].totalPred, result[i].totalGold, precision*100, recall*100, f1*100), true, outstreams);
		}
		print(String.format("Macro average F1: %.2f%%", 100*avgF1/result.length), true, outstreams);
	}
	
	private static List<Span> duplicate(List<Span> list){
		List<Span> result = new ArrayList<Span>();
		for(Span span: list){
			result.add(span);
		}
		return result;
	}
}

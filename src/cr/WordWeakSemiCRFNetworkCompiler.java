package cr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.statnlp.commons.types.Instance;
import com.statnlp.hybridnetworks.LocalNetworkParam;
import com.statnlp.hybridnetworks.Network;
import com.statnlp.hybridnetworks.NetworkCompiler;
import com.statnlp.hybridnetworks.NetworkException;
import com.statnlp.hybridnetworks.NetworkIDMapper;

/**
 * The class that defines the network/graph structure<br>
 * This is based on StatNLP framework for CRF on acyclic graphs
 * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
 *
 */
public class WordWeakSemiCRFNetworkCompiler extends NetworkCompiler {
	
	private final static boolean DEBUG = true;
	
	private static final long serialVersionUID = 6585870230920484539L;
	public SpanLabel[] labels;
	public int maxLength = 20;
	public int maxSegmentLength = 1;
	public transient long[] allNodes;
	public transient int[][][] allChildren;
	
	public enum NodeType {
		LEAF,
		ROOT,
		BEGIN,
		END,
	}
	
	static {
		NetworkIDMapper.setCapacity(new int[]{200, 150, 150});
	}

	public WordWeakSemiCRFNetworkCompiler(SpanLabel[] labels, int maxLength, int maxSegmentLength) {
		this.labels = labels;
		this.maxLength = Math.max(maxLength, this.maxLength);
		this.maxSegmentLength = Math.max(maxSegmentLength, this.maxSegmentLength);
		System.out.println(String.format("Max size: %s, Max segment length: %s", maxLength, maxSegmentLength));
		System.out.println(Arrays.asList(labels));
		buildUnlabeled();
	}

	@Override
	public SongNetwork compile(int networkId, Instance inst, LocalNetworkParam param) {
		try{
			if(inst.isLabeled()){
				return compileLabeled(networkId, (Song)inst, param);
			} else {
				return compileUnlabeled(networkId, (Song)inst, param);
			}
		} catch (NetworkException e){
			System.out.println(inst);
			throw e;
		}
	}
	
	private SpanLabel wordLabelToSpanLabel(WordLabel label){
		String form = label.form;
		if(form.startsWith("O")) {
			return SpanLabel.get("O");
		} else {
			return SpanLabel.get(form.substring(form.indexOf("-")+1));
		}
	}
	
	private SongNetwork compileLabeled(int networkId, Song instance, LocalNetworkParam param){
		SongNetwork network = new SongNetwork(networkId, instance, param);
		
		List<WordLabel> output = instance.getOutputTokenized();
		int size = output.size();
		
		long leaf = toNode_leaf();
		network.addNode(leaf);
		long prevNode = leaf;
		int prevLabelId = -1;
		int lastPos = 0;
		for(int pos=0; pos<size; pos++){
			WordLabel label = output.get(pos);
			SpanLabel spanLabel = wordLabelToSpanLabel(label);
			int labelId = spanLabel.id;
			if(prevLabelId == -1 || label.form.startsWith("O") || label.form.startsWith("B") || pos-lastPos >= maxSegmentLength){
				if(prevLabelId != -1){
					long end = toNode_end(pos-1, prevLabelId);
					network.addNode(end);
					network.addEdge(end, new long[]{prevNode});
					prevNode = end;
				}
				long begin = toNode_begin(pos, labelId);
				network.addNode(begin);
				network.addEdge(begin, new long[]{prevNode});
				prevNode = begin;
				prevLabelId = labelId;
				lastPos = pos;
			}
		}
		long root = toNode_root(size);
		network.addNode(root);
		long end = toNode_end(size-1, prevLabelId);
		network.addNode(end);
		network.addEdge(end, new long[]{prevNode});
		network.addEdge(root, new long[]{end});
		
		network.finalizeNetwork();
		
		if(DEBUG){
			System.out.println(network);
			SongNetwork unlabeled = compileUnlabeled(networkId, instance, param);
			System.out.println("Contained: "+unlabeled.contains(network));
		}
		return network;
	}
	
	private SongNetwork compileUnlabeled(int networkId, Song instance, LocalNetworkParam param){
		if(allNodes == null){
			buildUnlabeled();
		}
		int size = instance.getInputTokenized().size();
		long root = toNode_root(size);
		int root_k = Arrays.binarySearch(allNodes, root);
		int numNodes = root_k + 1;
		return new SongNetwork(networkId, instance, allNodes, allChildren, param, numNodes);
	}
	
	private void buildUnlabeled(){
		SongNetwork network = new SongNetwork();
		
		long leaf = toNode_leaf();
		network.addNode(leaf);
		List<Long> prevNodes = new ArrayList<Long>();
		List<Long> currNodes = new ArrayList<Long>();
		prevNodes.add(leaf);
		for(int pos=0; pos<maxLength; pos++){
			for(int labelIdx=0; labelIdx<labels.length; labelIdx++){
				int labelId = labels[labelIdx].id;
				long beginNode = toNode_begin(pos, labelId);
				long endNode = toNode_end(pos, labelId);
				
				network.addNode(beginNode);
				network.addNode(endNode);
				
				currNodes.add(endNode);
				
				for(int prevPos=pos; prevPos > pos-maxSegmentLength && prevPos >= 0; prevPos--){
					long prevBeginNode = toNode_begin(prevPos, labelId);
					network.addEdge(endNode, new long[]{prevBeginNode});
				}
				
				for(long prevNode: prevNodes){
					network.addEdge(beginNode, new long[]{prevNode});
				}
			}
			long root = toNode_root(pos+1);
			network.addNode(root);
			for(long currNode: currNodes){
				network.addEdge(root, new long[]{currNode});	
			}
			prevNodes = currNodes;
			currNodes = new ArrayList<Long>();
		}
		network.finalizeNetwork();
		allNodes = network.getAllNodes();
		allChildren = network.getAllChildren();
	}
	
	private long toNode_leaf(){
		return toNode(0, 0, NodeType.LEAF);
	}
	
	private long toNode_root(int pos){
		return toNode(pos+1, 0, NodeType.ROOT);
	}
	
	private long toNode_begin(int pos, int labelId){
		return toNode(pos+1, labelId+1, NodeType.BEGIN);
	}
	
	private long toNode_end(int pos, int labelId){
		return toNode(pos+1, labelId+1, NodeType.END);
	}
	
	private long toNode(int pos, int labelId, NodeType type){
		return NetworkIDMapper.toHybridNodeID(new int[]{pos, type.ordinal(), labelId});
	}

	@Override
	public Song decompile(Network net) {
		SongNetwork network = (SongNetwork)net;
		Song result = (Song)network.getInstance().duplicate();
		List<WordLabel> predictionTokenized = new ArrayList<WordLabel>();
		int node_k = network.countNodes()-1;
		while(node_k > 0){
			int[] children_k = network.getMaxPath(node_k);
			int[] child_arr = network.getNodeArray(children_k[0]);
			int end = child_arr[0]-1;
			NodeType nodeType = NodeType.values()[child_arr[1]];
			if(nodeType == NodeType.LEAF){
				break;
			} else {
				assert nodeType == NodeType.END;
			}
			int labelId = child_arr[2]-1;
			children_k = network.getMaxPath(children_k[0]);
			child_arr = network.getNodeArray(children_k[0]);
			int start = child_arr[0]-1;
			for(int pos=end; pos>=start; pos--){
				WordLabel wordLabel;
				String form = SpanLabel.get(labelId).form;
				if(pos > start){
					wordLabel = WordLabel.get("I-"+form);
				} else {
					wordLabel = WordLabel.get("B-"+form);
				}
				predictionTokenized.add(0, wordLabel);
			}
			node_k = children_k[0];
		}
		result.setPredictionTokenized(predictionTokenized);
		return result;
	}

}


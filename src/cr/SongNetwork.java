package cr;

import com.statnlp.commons.types.Instance;
import com.statnlp.hybridnetworks.LocalNetworkParam;
import com.statnlp.hybridnetworks.TableLookupNetwork;

/**
 * The data structure to represent SMS messages with their annotations as networks/graphs<br>
 * A network represents the model view of the problem.<br>
 * Compare with {@link SMSNPInstance}, which is the real-world view of the problem<br>
 * This is based on StatNLP framework for CRF on acyclic graphs
 * @author Aldrian Obaja <aldrianobaja.m@gmail.com>
 *
 */
public class SongNetwork extends TableLookupNetwork {
	
	private static final long serialVersionUID = -8384557055081197941L;
	public int numNodes = -1;

	public SongNetwork() {}

	public SongNetwork(int networkId, Instance inst, LocalNetworkParam param) {
		super(networkId, inst, param);
	}

	public SongNetwork(int networkId, Instance inst, long[] nodes, int[][][] children, LocalNetworkParam param, int numNodes) {
		super(networkId, inst, nodes, children, param);
		this.numNodes = numNodes;
	}
	
	public int countNodes(){
		if(numNodes < 0){
			return super.countNodes();
		}
		return numNodes;
	}
	
	public void remove(int k){}
	
	public boolean isRemoved(int k){
		return false;
	}

}

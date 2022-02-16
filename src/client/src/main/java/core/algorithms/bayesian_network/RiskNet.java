package core.algorithms.bayesian_network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nd4j.shade.guava.collect.Sets;

import com.bayesserver.Link;
import com.bayesserver.Node;
import com.bayesserver.State;
import com.bayesserver.Table;
import com.bayesserver.inference.InconsistentEvidenceException;
import com.bayesserver.inference.Inference;
import com.bayesserver.inference.InferenceFactory;
import com.bayesserver.inference.QueryOptions;
import com.bayesserver.inference.QueryOutput;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;

import core.model.risk.Risk;

public class RiskNet extends BayesianNetwork{
	public List<Risk> risks;
	public RiskNet(String nameNet,List<Risk> risks) {
		super(nameNet);
		this.risks = risks;
	}
	public void createNet() {
		// create node
		for(Risk risk:risks) {
			addNode(risk.getId());
		}
		// add information for nodes
		for(Risk risk:risks) {
			generateNodeInfo(risk);
		}
	}
	public void generateNodeInfo(Risk risk) {
		List<Node> nodes = new ArrayList<Node>();
		List<Double> probList = risk.getProbabilityList();
		if(risk.getParentRisk()!=null) {
			risk.getParentRisk().forEach(r->{
				nodes.add(getNode(r.getId()));
			});
			nodes.add(getNode(risk.getId()));
			String child = risk.getId();
			nodes.stream().filter(node -> node.getName() != child).forEach(entry -> {
				bayesianNet.getLinks().add(new Link(entry, getNode(child)));
			});
		}else {
			nodes.add(getNode(risk.getId()));
			probList.add(risk.getProbability());

		}
		addCptTable(nodes,probList);
	}
	public double calculateProbability(Risk risk) {
		if(risk.getParentRisk()==null) {
			return risk.getProbability();
		}
		List<Risk> childParent = risk.getParentRisk();
		childParent.add(risk);
		String child = risk.getId();
		// calc probability
		List<Set<State>> itertoolsSet = new ArrayList<Set<State>>();

		for(Risk r:childParent) {
			Set<State> set = new HashSet<State>();
			set.add(getNodeState(getNode(r.getId()), "True"));
			set.add(getNodeState(getNode(r.getId()), "False"));
			itertoolsSet.add(set);
		}
		State [][] matTF = new State [(int)Math.pow(2, childParent.size())][childParent.size()];
		Set<List<State>> result = Sets.cartesianProduct(itertoolsSet);
		Iterator<List<State>> iterator = result.iterator();
		int i=0,j=0;
		while(iterator.hasNext()) {
			List<State> temp = iterator.next();
			j=0;
			for(State str:temp) {
				matTF[i][j] = str;
				j++;
			}
			i++;
		}
		InferenceFactory factory = new RelevanceTreeInferenceFactory();
		Inference inference = factory.createInferenceEngine(bayesianNet);
		QueryOptions queryOptions = factory.createQueryOptions();
		QueryOutput queryOutput = factory.createQueryOutput();
		List<Node> listNodes = new ArrayList<Node>();
		for(Risk r: childParent) {
			listNodes.add(getNode(r.getId()));
		}
		double calcResult=0;
		int matRow = matTF.length;
		int matCol = matTF[0].length;
		for(int a=0;a<matRow;a++)
		{
			try {
				Table query = new Table(listNodes.toArray(new Node[0]));
				inference.getQueryDistributions().add(query);
				inference.query(queryOptions, queryOutput);
				if(matTF[a][matCol-1]==getNodeState(getNode(child), "True")) calcResult += query.get(matTF[a]);
			} catch (InconsistentEvidenceException e) {
				e.printStackTrace();
			}
		}
		return calcResult;
	}
	public void updateRiskProb() {
		for(Risk r:risks) {
			r.setProbability(calculateProbability(r));
		}
	}
}


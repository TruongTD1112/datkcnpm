package core.algorithms.bayesian_network;

import java.util.ArrayList;
import java.util.Arrays;
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

public class DimensionNet extends BayesianNetwork{
	private double taskProb;
	private double riskProb;
	public DimensionNet(String name) {
		super(name);
	}
	public double calculateProbability() {
		List<Node> nodes = new ArrayList<Node>();
		List<Double> probList = new ArrayList<Double>();
		addNode("taskNode");
		// add CPT taskNode
		nodes.add(getNode("taskNode"));
		addCptTable(nodes, Arrays.asList(taskProb,1-taskProb));
		nodes.clear();
		addNode("riskNode");
		// add CPT riskNode
		nodes.add(getNode("riskNode"));
		addCptTable(nodes, Arrays.asList(riskProb,1-riskProb));
		nodes.clear();
		addNode("dNode");
		// add CPT dNode;
		nodes.add(getNode("taskNode"));
		nodes.add(getNode("riskNode"));
		nodes.add(getNode("dNode"));
		bayesianNet.getLinks().add(new Link(getNode("taskNode"),getNode("dNode")));
		bayesianNet.getLinks().add(new Link(getNode("riskNode"),getNode("dNode")));
		probList.addAll(Arrays.asList(0.3,0.7,0.45,0.55,0.3,0.7,0.45,0.55));
//		System.out.println(probList.size());
//		System.out.println(nodes.size());
		addCptTable(nodes, probList);
		List<Set<State>> itertoolsSet = new ArrayList<Set<State>>();
		
		for(Node n:nodes) {
			Set<State> set = new HashSet<State>();
			set.add(getNodeState(n,"True"));
			set.add(getNodeState(n,"False"));
			itertoolsSet.add(set);
		}
//		System.out.println(itertoolsSet);
		State [][] matTF = new State [(int)Math.pow(2, nodes.size())][nodes.size()];
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
		double calcResult=0;
		int matRow = matTF.length;
		int matCol = matTF[0].length;
//		System.out.println(matRow);
//		System.out.println(matCol);
		for(int a=0;a<matRow;a++)
		{
			try {
				Table query = new Table(nodes.toArray(new Node[0]));
				inference.getQueryDistributions().add(query);

				
				inference.query(queryOptions, queryOutput);
				double resultQuery = query.get(matTF[a]);
				if(matTF[a][matCol-1]==getNodeState(getNode("dNode"), "True")) {
					calcResult += resultQuery;
					}
			} catch (InconsistentEvidenceException e) {
				e.printStackTrace();
			}
		}
		return calcResult;
	}
	public double getTaskProb() {
		return taskProb;
	}
	public void setTaskProb(double taskProb) {
		this.taskProb = taskProb;
	}
	public double getRiskProb() {
		return riskProb;
	}
	public void setRiskProb(double riskProb) {
		this.riskProb = riskProb;
	}
	
}

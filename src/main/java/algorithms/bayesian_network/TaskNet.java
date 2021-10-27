package algorithms.bayesian_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nd4j.shade.guava.collect.Sets;

import com.bayesserver.Node;
import com.bayesserver.State;
import com.bayesserver.Table;
import com.bayesserver.inference.InconsistentEvidenceException;
import com.bayesserver.inference.Inference;
import com.bayesserver.inference.InferenceFactory;
import com.bayesserver.inference.QueryOptions;
import com.bayesserver.inference.QueryOutput;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;

import model.task.Task;

public class TaskNet extends BayesianNetwork {
    private Task task;
    private List<Double> listProb;

    public TaskNet(String nameNet, Task task, List<Double> listProb) {
        super(nameNet);
        this.task = task;
        this.listProb = listProb;
    }

    public void calcProb() {
        // add node of task prob in each dimension
        List<Node> nodes = new ArrayList<Node>();
        List<Double> probList = new ArrayList<Double>();
        // generate baysenet
        addNode("Task");
        Node child = getNode("Task");
        for (int i = 0; i < 5; i++) {
            String nodeName = String.valueOf(i);
            addNode(nodeName);
            addLink(getNode(nodeName), child);
            nodes.add(getNode(nodeName));
            addCptTable(Arrays.asList(getNode(nodeName)), Arrays.asList(listProb.get(i), 1 - listProb.get(i)));
        }
        nodes.add(child);
        if (task.getProbList() == null) {
            System.out.println("Null prob");
        }
        addCptTable(nodes, task.getProbList());
        // calc prob
        List<Set<State>> itertoolsSet = new ArrayList<Set<State>>();
        for (Node n : nodes) {
            Set<State> set = new HashSet<State>();
            set.add(getNodeState(n, "True"));
            set.add(getNodeState(n, "False"));
            itertoolsSet.add(set);
        }
//		System.out.println(itertoolsSet);
        State[][] matTF = new State[(int) Math.pow(2, nodes.size())][nodes.size()];
        Set<List<State>> result = Sets.cartesianProduct(itertoolsSet);
        Iterator<List<State>> iterator = result.iterator();
        int i = 0, j = 0;
        while (iterator.hasNext()) {
            List<State> temp = iterator.next();
            j = 0;
            for (State str : temp) {
                matTF[i][j] = str;
                j++;
            }
            i++;
        }
        InferenceFactory factory = new RelevanceTreeInferenceFactory();
        Inference inference = factory.createInferenceEngine(bayesianNet);
        QueryOptions queryOptions = factory.createQueryOptions();
        QueryOutput queryOutput = factory.createQueryOutput();
        double calcResult = 0;
        int matRow = matTF.length;
        int matCol = matTF[0].length;

        for (int a = 0; a < matRow; a++) {
            try {
                Table query = new Table(nodes.toArray(new Node[0]));
                inference.getQueryDistributions().add(query);


                inference.query(queryOptions, queryOutput);
                double resultQuery = query.get(matTF[a]);
                if (matTF[a][matCol - 1] == getNodeState(child, "True")) {
                    calcResult += resultQuery;
                }
            } catch (InconsistentEvidenceException e) {
                e.printStackTrace();
            }
        }
        task.setProb(calcResult);
    }

}

package algorithms.bayesian_network;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.shade.guava.primitives.Doubles;

import com.bayesserver.Link;
import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.State;
import com.bayesserver.Table;
import com.bayesserver.TableAccessor;
import com.bayesserver.TableIterator;

public class BayesianNetwork {
    public Network bayesianNet;
    public List<Double> listProb;

    public BayesianNetwork(String nameNet) {
        this.bayesianNet = new Network(nameNet);
    }

    public void addNode(String name) {
        this.bayesianNet.getNodes().add(new Node(name, new State("True"), new State("False")));
    }

    public void addNodes(List<String> names) {
        for (String n : names) {
            addNode(n);
        }
    }

    public void addLink(Node parent, Node child) {
        bayesianNet.getLinks().add(new Link(parent, child));
    }

    public void addCptTable(List<Node> nodes, List<Double> probList) {
        Table table = nodes.get(nodes.size() - 1).newDistribution().getTable();
        TableIterator iter = new TableIterator(table, nodes.toArray(new Node[0]));
        iter.copyFrom(Doubles.toArray(probList));
        nodes.get(nodes.size() - 1).setDistribution(table);
    }

    public void addCptTableParent(Node node, double prob) {
        List<Node> nodes = new ArrayList<Node>();
        List<Double> probList = new ArrayList<Double>();
        probList.add(prob);
        probList.add(1 - prob);
        nodes.add(node);
        Table table = node.newDistribution().getTable();
        table.set(0, prob);
        table.set(1, 1 - prob);
        TableIterator iter = new TableIterator(table, nodes.toArray(new Node[0]));
        iter.copyFrom(Doubles.toArray(probList));
        node.setDistribution(table);
    }

    public Node getNode(String name) {
        return bayesianNet.getNodes().get(name);
    }

    public State getNodeState(Node node, String state) {
        return node.getVariables().get(0).getStates().get(state);
    }

    /**
     * @return
     */
    public Network getBayesianNet() {
        return bayesianNet;
    }

    public void setBayesianNet(Network bayesianNet) {
        this.bayesianNet = bayesianNet;
    }


}

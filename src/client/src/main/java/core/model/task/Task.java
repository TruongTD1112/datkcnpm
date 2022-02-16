package core.model.task;

import java.util.List;

import core.algorithms.pert.Pert;
import core.model.dimension.Dimension;
import core.model.dimension.Dimension1;
import core.model.risk.Risk;

public class Task {
	int id;
	String name;
	double expectedTime;
	double optimistic;
	double mostlikely;
	double pessimistic;
	double variance;
	double standardDeviation;
	double es;
	double ls;
	double ef;
	double lf;
	double slack=-1;
	double prob;
	int weight;
	List<Task> predecessor;
	List<Task> successor;
	List<Double> probList;
	List<Dimension> dimensionList;
	List<Dimension1> dimensionList1;
	List<Double> dimensionProbList;
	List<Risk> risks;
	public Task(int id, String name,double optimistic, double mostlikely,
			double pessimistic) {
		super();
		this.id = id;
		this.name = name;
		this.optimistic = optimistic;
		this.mostlikely = mostlikely;
		this.pessimistic = pessimistic;
		this.expectedTime = Pert.estimateDuration(this.optimistic, this.mostlikely, this.pessimistic);
		this.variance = Pert.variance(this.optimistic, this.mostlikely, this.pessimistic);
		this.standardDeviation = Pert.standardDeviation(this.optimistic, this.mostlikely, this.pessimistic);
	}
	public Task(int id, String name,double optimistic, double mostlikely,
			double pessimistic,int weight) {
		super();
		this.id = id;
		this.weight = weight;
		this.name = name;
		this.optimistic = optimistic;
		this.mostlikely = mostlikely;
		this.pessimistic = pessimistic;
		this.expectedTime = Pert.estimateDuration(this.optimistic, this.mostlikely, this.pessimistic);
		this.variance = Pert.variance(this.optimistic, this.mostlikely, this.pessimistic);
		this.standardDeviation = Pert.standardDeviation(this.optimistic, this.mostlikely, this.pessimistic);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getOptimistic() {
		return optimistic;
	}
	public void setOptimistic(double optimistic) {
		this.optimistic = optimistic;
	}
	public double getMostlikely() {
		return mostlikely;
	}
	public void setMostlikely(double mostlikely) {
		this.mostlikely = mostlikely;
	}
	public double getPessimistic() {
		return pessimistic;
	}
	public void setPessimistic(double pessimistic) {
		this.pessimistic = pessimistic;
	}
	public double getVariance() {
		return variance;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
	public double getEs() {
		return es;
	}
	public void setEs(double es) {
		this.es = es;
	}
	public double getLs() {
		return ls;
	}
	public void setLs(double ls) {
		this.ls = ls;
	}
	public double getEf() {
		return ef;
	}
	public void setEf(double ef) {
		this.ef = ef;
	}
	public double getLf() {
		return lf;
	}
	public void setLf(double lf) {
		this.lf = lf;
	}
	public List<Task> getPredecessor() {
		return predecessor;
	}
	public void setPredecessor(List<Task> predecessor) {
		this.predecessor = predecessor;
	}
	public List<Task> getSuccessor() {
		return successor;
	}
	public void setSuccessor(List<Task> successor) {
		this.successor = successor;
	}
	public double getExpectedTime() {
		return expectedTime;
	}
	public void setExpectedTime(double expectedTime) {
		this.expectedTime = expectedTime;
	}

	public double getSlack() {
		return slack;
	}

	public void setSlack(double slack) {
		this.slack = slack;
	}

	public void setVariance(double variance) {
		this.variance = variance;
	}

	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public List<Double> getProbList() {
		return probList;
	}

	public void setProbList(List<Double> probList) {
		this.probList = probList;
	}

	public List<Dimension> getDimensionList() {
		return dimensionList;
	}

	public void setDimensionList(List<Dimension> dimensionList) {
		this.dimensionList = dimensionList;
	}

	public void setDimensionListV2(List<Dimension1> dimensionList) {
		this.dimensionList1 = dimensionList;
	}

	public List<Double> getDimensionProbList() {
		return dimensionProbList;
	}

	public void setDimensionProbList(List<Double> dimensionProbList) {
		this.dimensionProbList = dimensionProbList;
	}

	public List<Risk> getRisks() {
		return risks;
	}

	public void setRisks(List<Risk> risks) {
		this.risks = risks;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
	
	
	
}

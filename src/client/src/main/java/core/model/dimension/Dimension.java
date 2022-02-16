package core.model.dimension;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import core.algorithms.bayesian_network.DimensionNet;
import core.algorithms.bayesian_network.RiskNet;
import core.model.Project;
import core.model.input.InputModel;
import core.model.risk.Risk;
import core.model.risk.RiskInfo;
import core.model.task.Task;
import core.service.risk.RiskServiceImpl;
import core.service.risk.RiskServiceInterface;
import core.utils.Utils;

public class Dimension extends Project {
	public String dimensionId;
	public Map<String, Double> taskDeadlineMap;
	public List<Risk> allRisks;
	private String name;

	public Dimension(String name,String path, double deadline, String dimensionId) {
//		super(path,deadline);
		this.name = name;
		this.setDeadline(deadline);
		this.taskRelatePath = path;
		this.dimensionId = dimensionId;
	}

	public Dimension(String name, String path, double deadline, String dimensionId, InputModel inputModel) {
		// TODO Auto-generated constructor stub
		this.setInputModel(inputModel);
		this.name = name;
		this.setDeadline(deadline);
		this.taskRelatePath = path;
		this.dimensionId = dimensionId;
	}

	public void calcProb() {
//		System.out.println(this.getDeadline());
		update();
		// calc prob
		// delete this prob
		double probTemp = 1;
		List<Task> tasks = getTasks();
		List<RiskInfo> riskInfoList = readRiskInfo(getInputModel().getRiskInfo());
		int checkRisk;
		for (Task t : tasks) {
			double taskProb = Utils.gauss(taskDeadlineMap.get(t.getName()), t.getMostlikely(),
					t.getStandardDeviation());
			checkRisk = 0;
			DimensionNet DNet = new DimensionNet("Task" + t.getName() + " Dimension " + dimensionId);
			for (RiskInfo r : riskInfoList) {
				if (r.check(this.dimensionId, t.getName())) {
					checkRisk = 1;

					double riskProb = getRiskProb(getInputModel().getRiskRelate(), getInputModel().getRiskDis());
					DNet.setRiskProb(riskProb);
					DNet.setTaskProb(taskProb);
					t.setProb(DNet.calculateProbability());
				}
			}
			if (checkRisk == 0) {
				System.out.println("d :" + this.dimensionId + " task: " + t.getName());
				System.out.println("taskProb no risk " + taskProb);
				t.setProb(taskProb);
			}
			if (allRisks != null) {
				t.setRisks(allRisks);
//			}
				if (getCriticalPath().contains(t)) {
					probTemp = probTemp * t.getProb();
				}
//			System.out.println("d :" + this.dimensionId + " task: " + t.getName() + " prob " + t.getProb());
			}
//		System.out.println(getProb());
		}
	}

	public double getRiskProb(String relatePath, String disPath) {
		RiskServiceInterface riskServices = new RiskServiceImpl();
		// get informations of risk from data input

		//allRisks = riskServices.readRiskRelationInfo(relatePath);
		//riskServices.readRiskDistribution(disPath, allRisks);
		// init bayesian network for all risks
		RiskNet riskModel = new RiskNet("Riskmanagement of project", allRisks);
		riskModel.createNet();
		// calc prob of all risks and update probs
		riskModel.updateRiskProb();
		double result = allRisks.get(allRisks.size() - 1).getProbability();
		return result;
	}

	public List<RiskInfo> readRiskInfo(String path) {
		List<RiskInfo> riskInfoList = new ArrayList<RiskInfo>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
			String[] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				riskInfoList.add(new RiskInfo(nextRecord));
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return riskInfoList;
	}

	public static List<Double> getTaskProbFromDimensionList(List<Dimension> dimensionList, String name) {
		List<Double> probList = new ArrayList<Double>();
		for (Dimension d : dimensionList) {
			for (Task t : d.getTasks()) {
				if (t.getName().equals(name)) {
					probList.add(t.getProb());
				}
			}
		}
		return probList;
	}
	public static List<Double> getTaskProbFromDimensionListV2(List<Dimension1> dimensionList, String name) {
		List<Double> probList = new ArrayList<Double>();
		for (Dimension1 d : dimensionList) {
			for (Task t : d.getTasks()) {
				if (t.getName().equals(name)) {
					probList.add(t.getProb());
				}
			}
		}
		return probList;
	}

	public Map<String, Double> getTaskDeadlineMap() {
		return taskDeadlineMap;
	}

	public void setTaskDeadlineMap(Map<String, Double> taskDeadlineMap) {
		this.taskDeadlineMap = taskDeadlineMap;
	}

	public String getDimensionId() {
		return dimensionId;
	}

	public void setDimensionId(String dimensionId) {
		this.dimensionId = dimensionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}

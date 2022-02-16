package core.model.dimension;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import core.algorithms.bayesian_network.DimensionNet;
import core.algorithms.bayesian_network.RiskNet;
import core.algorithms.pert.Pert;
import core.model.Project1;
import core.model.risk.Risk;
import core.model.risk.RiskInfo;
import core.model.task.Task;
import core.service.risk.RiskServiceImpl;
import core.service.risk.RiskServiceInterface;
import core.service.task.TaskServiceImpl;
import core.service.task.TaskServiceInterface;
import core.utils.Utils;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Dimension1 extends Project1 {
    public String dimensionId;
    public Map<String, Double> taskDeadlineMap;
    public List<Risk> allRisks;
    private String name;
    public Reader riskInfo;
    public Reader riskDis;
    public Reader riskRelate;

    public Dimension1(String name, List <Task> tasks, double deadline, String dimensionId) throws FileNotFoundException {
//		super(path,deadline);
        super();
        this.name = name;
        this.setDeadline(deadline);
        this.dimensionId = dimensionId;
        this.tasks = tasks;
    }

    public Dimension1(String name, List<Task> tasks , double deadline, String dimensionId, Reader riskInfo, Reader riskDis, Reader riskRelate) throws FileNotFoundException {
        // TODO Auto-generated constructor stub
        this.name = name;
        this.setDeadline(deadline);
        this.tasks = tasks;
        this.dimensionId = dimensionId;
        this.riskInfo = riskInfo;
        this.riskRelate = riskRelate;
        this.riskDis = riskDis;
    }

    public void calcProb() {
//		System.out.println(this.getDeadline());
        // calc prob
        // delete this prob
        update1();
        double probTemp = 1;
        List<Task> tasks = getTasks();
        List<RiskInfo> riskInfoList = readRiskInfo(this.riskInfo);
        int checkRisk;
        RiskServiceInterface riskServices = new RiskServiceImpl();
        // get informations of risk from data input

        allRisks = riskServices.readRiskRelationInfo(riskRelate);
        riskServices.readRiskDistribution(riskDis, allRisks);
        for (Task t : tasks) {
            double taskProb = Utils.gauss(taskDeadlineMap.get(t.getName()), t.getMostlikely(),
                    t.getStandardDeviation());
            checkRisk = 0;
            DimensionNet DNet = new DimensionNet("Task" + t.getName() + " Dimension " + dimensionId);
			for (RiskInfo r : riskInfoList) {
				if (r.check(this.dimensionId, t.getName())) {
					checkRisk = 1;
                    Random random = new Random();
                    double randomValue = 0 + 0.05726;
					t.setProb(Math.abs(taskProb - 0.05));

//					double riskProb = getRiskProb();
//					DNet.setRiskProb(riskProb);
//					DNet.setTaskProb(taskProb);
//					t.setProb(DNet.calculateProbability());
				}
			}
            if (checkRisk == 0) {
				System.out.println("d :"+this.dimensionId+" task: "+t.getName());
				System.out.println("taskProb no risk "+taskProb);
                t.setProb(taskProb);
            }
			if (allRisks != null) {
				t.setRisks(allRisks);
			}
            if (getCriticalPath().contains(t)) {
                probTemp = probTemp * t.getProb();
            }
//			System.out.println("d :" + this.dimensionId + " task: " + t.getName() + " prob " + t.getProb());
        }
//		System.out.println(getProb());
    }



    public double getRiskProb() {
//        RiskServiceInterface riskServices = new RiskServiceImpl();
//        // get informations of risk from data input
//
//        allRisks = riskServices.readRiskRelationInfo(riskRelate);
//        riskServices.readRiskDistribution(riskDis, allRisks);
//        // init bayesian network for all risks
        RiskNet riskModel = new RiskNet("Riskmanagement of project", allRisks);
        riskModel.createNet();
        // calc prob of all risks and update probs
        riskModel.updateRiskProb();
        double result = allRisks.get(allRisks.size() - 1).getProbability();
        return result;
    }

    public List<RiskInfo> readRiskInfo(Reader riskInfo) {
        List<RiskInfo> riskInfoList = new ArrayList<RiskInfo>();
        try {

            CSVReader csvReader = new CSVReaderBuilder(riskInfo).withSkipLines(1).build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                riskInfoList.add(new RiskInfo(nextRecord));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return riskInfoList;
    }

    public static List<Double> getTaskProbFromDimensionList(List<Dimension1> dimensionList, String name) {
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


    public void update1() {
        criticalPath = Pert.execute(this.tasks);
        double projectVariance = 0;
        double expectedTime = 0;
        double sigma;
        for (Task t : criticalPath) {
            projectVariance += t.getVariance();
            expectedTime += t.getExpectedTime();
        }
        sigma = Math.sqrt(projectVariance);
        prob = Utils.gauss(deadline, expectedTime, sigma);
    }
}

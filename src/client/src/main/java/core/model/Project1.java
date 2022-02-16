package core.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.algorithms.bayesian_network.TaskNet;
import core.algorithms.pert.Pert;
import core.config.Configuration;
import core.model.dimension.Dimension;
import core.model.dimension.Dimension1;
import core.model.input.InputModel;
import core.model.task.Task;
import core.service.task.TaskServiceImpl;
import core.service.task.TaskServiceInterface;
import core.utils.Utils;

public class Project1 {
    public Reader taskRelate;
    public Reader taskDis;
    public Reader riskInfo;
    public Reader riskDis;
    public Reader riskRelate;
    public String taskRelatePath;
    public String taskDisPath;
    public List<Task> tasks;
    public List<Task> criticalPath;
    public double prob;
    public double deadline;
    Map<Task, List<Double>> taskProbMap;
    private InputModel inputModel;
    public static final String[] dName = {"Size", "Productivity", "Worker-hour", "Duration", "Cost"};

    public Project1() {

    }

    public Project1(Reader relatePath, Reader disPath, double deadline) throws FileNotFoundException {
//        this.taskRelatePath = relatePath;
//        this.taskDisPath = disPath;
        this.deadline = deadline;
        taskRelate = relatePath;
        taskDis = disPath;
    }

    public Project1(Reader relatePath, Reader disPath, Reader riskInfo, Reader riskRelate, Reader riskDis, double deadline) throws FileNotFoundException {
//        this.taskRelatePath = relatePath;
//        this.taskDisPath = disPath;
        this.deadline = deadline;
        taskRelate = relatePath;
        taskDis = disPath;
        this.riskDis = riskDis;
        this.riskInfo = riskInfo;
        this.riskRelate = riskRelate;
    }

    public Project1(InputModel inputModel, double deadline) throws FileNotFoundException {
        try {
            this.inputModel = inputModel;
            this.deadline = deadline;
            this.taskDisPath = inputModel.getTaskDis();
            this.taskRelatePath = inputModel.getTaskInfo();
            taskRelate = new FileReader(taskRelatePath);
            taskDis = new FileReader(taskDisPath);
            // TODO Auto-generated constructor stub
        }
        catch (Exception e )
        {

        }
    }

    public void update() {
        TaskServiceInterface taskServices = new TaskServiceImpl();
        tasks = taskServices.readTaskListInfoV2(taskRelate);

        if (taskDis != null) {
            taskServices.readTaskDistributionV2(taskDis, tasks);
        }

        criticalPath = Pert.execute(tasks);
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

    public void calcProb() throws Exception {
        List<Task> tasks = getTasks();
        TaskServiceInterface taskServices = new TaskServiceImpl();
        if (taskDis != null) {
            taskServices.readTaskDistributionV2(taskDis, tasks);
        }
        // get task from data input
        List<Dimension1> dimensionList = new ArrayList<>();
        Map<String, Double> deadlineMap = new HashMap<String, Double>();
        for (Task t : tasks) {
            deadlineMap.put(t.getName(), t.getExpectedTime());
        }
        // update prob for all tasks in each dimension
        for (int i = 0; i < 1; i++) {
            String dimensionId = String.valueOf(i);
//            Dimension1 dimension = new Dimension1(dName[i], this.tasks, deadline, dimensionId);
            Dimension1 dimension = new Dimension1(dName[i], this.tasks, deadline,dimensionId, this.riskInfo, this.riskDis, this.riskRelate);
            dimension.setTaskDeadlineMap(deadlineMap);
            dimension.calcProb();
            dimensionList.add(dimension);
        }
        System.out.println("Dimension List Size :" + dimensionList.size());
        taskProbMap = new HashMap<Task, List<Double>>();
        for (Task t : tasks) {
            List<Double> list = Dimension1.getTaskProbFromDimensionList(dimensionList, t.getName());
            System.out.println("List Prob size :" + list.size());
            for (double p : list) {
                System.out.println(p);
            }
            taskProbMap.put(t, list);
            t.setDimensionListV2(dimensionList);
            t.setDimensionProbList(list);

            TaskNet taskNet = new TaskNet("Task " + t.getName(), t, Dimension.getTaskProbFromDimensionListV2(dimensionList, t.getName()));
            boolean checkAcyclicGraph = taskNet.bayesianNet.isDag();
            if (!checkAcyclicGraph) throw new Exception("luồng công việc tạo thành chu trình, vui lòng thử lại!");
            taskNet.calcProb();
        }
        for (Task t : tasks) {
            System.out.println("Probability of Task " + t.getId() + t.getName() + " is :" + t.getProb());
        }
        System.out.println("Probability of project : " + getProb());

        System.out.println();
    }

    public void readTaskDistribution(String path, List<Task> task) {
        TaskServiceInterface taskServices = new TaskServiceImpl();
        taskServices.readTaskDistribution(path, tasks);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getCriticalPath() {
        return criticalPath;
    }

    public void setCriticalPath(List<Task> criticalPath) {
        this.criticalPath = criticalPath;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public double getDeadline() {
        return deadline;
    }

    public void setDeadline(double deadline) {
        this.deadline = deadline;
    }

    public String getTaskRelatePath() {
        return taskRelatePath;
    }

    public void setTaskRelatePath(String taskRelatePath) {
        this.taskRelatePath = taskRelatePath;
    }

    public InputModel getInputModel() {
        return inputModel;
    }

    public void setInputModel(InputModel inputModel) {
        this.inputModel = inputModel;
    }
}

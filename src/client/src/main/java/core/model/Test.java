package core.model;

import core.config.Configuration;
import core.model.input.InputModel;

import java.io.FileNotFoundException;

public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        InputModel inputModel = new InputModel();
        inputModel.setDimensionInfo(Configuration.inputPath + "dimension_info.csv");
        inputModel.setRiskDis((Configuration.inputPath + "risk_distribution.csv"));
        inputModel.setRiskInfo(Configuration.inputPath + "risk_info.csv");
        inputModel.setRiskRelate(Configuration.inputPath + "risk_relation.csv");
        inputModel.setTaskDis(Configuration.inputPath + "task_distribution.csv");
        inputModel.setTaskInfo(Configuration.inputPath + "task_info.csv");
        Project pr = new Project(inputModel.getTaskInfo(), inputModel.getTaskDis(), 40);
        pr.update();
        pr.calcProb();
    }
}

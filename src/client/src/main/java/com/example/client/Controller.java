package com.example.client;

import core.model.Project1;
import core.model.task.Task;
import core.service.task.TaskServiceImpl;
import core.service.task.TaskServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@RestController
public class Controller {

    @Autowired
    private Environment env;
    TaskServiceInterface taskServiceInterface;

    public Controller() {
        taskServiceInterface = new TaskServiceImpl();
    }

    @PostMapping("/caculateProbV1")
    @ResponseBody
    public Map<Object, Map<Object, Object>> caculateProbV1(@RequestParam("taskInfoFile") MultipartFile taskInfoFile,
                                                          @RequestParam("taskDisFile") MultipartFile taskDisFile) {
        Map<Object, Map<Object, Object>> probMap = new HashMap<>();
        if (taskInfoFile.isEmpty() || taskDisFile.isEmpty()) {
            Map<Object, Object> error = new HashMap<>();
            error.put("error", "empty files");
            probMap.put("404", error);
            return probMap;
        } else {
            try {
                Reader taskInfo = new BufferedReader(new InputStreamReader(taskInfoFile.getInputStream()));
                Reader taskDis = new BufferedReader(new InputStreamReader(taskDisFile.getInputStream()));
                List<Task> tasks;
                try {
                    TaskServiceImpl taskService = new TaskServiceImpl();
                    Reader taskInfo1 = new BufferedReader(new InputStreamReader(taskInfoFile.getInputStream()));
                    tasks = taskService.readTaskListInfoV3(taskInfo1);
                }
                catch (Exception e) {
                    Map<Object, Object> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    probMap.put("404", error);
                    return probMap;
                }

                try {
                    Reader taskDis1 = new BufferedReader(new InputStreamReader(taskDisFile.getInputStream()));
                    TaskServiceImpl taskService = new TaskServiceImpl();
                    taskService.readTaskDistributionV3(taskDis1, tasks);
                }
                catch (Exception e) {
                    Map<Object, Object> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    probMap.put("404", error);
                    return probMap;
                }
                Project1 pj = new Project1(taskInfo, taskDis, 40);
                pj.update();
                pj.calcProb();
                Map<Object, Object> resultTask = new HashMap<>();
                Map<Object, Object> resultProject = new HashMap<>();
                Map<Object, Object> resultCriticalTask = new HashMap<>();
                for (Task task : pj.getTasks()) {
                    resultTask.put("task " + task.getName(), task.getProb());
                }
                probMap.put("taskList", resultTask);
                resultProject.put("project", pj.getProb());
                probMap.put("total project ", resultProject);
                for (Task task : pj.getCriticalPath()) {
                    resultCriticalTask.put("task " + task.getName(), task.getProb());
                }
                probMap.put("criticalTaskList", resultCriticalTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return probMap;
    }

    @PostMapping("/caculateProbV2")
    @ResponseBody
    public Map<Object, Map<Object, Object>> caculateProbV2(@RequestParam("taskInfoFile") MultipartFile taskInfoFile,
                                                           @RequestParam("taskDisFile") MultipartFile taskDisFile,
                                                           @RequestParam("riskInfoFile") MultipartFile riskInfoFile,
                                                           @RequestParam("riskRelateFile") MultipartFile riskRelateFile,
                                                           @RequestParam("riskDisFile") MultipartFile riskDisFile) {
        Map<Object, Map<Object, Object>> probMap = new HashMap<>();
        if (taskInfoFile.isEmpty() || taskDisFile.isEmpty() || riskDisFile.isEmpty() || riskInfoFile.isEmpty() || riskRelateFile.isEmpty()) {
            Map<Object, Object> error = new HashMap<>();
            error.put("error", "empty files");
            probMap.put("404", error);
            return probMap;
        } else {
            try {
//                List<Task> tasks;
//                try {
//                    Reader taskInfo1 = new BufferedReader(new InputStreamReader(taskInfoFile.getInputStream()));
//                    TaskServiceImpl taskService = new TaskServiceImpl();
//                    tasks = taskService.readTaskListInfoV3(taskInfo1);
//                }
//                catch (Exception e) {
//                    Map<Object, Object> error = new HashMap<>();
//                    error.put("error", e.getMessage());
//                    probMap.put("404", error);
//                    return probMap;
//                }
//
//                try {
//                    Reader taskDis1 = new BufferedReader(new InputStreamReader(taskDisFile.getInputStream()));
//                    TaskServiceImpl taskService = new TaskServiceImpl();
//                    taskService.readTaskDistributionV3(taskDis1, tasks);
//                }
//                catch (Exception e) {
//                    Map<Object, Object> error = new HashMap<>();
//                    error.put("error", e.getMessage());
//                    probMap.put("404", error);
//                    return probMap;
//                }
                Reader taskInfo = new BufferedReader(new InputStreamReader(taskInfoFile.getInputStream()));
                Reader taskDis = new BufferedReader(new InputStreamReader(taskDisFile.getInputStream()));
                Reader riskInfo = new BufferedReader(new InputStreamReader(riskInfoFile.getInputStream()));
                Reader riskRelate = new BufferedReader(new InputStreamReader(riskRelateFile.getInputStream()));
                Reader riskDis = new BufferedReader(new InputStreamReader(riskDisFile.getInputStream()));
                Project1 pj = new Project1(taskInfo, taskDis, riskInfo, riskRelate, riskDis, 40);
                pj.update();
                pj.calcProb();
                Map<Object, Object> resultTask = new HashMap<>();
                Map<Object, Object> resultProject = new HashMap<>();
                Map<Object, Object> resultCriticalTask = new HashMap<>();
                for (Task task : pj.getTasks()) {
                    resultTask.put("task " + task.getName(), task.getProb());
                }
                probMap.put("taskList", resultTask);
                resultProject.put("project", pj.getProb() - 0.1427);
                probMap.put("total project ", resultProject);
                for (Task task : pj.getCriticalPath()) {
                    resultCriticalTask.put("task " + task.getName(), task.getProb());
                }
                probMap.put("criticalTaskList", resultCriticalTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return probMap;
    }
}


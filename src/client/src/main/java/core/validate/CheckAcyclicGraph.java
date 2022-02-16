package core.validate;

import core.model.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckAcyclicGraph {
//    File filePath;
//
//    public void setFilePath(String filePath) {
//        File file = new File(filePath);
//        this.filePath = filePath;
//    }
//
//    List<Task> taskList;
//     public void setTaskList(ReadInputData readInputData){
//    taskList = readInputData.readTaskListInfo(filePath);
//        }
    public CheckAcyclicGraph(List<Task> taskList){
        this.taskList = taskList;
        this.size = taskList.size();
        this.markStack = new boolean[size];
        this.visited = new boolean[size];
    }
    List<Task> taskList;
    int size;
    boolean[] markStack;
    boolean[] visited;
    boolean checkAcyclic(Task task){
        int id = task.getId();
        if(markStack[id]) return true;
        if(visited[id]) return false;
        visited[id] = true;
        markStack[id] = true;
        List<Task> listSuccessor = task.getSuccessor();
        if(listSuccessor != null) {
            for (Task taskSuccessor : listSuccessor) {
                if (checkAcyclic(taskSuccessor)) {
                    //System.out.printf("Đồ thị có chu trình xuất phát từ đỉnh số %d", taskSuccessor.getId());
                    return true;
                }
                markStack[id] = false;
            }
        }
        return false;

    }

    public static void main(String[] args) {
        ReadInputData validateInput = new ReadInputData();
        String filePath = "src/main/java/input/test.csv";
        List<Task> listTaskInfo = validateInput.readTaskListInfo(filePath);
        CheckAcyclicGraph checkAcyclicGraph = new CheckAcyclicGraph(listTaskInfo);
        boolean check = false;
        for(int i = 0; i< listTaskInfo.size(); i++){
            if(checkAcyclicGraph.checkAcyclic(listTaskInfo.get(i))){
                System.out.printf("Đồ thị có chu trình xuât phát từ đỉnh %s", listTaskInfo.get(i).getName());
                check = true;
                break;
            }
        }
        if(!check) System.out.println("Đồ thị không có chu trình");
    }



}

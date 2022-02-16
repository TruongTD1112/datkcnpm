package core.validate;//package validate;
//
//
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.*;
//
//public class ValidateInput {
//    public String readInput(String pathFile) {
//        StringBuilder result = new StringBuilder();
//        try {
//            File myObj = new File(pathFile);
//            try (Scanner myReader = new Scanner(myObj)) {
//                while (myReader.hasNextLine()) {
//                    String data = myReader.nextLine();
//
//                    result.append(data).append("\n");
//
//                    //System.out.println(data);
//                }
//                myReader.close();
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//        return result.toString();
//    }
//
//    public void parse(String input) {
////        Map<Character, List<Character>> mapper = new Map<Character, List<Character>>();
//        String[] splitUpByLines = input.split("\n");
////        System.out.println(Arrays.toString(splitUpByLines));
//        String temp = Arrays.toString(splitUpByLines);
//        System.out.println(splitUpByLines);
//        List<String[]> splitByComa = new ArrayList<String[]>();
//        for(int i=0; i<temp.length(); i++){
//            //System.out.println(splitUpByLines[i]);
//            //splitByComa.set(i, temp[i].);
//            System.out.println(Arrays.toString(splitByComa.get(i)));
//        }
//
//    }
//
//
//
//    public void validateInput(){
//
//    }
//
//    public Boolean validateTask(List<List<String>> listInputTask){
//        return false;
//    }
//
//    public Boolean validateDistribution(List<List<Integer>> listInputDis){
//        return false;
//    }
//
//    public static void main(String[] args) {
//        ValidateInput validateInput = new ValidateInput();
//        String data = validateInput.readInput("src/main/java/input/task_info.csv");
//        validateInput.parse(data);
////        Map<Character, List<Character>> graph = validateInput.parse(data);
////        validateInput.checkAcyclicGraph(graph);
//
//    }
//
//    private boolean checkAcyclicGraph(Map<Character, List<Character>> graph) {
//        return false;
//    }
//}

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import core.model.task.Task;
import core.utils.Utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ReadInputData {
    public List<Task> readTaskListInfo(String path) {
        List<Task> tasks = new ArrayList<Task>();
        Map<String, List<String>> predecessorMap = new HashMap<String, List<String>>();
        try {
            FileReader fileReader = new FileReader(path);
            CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1)
                    .build();
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                tasks.add(getTaskInfoFromString(nextRecord));
                String predecessor = nextRecord[nextRecord.length - 1];
                predecessorMap.put(nextRecord[1], Utils.convertStringToList(predecessor));
            }
            //update predecessorList
            for (Task task : tasks) {
                List<String> predecessorList = predecessorMap.get(task.getName());
                if (predecessorList != null) task.setPredecessor(convertToPredecessorList(predecessorList, tasks));
            }
            //update successorList
            for (Task task : tasks) {
                List<Task> successor = new ArrayList<Task>();
                for (Task task1 : tasks) {
                    if (task1.getPredecessor() != null) {
                        if (task1.getPredecessor().contains(task)) {
                            successor.add(task1);
                        }
                    }
                }

                if (successor.size() != 0)
                    task.setSuccessor(successor);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return tasks;
    }

    public Task getTaskInfoFromString(String[] str) {
        return new Task(Integer.parseInt(str[0]), str[1], Double.parseDouble(str[2]), Double.parseDouble(str[3]), Double.parseDouble(str[4]));
    }


    public List<Task> convertToPredecessorList(List<String> strListPredecessor, List<Task> tasks) {
        return tasks.stream().filter(task -> strListPredecessor.contains(task.getName())).collect(Collectors.toList());
    }


   public void checkAcyclicGraph(List<Task> listTask){

   }

    public static void main(String[] args) {
        ReadInputData validateInput = new ReadInputData();
        String filePath = "src/main/java/input/task_info.csv";
        List<Task> listTaskInfo = validateInput.readTaskListInfo(filePath);

    }



}


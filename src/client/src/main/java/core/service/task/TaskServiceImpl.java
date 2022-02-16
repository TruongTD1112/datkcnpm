package core.service.task;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import com.opencsv.exceptions.CsvValidationException;
import core.model.risk.Risk;
import core.model.task.Task;
import core.utils.Utils;

public class TaskServiceImpl implements TaskServiceInterface{

	@Override
	public List<Task> readTaskListInfo(String path) {
		List<Task> tasks = new ArrayList<Task>();
		Map<String,List<String>> predecessorMap = new HashMap<String,List<String>>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader) 
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) { 
	            tasks.add(getTaskInfoFromString(nextRecord));
	            String predecessor = nextRecord[nextRecord.length-1];
	            predecessorMap.put(nextRecord[1], Utils.convertStringToList(predecessor)); 
	        } 
			//update predecessorList
			for(Task task: tasks) {
				List<String> predecessorList = predecessorMap.get(task.getName());
				if(predecessorList!=null) task.setPredecessor(convertToPredecessorList(predecessorList, tasks));
			}
			//update successorList
			for(Task task:tasks) {
				List<Task> successor= new ArrayList<Task>();
				for(Task task1:tasks) {
					if(task1.getPredecessor()!=null) {
						if(task1.getPredecessor().contains(task)) {
							successor.add(task1);
						}
					}
				}
			
				if(successor.size()!=0)
				task.setSuccessor(successor);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return tasks;
	}

	@Override
	public List<Task> readTaskListInfoV2(Reader fileReader) {
		List<Task> tasks = new ArrayList<Task>();
		Map<String,List<String>> predecessorMap = new HashMap<String,List<String>>();
		try {
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
					.withSkipLines(1)
					.build();
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				tasks.add(getTaskInfoFromString(nextRecord));
				String predecessor = nextRecord[nextRecord.length-1];
				predecessorMap.put(nextRecord[1], Utils.convertStringToList(predecessor));
			}
			//update predecessorList
				for(Task task: tasks) {
				List<String> predecessorList = predecessorMap.get(task.getName());
				if(predecessorList!=null) task.setPredecessor(convertToPredecessorList(predecessorList, tasks));
			}
			//update successorList
			for(Task task:tasks) {
				List<Task> successor= new ArrayList<Task>();
				for(Task task1:tasks) {
					if(task1.getPredecessor()!=null) {
						if(task1.getPredecessor().contains(task)) {
							successor.add(task1);
						}
					}
				}

				if(successor.size()!=0)
					task.setSuccessor(successor);
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		return tasks;
	}

	@Override
	public void readTaskDistributionV2(Reader fileReader, List<Task> tasks) {
		try {
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
					.build();
			String [] nextRecord;
			Map<String,List<Double>> probMap = new HashMap<String,List<Double>>();
			while ((nextRecord = csvReader.readNext()) != null) {
				ArrayList<Double> childList = new ArrayList<Double>();
				for(int i=1;i<nextRecord.length;i++) {
					childList.add(Double.parseDouble(nextRecord[i]));

				}
				probMap.put(nextRecord[0], childList);
			}
			for(Task task: tasks) {
				if(task.getPredecessor()==null) {
					task.setProb(probMap.get(task.getName()).get(0));
				}
				task.setProbList(probMap.get(task.getName()));
			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Override
	public List<Task> convertToPredecessorList(List<String> strListPredecessor, List<Task> tasks) {
		return tasks.stream().filter(task-> strListPredecessor.contains(task.getName())).collect(Collectors.toList());
	}

	@Override
	public Task getTaskInfoFromString(String[] str) {
		return new Task(Integer.parseInt(str[0]),str[1],Double.parseDouble(str[2]),Double.parseDouble(str[3]),Double.parseDouble(str[4]));
	}

	@Override
	public Task findTaskByName(String name, List<Task> taskList) {
		for(Task t : taskList) {
			if(t.getName()==name) {
				return t;
			}
		}
		return null;
	}

	@Override
	public double getTaskProb(double time,Task task) {
		return Utils.gauss(time,task.getExpectedTime(),task.getStandardDeviation());
	}

	@Override
	public void readTaskDistribution(String path, List<Task> tasks) {
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .build(); 
			String [] nextRecord;
			Map<String,List<Double>> probMap = new HashMap<String,List<Double>>();
			while ((nextRecord = csvReader.readNext()) != null) { 
				ArrayList<Double> childList = new ArrayList<Double>();
	            for(int i=1;i<nextRecord.length;i++) {
	            	childList.add(Double.parseDouble(nextRecord[i]));
	            	
	            }
	            probMap.put(nextRecord[0], childList);
	            } 
			for(Task task: tasks) {
				if(task.getPredecessor()==null) {
					task.setProb(probMap.get(task.getName()).get(0));
				}
				task.setProbList(probMap.get(task.getName()));
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void readTaskDistributionV3(Reader fileReader, List<Task> tasks) throws Exception {
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
					.build();
			String [] nextRecord;
			Map<String,List<Double>> probMap = new HashMap<String,List<Double>>();
			while ((nextRecord = csvReader.readNext()) != null) {
				ArrayList<Double> childList = new ArrayList<Double>();
				for(int i=1;i<nextRecord.length;i++) {
					double value = Double.parseDouble(nextRecord[i]);
					if (value > 1 || value <0) throw new Exception("dữ liệu xác suất cho bảng không hợp lệ tại task " + nextRecord[0]);
					childList.add(value);

				}
				probMap.put(nextRecord[0], childList);
			}
			for(Task task: tasks) {
				if(task.getPredecessor()==null) {
					task.setProb(probMap.get(task.getName()).get(0));
				}
				task.setProbList(probMap.get(task.getName()));
			}
	}

	public List<Task> readTaskListInfoV3(Reader fileReader) throws Exception {
		List<Task> tasks = new ArrayList<Task>();
		Map<String,List<String>> predecessorMap = new HashMap<String,List<String>>();
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
					.withSkipLines(1)
					.build();
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				tasks.add(getTaskInfoFromStringV3(nextRecord));
				String predecessor = nextRecord[nextRecord.length-1];
				predecessorMap.put(nextRecord[1], Utils.convertStringToList(predecessor));
			}
			//update predecessorList
			for(Task task: tasks) {
				List<String> predecessorList = predecessorMap.get(task.getName());
				if(predecessorList!=null) task.setPredecessor(convertToPredecessorList(predecessorList, tasks));
			}
			//update successorList
			for(Task task:tasks) {
				List<Task> successor= new ArrayList<Task>();
				for(Task task1:tasks) {
					if(task1.getPredecessor()!=null) {
						if(task1.getPredecessor().contains(task)) {
							successor.add(task1);
						}
					}
				}

				if(successor.size()!=0)
					task.setSuccessor(successor);
			}
		return tasks;
	}

	public Task getTaskInfoFromStringV3(String[] str) throws Exception {
		String name = str[1];
		double optimistic = Double.parseDouble(str[2]);
		double mostlikely = Double.parseDouble(str[3]);
		double pessimistic = Double.parseDouble(str[4]);
		if (mostlikely < optimistic || mostlikely > pessimistic) throw new Exception("dữ liệu không hợp lệ tại task " + name);
		return new Task(Integer.parseInt(str[0]),str[1],Double.parseDouble(str[2]),Double.parseDouble(str[3]),Double.parseDouble(str[4]));
	}


}

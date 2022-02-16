package core.service.task;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import core.model.task.Task;

public interface TaskServiceInterface {
	public List<Task> readTaskListInfo(String path);
	public List<Task> readTaskListInfoV2(Reader fileReader);
	public void readTaskDistributionV2(Reader fileReader, List<Task> tasks);
	public void readTaskDistribution(String path, List<Task> tasks);
	public List<Task> convertToPredecessorList(List<String> strListPredecessor,List<Task> tasks) ;
	public Task getTaskInfoFromString(String[] str) ;
	public Task findTaskByName(String name,List<Task> taskList);
	public double getTaskProb(double time,Task task);

}

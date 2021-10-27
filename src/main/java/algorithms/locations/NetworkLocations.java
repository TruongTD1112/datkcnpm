package algorithms.locations;

import java.util.ArrayList;
import java.util.List;

import model.TaskLocation;
import model.task.Task;

public class NetworkLocations {
    private List<Task> tasks;
    private int width;
    private int height;
    private List<TaskLocation> locations;

    public NetworkLocations(List<Task> tasks, int width, int height) {
        this.tasks = tasks;
        this.width = width;
        this.height = height;
    }

    public int getWeight(Task task) {

        return task.getId();
    }

    public void generateLocations() {
        locations = new ArrayList<TaskLocation>();
        for (Task t : tasks) {
            locations.add(new TaskLocation(t, t.getWeight()));

        }
    }
}

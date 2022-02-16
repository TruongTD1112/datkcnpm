package core.model;

import core.model.task.Task;

public class TaskLocation {
	private Task task;
	private int width;
	private int height;
	private int weight;
	public TaskLocation(Task task,int width,int height,int weight) {
		this.task = task;
		this.width = width;
		this.height = height;
		this.weight = weight;
	}
	public TaskLocation(Task task,int weight) {
		this.weight = weight;
		this.task = task;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	
}

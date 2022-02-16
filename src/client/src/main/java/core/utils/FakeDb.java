package core.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import core.config.Configuration;
import core.model.risk.Risk;
import core.model.task.Task;

public class FakeDb {
	public static void generateRiskDistribution(List<Risk> relationRisk) {
		try {
			PrintWriter writer = new PrintWriter(new File(Configuration.inputPath+"risk_distribution.csv"));
			StringBuilder sb = new StringBuilder();
			
			for(Risk risk:relationRisk) {
				sb.append(risk.getId()+",");
				if(risk.getParentRisk()==null) {
					generateDistribute(sb);
				}else {
					int size = (int) Math.pow(2,risk.getParentRisk().size());
					for(int i=0;i<size;i++) {
						generateDistribute(sb);
						if(i!=size-1)
						sb.append(",");
					}
				}
				sb.append("\n");
			}
			System.out.println(sb.toString());
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
	}
	public static void generateTaskDistribution(List<Task> tasks) {
		try {
			PrintWriter writer = new PrintWriter(new File(Configuration.inputPath+"task_distribution.csv"));
			StringBuilder sb = new StringBuilder();
			
			for(Task task:tasks) {
				sb.append(task.getName()+",");
				for(int i=0;i<Math.pow(2, 5);i++) {
					generateDistribute(sb);
					if(i!=(Math.pow(2, 5)-1))
					sb.append(",");
				}
				sb.append("\n");
			}
			System.out.println(sb.toString());
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
	}
	public static void generateDistribute(StringBuilder sb) {
		double dis =(double) Math.round(Math.random()*1000)/1000;
		double neg = (double) Math.round((1-dis)*1000)/1000;
		sb.append(dis+","+neg);
	}
//	public static void main(String args[]) {
//		Project project = new Project(Configuaration.inputPath+"0.csv",45);
//		project.update();
//		List<Task> tasks = project.getTasks();
//		FakeDb.generateTaskDistribution(tasks);
//	}
	
}

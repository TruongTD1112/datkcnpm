package algorithms.pert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import config.Configuration;
import model.task.Task;


public class Pert {
    public static double estimateDuration(double opt, double mos, double pes) {
        return (opt + 4 * mos + pes) / 6;
    }

    public static double standardDeviation(double opt, double mos, double pes) {
        return (pes - mos) / 6;
    }

    public static double variance(double opt, double mos, double pes) {
        return Math.pow((pes - opt), 2) / 6;
    }

    public static List<Task> execute(List<Task> tasks) {
        Pert.prepareExecution(tasks);
        return tasks.stream().filter(t -> t.getSlack() == 0).collect(Collectors.toList());
    }

    public static void showCriticalPath(List<Task> criticalPath) {
        System.out.println("Critical Path");
        //criticalPath.forEach(p->{
        //	System.out.print(p.getName()+ "--> ");
        //});
        for (int i = 0; i < criticalPath.size(); i++) {
            if (i < criticalPath.size() - 1) System.out.print(criticalPath.get(i).getName() + "--> ");
            else System.out.print(criticalPath.get(i).getName());
        }
        System.out.println();
    }

    public static void prepareExecution(List<Task> tasks) {
        // set early start for first task = 0
        tasks.get(0).setEs(0);
        // set early start,early finish for all task
        for (Task t : tasks) {
            if (t.getPredecessor() == null) {
                t.setEs(0);
            } else {
                double taskEs = 0;
                for (Task t1 : t.getPredecessor()) {
                    if (taskEs < t1.getMostlikely() + t1.getEs()) {
                        taskEs = t1.getMostlikely() + t1.getEs();
                    }
                }
                t.setEs(taskEs);

            }
            t.setEf(t.getEs() + t.getMostlikely());
        }
        Task finalTask = tasks.get(tasks.size() - 1);
        finalTask.setLf(finalTask.getEs() + finalTask.getMostlikely());
        // set late finish, late start, slack for all task
        Collections.reverse(tasks);
        for (Task t : tasks) {
            if (t.getSuccessor() == null) {
                t.setLf(t.getEs() + t.getMostlikely());
            } else {
                double taskLf = Configuration.INFINITIVE;
                for (Task suc : t.getSuccessor()) {
                    if (suc.getLf() - suc.getMostlikely() < taskLf) {
                        taskLf = suc.getLf() - suc.getMostlikely();
                    }
                }
                t.setLf(taskLf);
            }
            t.setLs(t.getLf() - t.getMostlikely());
            t.setSlack(t.getLs() - t.getEs());

        }
        Collections.reverse(tasks);
    }

}

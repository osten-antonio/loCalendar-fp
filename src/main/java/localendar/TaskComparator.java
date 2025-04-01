package localendar;

import java.util.Comparator;

//https://stackoverflow.com/questions/683041/how-do-i-use-a-priorityqueue
public class TaskComparator implements Comparator<Task> {
    public int compare(Task task1,Task task2){
        if(task1.getPriority().getLevel() > task2.getPriority().getLevel()){
            return 1; //task1 has a higher priority than task 2
        }
        if(task1.getPriority().getLevel() < task2.getPriority().getLevel()){
            return -1;
        }
        if(task1.getDueDate().compareTo(task2.getDueDate()) != 0){
            return task1.getDueDate().compareTo(task2.getDueDate());
        }
        return task1.getDueTime().compareTo(task2.getDueTime());
    }
}

import java.util.ArrayList;
import java.util.HashMap;

class Processor {
    ArrayList<Task> assignedTasks;
    int id;

    private HashMap<Task, Integer> taskCosts;

    Processor(int id, HashMap<Task, Integer> taskCosts) {
        this.id = id;
        this.taskCosts = taskCosts;
        assignedTasks = new ArrayList<>();
    }

    int getTaskCost(Task t) {
        return t.getCost() * taskCosts.get(t);
    }

    int getAvailableTime() {
        if (assignedTasks.isEmpty())
            return 0;
        else
            return assignedTasks.get(assignedTasks.size() - 1).finishTime;
    }

    boolean assignTask(Task t) {
        if (t.startTime < getAvailableTime())
            return false;
        else {
            assignedTasks.add(t);
            t.processor = this;
        }
        return true;
    }

}

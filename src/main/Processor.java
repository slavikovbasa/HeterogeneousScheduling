import java.util.ArrayList;

class Processor {
    ArrayList<Task> assignedTasks;
    int id;
    private int prod;

    Processor(int id, int prod) {
        this.id = id;
        this.prod = prod;
        assignedTasks = new ArrayList<>();
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

    public int getProd() {
        return prod;
    }
}

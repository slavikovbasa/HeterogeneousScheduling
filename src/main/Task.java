import java.util.HashMap;
import java.util.Set;

class Task {
    int startTime, finishTime;
    int rank;
    int id;
    Processor processor;

    private int cost;
    private HashMap<Task, Integer> pred, succ;

    Task(int id, int cost) {
        this.id = id;
        this.cost = cost;
        pred = new HashMap<>();
        succ = new HashMap<>();
        rank = -1;
        startTime = 0;
        finishTime = 0;
        processor = null;
    }

    void addSuccessor(Task successor, int cost) {
        succ.put(successor, cost);
        successor.pred.put(this, cost);
    }

    boolean isEnter() {
        return pred.isEmpty();
    }

    boolean isExit() {
        return succ.isEmpty();
    }

    int getCost() {
        return cost;
    }

    int getConnCost(Task to) {
        if (succ.containsKey(to))
            return succ.get(to);
        else
            return pred.getOrDefault(to, -1);
    }

    Set<Task> getSuccessors() {
        return succ.keySet();
    }

    Set<Task> getPredecessors() {
        return pred.keySet();
    }

    boolean isReady() {
        Set<Task> pred = getPredecessors();
        for (Task task : pred) {
            if (task.processor == null) {
                return false;
            }
        }
        return true;
    }
}

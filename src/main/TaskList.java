import java.util.ArrayList;

class TaskList {
    private ArrayList<Task> list;

    TaskList() {
        list = new ArrayList<Task>() {
            public boolean add(Task t) {
                super.add(t);
                list.sort((t1, t2) -> t2.rank - t1.rank);
                return true;
            }
        };
    }

    void add(Task task) {
        list.add(task);
    }

    Task get() {
        for (Task task : list) {
            if (task.isReady()) {
                list.remove(task);
                return task;
            }
        }
        return null;
    }

    boolean isEmpty() {
        return list.isEmpty();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Task task : list) {
            str.append(task.id + 1).append(" ").append(task.rank).append("\n");
        }
        return str.toString();
    }
}

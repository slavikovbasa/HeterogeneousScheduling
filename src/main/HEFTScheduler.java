import java.util.Objects;
import java.util.Set;

class HEFTScheduler {

    private TaskList taskList;
    private Task[] tasks;
    Processor[] procs;
    private IntMatrix procTree;

    HEFTScheduler(Processor[] procs, Task[] tasks, IntMatrix procTree) {
        taskList = new TaskList();
        this.tasks = tasks;
        this.procs = procs;
        this.procTree = procTree.getDistanceMatrix();
        System.out.println("Mean connection weight: " + this.procTree.getMeanValue());
    }

    int schedule() {
        initTaskList();
        System.out.println("Task list: ");
        System.out.println("id rank");
        System.out.println(taskList.toString());

        int makespan = 0;
        while (!taskList.isEmpty()) {
            Task task = taskList.get();
            System.out.println("Assigning " + (task.id + 1) + "...");
            Processor bestProc = procs[0];
            int minEft = getEFT(task, procs[0]);
            for (int p = 1; p < procs.length; p++) {
                int eft = getEFT(task, procs[p]);
                //System.out.println("EFT for t=" + (task.id + 1) + " and p=" + (procs[p].id + 1) + " is " + eft);
                if (eft < minEft) {
                    bestProc = procs[p];
                    minEft = eft;
                }
            }
            System.out.println("Earliest finish time " + minEft + " is gained with processor #" + (bestProc.id + 1));
            System.out.println();
            Set<Task> pred = Objects.requireNonNull(task).getPredecessors();
            int finishPrev;
            int maxFinishPrev = -1;
            for (Task t : pred) {
                finishPrev = getConnCost(t, task, t.processor, bestProc) + t.finishTime;
                if (finishPrev > maxFinishPrev)
                    maxFinishPrev = finishPrev;
            }
            task.startTime = Math.max(bestProc.getAvailableTime(), maxFinishPrev);
            task.finishTime = task.startTime + bestProc.getTaskCost(task);
            makespan = task.finishTime;
            if (!bestProc.assignTask(task))
                System.out.println("Bad task start time");

        }
        System.out.println("Makespan: " + makespan);
        return makespan;
    }

    private void initTaskList() {
        for (Task task : tasks) {
            task.rank = getRank(task);
            taskList.add(task);
        }
    }

    protected int getRank(Task task) {
        if (task.rank >= 0) {
            return task.rank;
        } else if (task.isExit()) {
            task.rank = getMeanCost(task);
            return task.rank;
        } else {
            Set<Task> succ = task.getSuccessors();
            int rankNext;
            int maxNextRank = 0;
            for (Task t : succ) {
                rankNext = getMeanConnCost(task, t) + getRank(t);
                if (rankNext > maxNextRank)
                    maxNextRank = rankNext;
            }
            task.rank = getMeanCost(task) + maxNextRank;
            return task.rank;
        }
    }

    /*Earliest finish time*/
    protected int getEFT(Task task, Processor proc) {
        return getEST(task, proc) + proc.getTaskCost(task);
    }

    /*Earliest start time*/
    private int getEST(Task task, Processor proc) {
        if (task.isEnter()) {
            return 0;
        } else {
            Set<Task> pred = task.getPredecessors();
            int finishPrev;
            int maxFinishPrev = -1;
            for (Task t : pred) {
                finishPrev = getConnCost(t, task, t.processor, proc) + t.finishTime;
                if (finishPrev > maxFinishPrev)
                    maxFinishPrev = finishPrev;
            }
            return Math.max(proc.getAvailableTime(), maxFinishPrev);
        }
    }

    private int getMeanCost(Task task) {
        int sum = 0;
        for (Processor proc : procs) {
            sum += proc.getTaskCost(task);
        }
        return sum / procs.length;
    }

    private int getMeanConnCost(Task start, Task end) {
        return start.getConnCost(end) * procTree.getMeanValue();
    }

    int getConnCost(Task start, Task end, Processor startProc, Processor endProc) {
        return start.getConnCost(end) * procTree.matrix[startProc.id][endProc.id];
    }
}

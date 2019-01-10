import java.util.Objects;
import java.util.Set;

class HEFTScheduler {

    private TaskList taskList;
    private Task[] tasks;
    Processor[] procs;
    ConnMatrix conns;

    HEFTScheduler(Processor[] procs, Task[] tasks, ConnMatrix procTree) {
        taskList = new TaskList();
        this.tasks = tasks;
        this.procs = procs;
        this.conns = procTree;
        System.out.println("Mean connection weight: " + this.conns.distMatrix.getMeanValue());
    }

    int schedule() {
        initTaskList();
        System.out.println("Task list: ");
        System.out.println("id  rank");
        System.out.println(taskList.toString());

        int makespan = 0;
        while (!taskList.isEmpty()) {
            Task task = taskList.get();
            System.out.println("Assigning " + (task.id + 1) + "...");
            Processor bestProc = procs[0];
            int minEft = getEFT(task, procs[0]);
            System.out.println("EFT for task " + (task.id + 1) + " and cpu 1 is " + minEft);
            for (int p = 1; p < procs.length; p++) {
                int eft = getEFT(task, procs[p]);
                System.out.println("EFT for task " + (task.id + 1) + " and cpu " + (procs[p].id + 1) + " is " + eft);
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
                finishPrev = conns
                        .getConnectionCost(t.processor.id, bestProc.id, t, task) + t.finishTime;
                conns.assignTransmission(t.processor.id, bestProc.id, t, task);
                if (finishPrev > maxFinishPrev)
                    maxFinishPrev = finishPrev;
            }
            task.startTime = Math.max(bestProc.getAvailableTime(), maxFinishPrev);
            task.finishTime = task.startTime + (task.getCost() + bestProc.getProd() - 1) / bestProc.getProd();
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
                rankNext = conns.distMatrix.getMeanValue() * task.getConnCost(t) + getRank(t);
                if (rankNext > maxNextRank)
                    maxNextRank = rankNext;
            }
            task.rank = getMeanCost(task) + maxNextRank;
            return task.rank;
        }
    }

    /*Earliest finish time*/
    protected int getEFT(Task task, Processor proc) {
        return getEST(task, proc) + (task.getCost() + proc.getProd() - 1) / proc.getProd();
    }

    /*Earliest start time*/
    private int getEST(Task task, Processor proc) {
        if (task.isEnter()) {
            return proc.getAvailableTime();
        } else {
            Set<Task> pred = task.getPredecessors();
            int finishPrev;
            int maxFinishPrev = -1;
            for (Task t : pred) {
                finishPrev = conns
                        .getConnectionCost(t.processor.id, proc.id, t, task) + t.finishTime;
                if (finishPrev > maxFinishPrev)
                    maxFinishPrev = finishPrev;
            }
            return Math.max(proc.getAvailableTime(), maxFinishPrev);
        }
    }

    private int getMeanCost(Task task) {
        int sum = 0;
        for (Processor proc : procs) {
            sum += (task.getCost() + proc.getProd() - 1) / proc.getProd();
        }
        return sum / procs.length;
    }
}

import java.util.Set;

public class PEFTScheduler extends HEFTScheduler {

    PEFTScheduler(Processor[] procs, Task[] tasks, ConnMatrix conns) {
        super(procs, tasks, conns);
    }

    @Override
    protected int getRank(Task task) {
        int sum = 0;
        for (Processor proc : procs) {
            sum += getOCT(task, proc);
        }
        return sum / procs.length;
    }

    private int getOCT(Task task, Processor proc) {
        if (task.isExit()) return 0;

        Set<Task> succ = task.getSuccessors();
        int maxVal = -1;
        for (Task t : succ) {
            int minVal = Integer.MAX_VALUE;
            for (Processor p : procs) {
                int newVal = getOCT(t, p) +
                        (task.getCost() + p.getProd() - 1) / p.getProd() +
                        conns.getConnectionCost(proc.id, p.id, task, t);
                if (newVal < minVal)
                    minVal = newVal;
            }
            if (minVal > maxVal)
                maxVal = minVal;
        }
        return maxVal;
    }

    @Override
    protected int getEFT(Task task, Processor proc) {
        return super.getEFT(task, proc) + getOCT(task, proc);
    }
}

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

                                 /*01  02  03  04  05  06  07  08  09  10  11  12  13  14  15*/
    private static int[] taskArr = {5, 11,  7, 12,  3,  2,  5,  9, 12, 16, 21,  4,  3,  2,  7};
    private static final int IF = Integer.MAX_VALUE;
    private static int[][] taskGraph = {
                 /*01  02  03  04  05  06  07  08  09  10  11  12  13  14  15*/
            /*01*/{IF,  3,  1,  2,  1,  1, IF, IF, IF, IF, IF, IF, IF, IF, IF},
            /*02*/{IF, IF, IF, IF, IF, IF,  7, IF, IF, IF, IF, IF, IF, IF, IF},
            /*03*/{IF, IF, IF, IF, IF, IF,  9, IF, IF, IF, IF, IF, IF, IF, IF},
            /*04*/{IF, IF, IF, IF, IF, IF, IF,  4, IF, IF, IF, IF, IF, IF, IF},
            /*05*/{IF, IF, IF, IF, IF, IF,  4, IF,  5, IF, IF, IF, IF, IF, IF},
            /*06*/{IF, IF, IF, IF, IF, IF, IF, IF,  2, IF, IF, IF, IF, IF, IF},
            /*07*/{IF, IF, IF, IF, IF, IF, IF, IF, IF,  1, 10, IF, IF, IF, IF},
            /*08*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  4, IF,  2, IF, 10},
            /*09*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  5, 10, IF, IF},
            /*10*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  3, IF},
            /*11*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, 15, IF},
            /*12*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  7},
            /*13*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  2},
            /*14*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, 10},
            /*15*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF},
    };

    private static int[][] procCost = {
                 /*01  02  03  04  05  06  07  08  09  10  11  12  13  14  15*/
            /*01*/{ 2,  2,  1,  3,  3,  1,  1,  2,  2,  3,  4,  1,  2,  1,  2},
            /*02*/{ 1,  4,  2,  2,  2,  1,  2,  1,  1,  2,  5,  3,  1,  2,  2},
            /*03*/{ 2,  5,  3,  3,  1,  1,  1,  2,  2,  3,  4,  2,  2,  1,  4},
            /*04*/{ 3,  3,  2,  2,  2,  2,  2,  1,  3,  1,  4,  3,  1,  1,  2},
            /*05*/{ 2,  4,  2,  1,  2,  2,  1,  2,  2,  3,  3,  1,  2,  3,  3},
            /*06*/{ 1,  3,  3,  2,  1,  3,  2,  1,  1,  1,  4,  3,  2,  1,  1},
            /*07*/{ 1,  2,  4,  2,  2,  2,  1,  2,  2,  3,  3,  4,  2,  1,  2},
            /*08*/{ 1,  1,  3,  4,  1,  2,  2,  1,  3,  1,  5,  2,  2,  1,  1},
            /*09*/{ 2,  4,  1,  2,  2,  1,  1,  2,  2,  3,  4,  3,  2,  3,  4},
            /*10*/{ 3,  4,  3,  1,  1,  1,  2,  1,  1,  2,  5,  1,  2,  1,  2},
    };

    private static int[][] processorsTree = {
                 /*01  02  03  04  05  06  07  08  09  10*/
            /*01*/{ 0, IF,  1, IF, IF, IF, IF, IF, IF, IF},
            /*02*/{IF,  0,  2, IF, IF, IF, IF, IF, IF, IF},
            /*03*/{ 1,  2,  0, IF,  3, IF, IF, IF, IF, IF},
            /*04*/{IF, IF, IF,  0,  1, IF, IF, IF, IF, IF},
            /*05*/{IF, IF,  3,  1,  0,  4,  2, IF, IF, IF},
            /*06*/{IF, IF, IF, IF,  4,  0, IF, IF, IF, IF},
            /*07*/{IF, IF, IF, IF,  2, IF,  0, IF, IF,  2},
            /*08*/{IF, IF, IF, IF, IF, IF, IF,  0,  1, IF},
            /*09*/{IF, IF, IF, IF, IF, IF, IF,  1,  0,  3},
            /*10*/{IF, IF, IF, IF, IF, IF,  2, IF,  3,  0}
    };

    public static void main(String[] args) {
        normalize();

        int taskNum = taskArr.length;
        int procNum = procCost.length;

        Task[] tasks = new Task[taskNum];
        for (int i = 0; i < taskNum; i++)
            tasks[i] = new Task(i, taskArr[i]);

        for (int i = 0; i < taskNum; i++)
            for (int j = 0; j < taskNum; j++)
                if (taskGraph[i][j] < Integer.MAX_VALUE)
                    tasks[i].addSuccessor(tasks[j], taskGraph[i][j]);

        IntMatrix procTree = new IntMatrix(processorsTree);

        Processor[] procs = new Processor[procNum];
        for (int i = 0; i < procNum; i++) {
            HashMap<Task, Integer> taskCosts = new HashMap<>();
            for (int j = 0; j < taskNum; j++) {
                taskCosts.put(tasks[j], procCost[i][j]);
            }
            procs[i] = new Processor(i, taskCosts);
        }

        HEFTScheduler heftScheduler = new HEFTScheduler(procs, tasks, procTree);
        int makespan = heftScheduler.schedule();
        printSchedule(procs, makespan, heftScheduler);

        tasks = new Task[taskNum];
        for (int i = 0; i < taskNum; i++)
            tasks[i] = new Task(i, taskArr[i]);

        for (int i = 0; i < taskNum; i++)
            for (int j = 0; j < taskNum; j++)
                if (taskGraph[i][j] < Integer.MAX_VALUE)
                    tasks[i].addSuccessor(tasks[j], taskGraph[i][j]);

        procTree = new IntMatrix(processorsTree);
        procs = new Processor[procNum];
        for (int i = 0; i < procNum; i++) {
            HashMap<Task, Integer> taskCosts = new HashMap<>();
            for (int j = 0; j < taskNum; j++) {
                taskCosts.put(tasks[j], procCost[i][j]);
            }
            procs[i] = new Processor(i, taskCosts);
        }
        PEFTScheduler peftScheduler = new PEFTScheduler(procs, tasks, procTree);
        makespan = peftScheduler.schedule();
        printSchedule(procs, makespan, peftScheduler);
    }

    private static void printSchedule(Processor[] procs, int makespan, HEFTScheduler scheduler) {
        int[][] procsSymbols = new int[procs.length][makespan];
        String[] transitions = new String[makespan + 1];
        for (int i = 0; i <= makespan; i++)
            transitions[i] = "";
        for (int i = 0; i < procs.length; i++) {
            Processor proc = procs[i];
            ArrayList<Task> tasks = proc.assignedTasks;
            for (Task task : tasks) {
                for (int j = task.startTime; j < task.finishTime; j++)
                    procsSymbols[i][j] = task.id + 1;
                StringBuilder str = new StringBuilder();
                for (Task t: task.getSuccessors()){
                    str.append(task.id + 1)
                            .append("(")
                            .append(task.processor.id + 1)
                            .append(") -[")
                            .append(scheduler.getConnCost(task, t, task.processor, t.processor))
                            .append("]-> ")
                            .append(t.id + 1)
                            .append("(")
                            .append(t.processor.id + 1)
                            .append("), ");
                }
                transitions[task.finishTime] += str.toString();
            }
        }

        System.out.printf("%3s | ", "T");
        for (int j = 0; j < procs.length; j++) {
            System.out.printf("%2d ", j + 1);
        }
        System.out.println();

        for (int i = 0; i < makespan; i++) {
            System.out.printf("%3d | ", i + 1);
            for (int j = 0; j < procs.length; j++) {
                String str = "--";
                if (procsSymbols[j][i] != 0)
                    str = procsSymbols[j][i] + "";
                System.out.printf("%2s ", str);
            }
            if (transitions[i] != null) System.out.print(transitions[i]);
            System.out.println();
        }
        System.out.println();
    }

    private static void normalize() {
        int taskNum = taskArr.length;

        ArrayList<Integer> entries = new ArrayList<>();
        for (int j = 0; j < taskNum; j++) {
            for (int i = 0; i < taskNum; i++) {
                if (taskGraph[i][j] < IF)
                    break;
                else if (i == taskNum - 1)
                    entries.add(j);
            }
        }
        if (entries.size() < 1) {
            taskGraph = null;
            return;
        } else if (entries.size() > 1) {
            int[] newTaskArr = new int[taskNum + 1];
            System.arraycopy(taskArr, 0, newTaskArr, 0, taskNum);
            newTaskArr[taskNum] = 0;
            taskArr = newTaskArr;

            int[][] newTaskGraph = new int[taskNum + 1][taskNum + 1];
            for (int i = 0; i < taskNum; i++)
                System.arraycopy(taskGraph[i], 0, newTaskGraph[i], 0, taskNum);

            for (int i = 0; i <= taskNum; i++) {
                if (entries.contains(i))
                    newTaskGraph[taskNum][i] = 0;
                else
                    newTaskGraph[taskNum][i] = IF;
            }


            for (int i = 0; i < taskNum; i++) {
                newTaskGraph[i][taskNum] = IF;
            }

            taskGraph = newTaskGraph;

            int[][] newProcCosts = new int[procCost.length][taskNum + 1];
            for (int i = 0; i < procCost.length; i++)
                System.arraycopy(procCost[i], 0, newProcCosts[i], 0, taskNum);
            for (int i = 0; i < procCost.length; i++) {
                newProcCosts[i][taskNum] = 0;
            }
            procCost = newProcCosts;
        }

        taskNum = taskArr.length;

        ArrayList<Integer> exits = new ArrayList<>();
        for (int i = 0; i < taskNum; i++) {
            for (int j = 0; j < taskNum; j++) {
                if (taskGraph[i][j] < IF)
                    break;
                else if (j == taskNum - 1)
                    exits.add(i);
            }
        }

        if (exits.size() < 1) {
            taskGraph = null;
        } else if (exits.size() > 1) {
            int[] newTaskArr = new int[taskNum + 1];
            System.arraycopy(taskArr, 0, newTaskArr, 0, taskNum);
            newTaskArr[taskNum] = 0;
            taskArr = newTaskArr;

            int[][] newTaskGraph = new int[taskNum + 1][taskNum + 1];
            for (int i = 0; i < taskNum; i++)
                System.arraycopy(taskGraph[i], 0, newTaskGraph[i], 0, taskNum);

            for (int i = 0; i <= taskNum; i++) {
                if (exits.contains(i))
                    newTaskGraph[i][taskNum] = 0;
                else
                    newTaskGraph[i][taskNum] = IF;
            }

            for (int i = 0; i < taskNum; i++) {
                newTaskGraph[taskNum][i] = IF;
            }
            taskGraph = newTaskGraph;

            int[][] newProcCosts = new int[procCost.length][taskNum + 1];
            for (int i = 0; i < procCost.length; i++)
                System.arraycopy(procCost[i], 0, newProcCosts[i], 0, taskNum);
            for (int i = 0; i < procCost.length; i++) {
                newProcCosts[i][taskNum] = 0;
            }
            procCost = newProcCosts;
        }
    }

}

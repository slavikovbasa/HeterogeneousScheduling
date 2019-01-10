import java.util.ArrayList;

public class Main {

    private static final int IF = Integer.MAX_VALUE;

                                 /*01  02  03  04  05  06  07  08  09  10  11  12  13  14  15*/
    private static int[] taskArr = {5, 11,  7, 12,  3,  2,  5,  9, 12, 16, 21,  4,  3,  2,  7};
    private static int[][] taskGraph = {
                 /*01  02  03  04  05  06  07  08  09  10  11  12  13  14  15*/
            /*01*/{IF,  2,  1,  2,  1,  1, IF, IF, IF, IF, IF, IF, IF, IF, IF},
            /*02*/{IF, IF, IF, IF, IF, IF,  3, IF, IF, IF, IF, IF, IF, IF, IF},
            /*03*/{IF, IF, IF, IF, IF, IF,  4, IF, IF, IF, IF, IF, IF, IF, IF},
            /*04*/{IF, IF, IF, IF, IF, IF, IF,  2, IF, IF, IF, IF, IF, IF, IF},
            /*05*/{IF, IF, IF, IF, IF, IF,  2, IF,  2, IF, IF, IF, IF, IF, IF},
            /*06*/{IF, IF, IF, IF, IF, IF, IF, IF,  1, IF, IF, IF, IF, IF, IF},
            /*07*/{IF, IF, IF, IF, IF, IF, IF, IF, IF,  1,  5, IF, IF, IF, IF},
            /*08*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  2, IF,  2, IF,  5},
            /*09*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  2,  5, IF, IF},
            /*10*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  2, IF},
            /*11*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  6, IF},
            /*12*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  3},
            /*13*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  1},
            /*14*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF,  5},
            /*15*/{IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF, IF},
    };
                                  /*01  02  03  04  05  06  07  08*/
    private static int[] procProd = {1,  2,  3,  2,  2,  1,  2,  1};
    private static int[][] processorsTree = {
                 /*01  02  03  04  05  06  07  08
            /*01*/{ 0,  1,  1,  2, IF, IF, IF, IF},
            /*02*/{ 1,  0, IF, IF,  2,  3, IF, IF},
            /*03*/{ 1, IF,  0, IF, IF, IF,  2, IF},
            /*04*/{ 2, IF, IF,  0, IF, IF, IF,  1},
            /*05*/{IF,  2, IF, IF,  0, IF, IF, IF},
            /*06*/{IF,  3, IF, IF, IF,  0, IF, IF},
            /*07*/{IF, IF,  2, IF, IF, IF,  0, IF},
            /*08*/{IF, IF, IF,  1, IF, IF, IF,  0}
    };

    public static void main(String[] args) {
        normalize();

        int taskNum = taskArr.length;
        int procNum = procProd.length;

        Task[] tasks = new Task[taskNum];
        for (int i = 0; i < taskNum; i++)
            tasks[i] = new Task(i, taskArr[i]);

        for (int i = 0; i < taskNum; i++)
            for (int j = 0; j < taskNum; j++)
                if (taskGraph[i][j] < Integer.MAX_VALUE)
                    tasks[i].addSuccessor(tasks[j], taskGraph[i][j]);

        Connection[][] conns = new Connection[procNum][procNum];

        for (int i = 0; i < procNum; i++) {
            for (int j = 0; j < procNum; j++) {
                if (conns[j][i] != null) conns[i][j] = conns[j][i];
                else conns[i][j] = new Connection(processorsTree[i][j]);
            }
        }

        ConnMatrix procTree = new ConnMatrix(conns);

        System.out.println("P    1  2  3  4  5  6  7  8\n");
        for (int i = 0; i < procNum; i++) {
            System.out.print(i + 1 + "  ");
            for (int j = 0; j < procNum; j++) {
                System.out.printf("%3d", procTree.distMatrix.matrix[i][j]);
            }
            System.out.println();
        }

        Processor[] procs = new Processor[procNum];
        for (int i = 0; i < procNum; i++)
            procs[i] = new Processor(i, procProd[i]);

        HEFTScheduler scheduler = new PEFTScheduler(procs, tasks, procTree);
        int makespan = scheduler.schedule();
        printSchedule(procs, procTree, makespan);
    }

    private static void printSchedule(Processor[] procs, ConnMatrix conns, int makespan) {
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
            }
        }

        for (int i = 0; i < procs.length; i++) {
            for (int j = 0; j < procs.length; j++) {
                if (j > i) continue;
                Connection c = conns.matrix[i][j];
                if (c.getCost() == IF) continue;

                for (Transmission t: c.getTransmissions()){
                    String str = String.valueOf(t.fromTask.id + 1) +
                            "(p" +
                            (t.fromProc + 1) +
                            ")-[" +
                            (t.finishTime - t.startTime) +
                            "]->" +
                            (t.toTask.id + 1) +
                            "(p" +
                            (t.toProc + 1) +
                            "), ";
                    transitions[t.startTime] += str;
                }
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
            if (transitions[i] != null && !transitions[i].equals("")) System.out.print(transitions[i] + "\b\b");
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
        }
    }
}

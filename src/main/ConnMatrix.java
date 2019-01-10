import java.util.ArrayList;

class ConnMatrix {
    Connection[][] matrix;
    IntMatrix distMatrix;

    ConnMatrix(Connection[][] matrix) {
        int len = matrix.length;
        this.matrix = new Connection[len][len];

        for (int i = 0; i < len; i++)
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, matrix.length);
        int[][] costs = new int[len][len];

        for (int i = 0; i < len; i++)
            for (int j = 0; j < len; j++)
                costs[i][j] = matrix[i][j].getCost();

        this.distMatrix = new IntMatrix(costs).getDistanceMatrix();
    }

    void assignTransmission(int from, int to, Task fromTask, Task toTask) {
        ArrayList<Integer> path = distMatrix.getPath(from, to);
        int time = fromTask.finishTime;
        while (path.size() > 1) {
            Connection c = matrix[path.get(0)][path.get(1)];
            int transmTime = fromTask.getConnCost(toTask) * matrix[path.get(0)][path.get(1)].getCost();
            if (time < c.getAvailableTime()) time = c.getAvailableTime();
            Transmission t = new Transmission(time, time + transmTime, fromTask, toTask, path.get(0), path.get(1));
            matrix[path.get(0)][path.get(1)].assignTransmission(t);
            path.remove(0);
            time += transmTime;
        }
    }

    int getConnectionCost(int startProc, int endProc, Task fromTask, Task toTask) {
        ArrayList<Integer> path = distMatrix.getPath(startProc, endProc);
        int cost = 0;
        int time = fromTask.finishTime;
        while (path.size() > 1) {
            int delay = matrix[path.get(0)][path.get(1)].getAvailableTime() - time;
            delay = delay > 0? delay: 0;
            int inc = matrix[path.get(0)][path.get(1)].getCost() * fromTask.getConnCost(toTask) + delay;
            cost += inc;
            time += inc;
            path.remove(0);
        }
        return cost;
    }
}

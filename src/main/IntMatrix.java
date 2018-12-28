import java.util.ArrayList;

class IntMatrix {
    int[][] matrix;
    private int[][] next;

    IntMatrix(int[][] matrix) {
        int len = matrix.length;
        this.matrix = new int[len][len];
        this.next = new int[len][len];

        for (int i = 0; i < len; i++)
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, matrix.length);

        for (int i = 0; i < len; i++)
            for (int j = 0; j < len; j++)
                next[i][j] = j;
    }

    IntMatrix getDistanceMatrix() {
        int len = matrix.length;
        int[][] distances = new int[len][len];

        for (int i = 0; i < len; i++)
            System.arraycopy(matrix[i], 0, distances[i], 0, len);

        for (int k = 0; k < len; k++) {
            for (int i = 0; i < len; i++) {
                for (int j = 0; j < len; j++) {
                    int newDistance = distances[i][k] + distances[k][j];
                    if (distances[i][k] == Integer.MAX_VALUE || distances[k][j] == Integer.MAX_VALUE)
                        newDistance = Integer.MAX_VALUE;

                    if (distances[i][j] > newDistance) {
                        distances[i][j] = newDistance;
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
        IntMatrix distMatrix = new IntMatrix(distances);
        distMatrix.setNext(next);
        return distMatrix;
    }

    ArrayList<Integer> getPath(int from, int to) {
        ArrayList<Integer> path = new ArrayList<>();
        path.add(from);

        while (from != to) {
            from = next[from][to];
            path.add(from);
        }
        return path;
    }

    private void setNext(int[][] next) {
        this.next = new int[next.length][next[0].length];
        for (int i = 0; i < next.length; i++)
            System.arraycopy(next[i], 0, this.next[i], 0, next.length);
    }

    int getMeanValue() {
        int sum = 0;
        for (int[] row : matrix)
            for (int col : row)
                sum += col;

        return sum / (matrix.length * (matrix.length - 1));
    }
}

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class Tests {
    @Test
    public void shortestPathTest() {
        int[][] matrixArr = {
                {0, Integer.MAX_VALUE, -2, Integer.MAX_VALUE},
                {4, 0, 3, Integer.MAX_VALUE},
                {Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 2},
                {Integer.MAX_VALUE, -1, Integer.MAX_VALUE, 0}
        };
        IntMatrix matrix = new IntMatrix(matrixArr);
        IntMatrix dist = matrix.getDistanceMatrix();
        int[][] expected = {
                {0, -1, -2, 0},
                {4, 0, 2, 4},
                {5, 1, 0, 2},
                {3, -1, 1, 0}
        };
        assertArrayEquals(expected, dist.matrix);

        ArrayList<Integer> expectedPath = new ArrayList<>();
        expectedPath.add(0);
        expectedPath.add(2);
        expectedPath.add(3);
        expectedPath.add(1);

        assertEquals(expectedPath, dist.getPath(0, 1));
    }

    @Test
    public void schedulerTest() {
        int INF = Integer.MAX_VALUE;
        int[][] taskGraph = {
                     /*01 02 03 04 05 06 07 08 09 10
                /*01*/{INF, 17, 31, 29, 13, 7, INF, INF, INF, INF},
                /*02*/{INF, INF, INF, INF, INF, INF, INF, 3, 30, INF},
                /*03*/{INF, INF, INF, INF, INF, INF, 16, INF, INF, INF},
                /*04*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*05*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*06*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*07*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*08*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*09*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                /*10*/{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }
}
package utils;

import java.util.List;

/**
 * Some static method for matrix manipulation
 */
public class MatrixUtil {

    private MatrixUtil() {
    }

    /**
     * Horizontal stack a list of matrix
     *
     * @param matrixList list of matrix to stack
     * @param shape the final shape wanted [numRow, numCol]
     * @return the stacked matrix
     */
    public static Integer[][] hstack(List<Integer[][]> matrixList, int[] shape) {
        if (shape == null) throw new RuntimeException("shape array can't be null");
        if (shape.length != 2)
            throw new RuntimeException("shape must be an array of lenght 2, the provided shape is " + shape.length);
        Integer[][] res = new Integer[shape[0]][shape[1]];
        int offset = 0;
        for (Integer[][] matrix : matrixList) {
            for (int row = 0; row < matrix.length; row++) {
                System.arraycopy(matrix[row], 0, res[row], offset, matrix[row].length);
            }
            offset += matrix[0].length; // FIXME: matrix[0].length may produce NullPointer
        }
        return res;
    }

    /**
     * Vertical stack a list of matrix
     *
     * @param matrixList list of matrix to stack
     * @param shape the final shape wanted [numRow, numCol]
     * @return the stacked matrix
     */
    public static Integer[][] vstack(List<Integer[][]> matrixList, int[] shape) {
        if (shape == null) throw new RuntimeException("shape array can't be null");
        if (shape.length != 2)
            throw new RuntimeException("shape must be an array of lenght 2, the provided shape is " + shape.length);
        Integer[][] res = new Integer[shape[0]][shape[1]];
        int offset = 0;
        for (Integer[][] matrix : matrixList) {
            for (int row = 0; row < matrix.length; row++) {
                System.arraycopy(matrix[row], 0, res[offset + row], 0, matrix[row].length);
            }
            offset += matrix.length;
        }
        return res;
    }
}

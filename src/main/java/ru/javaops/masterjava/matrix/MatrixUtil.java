package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    private static final ExecutorService multiplyExecutor = Executors.newFixedThreadPool(MainMatrix.THREAD_NUMBER);

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<Void> completionService = new ExecutorCompletionService<>(multiplyExecutor);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < matrixSize; i++) {
            int index = i;
            futures.add(completionService.submit(() -> multiplyMatrix(matrixA[index], matrixB, matrixC, index, matrixSize)));
        }
        while (!futures.isEmpty()) {
            futures.remove(completionService.poll());
        }
        return matrixC;
    }

    private static Void multiplyMatrix(int[] row, int[][] matrixB, int[][] matrixC, int i, int matrixSize) {
        int[] column = new int[matrixSize];
        for (int k = 0; k < matrixSize; k++) {
            column[k] = matrixB[k][i];
        }
        for (int j = 0; j < matrixSize; j++) {
            matrixC[i][j] = getSum(column, row);
        }
        return null;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            multiplyMatrix(matrixA[i], matrixB, matrixC, i, matrixSize);
        }
        return matrixC;
    }

    private static int getSum(int[] column, int[] row) {
        final int matrixSize = column.length;
        int sum = 0;
        for (int k = 0; k < matrixSize; k++) {
            sum += row[k] * column[k];
        }
        return sum;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}

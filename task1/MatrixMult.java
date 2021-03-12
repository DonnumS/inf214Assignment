package task1;

import java.util.*;
import java.lang.InterruptedException;
import java.util.Random;

public class MatrixMult {

    public static int SIZE = 1000;

    public static void main(String[] args) {
        double[][] A = new double[SIZE][SIZE];
        double[][] B = new double[SIZE][SIZE];
        double[][] C = new double[SIZE][SIZE];
        double[][] D = new double[SIZE][SIZE];

        // Generate the two matrices A and B randomly.
        A = generate(SIZE, SIZE);
        B = generate(SIZE, SIZE);

        // Use threads to multiply A and B resulting in C
        long startTime = System.nanoTime();
        multiply(A, B, C);
        long stopTime = System.nanoTime();

        // Use the SerialMult class to produce A * B = D
        // Used to check if C returns correct output
        long startTimeSerial = System.nanoTime();
        SerialMult.mult(A, B, D);
        long stopTimeSerial = System.nanoTime();

        System.out.println("Execution time");
        System.out.println("SerialMult: ");
        System.out.println(stopTimeSerial - startTimeSerial);
        System.out.println("\nRowMultiplierTask: ");
        System.out.println(stopTime - startTime);
        // Check if C and D are identical
        if (sameMatrix(C, D)) {
            System.out.println("\nC and D are identical\nMatrix multiplication succesfull\n");
        } else {
            System.out.println("\nC and D are NOT identical\nMatrix multiplication failed\n");
        }

    }

    public static void multiply(double[][] A, double[][] B, double[][] C) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            RowMultiplierTask task = new RowMultiplierTask(C, A, B, i);
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);

            if (threads.size() % 10 == 0) {
                waitForThreads(threads);
            }
        }
    }

    private static void waitForThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threads.clear();
    }

    // Generate random matrix
    public static double[][] generate(int rows, int columns) {
        double[][] ret = new double[rows][columns];
        Random random = new Random();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                ret[i][j] = random.nextDouble() * 10;
        return ret;
    }

    // Check if matrix C is equal to matrix D
    public static boolean sameMatrix(double[][] C, double[][] D) {
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C[0].length; j++) {
                if (C[i][j] != D[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }
}
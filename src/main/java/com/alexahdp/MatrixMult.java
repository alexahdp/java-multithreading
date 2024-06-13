package com.alexahdp;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringJoiner;

public class MatrixMult {
    private static String INPUT_MATRIX_FILE1 = "./resources/matrix_1.txt";
    private static String INPUT_MATRIX_FILE2 = "./resources/matrix_2.txt";

    public static void main(String[] args) throws IOException {
        var m1 = Matrix.fromFile(INPUT_MATRIX_FILE1);
        var m2 = Matrix.fromFile(INPUT_MATRIX_FILE2);

        var mm = new MatrixMultiplier(m1, m2);

        mm.start();
        // wait to finish
        try {
            mm.join();
            mm.result.saveToFile("./resources/matrix_result.txt");
            System.out.println("Success");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class Matrix {
        private static final int N = 10;
        private Scanner scanner;
        private float[][] value;

        public Matrix(float[][] value) {
            this.value = value;
        }

        public void saveToFile(String filePath) throws IOException {
            try (FileWriter fileWriter = new FileWriter(filePath)) {
                for (int l = 0; l < N; l++) {
                    StringJoiner sj = new StringJoiner(",");
                    for (int c = 0; c < N; c++) {
                        sj.add(String.format("%.2f", value[l][c]));
                    }
                    fileWriter.write(sj.toString());
                    fileWriter.write("\n");
                }
            }
        }

        public static Matrix fromFile(String filePath) throws IOException {
            try (FileReader fileReader = new FileReader(filePath)) {
                var scanner = new Scanner(fileReader);
                float[][] value = read(scanner);
                return new Matrix(value);
            }
        }

        private static float[][] read(Scanner scanner) {
            float[][] matrix = new float[N][N];
            String line = scanner.nextLine();
            int r = 0;
            while (line != null) {
                String[] lineItems = line.split(",");
                for  (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(lineItems[c]);
                }
                r ++;
                if (scanner.hasNext()) {
                    line = scanner.nextLine();
                } else {
                    line = null;
                }
            }
            return matrix;
        }
    }

    private static class MatrixMultiplier extends Thread {
        // TODO
        private static final int N = 10;
        private final Matrix m1;
        private final Matrix m2;
        private Matrix result;

        public MatrixMultiplier(Matrix m1, Matrix m2) {
            this.m1 = m1;
            this.m2 = m2;
        }

        @Override
        public void run() {
            float[][] res = multiply(m1.value, m2.value);
            result = new Matrix(res);
        }

        private float[][] multiply(float [][] m1, float [][] m2) {
            float [][] result = new float[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        result[i][j] += m1[i][k] * m2[k][j];
                    }
                }
            }
            return result;
        }
    }
}

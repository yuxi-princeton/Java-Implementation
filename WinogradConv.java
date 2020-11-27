/* *****************************************************************************
 *  Name:    Alan Turing
 *  NetID:   aturing
 *  Precept: P00
 *
 *  Description:  Prints 'Hello, World' to the terminal window.
 *                By tradition, this is everyone's first program.
 *                Prof. Brian Kernighan initiated this tradition in 1974.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class WinogradConv {
    private static final int inputSize = 4; // n
    private static final int outputSize = 2; // m
    private static final int kernelSize = 3; // r
    private double[][] inputTile; // n*n input tile
    private double[][] outputTile; // m*m output tile
    private double[][] kernelTile; // r*r kernel matrix

    // constructor
    public WinogradConv(double[][] input, double[][] kernel) {
        // inputSize = input.length;
        // kernelSize = kernel.length;
        // outputSize = inputSize - kernelSize + 1;

        // initiating and making copies of input and kernel tiles
        inputTile = new double[inputSize][inputSize];
        kernelTile = new double[kernelSize][kernelSize];
        outputTile = new double[outputSize][outputSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++)
                inputTile[i][j] = input[i][j];
        }
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++)
                kernelTile[i][j] = kernel[i][j];
        }
    }


    // F(2*2, 3*3)
    // kernel size = 3*3, input size: 4*4, output size = 2*2
    public void winogradCalc() {
        double[][] inputTransform = new double[4][4];
        double[][] kernelTransform = new double[4][4];

        // Input transforml
        inputTransform[0][0] = inputTile[0][0] - inputTile[0][2] - inputTile[2][0]
                + inputTile[2][2];
        inputTransform[0][1] = inputTile[0][1] + inputTile[0][2] - inputTile[2][1]
                - inputTile[2][2];
        inputTransform[0][2] = inputTile[0][2] - inputTile[0][1] + inputTile[2][1]
                - inputTile[2][2];
        inputTransform[0][3] = inputTile[0][1] - inputTile[0][3] - inputTile[2][1]
                + inputTile[2][3];
        inputTransform[1][0] = inputTile[1][0] - inputTile[1][2] + inputTile[2][0]
                - inputTile[2][2];
        inputTransform[1][1] = inputTile[1][1] + inputTile[1][2] + inputTile[2][1]
                + inputTile[2][2];
        inputTransform[1][2] = inputTile[1][2] - inputTile[1][1] - inputTile[2][1]
                + inputTile[2][2];
        inputTransform[1][3] = inputTile[1][1] - inputTile[1][3] + inputTile[2][1]
                - inputTile[2][3];
        inputTransform[2][0] = inputTile[1][2] - inputTile[1][0] + inputTile[2][0]
                - inputTile[2][2];
        inputTransform[2][1] = inputTile[2][1] - inputTile[1][2] - inputTile[1][1]
                + inputTile[2][2];
        inputTransform[2][2] = inputTile[1][1] - inputTile[1][2] - inputTile[2][1]
                + inputTile[2][2];
        inputTransform[2][3] = inputTile[1][3] - inputTile[1][1] + inputTile[2][1]
                - inputTile[2][3];
        inputTransform[3][0] = inputTile[1][0] - inputTile[1][2] - inputTile[3][0]
                + inputTile[3][2];
        inputTransform[3][1] = inputTile[1][1] + inputTile[1][2] - inputTile[3][1]
                - inputTile[3][2];
        inputTransform[3][2] = inputTile[1][2] - inputTile[1][1] + inputTile[3][1]
                - inputTile[3][2];
        inputTransform[3][3] = inputTile[1][1] - inputTile[1][3] - inputTile[3][1]
                + inputTile[3][3];

        // Filter transform
        kernelTransform[0][0] = kernelTile[0][0];
        kernelTransform[0][0] *= 4;
        kernelTransform[0][1] = kernelTile[0][0] + kernelTile[0][1] + kernelTile[0][2];
        kernelTransform[0][1] *= 2;
        kernelTransform[0][2] = kernelTile[0][0] - kernelTile[0][1] + kernelTile[0][2];
        kernelTransform[0][2] *= 2;
        kernelTransform[0][3] = kernelTile[0][2];
        kernelTransform[0][3] *= 4;
        kernelTransform[1][0] = kernelTile[0][0] + kernelTile[1][0] + kernelTile[2][0];
        kernelTransform[1][0] *= 2;
        kernelTransform[1][1] = kernelTile[0][0] + kernelTile[0][1] + kernelTile[0][2]
                + kernelTile[1][0] + kernelTile[1][1]
                + kernelTile[1][2] + kernelTile[2][0] + kernelTile[2][1] + kernelTile[2][2];
        // kernelTransform[1][1] /= 4;
        kernelTransform[1][2] =
                kernelTile[0][0] - kernelTile[0][1] + kernelTile[0][2] + kernelTile[1][0]
                        - kernelTile[1][1]
                        + kernelTile[1][2] + kernelTile[2][0] - kernelTile[2][1]
                        + kernelTile[2][2];
        // kernelTransform[1][2] /= 4;
        kernelTransform[1][3] = kernelTile[0][2] + kernelTile[1][2] + kernelTile[2][2];
        kernelTransform[1][3] *= 2;
        kernelTransform[2][0] = kernelTile[0][0] - kernelTile[1][0] + kernelTile[2][0];
        kernelTransform[2][0] *= 2;
        kernelTransform[2][1] =
                kernelTile[0][0] + kernelTile[0][1] + kernelTile[0][2] - kernelTile[1][0]
                        - kernelTile[1][1]
                        - kernelTile[1][2] + kernelTile[2][0] + kernelTile[2][1] + kernelTile[2][2];
        // kernelTransform[2][1] /= 4;
        kernelTransform[2][2] =
                kernelTile[0][0] - kernelTile[0][1] + kernelTile[0][2] - kernelTile[1][0]
                        + kernelTile[1][1]
                        - kernelTile[1][2] + kernelTile[2][0] - kernelTile[2][1] + kernelTile[2][2];
        // kernelTransform[2][2] /= 4;
        kernelTransform[2][3] = kernelTile[0][2] - kernelTile[1][2] + kernelTile[2][2];
        kernelTransform[2][3] *= 2;
        kernelTransform[3][0] = kernelTile[2][0];
        kernelTransform[3][0] *= 4;
        kernelTransform[3][1] = kernelTile[2][0] + kernelTile[2][1] + kernelTile[2][2];
        kernelTransform[3][1] *= 2;
        kernelTransform[3][2] = kernelTile[2][0] - kernelTile[2][1] + kernelTile[2][2];
        kernelTransform[3][2] *= 2;
        kernelTransform[3][3] = kernelTile[2][2];
        kernelTransform[3][3] *= 4;

        double[][] outputTemp = new double[4][4];
        // Batched-GEMM
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                outputTemp[i][j] = inputTransform[i][j] * kernelTransform[i][j];
            }
        }


        // Output transform
        outputTile[0][0] = outputTemp[0][0] + outputTemp[0][1] + outputTemp[0][2] + outputTemp[1][0]
                + outputTemp[1][1] + outputTemp[1][2] + outputTemp[2][0] + outputTemp[2][1]
                + outputTemp[2][2];
        outputTile[0][0] /= 4;
        outputTile[0][1] =
                outputTemp[0][1] - outputTemp[0][2] - outputTemp[0][3] + outputTemp[1][1]
                        - outputTemp[1][2]
                        - outputTemp[1][3] + outputTemp[2][1] - outputTemp[2][2] - outputTemp[2][3];
        outputTile[0][1] /= 4;
        outputTile[1][0] = outputTemp[1][0] + outputTemp[1][1] + outputTemp[1][2] - outputTemp[2][0]
                - outputTemp[2][1] - outputTemp[2][2] - outputTemp[3][0] - outputTemp[3][1]
                - outputTemp[3][2];
        outputTile[1][0] /= 4;
        outputTile[1][1] =
                outputTemp[1][1] - outputTemp[1][2] - outputTemp[1][3] - outputTemp[2][1]
                        + outputTemp[2][2]
                        + outputTemp[2][3] - outputTemp[3][1] + outputTemp[3][2] + outputTemp[3][3];
        outputTile[1][1] /= 4;
        // https://www.cnblogs.com/shine-lee/p/10906535.html
    }

    // return the output calculated
    public double[][] getOutputTile() {
        return outputTile;
    }

    // method for matrix multiplication
    public int[][] outputRef() {
        int[][] outputRef = new int[outputSize][outputSize];
        for (int i = 0; i < outputSize; i++)
            for (int j = 0; j < outputSize; j++) {
                outputRef[i][j] = 0;
                for (int s = 0; s < kernelSize; ++s)
                    for (int t = 0; t < kernelSize; ++t)
                        outputRef[i][j] += kernelTile[s][t] * inputTile[i + s][j + t];
            }
        return outputRef;
    }

    // string representation
    public String toString() {
        StringBuilder result = new StringBuilder("");
        result.append("input tile: \n");
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                result.append(inputTile[i][j]);
                result.append(" ");
            }
            result.append('\n');
        }
        result.append("kernel tile: \n");
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                result.append(kernelTile[i][j]);
                result.append(" ");
            }
            result.append('\n');
        }
        result.append("output tile: \n");
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                result.append(outputTile[i][j]);
                result.append(" ");
            }
            result.append('\n');
        }
        return result.toString();
    }

    public static void main(String[] args) {
        int inputSize = 4;
        int kernelSize = 3;
        double[][] input = new double[inputSize][inputSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                input[i][j] = StdRandom.uniform(1, 10);
            }
        }
        double[][] kernel = new double[kernelSize][kernelSize];
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                kernel[i][j] = StdRandom.uniform(1, 10);
            }
        }

        WinogradConv winogradConv1 = new WinogradConv(input, kernel);
        winogradConv1.winogradCalc();
        StdOut.println(winogradConv1);
        int outputSize = inputSize - kernelSize + 1;
        int[][] outputRef = winogradConv1.outputRef();
        StdOut.println("Reference Output:");
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                StdOut.print(outputRef[i][j]);
                StdOut.print(" ");
            }
            StdOut.println();
        }
        StdOut.println();

    }
}

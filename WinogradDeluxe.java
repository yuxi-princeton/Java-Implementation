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

public class WinogradDeluxe {
    private static final int inputSize = 6; // n
    private static final int outputSize = 2; // m
    private static final int kernelSize = 5; // r
    private double[][] inputTile; // n*n input tile
    private double[][] outputTile; // m*m output tile
    private double[][] kernelTile; // r*r kernel matrix


    // constructor
    public WinogradDeluxe(double[][] input, double[][] kernel) {
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

    // F(2*2, 5*5)
    // kernel size = 5*5, input size: 4*4, output size = 4*4
    public void winogradDeluxeCalc() {
        double[][] a = new double[4][4];
        double[][] b = new double[4][4];
        double[][] c = new double[4][4];
        double[][] d = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                a[i][j] = inputTile[i][j];
                b[i][j] = inputTile[i + 2][j];
                c[i][j] = inputTile[i][j + 2];
                d[i][j] = inputTile[i + 2][j + 2];
            }
        }
        // for (int i = 0; i < 4; i++) {
        //     b[i][0] = 0;
        //     c[0][i] = 0;
        //     d[i][0] = 0;
        //     d[0][i] = 0;
        // }
        double[][] a_kernel = new double[3][3];
        double[][] b_kernel = new double[3][3];
        double[][] c_kernel = new double[3][3];
        double[][] d_kernel = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                a_kernel[i][j] = kernelTile[i][j];
                b_kernel[i][j] = kernelTile[i + 2][j];
                c_kernel[i][j] = kernelTile[i][j + 2];
                d_kernel[i][j] = kernelTile[i + 2][j + 2];
            }
        }
        for (int i = 0; i < 3; i++) {
            b_kernel[0][i] = 0;
            c_kernel[i][0] = 0;
            d_kernel[i][0] = 0;
            d_kernel[0][i] = 0;
        }

        WinogradConv winogradConv1 = new WinogradConv(a, a_kernel);
        WinogradConv winogradConv2 = new WinogradConv(b, b_kernel);
        WinogradConv winogradConv3 = new WinogradConv(c, c_kernel);
        WinogradConv winogradConv4 = new WinogradConv(d, d_kernel);
        winogradConv1.winogradCalc();
        winogradConv2.winogradCalc();
        winogradConv3.winogradCalc();
        winogradConv4.winogradCalc();
        double[][] aResult = winogradConv1.getOutputTile();
        double[][] bResult = winogradConv2.getOutputTile();
        double[][] cResult = winogradConv3.getOutputTile();
        double[][] dResult = winogradConv4.getOutputTile();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                outputTile[i][j] = aResult[i][j] + bResult[i][j] + cResult[i][j] + dResult[i][j];
                // outputTile[0][0] += aResult[i][j];
                // outputTile[0][1] += bResult[i][j];
                // outputTile[1][0] += cResult[i][j];
                // outputTile[1][1] += dResult[i][j];
            }
        }

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


    // return outputTile
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

    public static void main(String[] args) {
        int inputSize = 6;
        int kernelSize = 5;
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

        WinogradDeluxe winogradConv1 = new WinogradDeluxe(input, kernel);
        winogradConv1.winogradDeluxeCalc();
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
    }
}

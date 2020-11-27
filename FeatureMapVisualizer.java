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

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.io.IOException;

public class FeatureMapVisualizer {
    // delay in miliseconds (controls animation speed)
    private static final int DELAY = 100;

    // draw n-by-n input image
    public static void draw(String filename, int n) throws IOException {
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setXscale(-0.05 * n, 1.05 * n);
        StdDraw.setYscale(-0.05 * n, 1.05 * n);   // leave a 5% border
        StdDraw.filledSquare(n / 2.0, n / 2.0, n / 2.0);

        int[] image = new int[n * n];
        // DataInputStream input = new DataInputStream(new FileInputStream(
        //         filename));
        // for (int i = 0; i < n * n; i++)
        //     image[i] = 128 + input.readByte();
        // input.close();
        ConvLayer convLayer1 = new ConvLayer("conv1.input.dat", "conv1.dat", 1, 32, 32,
                                             32, 5, 2, 1, 7, 6, 7);
        double[][][] paddedInput1 = convLayer1.getPaddedInput();
        convLayer1.convCalc();
        double[][][] output = convLayer1.getOutput();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // image[i * n + j] = 64 + 2 * output[0][i][j];
                StdOut.print(paddedInput1[0][i][j]);
                StdOut.print("  ");
                image[i * n + j] = (int) (paddedInput1[0][i][j] * 256);
            }
            StdOut.println();
        }


        // draw n-by-n grid
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                Color color = new Color(image[row * n + col], image[row * n + col],
                                        image[row * n + col]);
                StdDraw.setPenColor(color);
                StdDraw.filledSquare(col + 0.5, n - row - 0.5, 0.45);
            }
        }

    }

    private static void simulateFromFile(String filename) throws IOException {
        // turn on animation mode
        StdDraw.enableDoubleBuffering();

        // repeatedly read in sites to open and draw resulting system
        draw(filename, 36);
        StdDraw.show();
        StdDraw.pause(DELAY);
    }

    public static void main(String[] args) throws IOException {
        String filename = args[0];
        simulateFromFile(filename);
    }
}

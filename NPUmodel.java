/* *****************************************************************************
 *  Name:    Yuxi Zheng & Chengyong Li
 *  Course: FPGA
 *
 *  Description:  Prints 'Hello, World' to the terminal window.
 *                By tradition, this is everyone's first program.
 *                Prof. Brian Kernighan initiated this tradition in 1974.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class NPUmodel {
    // private Byte[] readFile(String filename) {
    //     DataInputStream input = new DataInputStream(new FileInputStream(
    //             "conv1.input.dat"));
    //     int i = 0;
    //     while (input.available() > 0) {
    //         i++;
    //         StdOut.printf("%4d", input.readByte());
    //         if (i == 16) {
    //             StdOut.println();
    //             i = 0;
    //         }
    //     }
    // }

    public static void main(String[] args) throws IOException {
        // read in input image matrix (uncomment to see result)


        // reference answer for convolution layer 1
        DataInputStream imageFile = new DataInputStream(new FileInputStream(
                "conv1.output.dat"));
        int channelNum = 32;
        int inputHeight = 32;
        int inputWidth = 32;
        double[][][] image = new double[channelNum][inputHeight][inputWidth];


        // // reference answer for convolution layer 2
        // DataInputStream imageFile = new DataInputStream(new FileInputStream(
        //         "conv2.output.dat"));
        // int channelNum = 64;
        // int inputHeight = 16;
        // int inputWidth = 16;
        // double[][][] image = new double[channelNum][inputHeight][inputWidth];

        // // reference answer for convolution layer 3
        // DataInputStream imageFile = new DataInputStream(new FileInputStream(
        //         "conv3.output.dat"));
        // int channelNum = 64;
        // int inputHeight = 8;
        // int inputWidth = 8;
        // double[][][] image = new double[channelNum][inputHeight][inputWidth];

        // // reference answer for convolution layer 4
        // DataInputStream imageFile = new DataInputStream(new FileInputStream(
        //         "conv4.output.dat"));
        // int channelNum = 128;
        // int inputHeight = 4;
        // int inputWidth = 4;
        // double[][][] image = new double[channelNum][inputHeight][inputWidth];

        for (int i = 0; i < channelNum; i++)
            for (int j = 0; j < inputHeight; j++) {
                for (int k = 0; k < inputWidth; k++) {
                    image[i][j][k] = imageFile.readByte();
                }
            }
        imageFile.close();

        for (int i = 0; i < 1; i++)
            for (int j = 0; j < inputHeight; j++) {
                for (int k = 0; k < inputWidth; k++) {
                    StdOut.printf("%8.1f", image[i][j][k]);

                }
                StdOut.println();
            }

        // read in kernel matrix
        DataInputStream kernelFile = new DataInputStream(new FileInputStream(
                "conv1.dat"));
        int inputChannelNum = 1;
        int outputChannelNum = 32;
        int kernelSize = 5;
        double[][][][] kernel
                = new double[outputChannelNum][inputChannelNum][kernelSize][kernelSize];
        for (int i = 0; i < outputChannelNum; i++)
            for (int j = 0; j < inputChannelNum; j++) {
                for (int k = 0; k < kernelSize; k++) {
                    for (int l = 0; l < kernelSize; l++) {
                        kernel[i][j][k][l] = kernelFile.readByte();
                    }
                }
            }
        kernelFile.close();
    }
}

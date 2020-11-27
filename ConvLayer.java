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

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ConvLayer {
    // private int[][][] input; // channelNum*n*n input tile
    private double[][][] output; // m*m output tile
    private double[][][][] kernel; // r*r kernel matrix
    private double[][][] paddedInput;
    private int inputChannelNum;
    private int inputHeight;
    private int inputWidth;
    private int outputChannelNum;
    private int kernelSize;
    private int stride;
    private int outputSize;
    private int Qin;
    private int Qout;
    private int Qw;

    public ConvLayer(String inputFilename, String kernelFilename, int inputChannelNum,
                     int inputHeight, int inputWidth,
                     int outputChannelNum, int kernelSize, int padding, int stride, int Qin,
                     int Qout, int Qw) throws
                                       IOException {

        int[] inputFromFile = inputRead(inputFilename, inputChannelNum, inputHeight, inputWidth);
        paddedInput = new double[inputChannelNum][inputHeight + padding * 2][inputWidth
                + padding * 2];
        for (int i = 0; i < inputChannelNum; i++) {
            for (int j = 0; j < inputHeight; j++) {
                for (int k = 0; k < inputWidth; k++) {
                    paddedInput[i][j + padding][k + padding]
                            =
                            inputFromFile[i * inputHeight * inputWidth
                                    + j * inputWidth + k];
                }
            }
        }
        int[] kernelFromFile = kernelRead(kernelFilename, inputChannelNum, outputChannelNum,
                                          kernelSize);
        kernel = new double[outputChannelNum][inputChannelNum][kernelSize][kernelSize];
        for (int i = 0; i < outputChannelNum; i++)
            for (int j = 0; j < inputChannelNum; j++) {
                for (int k = 0; k < kernelSize; k++) {
                    for (int l = 0; l < kernelSize; l++) {
                        kernel[i][j][k][l]
                                =
                                kernelFromFile[i * inputChannelNum * kernelSize
                                        * kernelSize
                                        + j * kernelSize * kernelSize + k * kernelSize + l];
                    }
                }
            }

        outputSize = inputWidth + padding * 2 - kernelSize + 1;
        output = new double[outputChannelNum][outputSize][outputSize];
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.outputChannelNum = outputChannelNum;
        this.inputChannelNum = inputChannelNum;
        this.inputWidth = inputWidth;
        this.inputHeight = inputHeight;
        this.Qin = Qin;
        this.Qw = Qw;
        this.Qout = Qout;
    }


    public void convCalc() {
        int inputTileSize = kernelSize + stride;
        int inputTileRowNum = 16;
        int outputTileSize = 2;
        double[][] inputTile = new double[inputTileSize][inputTileSize];
        for (int outputChannel = 0; outputChannel < outputChannelNum; outputChannel++) {
            for (int inputChannel = 0; inputChannel < inputChannelNum; inputChannel++) {
                for (int inputTileRow = 0; inputTileRow < inputTileRowNum; inputTileRow++) {
                    for (int inputTileCol = 0; inputTileCol < inputTileRowNum; inputTileCol++) {
                        for (int i = 0; i < inputTileSize; i++) {
                            for (int j = 0; j < inputTileSize; j++) {
                                inputTile[i][j] = paddedInput[inputChannel][i
                                        + inputTileRow * 2][j
                                        + inputTileCol * 2];
                                // if (inputTile[i][j] != 0) {
                                //     int a = 1;
                                // }
                            }
                        }

                        WinogradDeluxe winogradDeluxe = new WinogradDeluxe(inputTile,
                                                                           kernel[outputChannel][inputChannel]);
                        winogradDeluxe.winogradDeluxeCalc();
                        double[][] outputTile = winogradDeluxe.getOutputTile();
                        output[outputChannel][outputTileSize * inputTileRow][outputTileSize
                                * inputTileCol] = Math.floor(outputTile[0][0] * Math.pow(2, -9));

                        output[outputChannel][1 + outputTileSize * inputTileRow][outputTileSize
                                * inputTileCol] = Math
                                .floor(outputTile[1][0] * Math.pow(2, -9));


                        output[outputChannel][outputTileSize * inputTileRow][1
                                + outputTileSize * inputTileCol] = Math
                                .floor(outputTile[0][1] * Math.pow(2, -9))
                        ;


                        output[outputChannel][1 + outputTileSize * inputTileRow][1
                                + outputTileSize * inputTileCol] = Math
                                .floor(outputTile[1][1] * Math.pow(2, -9))
                        ;


                    }
                }


            }
        }

    }

    public double[][][] getOutput() {
        return output;
    }

    public double[][][] getPaddedInput() {
        return paddedInput;
    }

    // private method that reads in input from a given file
    private int[] inputRead(String filename, int channelNum, int inputHeight, int inputWidth) throws
                                                                                              IOException {
        DataInputStream imageFile = new DataInputStream(new FileInputStream(
                filename));
        int[] image = new int[channelNum * inputHeight * inputWidth];

        for (int i = 0; i < channelNum * inputHeight * inputWidth; i++)
            image[i] = imageFile.readByte();
        imageFile.close();
        return image;
    }

    // private method that reads in kernel from a given file
    private int[] kernelRead(String filename, int inputChannelNum, int outputChannelNum,
                             int kernelSize) throws
                                             IOException {
        DataInputStream kernelFile = new DataInputStream(new FileInputStream(
                filename));
        int[] kernel = new int[outputChannelNum * inputChannelNum * kernelSize * kernelSize];
        for (int i = 0; i < outputChannelNum * inputChannelNum * kernelSize * kernelSize; i++) {
            kernel[i] = kernelFile.readByte();
        }
        return kernel;
    }

    public static void main(String[] args) throws IOException {
        ConvLayer convLayer1 = new ConvLayer("conv1.input.dat", "conv1.dat", 1, 32, 32,
                                             32, 5, 2, 1, 7, 5, 7);
        double[][][] paddedInput1 = convLayer1.getPaddedInput();
        convLayer1.convCalc();
        double[][][] output = convLayer1.getOutput();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < output[0].length; j++) {
                for (int k = 0; k < output[0][0].length; k++) {
                    StdOut.printf("%8.1f", output[i][j][k]);
                }
                StdOut.println();
            }
        }
    }
}

/* *****************************************************************************
 *  Name:    Yuxi Zheng & Yongcheng Li
 *
 *
 *  Description:  a universal convolution layer that can calculates the result
 * of convolution using any kernel with size equal to five or less. It also
 * gives an option of using winograd method for acceleration.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ConvLayer {
    private double[] output; // m*m output tile
    private double[] kernel; // r*r kernel matrix
    private double[] paddedInput; // input after padding
    private int paddedInputHeight; // height of the paddedInput
    private int inputChannelNum; // number of input channels
    private int outputChannelNum; // number of output channels
    private int kernelSize; // kernel size
    private int stride; // stride
    private int outputSize; // size of output matrix
    private int Qin; // quantization bits for input
    private int Qout; // quantization bits for output
    private int Qw; // quantizaqtion bits for weight
    private boolean useWinograd; // 1 if use winograd method, 0 if use standard convolution


    // constructor for one convolution layer, the parameters that needs to specify (in order) are
    // name of the input file, name of the kernel file, number of input channels, height of input,
    // width of input, number of output channels, kernel size, padding, stride, quantization bits of input,
    // quantization bits for output, quantization bits of weight, and whether to use winograd
    // method for acceleration
    public ConvLayer(String inputFilename, String kernelFilename, int inputChannelNum,
                     int inputHeight, int inputWidth,
                     int outputChannelNum, int kernelSize, int padding, int stride, int Qin,
                     int Qout, int Qw, boolean useWinograd) throws
                                                            IOException {
        // read inputs into a 1D array from file
        int[] inputFromFile = inputRead(inputFilename, inputChannelNum, inputHeight, inputWidth);
        // add padding of 0 to all four sides and store the new transformed matrix in a 1D array
        // named paddedInput
        paddedInputHeight = inputHeight + padding * 2;
        paddedInput = new double[inputChannelNum * paddedInputHeight * paddedInputHeight];
        for (int i = 0; i < inputChannelNum; i++) {
            for (int j = 0; j < inputHeight; j++) {
                for (int k = 0; k < inputWidth; k++) {
                    paddedInput[i * paddedInputHeight * paddedInputHeight
                            + (j + padding) * paddedInputHeight + k + padding]
                            =
                            inputFromFile[i * inputHeight * inputWidth
                                    + j * inputWidth + k];
                }
            }
        }

        // read in kernel matrix from file
        // kernel = new double[outputChannelNum*inputChannelNum*kernelSize*kernelSize];
        kernel = kernelRead(kernelFilename, inputChannelNum, outputChannelNum,
                            kernelSize);

        // initialize the output as a 1D array
        outputSize = (inputWidth + padding * 2 - kernelSize + 1) / stride;
        output = new double[outputChannelNum * outputSize * outputSize];

        this.kernelSize = kernelSize;
        this.stride = stride;
        this.outputChannelNum = outputChannelNum;
        this.inputChannelNum = inputChannelNum;
        this.Qin = Qin;
        this.Qw = Qw;
        this.Qout = Qout;
        this.useWinograd = useWinograd;
    }

    // a method that first splits input into small input tiles and perform convolution calculation
    // on them separately and later sums up different input channels and put the result into
    // the respective part in the ouput channels
    public void convCalc() {
        int inputTileSize = kernelSize + stride;
        int outputTileSize = 2;
        int inputTileRowNum = outputSize / outputTileSize;
        double[] inputTile = new double[inputTileSize * inputTileSize];
        double[] kernelTile = new double[kernelSize * kernelSize];
        for (int outputChannel = 0; outputChannel < outputChannelNum; outputChannel++) {
            // output tiles are summed over input Channel
            for (int inputChannel = 0; inputChannel < inputChannelNum; inputChannel++) {

                // when the output channel and the input channel is fixed, we will be
                // using the same kernel, so we can read in the kernel tile here once
                for (int i = 0; i < kernelSize; i++) {
                    for (int j = 0; j < kernelSize; j++) {
                        kernelTile[i * kernelSize + j] = kernel[
                                outputChannel * inputChannelNum * kernelSize * kernelSize
                                        + inputChannel * kernelSize * kernelSize + i * kernelSize
                                        + j];
                    }
                }

                // (inputTileRow, inputTileCol) means that we are at the inputTileRow-th input tile
                // when we are counting vertically, and we are at the inputTileCol-th input tile if
                // we are counting horizontally.
                for (int inputTileRow = 0; inputTileRow < inputTileRowNum; inputTileRow++) {
                    for (int inputTileCol = 0; inputTileCol < inputTileRowNum; inputTileCol++) {
                        // each time, we slide our input tile according to the stride
                        // so we read in the input tile from paddedInput from the corresponding
                        // index
                        for (int i = 0; i < inputTileSize; i++) {
                            for (int j = 0; j < inputTileSize; j++) {
                                inputTile[i * inputTileSize + j] = paddedInput[
                                        inputChannel * paddedInputHeight * paddedInputHeight + (i
                                                + inputTileRow * outputTileSize * stride)
                                                * paddedInputHeight + (j
                                                + inputTileCol * outputTileSize * stride)];
                            }
                        }
                        // calculate the 2-by-2 output tile using the current input tile and kernel tile
                        double[] outputTile = getOutputTile(inputTile,
                                                            kernelTile);

                        // put the output tile into the correct places in the output array
                        // for each output channel, we sum the output tiles that we got from each
                        // input channel with different kernel tiles
                        output[outputChannel * outputSize * outputSize
                                + (outputTileSize * inputTileRow) * outputSize + outputTileSize
                                * inputTileCol] += outputTile[0];

                        output[outputChannel * outputSize * outputSize
                                + outputTileSize * inputTileRow * outputSize + 1
                                + outputTileSize * inputTileCol] += outputTile[1];

                        output[outputChannel * outputSize * outputSize
                                + (1 + outputTileSize * inputTileRow) * outputSize + outputTileSize
                                * inputTileCol] += outputTile[2];

                        output[outputChannel * outputSize * outputSize
                                + (1 + outputTileSize * inputTileRow) * outputSize + 1
                                + outputTileSize * inputTileCol] += outputTile[3];

                    }
                }

            }
        }
        outputQuantize();
    }

    // private helper method that calculates the output tile from a small input tile
    // and a small kernel tile of various size with various stride and varies the
    // calculation method depending on kernelsize and boolean variable useWinograd
    private double[] getOutputTile(double[] inputTile, double[] kernelTile) {
        double[] outputTileOneDim = new double[4];
        double[][] transformedInputTile = arrayTransform(inputTile);
        double[][] transformedKernelTile = arrayTransform(kernelTile);
        double[][] outputTile;

        // use WinogradDeluxe class to calculate convolution if user specifies to
        // use winograd, kernel size is 5 and stride is 1
        if (useWinograd && kernelSize == 5 && stride == 1) {
            WinogradDeluxe winogradDeluxe = new WinogradDeluxe(transformedInputTile,
                                                               transformedKernelTile);
            winogradDeluxe.winogradDeluxeCalc();
            outputTile = winogradDeluxe.getOutputTile();
        }

        // use WinogradConv class to calculate convolution if user specifies to
        // use winograd, kernel size is 3 and stride is 1
        else if (useWinograd && kernelSize == 3 && stride == 1) {
            WinogradConv winogradConv = new WinogradConv(transformedInputTile,
                                                         transformedKernelTile);
            winogradConv.winogradCalc();
            outputTile = winogradConv.getOutputTile();
        }

        // otherwise, use the standard method for calculated convolution
        else {
            outputTile = standardConv(transformedInputTile,
                                      transformedKernelTile, stride);

        }
        outputTileOneDim[0] = outputTile[0][0];
        outputTileOneDim[1] = outputTile[0][1];
        outputTileOneDim[2] = outputTile[1][0];
        outputTileOneDim[3] = outputTile[1][1];
        return outputTileOneDim;
    }

    // standard convolution calculation for input and kernel of any size and for any integer stride
    public double[][] standardConv(double[][] inputRef, double[][] kernelRef, int strideRef) {
        int inputSizeRef = inputRef.length;
        int kernelSizeRef = kernelRef.length;
        int outputSizeRef = (inputSizeRef - kernelSizeRef) / strideRef + 1;
        double[][] outputRef = new double[outputSizeRef][outputSizeRef];
        for (int i = 0; i < outputSizeRef; i++)
            for (int j = 0; j < outputSizeRef; j++) {
                for (int s = 0; s < kernelSizeRef; s++)
                    for (int t = 0; t < kernelSizeRef; t++)
                        outputRef[i][j] += kernelRef[s][t]
                                * inputRef[i * strideRef + s][
                                j * strideRef + t];
            }
        return outputRef;
    }

    // private method that does quantization and takes the floor
    private void outputQuantize() {
        int quantizationStep = Qin + Qw - Qout;
        for (int outputChannel = 0; outputChannel < outputChannelNum; outputChannel++) {
            for (int row = 0; row < outputSize; row++) {
                for (int col = 0; col < outputSize; col++) {
                    int index = outputChannel * outputSize * outputSize + row * outputSize + col;
                    output[index] = Math.floor(output[index] * Math.pow(2, -quantizationStep));
                    // if the number is greater than 127 after quantization
                    // we make it 127
                    if (output[index] > 127)
                        output[index] = 127;
                        // if the number is less than -127 after quantization
                        // we make it -127
                    else if (output[index] < -127)
                        output[index] = -127;
                }
            }
        }
    }

    // method that returns the output
    public double[] getOutput() {
        return output;
    }

    // method that returns the padded input (for testing purposes)
    public double[] getPaddedInput() {
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
    private double[] kernelRead(String filename, int inputChannelNum, int outputChannelNum,
                                int kernelSize) throws
                                                IOException {
        DataInputStream kernelFile = new DataInputStream(new FileInputStream(
                filename));
        double[] kernelFromFile = new double[outputChannelNum * inputChannelNum * kernelSize
                * kernelSize];
        for (int i = 0; i < outputChannelNum * inputChannelNum * kernelSize * kernelSize; i++) {
            kernelFromFile[i] = kernelFile.readByte();
        }
        return kernelFromFile;
    }

    // private method that transforms 1D array into a square 2D array
    private double[][] arrayTransform(double[] inputArray) {
        int arraySize = (int) Math.sqrt(inputArray.length);
        double[][] outputArray = new double[arraySize][arraySize];
        for (int i = 0; i < arraySize; i++)
            for (int j = 0; j < arraySize; j++)
                outputArray[i][j] = inputArray[i * arraySize + j];
        return outputArray;
    }

    public static void main(String[] args) throws IOException {
        // public ConvLayer(String inputFilename, String kernelFilename, int inputChannelNum,
        // int inputHeight, int inputWidth,
        // int outputChannelNum, int kernelSize, int padding, int stride, int Qin,
        // int Qout, int Qw, boolean useWinograd)

        // test for the first convolution layer
        ConvLayer convLayer1 = new ConvLayer("conv1.input.dat", "conv1.dat", 1, 32, 32,
                                             32, 5, 2, 1, 7, 5, 7, false);
        convLayer1.convCalc();
        double[] output1 = convLayer1.getOutput();
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 32; j++) {
                for (int k = 0; k < 32; k++) {
                    StdOut.printf("%8.1f", output1[i * 32 * 32 + j * 32 + k]);
                }
                StdOut.println();
            }
        }

        // test for the second convolution layer
        ConvLayer convLayer2 = new ConvLayer("conv2.input.dat", "conv2.dat", 32, 16, 16,
                                             64, 3, 1, 1, 5, 5, 8, true);
        convLayer2.convCalc();
        double[] output2 = convLayer2.getOutput();
        // for (int i = 0; i < 1; i++) {
        //     for (int j = 0; j < 16; j++) {
        //         for (int k = 0; k < 16; k++) {
        //             StdOut.printf("%8.1f", output2[i * 16 + j * 16 + k]);
        //         }
        //         StdOut.println();
        //     }
        // }

        // test for the third convolution layer
        ConvLayer convLayer3 = new ConvLayer("conv3.input.dat", "conv3.dat", 64, 16, 16,
                                             64, 3, 1, 2, 5, 5, 8, false);
        convLayer3.convCalc();
        double[] output3 = convLayer3.getOutput();
        // for (int i = 0; i < 1; i++) {
        //     for (int j = 0; j < 8; j++) {
        //         for (int k = 0; k < 8; k++) {
        //             StdOut.printf("%8.1f", output3[i * 8 * 8 + j * 8 + k]);
        //         }
        //         StdOut.println();
        //     }
        // }

        // test for the fourth convolution layer
        ConvLayer convLayer4 = new ConvLayer("conv4.input.dat", "conv4.dat", 64, 8, 8,
                                             128, 3, 1, 2, 5, 5, 8, false);
        convLayer4.convCalc();
        double[] output4 = convLayer4.getOutput();
        // for (int i = 0; i < 1; i++) {
        //     for (int j = 0; j < 4; j++) {
        //         for (int k = 0; k < 4; k++) {
        //             StdOut.printf("%8.1f", output4[i * 4 * 4 + j * 4 + k]);
        //         }
        //         StdOut.println();
        //     }
        // }
    }
}

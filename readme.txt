This model is an MNIST-based (10 categories) simple DNN with all the data quantized in linear 8bit with 2-Q scaling. 
The inputs of the model are normalized to 0~1 (so QIN of the 1st layer is 7). The final results of the model are 10 
bytes (i.e. the outputs of the FC layer, no need to consider the Softmax function). The amount of all the weights in
this model is only 128KB, and you can store them all in the BRAM. For convenience, bias and BN function are not used.
Therefore, it is a very simple linear calculation for you to implement the DNN accelerator. The quantization method 
for the next layer is floor(x) (not round or ceil).

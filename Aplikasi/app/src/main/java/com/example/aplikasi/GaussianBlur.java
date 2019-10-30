package com.example.aplikasi;

import android.graphics.Bitmap;

public class GaussianBlur {

    //gaussian blur code here
    /*
    “Gaussian Blur” is very famous algorithm for blurry effect.
    It uses the concepts of Convolution Filtering.
    You might need to refer to my previous article on Convolution Matrix.
    The matrix used in Gaussian Blur is
    [ 1 - 2 - 1][ 2 - 4 - 2][ 1 - 2 - 1]The factor is 16 and with offset 0.
    * */
    public static Bitmap doGaussian(Bitmap img, int factor, int offset) {
        //set gaussian blur configuration
        double[][] GaussianBlurConfig = new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        // create instance of Convolution matrix
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        // Apply Configuration
        convMatrix.applyConfig(GaussianBlurConfig);
        convMatrix.Factor = factor;
        convMatrix.Offset = offset;
        //return out put bitmap
        return ConvolutionMatrix.computeConvolution3x3(img, convMatrix);
    }
}

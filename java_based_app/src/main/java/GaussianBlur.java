
import java.awt.image.BufferedImage;

public class GaussianBlur{

    BufferedImage src;

    GaussianBlur(BufferedImage bmp){
        this.src = bmp;
    }

    public BufferedImage doGaussianBlur(int factor, int offset){
        double[][] gaussianBlurConfig = new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);

        convMatrix.applyConfig(gaussianBlurConfig);
        convMatrix.Factor = factor;
        convMatrix.Offset = offset;
        return ConvolutionMatrix.computeConvolution3x3(this.src, convMatrix);

    }
}

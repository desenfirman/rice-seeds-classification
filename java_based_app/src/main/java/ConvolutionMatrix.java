
import java.awt.*;
import java.awt.image.BufferedImage;

public class ConvolutionMatrix {
    public static final int SIZE = 3;

    public double[][] Matrix;
    public double Factor = 1;
    public double Offset = 1;

    BufferedImage result;

    //Constructor with argument of size
    public ConvolutionMatrix(int size) {
        Matrix = new double[size][size];
    }

    public void setAll(double value) {
        for (int x = 0; x < SIZE; ++x) {
            for (int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = value;
            }
        }
    }

    public void applyConfig(double[][] config) {
        for(int x = 0; x < SIZE; ++x) {
            for(int y = 0; y < SIZE; ++y) {
                Matrix[x][y] = config[x][y];
            }
        }
    }

    public static BufferedImage computeConvolution3x3(BufferedImage src, ConvolutionMatrix matrix) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        int A, R, G, B;
        int sumR, sumG, sumB;
        Color[][] pixels = new Color[SIZE][SIZE];

        for(int y = 0; y < height - 2; ++y) {
            for(int x = 0; x < width - 2; ++x) {

                // get pixel matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        pixels[i][j] = new Color(src.getRGB(x + i, y + j));
                    }
                }

                // get alpha of center pixel
//                A = Color.alpha(pixels[1][1]);

                // init color sum
                sumR = sumG = sumB = 0;

                // get sum of RGB on matrix
                for(int i = 0; i < SIZE; ++i) {
                    for(int j = 0; j < SIZE; ++j) {
                        sumR += (pixels[i][j].getRed() * matrix.Matrix[i][j]);
                        sumG += (pixels[i][j].getGreen() * matrix.Matrix[i][j]);
                        sumB += (pixels[i][j].getBlue() * matrix.Matrix[i][j]);
                    }
                }

                // get final Red
                R = (int)(sumR / matrix.Factor + matrix.Offset);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                // get final Green
                G = (int)(sumG / matrix.Factor + matrix.Offset);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                // get final Blue
                B = (int)(sumB / matrix.Factor + matrix.Offset);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // apply new pixel
//                result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
                Color new_color = new Color(R, G, B);
                result.setRGB(x+1, y+1, new_color.getRGB());
            }
        }

        // final image
        return result;
    }
}

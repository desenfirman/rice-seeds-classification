
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class Grayscale {
    public static BufferedImage grayScaleImage(BufferedImage src) {
        // constant factors
        final double GS_RED = 0.299 + .3;
        final double GS_GREEN = 0.587 - .5;
        final double GS_BLUE = 0.114 + .2;

        // create output bitmap
//        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        BufferedImage bmOut = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        // pixel information
        int A, R, G, B;
        Color pixel;

        // get image size
        int width = src.getWidth();
        int height = src.getHeight();

        // scan through every single pixel
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get one pixel color
//                pixel = src.getPixel(x, y);
                pixel = new Color(src.getRGB(x, y));
                // retrieve color of all channels
                A = pixel.getAlpha();
                R = pixel.getRed();
                G = pixel.getGreen();
                B = pixel.getBlue();
                // take conversion up to one single value
                R = G = B = (int) (GS_RED * R + GS_GREEN * G + GS_BLUE * B);
                // set new pixel color to output bitmap
                Color new_pixel = new Color(R, G, B);
                bmOut.setRGB(x, y, new_pixel.getRGB());
            }
        }

        // return final image
        return bmOut;
    }
}

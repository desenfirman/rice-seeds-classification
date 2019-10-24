import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageObj {
    private String path;
    private File img_file_handler;
    private BufferedImage bmp;

    private BufferedImage processed_bmp;

    public ImageObj(String path){
        this.path = path;

        this.img_file_handler = new File(this.path);


        this.bmp = null;
        try {
            this.bmp = ImageIO.read(img_file_handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage resizeImage(BufferedImage img, int width, int height){
        Image img_resized = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(width, height, img.getType());

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img_resized, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public Map<String, Integer> getObjectDimension(){
        int Xmax = Integer.MIN_VALUE;
        int Xmin = Integer.MAX_VALUE;
        int Ymax = Integer.MIN_VALUE;
        int Ymin = Integer.MAX_VALUE;


        bmp = bmp.getSubimage(bmp.getWidth() * 1/5, bmp.getHeight() * 1/3,  bmp.getWidth() * 3/5, bmp.getHeight() / 3);
        bmp = resizeImage(bmp, (int) (bmp.getWidth() * 0.25), (int) (bmp.getHeight() * 0.25));
        bmp = Grayscale.grayScaleImage(bmp);
        bmp = (new GaussianBlur(bmp)).doGaussianBlur(16, 10);
        bmp =  Dilation.grayscaleImage(bmp);
        bmp = CannyEdgeDetector.process(bmp, 50);
        bmp =  Dilation.binaryImage(bmp, true);
        bmp = Erosion.binaryImage(bmp, false);
        bmp =  Dilation.grayscaleImage(bmp);


        bmp = bmp.getSubimage(bmp.getWidth() * 1/7, bmp.getHeight() * 1/7,  bmp.getWidth() * 4/7, bmp.getHeight() * 5/7);

        this.processed_bmp = bmp;

        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                boolean isWhitePixel = (bmp.getRGB(x, y) == 0xFFFFFFFF);
                if (isWhitePixel){
                    Xmax = (x > Xmax) ? x : Xmax;
                    Xmin = (x < Xmin) ? x : Xmin;

                    Ymax = (y > Ymax) ? y : Ymax;
                    Ymin = (y < Ymin) ? y : Ymin;
                }
            }
        }


        int width = Xmax - Xmin;
        int height = Ymax - Ymin;

        if (height > width){
            int temp = height;
            height = width;
            width = temp;
        }

        Map<String, Integer> w_h = new HashMap<String, Integer>();
        w_h.put("width", width);
        w_h.put("height", height);

        return w_h;
    }

    public static void main(String[] args) {
        ImageObj imageObj = new ImageObj("in/ciherang (1).jpg");

    }
}

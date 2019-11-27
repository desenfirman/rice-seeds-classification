import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ImageObj {
    private String path;
    private File img_file_handler;
    private BufferedImage bmp;
    private Double gblur_kernel_size, canny_threshold, canny_range, dilate_size, erode_size;

    private BufferedImage processed_bmp;

    public ImageObj(){
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    public ImageObj(String path){
        this();
        this.path = path;

        this.img_file_handler = new File(this.path);
        this.gblur_kernel_size = this.canny_range = this.canny_threshold = this.dilate_size = this.erode_size = 0.0;


        this.bmp = null;
        try {
            this.bmp = ImageIO.read(img_file_handler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setGblur_kernel_size(Double gblur_kernel_size) {
        this.gblur_kernel_size = gblur_kernel_size;
    }

    public void setCanny_threshold(Double canny_threshold) {
        this.canny_threshold = canny_threshold;
    }

    public void setCanny_range(Double canny_range) {
        this.canny_range = canny_range;
    }

    public void setDilate_size(Double dilate_size) {
        this.dilate_size = dilate_size;
    }

    public void setErode_size(Double erode_size) {
        this.erode_size = erode_size;
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


    public void exportConfiguration(String path){
        HashMap<String, Double> configs = new HashMap<String, Double>();
        configs.put("gblur_kernel_size", gblur_kernel_size);
        configs.put("canny_threshold", canny_threshold);
        configs.put("canny_range", canny_range);
        configs.put("dilate_size", dilate_size);
        configs.put("erode_size", erode_size);

        Gson gson = new Gson();
        String jsonString = gson.toJson(configs);
        try(FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public Map<String, Integer> getObjectDimension() {
        int Xmax = Integer.MIN_VALUE;
        int Xmin = Integer.MAX_VALUE;
        int Ymax = Integer.MIN_VALUE;
        int Ymin = Integer.MAX_VALUE;

        bmp = bmp.getSubimage(bmp.getWidth() * 1 / 5, bmp.getHeight() * 1 / 3, bmp.getWidth() * 3 / 5, bmp.getHeight() / 3);
        bmp = resizeImage(bmp, (int) (bmp.getWidth() * 0.25), (int) (bmp.getHeight() * 0.25));

        Mat bmpMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bmp.getRaster().getDataBuffer()).getData();
        bmpMat.put(0,0, data);


        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();

        Imgproc.cvtColor(bmpMat, grayImage, Imgproc.COLOR_BGR2GRAY);

        // reduce noise with a input kernel size
        Imgproc.blur(grayImage, detectedEdges, new Size(this.gblur_kernel_size, this.gblur_kernel_size));

        // canny detector, with ratio of lower threshold & upper threshold is canny_range times ratio
        Imgproc.Canny(detectedEdges, detectedEdges, this.canny_threshold, this.canny_threshold * this.canny_range);

        Mat dilate_element = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new  Size(this.dilate_size, this.dilate_size));
        Imgproc.dilate(detectedEdges, detectedEdges, dilate_element);

        Mat erode_element = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new  Size(this.erode_size, this.erode_size));
        Imgproc.erode(detectedEdges, detectedEdges, erode_element);

        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", detectedEdges, mob);
        byte[] return_buff = mob.toArray();


        try {
            bmp = ImageIO.read(new ByteArrayInputStream(return_buff));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(bmp.toString());


        this.processed_bmp = bmp;

        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                boolean isWhitePixel = (bmp.getRGB(x, y) == 0xFFFFFFFF);
                if (isWhitePixel) {
                    Xmax = (x > Xmax) ? x : Xmax;
                    Xmin = (x < Xmin) ? x : Xmin;

                    Ymax = (y > Ymax) ? y : Ymax;
                    Ymin = (y < Ymin) ? y : Ymin;
                }
            }
        }


        int width = Xmax - Xmin;
        int height = Ymax - Ymin;

        if (height > width) {
            int temp = height;
            height = width;
            width = temp;
        }

        Map<String, Integer> w_h = new HashMap<String, Integer>();
        w_h.put("width", width);
        w_h.put("height", height);

        return w_h;
    }

    public BufferedImage getBmp() {
        return bmp;
    }
}

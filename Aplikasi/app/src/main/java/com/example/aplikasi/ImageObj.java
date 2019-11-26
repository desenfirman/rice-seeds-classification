package com.example.aplikasi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;
import java.util.Map;

public class ImageObj {
    private Uri uri;
    private Bitmap bmp;
    private Double gblur_kernel_size, canny_threshold, canny_range, dilate_size, erode_size;

    public ImageObj(){
        if (!OpenCVLoader.initDebug())
            Log.e("OpenCv", "Unable to load OpenCV");
        else
            Log.d("OpenCv", "OpenCV loaded");
    }

    public ImageObj(Bitmap bmp){
        this();
        this.uri = uri;

        this.gblur_kernel_size = this.canny_range = this.canny_threshold = this.dilate_size = this.erode_size = 0.0;
        this.bmp = bmp;
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


    public Map<String, Integer> getObjectDimension() {
        int Xmax = Integer.MIN_VALUE;
        int Xmin = Integer.MAX_VALUE;
        int Ymax = Integer.MIN_VALUE;
        int Ymin = Integer.MAX_VALUE;
        bmp = Bitmap.createBitmap(bmp, bmp.getWidth() * 1 / 5, bmp.getHeight() * 1 / 3, bmp.getWidth() * 3 / 5, bmp.getHeight() / 3);
        bmp = Bitmap.createScaledBitmap(bmp, ((int) (bmp.getWidth() * 0.25)), ((int) (bmp.getHeight() * 0.25)), false);

        Bitmap bmp32 = bmp.copy(Bitmap.Config.RGB_565, true);
        Mat bmpMat = new Mat();
        Utils.bitmapToMat(bmp32, bmpMat);


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
        Utils.matToBitmap(detectedEdges, bmp);


        int count = 0;
        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y < bmp.getHeight(); y++) {
                boolean isWhitePixel = ( bmp.getPixel(x, y) == Color.WHITE);
                if (isWhitePixel) {
                    Xmax = (x > Xmax) ? x : Xmax;
                    Xmin = (x < Xmin) ? x : Xmin;

                    Ymax = (y > Ymax) ? y : Ymax;
                    Ymin = (y < Ymin) ? y : Ymin;
                    Log.d("Info", String.valueOf(++count));
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

    public Bitmap getBmp() {
        return bmp;
    }
}

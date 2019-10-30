package com.example.aplikasi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageObj {
    private Uri uri;
    private File img_file_handler;
    private Bitmap bmp;
    private int gaussFactor, gaussOffset, cannyThreshold;

    private Image processed_bmp;

    public ImageObj(){

    }

    public ImageObj(Bitmap bmp){
        this.uri = uri;

        this.gaussFactor = 0;
        this.gaussOffset = 0;
        this.cannyThreshold = 0;

        this.bmp = bmp;
    }

    public static Bitmap resizeImage(Bitmap img, int width, int height){
        return Bitmap.createScaledBitmap(img, width, height, false);
    }



    public void setGaussFactor(int gaussFactor) {
        this.gaussFactor = gaussFactor;
    }

    public void setGaussOffset(int gaussOffset) {
        this.gaussOffset = gaussOffset;
    }

    public void setCannyThreshold(int cannyThreshold) {
        this.cannyThreshold = cannyThreshold;
    }

    public void importConfiguration(BufferedReader reader){
        String line = "";
        StringBuilder builder = new StringBuilder();
        while (true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.append(line);
        }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        String jsonString = builder.toString();

        HashMap<String, Double> configs;
        Gson gsonBuilder = new GsonBuilder().create();
        configs = gsonBuilder.fromJson(jsonString, HashMap.class);

        setGaussFactor(configs.get("gaussFactor").intValue());
        setGaussOffset(configs.get("gaussOffset").intValue());
        setCannyThreshold(configs.get("cannyThreshold").intValue());
    }

    public void exportConfiguration(String path){
        HashMap<String, Integer> configs = new HashMap<String, Integer>();
        configs.put("gaussFactor", gaussFactor);
        configs.put("gaussOffset", gaussOffset);
        configs.put("cannyThreshold", cannyThreshold);
        Gson gson = new Gson();
        String jsonString = gson.toJson(configs);
//        try(FileWriter fileWriter = new FileWriter(path)) {
//            fileWriter.write(jsonString);
//        } catch (IOException e) {
//            System.out.println(e.getLocalizedMessage());
//        }
    }

    public Map<String, Integer> getObjectDimension() {
        int Xmax = Integer.MIN_VALUE;
        int Xmin = Integer.MAX_VALUE;
        int Ymax = Integer.MIN_VALUE;
        int Ymin = Integer.MAX_VALUE;


        bmp = Bitmap.createBitmap(bmp, bmp.getWidth() * 1 / 5, bmp.getHeight() * 1 / 3, bmp.getWidth() * 3 / 5, bmp.getHeight() / 3);
        bmp = Bitmap.createScaledBitmap(bmp, ((int) (bmp.getWidth() * 0.25)), ((int) (bmp.getHeight() * 0.25)), false);
        bmp = (new GaussianBlur(bmp)).doGaussianBlur(gaussFactor, gaussOffset);
        bmp = CannyEdgeDetector.process(bmp, cannyThreshold);
        bmp = Dilation.binaryImage(bmp, false);
        bmp = Erosion.binaryImage(bmp, true);


        bmp = Bitmap.createBitmap(bmp, bmp.getWidth() * 1 / 7, bmp.getHeight() * 1 / 7, bmp.getWidth() * 4 / 7, bmp.getHeight() * 5 / 7);

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
}

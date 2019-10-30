package com.example.aplikasi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Dilation {
    private  static  int width, height;
    private  static Bitmap src;

    public static Bitmap binaryImage(Bitmap img, boolean dilateBackgroundPixel){
        /**
         * Dimension of the image img.
         */
        int width = img.getWidth();
        int height = img.getHeight();

        /**
         * This will hold the dilation result which will be copied to image img.
         */
        int output[] = new int[width * height];

        /**
         * If dilation is to be performed on BLACK pixels then
         * targetValue = 0
         * else
         * targetValue = 255;  //for WHITE pixels
         */
        int targetValue = (dilateBackgroundPixel == true)?0:255;

        /**
         * If the target pixel value is WHITE (255) then the reverse pixel value will
         * be BLACK (0) and vice-versa.
         */
        int reverseValue = (targetValue == 255)?0:255;

        //perform dilation
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                //For BLACK pixel RGB all are set to 0 and for WHITE pixel all are set to 255.
                int red = Color.red(img.getPixel(x, y));
                if(red == targetValue){
                    /**
                     * We are using a 3x3 kernel
                     * [1, 1, 1
                     *  1, 1, 1
                     *  1, 1, 1]
                     */
                    boolean flag = false;   //this will be set if a pixel of reverse value is found in the mask
                    for(int ty = y - 1; ty <= y + 1 && flag == false; ty++){
                        for(int tx = x - 1; tx <= x + 1 && flag == false; tx++){
                            if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                                //origin of the mask is on the image pixels
                                if(Color.red(img.getPixel(tx, ty)) != targetValue){
                                    flag = true;
                                    output[x+y*width] = reverseValue;
                                }
                            }
                        }
                    }
                    if(flag == false){
                        //all pixels inside the mask [i.e., kernel] were of targetValue
                        output[x+y*width] = targetValue;
                    }
                }else{
                    output[x+y*width] = reverseValue;
                }
            }
        }

        /**
         * Save the dilation value in image img.
         */
        Bitmap returnIMG = Bitmap.createBitmap(img);
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];
//                int pix = returnIMG.getPixel(x, y);
                returnIMG.setPixel(x, y, Color.rgb(v, v, v));
            }
        }
        Log.d("PanjangLebar", "Panjang: " + returnIMG.getWidth() + " Lebar: " + returnIMG.getHeight());
        return returnIMG;
    }

    /**
     * This method will perform dilation operation on the grayscale image img.
     *
     * @param img The image on which dilation operation is performed
     */
    public static Bitmap grayscaleImage(Bitmap img){
        /**
         * Dimension of the image img.
         */
        int width = img.getWidth();
        int height = img.getHeight();

        //buff
        int buff[];

        //output of dilation
        int output[] = new int[width*height];

        //perform dilation
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                buff = new int[9];
                int i = 0;
                for(int ty = y - 1; ty <= y + 1; ty++){
                    for(int tx = x - 1; tx <= x + 1; tx++){
                        if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                            //pixel under the mask
                            buff[i] = Color.red(img.getPixel(tx, ty));
                            i++;
                        }
                    }
                }

                //sort buff
                java.util.Arrays.sort(buff);

                //save highest value
                output[x+y*width] = buff[8];
            }
        }

        /**
         * Save the erosion value in image img.
         */
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];
                img.setPixel(x, y, Color.rgb(v, v, v));
            }
        }
        return img;
    }


    public static Bitmap alldilate(Bitmap bmp){
        bmp = grayscaleImage(bmp);
        bmp = binaryImage(bmp,true);
        return bmp;
    }

}//class ends here


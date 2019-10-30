package com.example.aplikasi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

public class PanjangLebar {
    int Xmin, Xmax, Ymin, Ymax;
    int w;
    int h;
    private Intent PanjangIntent;
    private static boolean BackgroundPixel;

    public void Dimensions (Bitmap src){
        try {

            w = src.getWidth();
            h = src.getHeight();
            Log.d("PanjangLebar", "w="+w+" - h="+h);

            int Xmax = Integer.MIN_VALUE;
            int Xmin = Integer.MAX_VALUE;
            int Ymax = Integer.MIN_VALUE;
            int Ymin = Integer.MAX_VALUE;


            /**
             * If dilation is to be performed on BLACK pixels then
             * targetValue = 0
             * else
             * targetValue = 255;  //for WHITE pixels
             */
            //int targetValue = (BackgroundPixel == true) ? 0 : 255;


            /** performing thresholding on the image pixels */
//            byte[][] pixels = new byte[src.getWidth()][];
            int count = 0;
            for (int x = 0; x < src.getWidth(); x++) {
//                pixels[x] = new byte[src.getHeight()];

                for (int y = 0; y < src.getHeight(); y++) {
                    int temp = (src.getPixel(x, y) == 0xFFFFFFFF ? 1 : 0);
                    Log.d("PanjangLebar", "X ="+x+", Y = "+y+", Nilai pixel = "+temp);
                    if (temp == 1) {
                        count++;
                        if(x > Xmax) {
                            Xmax = x;
                        }
                        if (x < Xmin) {
                            Xmin  = x;
                        }
                        if(y > Ymax){
                            Ymax = y;
                        }
                        if (y < Ymin){
                            Ymin  = y;
                        }
                    }
                }
            }
            Log.d("PanjangLebar", "jumlah pixel putih: "+count);
            this.Xmax = Xmax;
            this.Xmin = Xmin;
            this.Ymax = Ymax;
            this.Ymin = Ymin;
        }
        catch(Exception e){
            System.out.println("File tidak ada");
        }
    }

    public int hasilPanjang() {
        Log.d("PanjangLebar", "xmax = "+Xmax+", xmin = "+Xmin);
        int Panjang = Xmax - Xmin;
        return Panjang;
    }

    public float hasilLebar() {
        Log.d("PanjangLebar", "ymax = "+Ymax+", ymin = "+Ymin);
        return Ymax - Ymin;
    }
}


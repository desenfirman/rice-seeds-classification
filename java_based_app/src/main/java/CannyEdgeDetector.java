

//import android.graphics.Bitmap;
//import android.graphics.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class CannyEdgeDetector {
    private static int width, height;
    private static BufferedImage src, n;

    public static BufferedImage process(BufferedImage bmp, int threshold) {
        src = bmp;
        width = src.getWidth();
        height = src.getHeight();

//        n = Bitmap.createBitmap(src,0,0,width,height);
//        hitungThreshold();

        int[][] allPixR = new int[width][height];
        int[][] allPixG = new int[width][height];
        int[][] allPixB = new int[width][height];

        for ( int i = 0; i < src.getWidth(); i++ )
        {
            for ( int j = 0; j < src.getHeight(); j++ )
            {
                Color pixel = new Color(src.getRGB(i, j));
                allPixR[i][ j] = pixel.getRed();
                allPixG[i][ j] = pixel.getGreen();
                allPixB[i][ j] = pixel.getBlue();
            }
        }

        n = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);

        for ( int i = 2; i < width - 2; i++ )
        {
            for ( int j = 2; j < height - 2; j++ )
            {
                int red = (
                        ( ( allPixR [i - 2][ j - 2] )  + ( allPixR [i - 1][ j - 2] ) * 4 + ( allPixR [i][ j - 2] ) * 7 + ( allPixR [i + 1][ j - 2] ) * 4 + ( allPixR [i + 2][ j - 2] )
                                + ( allPixR [i - 2][ j - 1] ) * 4 + ( allPixR [i - 1][ j - 1] ) * 16 + ( allPixR [i][ j - 1] ) * 26 + ( allPixR [i + 1][ j - 1] ) * 16 + ( allPixR [i + 2][ j - 1] ) * 4
                                + ( allPixR [i - 2][ j] ) * 7 + ( allPixR [i - 1][ j] ) * 26 + ( allPixR [i][ j] ) * 41 + ( allPixR [i + 1][ j] ) * 26 + ( allPixR [i + 2][ j] ) * 7
                                + ( allPixR [i - 2][ j + 1] ) * 4 + ( allPixR [i - 1][ j + 1] ) * 16 + ( allPixR [i][ j + 1] ) * 26 + ( allPixR [i + 1][ j + 1] ) * 16 + ( allPixR [i + 2][ j + 1] ) * 4
                                + ( allPixR [i - 2][ j + 2] )  + ( allPixR [i - 1][ j + 2] ) * 4 + ( allPixR [i][ j + 2] ) * 7 + ( allPixR [i + 1][ j + 2] ) * 4 + ( allPixR [i + 2][ j + 2] )  ) / 273
                );

                int green = (
                        ( ( allPixG[i - 2][ j - 2] )  + ( allPixG[i - 1][ j - 2] ) * 4 + ( allPixG[i][ j - 2] ) * 7 + ( allPixG[i + 1][ j - 2] ) * 4 + ( allPixG[i + 2][ j - 2] )
                                + ( allPixG[i - 2][ j - 1] ) * 4 + ( allPixG[i - 1][ j - 1] ) * 16 + ( allPixG[i][ j - 1] ) * 26 + ( allPixG[i + 1][ j - 1] ) * 16 + ( allPixG[i + 2][ j - 1] ) * 4
                                + ( allPixG[i - 2][ j] ) * 7 + ( allPixG[i - 1][ j] ) * 26 + ( allPixG[i][ j] ) * 41 + ( allPixG[i + 1][ j] ) * 26 + ( allPixG[i + 2][ j] ) * 7
                                + ( allPixG[i - 2][ j + 1] ) * 4 + ( allPixG[i - 1][ j + 1] ) * 16 + ( allPixG[i][ j + 1] ) * 26 + ( allPixG[i + 1][ j + 1] ) * 16 + ( allPixG[i + 2][ j + 1] ) * 4
                                + ( allPixG[i - 2][ j + 2] )  + ( allPixG[i - 1][ j + 2] ) * 4 + ( allPixG[i][ j + 2] ) * 7 + ( allPixG[i + 1][ j + 2] ) * 4 + ( allPixG[i + 2][ j + 2] )  ) / 273
                );

                int blue = (
                        ( ( allPixB[i - 2][ j - 2] )  + ( allPixB[i - 1][ j - 2] ) * 4 + ( allPixB[i][ j - 2] ) * 7 + ( allPixB[i + 1][ j - 2] ) * 4 + ( allPixB[i + 2][ j - 2] )
                                + ( allPixB[i - 2][ j - 1] ) * 4 + ( allPixB[i - 1][ j - 1] ) * 16 + ( allPixB[i][ j - 1] ) * 26 + ( allPixB[i + 1][ j - 1] ) * 16 + ( allPixB[i + 2][ j - 1] ) * 4
                                + ( allPixB[i - 2][ j] ) * 7 + ( allPixB[i - 1][ j] ) * 26 + ( allPixB[i][ j] ) * 41 + ( allPixB[i + 1][ j] ) * 26 + ( allPixB[i + 2][ j] ) * 7
                                + ( allPixB[i - 2][ j + 1] ) * 4 + ( allPixB[i - 1][ j + 1] ) * 16 + ( allPixB[i][ j + 1] ) * 26 + ( allPixB[i + 1][ j + 1] ) * 16 + ( allPixB[i + 2][ j + 1] ) * 4
                                + ( allPixB[i - 2][ j + 2] )  + ( allPixB[i - 1][ j + 2] ) * 4 + ( allPixB[i][ j + 2] ) * 7 + ( allPixB[i + 1][ j + 2] ) * 4 + ( allPixB[i + 2][ j + 2] ) ) / 273
                );
                Color new_color = new Color(red, green, blue);
                n.setRGB(i, j, new_color.getRGB());
            }
        }

        int[][] allPixRn = new int[width][height];
        int[][] allPixGn = new int[width][height];
        int[][] allPixBn = new int[width][height];

        for ( int i = 0; i < width; i++ )
        {
            for ( int j = 0; j < height; j++ )
            {
//                int pixi2 = n.getPixel(i,j);
                Color pixi2 = new Color(n.getRGB(i, j));
                allPixRn[i][ j] = pixi2.getRed();
                allPixGn[i][ j] = pixi2.getGreen();
                allPixBn[i][ j] = pixi2.getBlue();
            }
        }


        int[][] gx = new int[][] { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int[][] gy = new int[][] { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } };
        int new_rx = 0, new_ry = 0;
        int new_gx = 0, new_gy = 0;
        int new_bx = 0, new_by = 0;
        int rc, gc, bc;
        int gradR, gradG, gradB;

        int[][] graidientR = new int [width][ height];
        int[][] graidientG = new int [width][ height];
        int[][] graidientB = new int [width][ height];

        int atanR,atanG,atanB;

        int[][] tanR = new int[width][ height];
        int[][] tanG = new int[width][ height];
        int[][] tanB = new int[width][ height];


        for ( int i = 1; i < src.getWidth() - 1; i++ )
        {
            for ( int j = 1; j < src.getHeight() - 1; j++ )
            {

                new_rx = 0;
                new_ry = 0;
                new_gx = 0;
                new_gy = 0;
                new_bx = 0;
                new_by = 0;
                rc = 0;
                gc = 0;
                bc = 0;

                for ( int wi = -1; wi < 2; wi++ )
                {
                    for ( int hw = -1; hw < 2; hw++ )
                    {
                        rc = allPixRn[i + hw ][j + wi];
                        new_rx += gx[wi + 1][ hw + 1] * rc;
                        new_ry += gy[wi + 1][ hw + 1] * rc;

                        gc = allPixGn[i + hw][ j + wi];
                        new_gx += gx[wi + 1][ hw + 1] * gc;
                        new_gy += gy[wi + 1][ hw + 1] * gc;

                        bc = allPixBn[i + hw][ j + wi];
                        new_bx += gx[wi + 1][ hw + 1] * bc;
                        new_by += gy[wi + 1][ hw + 1] * bc;
                    }
                }

                gradR = (int)Math.sqrt (( new_rx * new_rx ) + ( new_ry * new_ry ));
                graidientR [i][j]= gradR;

                gradG = (int)Math.sqrt (( new_gx * new_gx ) + ( new_gy * new_gy ));
                graidientG [i][j]= gradG;

                gradB = (int)Math.sqrt (( new_bx * new_gx ) + ( new_by * new_by ));
                graidientB [i][j]= gradB;


                atanR= (int)( ( Math.atan ((double)new_ry / new_rx) ) * ( 180 / Math.PI ) );
                if ( (atanR > 0 && atanR < 22.5) || (atanR > 157.5 && atanR < 180) )
                {
                    atanR = 0;
                }
                else if (atanR > 22.5 && atanR < 67.5){
                    atanR = 45;
                }
                else if (atanR > 67.5 && atanR < 112.5){
                    atanR = 90;
                }
                else if (atanR > 112.5 && atanR < 157.5) {
                    atanR = 135;
                }

                if ( atanR == 0 )
                {
                    tanR[i][ j] = 0;
                }
                else if ( atanR == 45 )
                {
                    tanR[i][ j] = 1;
                }
                else if ( atanR == 90 )
                {
                    tanR[i][ j] = 2;
                }
                else if ( atanR == 135 )
                {
                    tanR[i][ j] = 3;
                }


                atanG = (int)( ( Math.atan ((double)new_gy / new_gx) ) * ( 180 / Math.PI ) );
                if ( ( atanG > 0 && atanG < 22.5 ) || ( atanG > 157.5 && atanG < 180 ) )
                {
                    atanG = 0;
                }
                else if ( atanG > 22.5 && atanG < 67.5 )
                {
                    atanG = 45;
                }
                else if ( atanG > 67.5 && atanG < 112.5 )
                {
                    atanG = 90;
                }
                else if ( atanG > 112.5 && atanG < 157.5 )
                {
                    atanG = 135;
                }


                if (atanG == 0){
                    tanG[i][ j] = 0;
                }
                else if (atanG == 45) {
                    tanG[i][ j] = 1;
                }
                else if ( atanG == 90 )
                {
                    tanG[i][ j] = 2;
                }
                else if ( atanG == 135 )
                {
                    tanG[i][ j] = 3;
                }


                atanB = (int)( ( Math.atan ((double)new_by / new_bx) ) * ( 180 / Math.PI ) );
                if ( ( atanB > 0 && atanB < 22.5 ) || ( atanB > 157.5 && atanB < 180 ) )
                {
                    atanB = 0;
                }
                else if ( atanB > 22.5 && atanB < 67.5 )
                {
                    atanB = 45;
                }
                else if ( atanB > 67.5 && atanB < 112.5 )
                {
                    atanB = 90;
                }
                else if ( atanB > 112.5 && atanB < 157.5 )
                {
                    atanB = 135;
                }

                if ( atanB == 0 )
                {
                    tanB[i][ j] = 0;
                }
                else if ( atanB == 45 )
                {
                    tanB[i][ j] = 1;
                }
                else if ( atanB == 90 )
                {
                    tanB[i][ j] = 2;
                }
                else if ( atanB == 135 )
                {
                    tanB[i][ j] = 3;
                }
            }
        }

        int[][] allPixRs = new int[width][ height];
        int[][] allPixGs = new int[width][ height];
        int[][] allPixBs = new int[width][ height];

        for ( int i = 2; i < width-2; i++ )
        {
            for ( int j = 2; j < height-2; j++ )
            {

                if ( tanR[i][ j] == 0 )
                {
                    if ( graidientR[i - 1][ j] < graidientR[i][ j] && graidientR[i + 1][ j] < graidientR[i][ j] )
                    {
                        allPixRs[i][ j] = graidientR[i][ j];
                    }
                    else {
                        allPixRs[i][j] = 0;
                    }
                }
                if ( tanR[i][ j] == 1 )
                {
                    if ( graidientR[i - 1][ j + 1] < graidientR[i][ j] && graidientR[i + 1][ j - 1] < graidientR[i][ j] )
                    {
                        allPixRs[i][ j] = graidientR[i][ j];
                    }
                    else
                    {
                        allPixRs[i][ j] = 0;
                    }
                }
                if ( tanR[i][ j] == 2 )
                {
                    if ( graidientR[i][ j - 1] < graidientR[i][ j] && graidientR[i][ j + 1] < graidientR[i][ j] )
                    {
                        allPixRs[i][ j] = graidientR[i][ j];
                    }
                    else
                    {
                        allPixRs[i][ j] = 0;
                    }
                }
                if ( tanR[i][ j] == 3 )
                {
                    if ( graidientR[i - 1][ j - 1] < graidientR[i][ j] && graidientR[i + 1][ j + 1] < graidientR[i][ j] )
                    {
                        allPixRs[i][ j] = graidientR[i][ j];
                    }
                    else
                    {
                        allPixRs[i][ j] = 0;
                    }
                }

                if ( tanG[i][ j] == 0 )
                {
                    if ( graidientG[i - 1][ j] < graidientG[i][ j] && graidientG[i + 1][ j] < graidientG[i][ j] )
                    {
                        allPixGs[i][ j] = graidientG[i][ j];
                    }
                    else
                    {
                        allPixGs[i][ j] = 0;
                    }
                }
                if ( tanG[i][ j] == 1 )
                {
                    if ( graidientG[i - 1][ j + 1] < graidientG[i][ j] && graidientG[i + 1][ j - 1] < graidientG[i][ j] )
                    {
                        allPixGs[i][ j] = graidientG[i][ j];
                    }
                    else
                    {
                        allPixGs[i][ j] = 0;
                    }
                }
                if ( tanG[i][ j] == 2 )
                {
                    if ( graidientG[i][ j - 1] < graidientG[i][ j] && graidientG[i][ j + 1] < graidientG[i][ j] )
                    {
                        allPixGs[i][ j] = graidientG[i][ j];
                    }
                    else
                    {
                        allPixGs[i][ j] = 0;
                    }
                }
                if ( tanG[i][ j] == 3 )
                {
                    if ( graidientG[i - 1][ j - 1] < graidientG[i][ j] && graidientG[i + 1][ j + 1] < graidientG[i][ j] )
                    {
                        allPixGs[i][ j] = graidientG[i][ j];
                    }
                    else
                    {
                        allPixGs[i][ j] = 0;
                    }
                }

                if ( tanB[i][ j] == 0 )
                {
                    if ( graidientB[i - 1][ j] < graidientB[i][ j] && graidientB[i + 1][ j] < graidientB[i][ j] )
                    {
                        allPixBs[i][ j] = graidientB[i][ j];
                    }
                    else
                    {
                        allPixBs[i][ j] = 0;
                    }
                }
                if ( tanB[i][ j] == 1 )
                {
                    if ( graidientB[i - 1][ j + 1] < graidientB[i][ j] && graidientB[i + 1][ j - 1] < graidientB[i][ j] )
                    {
                        allPixBs[i][ j] = graidientB[i][ j];
                    }
                    else
                    {
                        allPixBs[i][ j] = 0;
                    }
                }
                if ( tanB[i][ j] == 2 )
                {
                    if ( graidientB[i][ j - 1] < graidientB[i][ j] && graidientB[i][ j + 1] < graidientB[i][ j] )
                    {
                        allPixBs[i][ j] = graidientB[i][ j];
                    }
                    else
                    {
                        allPixBs[i][ j] = 0;
                    }
                }
                if ( tanB[i][ j] == 3 )
                {
                    if ( graidientB[i - 1][ j - 1] < graidientB[i][ j] && graidientB[i + 1][ j + 1] < graidientB[i][ j] )
                    {
                        allPixBs[i][ j] = graidientB[i][ j];
                    }
                    else
                    {
                        allPixBs[i][ j] = 0;
                    }
                }
            }
        }

        int[][] allPixRf = new int[width][ height];
        int[][] allPixGf = new int[width][ height];
        int[][] allPixBf = new int[width][ height];

//        Bitmap bb = Bitmap.createBitmap(src,0,0,width,height);
        BufferedImage bb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 2; i <width-2 ;i++){
            for ( int j = 2; j < height-2;j++ )
            {
                if ( allPixRs[i][ j] > threshold )
                {
                    allPixRf[i][ j] = 1;
                }
                else {
                    allPixRf[i][ j] = 0;
                }

                if ( allPixGs[i][ j] > threshold )
                {
                    allPixGf[i][ j] = 1;
                }
                else
                {
                    allPixGf[i][ j] = 0;
                }

                if ( allPixBs[i][ j] > threshold )
                {
                    allPixBf[i][ j] = 1;
                }
                else
                {
                    allPixBf[i][ j] = 0;
                }



                if ( allPixRf[i][ j] == 1 || allPixGf[i][ j] == 1 || allPixBf[i][ j] == 1 )
                {
                    Color white = new Color(255,255,255);
                    bb.setRGB(i,j, white.getRGB());
                }
                else{
                    Color black = new Color(0,0,0);
                    bb.setRGB(i,j, black.getRGB());
//                    bb.setPixel (i, j, Color.rgb(0,0,0));
                }
            }
        }
//        src = Bitmap.createBitmap(bb,0,0,bb.getWidth(),bb.getHeight());

        return bb;
    }

}



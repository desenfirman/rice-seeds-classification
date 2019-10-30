
import java.awt.*;
import java.awt.image.BufferedImage;

public class Dilation {
    private  static  int width, height;
    private  static BufferedImage src;

    public static BufferedImage binaryImage(BufferedImage img, boolean dilateWhitePixels){
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
        int targetValue = (dilateWhitePixels == true)?0:255;

        /**
         * If the target pixel value is WHITE (255) then the reverse pixel value will
         * be BLACK (0) and vice-versa.
         */
        int reverseValue = (targetValue == 255)?0:255;

        //perform dilation
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                //For BLACK pixel RGB all are set to 0 and for WHITE pixel all are set to 255.
                int red = (new Color(img.getRGB(x, y))).getRed();
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
                                if( (new Color(img.getRGB(tx, ty))).getRed() != targetValue){
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
        BufferedImage returnIMG = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];
//                int pix = returnIMG.getPixel(x, y);

                returnIMG.setRGB(x, y, (new Color(v, v, v ).getRGB()));
            }
        }
        return returnIMG;
    }


    /**
     * This method will perform dilation operation on the grayscale image img.
     *
     * @param img The image on which dilation operation is performed
     */
    public static BufferedImage grayscaleImage(BufferedImage img){
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
                            buff[i] = new Color(img.getRGB(tx, ty)).getRed();
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
        BufferedImage out = new BufferedImage(width, height, img.getType());
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];
                out.setRGB(x, y, (new Color(v, v, v)).getRGB());
//                img.setPixel(x, y, 255, v, v, v);
            }
        }
        return out;
    }

    /**
     * This method will perform dilation operation on the grayscale image img.
     * It will find the maximum value among the pixels that are under the mask [element value 1] and will
     * set the origin to the maximum value.
     *
     * @param img The image on which dilation operation is performed
     * @param mask the square mask.
     * @param maskSize the size of the square mask. [i.e., number of rows]
     */
    public static BufferedImage grayscaleImage(BufferedImage img, int mask[], int maskSize){
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
                buff = new int[maskSize * maskSize];
                int i = 0;
                for(int ty = y - maskSize/2, mr = 0; ty <= y + maskSize/2; ty++, mr++){
                    for(int tx = x - maskSize/2, mc = 0; tx <= x + maskSize/2; tx++, mc++){
                        /**
                         * Sample 3x3 mask [kernel or structuring element]
                         * [0, 1, 0
                         *  1, 1, 1
                         *  0, 1, 0]
                         *
                         * Only those pixels of the image img that are under the mask element 1 are considered.
                         */
                        if(ty >= 0 && ty < height && tx >= 0 && tx < width){
                            //pixel under the mask

                            if(mask[mc+mr*maskSize] != 1){
                                continue;
                            }

                            buff[i] = new Color(img.getRGB(tx, ty)).getRed();
                            i++;
                        }
                    }
                }

                //sort buff
                java.util.Arrays.sort(buff);

                //save lowest value
                output[x+y*width] = buff[(maskSize*maskSize) - 1];
            }
        }

        /**
         * Save the dilation value in image img.
         */
        BufferedImage out = new BufferedImage(width, height, img.getType());
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int v = output[x+y*width];
                out.setRGB(x, y, new Color(v, v, v).getRGB());
//                img.setPixel(x, y, 255, v, v, v);
            }
        }
        return out;
    }
}

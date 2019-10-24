

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main extends JFrame{


    private JPanel rootPanel;

    public Main(){
        File img_file_handler = new File("in/ciherang (1).jpg");
        System.out.println(img_file_handler);
//        BufferedImage bmp = null;
//        try {
//            bmp = ImageIO.read(img_file_handler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        bmp = bmp.getSubimage(bmp.getWidth() * 1/5, bmp.getHeight() * 1/3,  bmp.getWidth() * 3/5, bmp.getHeight() / 3);
//        bmp = resizeImage(bmp, (int) (bmp.getWidth() * 0.25), (int) (bmp.getHeight() * 0.25));
//        bmp = Grayscale.grayScaleImage(bmp);
//        bmp = (new GaussianBlur(bmp)).doGaussianBlur(16, 10);
//        bmp =  Dilation.grayscaleImage(bmp);
//        bmp = CannyEdgeDetector.process(bmp, 50);
//        bmp =  Dilation.binaryImage(bmp, true);
//        bmp = Erosion.binaryImage(bmp, false);
//        bmp =  Dilation.grayscaleImage(bmp);

//        bmp = bmp.getSubimage(bmp.getWidth() * 1/7, bmp.getHeight() * 1/7,  bmp.getWidth() * 4/7, bmp.getHeight() * 5/7);


//        imgView.setIcon(new ImageIcon(bmp));
//        informationLabel.setText(informationLabel.getText() + ": width=" + bmp.getWidth() + " height=" + bmp.getHeight() );

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

    public static void main(String... aArgs){
        Main frame = new Main();
        frame.setSize(800, 600);
        frame.setContentPane(frame.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}

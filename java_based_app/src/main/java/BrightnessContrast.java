
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class BrightnessContrast {

    public static BufferedImage adjustBrightness(BufferedImage src){
        RescaleOp rescaleOp = new RescaleOp(1.2f, 20, null);
        rescaleOp.filter(src, src);
        return src;
    }
}

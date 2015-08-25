import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Nabin Mulepati
 */

class NMImageCanvas extends Canvas {
    BufferedImage bufferedImage;
    
    NMImageCanvas(BufferedImage bufferedImage) {
       this.bufferedImage = bufferedImage;
    }
    
    public void resetCanvas(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
    
    @Override
    public void paint(Graphics graphics) {
        graphics.drawImage(this.bufferedImage, 0, 0, Color.red, null);
    }
}

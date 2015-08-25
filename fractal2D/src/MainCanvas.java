
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;


/**
 *
 * @author nabinchha
 */
class MainCanvas extends Canvas
{
    BufferedImage X;
    MainCanvas(BufferedImage I)
    {
       X = I;
    }
    public void resetCanvas(BufferedImage I)
    {
        X = I;
    }
    public void paint(Graphics g)
    {
       // Graphics2D g2 = (Graphics2D)g;
        g.drawImage(X, 0, 0, Color.red, null);
    //  g.drawImage(Main.I, 0, 0, Color.red, null);
        Dimension s = getSize();
    }
}

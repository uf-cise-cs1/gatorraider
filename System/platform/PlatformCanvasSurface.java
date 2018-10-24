package platform;

import general.CanvasSurface;

import java.awt.*;
import javax.swing.*;

public class PlatformCanvasSurface extends JComponent
{
    private CanvasSurface surface;

    private JFrame frame;
    protected Graphics buffer;
    private java.awt.Image offscreen;

    public PlatformCanvasSurface(CanvasSurface _surface, int width, int height)
    {
        surface = _surface;
        setPreferredSize(new Dimension(width, height));

        frame = new JFrame()
        {
            {
                getContentPane().add(BorderLayout.CENTER, PlatformCanvasSurface.this);
                pack();
                Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
                setLocation((int)(screen.getWidth()*3/8),(int)(screen.getHeight()*3/8));
                setVisible(true);
                setResizable(false);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                repaint();
            }

        };
    }

    public JFrame getFrame()
    {
        return frame;
    }

    public void paintComponent(Graphics g)
    {
        if(offscreen==null)
        {
            offscreen=createImage(this.getPreferredSize().width,this.getPreferredSize().height);
            buffer =offscreen.getGraphics();
        }

        surface.paint();

        g.drawImage(offscreen,0,0,this);
    }

    public void setColor(int color)
    {
        buffer.setColor(new Color(color));
    }

    public void drawRect(int x, int y, int rectWidth, int rectHeight)
    {
        buffer.drawRect(x, y, rectWidth, rectHeight);
    }

    public void fillRect(int x, int y, int rectWidth, int rectHeight)
    {
        buffer.fillRect(x, y, rectWidth, rectHeight);
    }

    public void drawLine(int x1, int y1, int x2, int y2)
    {
        buffer.drawLine(x1, y1, x2, y2);
    }

    // Drawing commands
    public void drawImage(java.awt.Image subject, int x, int y)
    {
        buffer.drawImage(subject, x, y, null);
    }

    public void fillOval(int x, int y, int width, int height)
    {
        buffer.fillOval(x, y, width, height);
    }
/*    public void drawImage(Image subject, int x, int y, int xScale, int yScale)
    {
        buffer.drawImage(subject, x, y, xScale, yScale);
    }

    public void drawChar(int x, int y, char toDraw, PixelFont font, int color)
    {
        buffer.drawString(x, y, "" + toDraw, font, color);
    }

    public void drawString(int x, int y, String text, PixelFont font, int color)
    {
        buffer.drawString(x, y, text, font, color);
    }*/

    public void drawString(String text, int x, int y)
    {
        buffer.drawString(text, x, y);
    }
}

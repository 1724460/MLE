package mle.kNearest;

import javax.swing.*;
import java.awt.*;

public class PaintKNearest extends Canvas {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static void main(String[] args) {
        JFrame frame = new JFrame("K-Nearest");
        Canvas canvas = new PaintKNearest();
        canvas.setSize(WIDTH, HEIGHT);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    public void paint(Graphics g) {
        KNearest kNearest = new KNearest(3);

        g.setColor(Color.WHITE);
        g.drawRect(0, 0, WIDTH, HEIGHT);

        for(int x = 0; x < WIDTH; x++){
            for (int y = 0; y < HEIGHT; y++){
                g.setColor(new Color(255,100,100,100));
                int l = kNearest.getKNearest((2d *x / WIDTH)-1, (2d * y / HEIGHT-1));
                if(l == 1){
                    g.setColor(new Color(100,100,255,100));
                }
                g.drawRect(x, y, 1, 1);
            }
        }

        for(KNearest.LabeledPoint p : kNearest.getPrototypes()){
            g.setColor(new Color(255,0,0,255));
            if(p.label == 1)
                g.setColor(new Color(0,0,255,255));

            int x = (int)(((p.x + 1 )/ 2)*WIDTH);
            int y = (int)(((p.y + 1 )/ 2)*HEIGHT);
            g.fillOval(x-2, y-2, 4,4);

        }

    }
}

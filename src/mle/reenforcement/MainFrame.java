package mle.reenforcement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    public static final int imageWidth = 360;
    public static final int imageHeight = 360;
    public InputOutput inputOutput = new InputOutput(this);
    public boolean stop = false;
    ImagePanel canvas = new ImagePanel();
    ImageObserver imo = null;
    Image renderTarget = null;
    public int mousex, mousey, mousek;
    public int key;

    //QAgent
    int stateSize = 11 * 12 * 10 * 2 * 2 * 10, actionSize = 3;
    double[][] q;
    double epsilon = 0.05;
    double learnRate = 0.05;
    double discountFactor = 1;
    int learnEpisodes = 10000;


    public MainFrame(String[] args) {
        super("PingPong");

        getContentPane().setSize(imageWidth, imageHeight);
        setSize(imageWidth + 50, imageHeight + 50);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        canvas.img = createImage(imageWidth, imageHeight);

        add(canvas);

        run();
    }

    public void run() {

        long sleep = 0;
        boolean render = false;

        //counter variables
        int episodes = 0;
        int games = 0;
        int wins = 0;

        //init state
        int xBall = 5, yBall = 6, xSchlaeger = 5, xV = 1, yV = 1;

        //init q values
        q = new double[stateSize][actionSize];
        for (int s = 0; s < stateSize; s++) {
            for (int a = 0; a < actionSize; a++) {
                q[s][a] = 0.04 * Math.random() + 0.01;
            }
        }

        while (!stop) {
            inputOutput.fillRect(0, 0, imageWidth, imageHeight, Color.black);
            inputOutput.fillRect(xBall * 30, yBall * 30, 30, 30, Color.green);
            inputOutput.fillRect(xSchlaeger * 30, 11 * 30 + 20, 90, 10, Color.orange);


            int state = toState(xSchlaeger, xBall, yBall, xV, yV);


            //choose action
            int bestActionIndex = getBestActionIndex(state);
            int action = modifyActionIndexAccordingToEpsilonGreedy(bestActionIndex);
            if (action == 0)
                xSchlaeger--;
            if (action == 2)
                xSchlaeger++;

            //keep batter in field
            if (xSchlaeger < 0) {
                xSchlaeger = 0;
            }
            if (xSchlaeger > 9) {
                xSchlaeger = 9;
            }

            //move ball
            xBall += xV;
            yBall += yV;
            //ricochet
            if (xBall > 9 || xBall < 1) {
                xV = -xV;
            }
            if (yBall > 10 || yBall < 1) {
                yV = -yV;
            }


            //detect hit or miss
            double reward = 0;
            if (yBall == 11) {
                if (xSchlaeger == xBall || xSchlaeger == xBall - 1 || xSchlaeger == xBall - 2) {
                    reward = 1;
                    wins++;
                } else {
                    reward = -1;
                }
                episodes++;
                games++;

                //stay responsive in learn phase
                if (!render && episodes % 1000 == 0) {
                    System.out.println(episodes);
                    System.out.println("wins/games ratio: " + wins * 1d / games);
                    wins = 0;
                    games = 0;
                }
                //output in play phase
                if (render)
                    System.out.println("wins: " + wins + "    looses: " + (games - wins));
            }

            //get next state
            int nextState = toState(xSchlaeger, xBall, yBall, xV, yV);
            //get the index of the best action of the next state
            int bestActionIndexOfNextState = getBestActionIndex(nextState);
            //update q value
            q[state][action] += learnRate * (reward + discountFactor * q[nextState][bestActionIndexOfNextState] - q[state][action]);


            //detect the end of the learn phase
            if (episodes == learnEpisodes) {
                sleep = 5;
                epsilon = 0; //get greedy
                wins = 0;//reset stats
                games = 0;
                render = true;
            }

            try {
                Thread.sleep(sleep);                 //1000 milliseconds is one second.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            if (render) {
                repaint();
                validate();
            }
        }

        setVisible(false);
        dispose();
    }

    private int toState(int xBatter, int xBall, int yBall, int xV, int yV) {
        int xVstate = (xV + 1) / 2;
        int yVstate = (yV + 1) / 2;
        int[] x = {xBatter, xBall, yBall, xVstate, yVstate};
        int[] xMax = {10, 11, 12, 2, 2};
        int s = x[0];

        for (int i = 1; i < x.length; i++) {
            s = s * xMax[i] + x[i];
        }

        return s;
    }

    private int getBestActionIndex(int s) {
        double cMax = -999999999;
        int cIndex = 0;
        for (int a = 0; a < actionSize; a++) {
            double cValue = q[s][a];
            if (cValue > cMax) {
                cMax = cValue;
                cIndex = a;
            }
        }
        return cIndex;
    }

    private int modifyActionIndexAccordingToEpsilonGreedy(int a) {
        if (Math.random() < epsilon) {
            a += (actionSize - 1) * Math.random();
            a = (a + 1) % actionSize;
            a = (int) Math.random() * actionSize;
        }
        return a;
    }


    public void mouseReleased(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mousePressed(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mouseExited(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mouseEntered(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mouseClicked(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mouseMoved(MouseEvent e) {
        // System.out.println(e.toString());
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void mouseDragged(MouseEvent e) {
        mousex = e.getX();
        mousey = e.getY();
        mousek = e.getButton();
    }

    public void keyTyped(KeyEvent e) {
        key = e.getKeyCode();
    }

    public void keyReleased(KeyEvent e) {
        key = e.getKeyCode();
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e.toString());
    }

    /**
     * Construct main frame
     *
     * @param args passed to MainFrame
     */
    public static void main(String[] args) {
        new MainFrame(args);
    }
}

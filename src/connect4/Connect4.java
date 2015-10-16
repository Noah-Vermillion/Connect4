
package connect4;

import javax.swing.JFrame;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class Connect4 extends JFrame implements Runnable {   
    static final int XBORDER = 20;
    static final int YBORDER = 20;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + 495;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + 525;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int numRows = 8;
    final int numColumns = 8;
    
    Piece board[][];
    
    boolean Gameover;
    int redVictor = 0;
    
    int turnNum = 1;
    int victor = 0;
    
    static Connect4 frame1;
    public static void main(String[] args) 
    {
        frame1 = new Connect4();
        frame1.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setVisible(true);
    }
    public Connect4() {

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                int whatRowPlace = numRows -1;
                        for (int zx=0;zx<numColumns+1;zx++)
                        {
                            if(zx*getWidth2()/numColumns > (e.getX()-getX(0)))
                            {
                                while(whatRowPlace >=0 && board[whatRowPlace][zx-1] != null)
                                {
                                    whatRowPlace --;
                                }
                                if(whatRowPlace>=0)
                                {
                                    if(turnNum % 2 == 1)
                                    {
                                        board[whatRowPlace][zx-1]= new Piece(Color.red);
                                       
                                    }
                                    else if(turnNum % 2 == 0)
                                    {
                                        board[whatRowPlace][zx-1]= new Piece(Color.black);
                                       
                                    }
                                }
                                turnNum++;
                                break;
                            }
                            
                        }

                 }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
               
                 if (e.VK_E == e.getKeyCode())
                {
                  reset();
                }

                repaint();
            }
        });
        init();
        start();
    }




    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }
////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

//fill background
        g.setColor(Color.cyan);

        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.setColor(Color.red);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
                if (board[zrow][zcolumn] != null)
                {
                    g.setColor(board[zrow][zcolumn].getColor());

                    g.fillOval(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                }
               
            }
        }
         if(victor == 1)
         {
            g.setColor(Color.blue);
            g.setFont(new Font("Arial Black", Font.PLAIN,50));
            g.drawString("Player 1 Wins" ,getWidth2()/2-150 , getHeight2()/2);         }
         else if(victor == 2)
         {
            g.setColor(Color.blue);
            g.setFont(new Font("Arial Black", Font.PLAIN,50));
            g.drawString("Player 2 Wins" ,getWidth2()/2-150 , getHeight2()/2);
         }

        gOld.drawImage(image, 0, 0, null);
    }


////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.001;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        board = new Piece[numRows][numColumns];
        redVictor = 0;
        victor = 0;
        turnNum = 1;
        Gameover = false;          
      
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {

        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }

            reset();
        
        }
    
         if(Gameover)
         return;
         isWin();
         
    }  
////////////////////////////////////////////////////////////////////////////
    
    public void isWin()
    {
//      horizontal Win  
        for(int zrow = 0; zrow<numRows; zrow++)
        {
            int redCount = 0;
            int blackCount = 0;
             for(int zcol = 0; zcol<numColumns; zcol++)
             {
                  if(board[zrow][zcol] != null)
                  {
                      if(board[zrow][zcol].getColor() == Color.red)
                      {
                          redCount++;
                          blackCount = 0;
                          if(redCount == 4)
                            {
                                victor = 1;
                                break;
                            }
                      }
                      else if(board[zrow][zcol].getColor() == Color.black)
                      {
                          blackCount++;
                          redCount = 0;
                          if(blackCount == 4)
                            {
                                 victor = 2;
                                 break;
                            }
                      }
                  }
             }  
             blackCount = 0;
             redCount = 0;
        }
        //Vertical Win
        for(int zcol = 0; zcol<numColumns; zcol++)
        {
            int redCount = 0;
            int blackCount = 0;
             for(int zrow = 0; zrow<numRows; zrow++)
             {
                  if(board[zrow][zcol] != null)
                  {
                      if(board[zrow][zcol].getColor() == Color.red)
                      {
                          redCount++;
                          blackCount = 0;
                          if(redCount == 4)
                            {
                                victor = 1;
                                break;
                            }
                      }
                      else if(board[zrow][zcol].getColor() == Color.black)
                      {
                          blackCount++;
                          redCount = 0;
                          if(blackCount == 4)
                            {
                                 victor = 2;
                                 break;
                            }
                      }
                  }
             }  
             blackCount = 0;
             redCount = 0;
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}


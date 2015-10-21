/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connect4;


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
    boolean playerOnesTurn;
    boolean moveHappened;
    int currentRow;
    int currentColumn;
    enum WinState
    {
        None,PlayerOne,PlayerTwo,Tie
    }
    WinState winState;
    int winRow;
    int winColumn;
    enum WinDirection
    {
        Horizontal,Vertical,DiagonalUp,DiagonalDown
    }
    WinDirection winDirection;    
    int piecesOnBoard;
    int connectWhat = 4;
    
       int player1Score = 0;
       int player2Score = 0;
       int player1MovesLeft = 5;
       int player2MovesLeft = 5;
    
    static Connect4 frame1;
    public static void main(String[] args) {
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
                    if (moveHappened || winState != WinState.None)
                        return;
                    
                    
                    int xpos = e.getX() - getX(0);
                    int ypos = e.getY() - getY(0);
                    if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
                        return;
//Calculate the width and height of each board square.
                    int ydelta = getHeight2()/numRows;
                    int xdelta = getWidth2()/numColumns;
                    currentColumn = xpos/xdelta;
//                    int row = ypos/ydelta;
                    currentRow = numRows - 1;
                    while (currentRow >= 0 && board[currentRow][currentColumn] != null)
                    {
                        currentRow--;
                    }
                    if (currentRow >= 0)
                    {
                        if (playerOnesTurn)
                            board[currentRow][currentColumn] = new Piece(Color.red, (int)((Math.random() * 3) + 1));
                        else
                            board[currentRow][currentColumn] = new Piece(Color.black, (int)((Math.random() * 3) + 1));
                        playerOnesTurn = !playerOnesTurn;
                        moveHappened = true;
                        piecesOnBoard++;
                    }
                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    int xpos = e.getX() - getX(0);
                    int ypos = e.getY() - getY(0);
                    if (xpos < 0 || ypos < 0 || xpos > getWidth2() || ypos > getHeight2())
                        return;
//Calculate the width and height of each board square.
                    int ydelta = getHeight2()/numRows;
                    int xdelta = getWidth2()/numColumns;
                    if (winState == WinState.None && player1MovesLeft > 0 && playerOnesTurn == true)
                    {
                        if (currentRow >= 0)
                        {
                            if(board[currentRow][currentColumn].getColor() != Color.pink)
                            {
                                board[currentRow][currentColumn].setColor(Color.pink);
                                player1MovesLeft --;
                                playerOnesTurn = !playerOnesTurn;
                                moveHappened = true;
                            }
                        }
                    }
                    if (winState == WinState.None && player2MovesLeft > 0 && playerOnesTurn != true)
                    {
                        if (currentRow >= 0)
                        {
                            if(board[currentRow][currentColumn].getColor() != Color.pink)
                            {
                                board[currentRow][currentColumn].setColor(Color.pink);
                                player2MovesLeft --;
                                playerOnesTurn = !playerOnesTurn;
                                moveHappened = true;
                            }
                        }
                    }
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
                if (e.VK_ESCAPE == e.getKeyCode())
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

        g.setColor(Color.gray);
//horizontal lines
//        for (int zi=1;zi<numRows;zi++)
//        {
//            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
//            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
//        }
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
//                    g.fillOval(getX(0)+zcolumn*getWidth2()/numColumns,
//                    getY(0)+zrow*getHeight2()/numRows,
//                    getWidth2()/numColumns,
//                    getHeight2()/numRows);
                    int xvals[] = {getX(0)+zcolumn*getWidth2()/numColumns,getX(0)+zcolumn*getWidth2()/numColumns,getX(0)+zcolumn*getWidth2()/numColumns + getWidth2()/numColumns};
                    int yvals[] = {getY(0)+zrow*getHeight2()/numRows,getY(0)+zrow*getHeight2()/numRows + getHeight2()/numRows,getY(0)+zrow*getHeight2()/numRows + getHeight2()/numRows};
                    g.fillPolygon(xvals, yvals, xvals.length);
                    if(board[zrow][zcolumn].getColor() != Color.pink)
                    {
                    g.setColor(Color.green);
                    g.setFont(new Font("Monospaced",Font.BOLD,40) );
                    g.drawString("" + board[zrow][zcolumn].getValue(), getX(0)+zcolumn*getWidth2()/numColumns+10, getY(0)+zrow*getHeight2()/numRows+50);   
                    }
                }
            }
        }
            g.setColor(Color.black);
            g.setFont(new Font("Monospaced",Font.BOLD,20) );
            g.drawString("Player 1 Score " + player1Score, 10, getY(0));     
            
            g.setFont(new Font("Monospaced",Font.BOLD,20) );
            g.drawString("Player 2 Score " + player2Score, 300, getY(0)); 
            
            g.setFont(new Font("Monospaced",Font.BOLD,15) );
            g.drawString("Player 1 Moves Left " + player1MovesLeft, 10, getYNormal(0) + 15);     
            
            g.setFont(new Font("Monospaced",Font.BOLD,15) );
            g.drawString("Player 2 Moves Left " + player2MovesLeft, 300, getYNormal(0) + 15);      
        if (winState == WinState.PlayerOne)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("Player 1 has won.", 50, 200);            
        }
        else if (winState == WinState.PlayerTwo)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("Player 2 has won.", 50, 200);            
        }
        else if (winState == WinState.Tie)
        {
            g.setColor(Color.gray);
            g.setFont(new Font("Monospaced",Font.BOLD,40) );
            g.drawString("It is a tie.", 50, 200);            
        }

        gOld.drawImage(image, 0, 0, null);
    }


////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.03;    //time that 1 frame takes.
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
//        for (int zrow = 0;zrow < numRows;zrow++)
//        {
//            for (int zcolumn = 0;zcolumn < numColumns;zcolumn++)
//            {
//                board[zrow][zcolumn] = null;
//            }
//        }
        playerOnesTurn = true;
        moveHappened = false;
        winState = WinState.None;
        piecesOnBoard = 0;
        player1MovesLeft = 5;
        player2MovesLeft = 5;
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
        
        
        if (moveHappened)
        {
            moveHappened = false;
            checkWin();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public boolean checkWin() {
//check horizontal.
        
        int startColumn = currentColumn - (connectWhat - 1);
        if (startColumn < 0)
            startColumn = 0;
        int endColumn = currentColumn + (connectWhat - 1);
        if (endColumn > numColumns-1)
            endColumn = numColumns - 1;
        int numMatch = 0;
        
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[currentRow][col] != null && board[currentRow][col].getColor() != Color.pink && board[currentRow][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else if(numMatch == connectWhat)
            {
                break;
            }
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = currentRow;
            }
        }
        
        if (numMatch == connectWhat)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                for (int howMany = 0; howMany<connectWhat;howMany++)
                {
                    board[winRow][winColumn+howMany].setColor(Color.blue);
                    if(winState == WinState.PlayerOne )
                    {
                        player1Score += board[winRow][winColumn+howMany].getValue();
                    }
                    else if(winState == WinState.PlayerTwo )
                    {
                        player2Score += board[winRow][winColumn+howMany].getValue();
                    }
                }
                
            }            
            return (true);
        }
        
//check vertical.
        int startRow = currentRow - (connectWhat - 1);
        if (startRow < 0)
            startRow = 0;
        int endRow = currentRow + (connectWhat - 1);
        if (endRow > numRows-1)
            endRow = numRows - 1;
        numMatch = 0;
        
        for (int row = startRow;numMatch != 4 && row<=endRow;row++)
        {
            if (board[row][currentColumn] != null && board[row][currentColumn].getColor() != Color.pink && board[row][currentColumn].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = currentColumn;
                winRow = row;
            }            
        }
        
        if (numMatch == (connectWhat))
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                for (int howMany = 0; howMany<connectWhat;howMany++)
                {
                board[winRow+howMany][winColumn].setColor(Color.blue);
                if(winState == WinState.PlayerOne )
                    {
                        player1Score += board[winRow+howMany][winColumn].getValue();
                    }
                    else if(winState == WinState.PlayerTwo )
                    {
                        player2Score += board[winRow+howMany][winColumn].getValue();
                    }
                }
            }             
            return (true);
        }        
//check diagonal right down.
        startColumn = currentColumn - (connectWhat - 1);
        startRow = currentRow - (connectWhat - 1);
        if (startColumn < 0 || startRow < 0)
        {
            if (startColumn < startRow)
            {
                startRow -= startColumn;
                startColumn = 0;
            }
            else
            {
                startColumn -= startRow;
                startRow = 0;
            }
        }
        endColumn = currentColumn + (connectWhat - 1);
        endRow = currentRow + (connectWhat - 1);
        if (endColumn > numColumns-1 || endRow > numRows-1)
        {
            if (endColumn > endRow)
            {
                endRow -= (endColumn - (numColumns - 1));
                endColumn = numColumns-1;
            }
            else
            {
                endColumn -= (endRow - (numRows - 1));
                endRow = numRows-1;
            }
        }
 
        numMatch = 0;    
        int row = startRow;
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[row][col] != null && board[row][col].getColor() != Color.pink && board[row][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = row;
            }
            row++;
        }
        
        if (numMatch == (connectWhat))
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
                for (int howMany = 0; howMany<connectWhat;howMany++)
                {
                board[winRow+howMany][winColumn+howMany].setColor(Color.blue);
                 if(winState == WinState.PlayerOne )
                    {
                        player1Score += board[winRow+howMany][winColumn+howMany].getValue();
                    }
                    else if(winState == WinState.PlayerTwo )
                    {
                        player2Score += board[winRow+howMany][winColumn+howMany].getValue();
                    }
                }
            }            
            return (true);
        }
                
 
//check diagonal right up.
        startColumn = currentColumn - (connectWhat - 1);
        startRow = currentRow + (connectWhat - 1);
        if (startColumn < 0 || startRow > numRows-1)
        {
            if (startColumn < numRows - 1 - startRow)
            {
                startRow += startColumn;
                startColumn = 0;
            }
            else
            {
                startColumn += startRow - (numRows - 1);
                startRow = numRows - 1;
            }
        }
        endRow = currentRow - (connectWhat - 1);
        endColumn = currentColumn + (connectWhat - 1);
        if (endRow < 0 || endColumn > numColumns-1)
        {
            if (endRow < numColumns - 1 - endColumn)
            {
                endColumn += endRow;
                endRow = 0;
            }
            else
            {
                endRow += endColumn - (numColumns - 1);
                endColumn = numColumns - 1;
            }
        }        
 
        numMatch = 0;    
        row = startRow;
        for (int col = startColumn;numMatch != 4 && col<=endColumn;col++)
        {
            if (board[row][col] != null && board[row][col].getColor() != Color.pink && board[row][col].getColor() == board[currentRow][currentColumn].getColor())
                numMatch++;
            else if (numMatch == connectWhat)
                break;
            else if (board[row][col] == null || board[row][col].getColor() != board[currentRow][currentColumn].getColor())
                numMatch = 0;
            if (numMatch == 1)
            {
                winColumn = col;
                winRow = row;
            }
            row--;
        }
        
        if (numMatch == connectWhat)
        {
            if (board[currentRow][currentColumn].getColor() == Color.red)
                winState = WinState.PlayerOne;
            else
                winState = WinState.PlayerTwo;
            {
               for (int howMany = 0; howMany<connectWhat;howMany++)
                {
                board[winRow-howMany][winColumn+howMany].setColor(Color.blue);
                 if(winState == WinState.PlayerOne )
                    {
                        player1Score += board[winRow-howMany][winColumn+howMany].getValue();
                    }
                    else if(winState == WinState.PlayerTwo )
                    {
                        player2Score += board[winRow-howMany][winColumn+howMany].getValue();
                    }
                }
            }            
            return (true);
        }
                  
        if (piecesOnBoard >= numRows*numColumns)
        {
            winState = WinState.Tie;
            return(true);
        }
        return(false);
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

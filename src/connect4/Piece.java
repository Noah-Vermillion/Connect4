package connect4;

import javax.swing.JFrame;
import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class Piece 
{
    Graphics2D g;
    private Color myColor;
    Piece(Color _color)
    {
        myColor = _color;
    }
    public Color getColor()
    {
        return(myColor);
    }
}
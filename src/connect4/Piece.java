package connect4;
import java.awt.*;

public class Piece {
    private Color color;
    private int value;
    Piece(Color _color, int _value)
    {
        color = _color;
        value = _value;
    }
    Piece(Color _color)
    {
        color = _color;
    }
    Color getColor()
    {
        return (color);
    }
    void setColor(Color _color)
    {
        color = _color;
    }  
    int getValue()
    {
        return (value);
    }
    void setValue(int _value)
    {
        value = _value;
    }  
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * An extended text component.
 *
 * @author Michael Binz
 */
public class MmtText extends Text
{
    public MmtText()
    {
    }
    public MmtText( String text )
    {
        super( text );
    }

    private Color _color;

    public void setMmtColor( Color color )
    {
        _color = color;
    }

    public Color getMmtColor()
    {
        return _color;
    }

    private Font _font;

    public void setMmtFont( Font font )
    {
        _font = font;
    }
    public Font getMmtFont()
    {
        return _font;
    }
}

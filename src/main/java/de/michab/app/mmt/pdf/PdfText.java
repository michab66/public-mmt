/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Arrays;
import java.util.List;

import org.smack.util.StringUtil;

import de.michab.app.mmt.screens.ScreenUtils;

/**
 * A PDF text field.
 *
 * @author Michael Binz
 */
public class PdfText extends PdfPageElement
{
    private final List<String> _textLines;

    public PdfText( String text )
    {
        this( text.split( "\n" ) );
    }

    public PdfText( String ... text )
    {
        text.getClass();

        _textLines = Arrays.asList( text );

        setFont( ScreenUtils.getFontMedium() );

        for ( int i = 0 ; i < _textLines.size() ; i++ )
            if (  null == _textLines.get( i ) )
                _textLines.set( i, StringUtil.EMPTY_STRING );

        updateSize();
    }

    @Override
    public PdfPageElement setFont( Font font )
    {
        PdfPageElement result = super.setFont( font );

        updateSize();

        return result;
    }

    /**
     *
     */
    private final void updateSize()
    {
        FontMetrics fm = PdfGfx.createFontMetrics( getFont() );

        float h = (fm.getAscent() + fm.getDescent()) * _textLines.size();
        if ( _textLines.size() > 1 )
            h += fm.getLeading() * (_textLines.size()-1);

        float w = 0.0f;

        for ( String c : _textLines )
            w = Math.max( w, fm.stringWidth( c ) );

        setSize( w, h );
    }

    @Override
    public void paint( PdfGfx gfx2 ) throws Exception
    {
        gfx2.setFont( getFont() );
        gfx2.setColor( getForeground() );

        FontMetrics metrics = gfx2.getFontMetrics();

        float y = getY() + metrics.getAscent();

        for ( String c : _textLines )
        {
            gfx2.drawString( c, getX(), y );
            y += metrics.getHeight();
        }
    }
}

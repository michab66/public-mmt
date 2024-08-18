/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * A {@link FontMetrics} implementation used by the {@link PdfGfx}
 * graphics context.
 *
 * @author Michael Binz
 */
class PdfFontMetrics extends FontMetrics
{
    private static final Logger LOG =
            Logger.getLogger( PdfFontMetrics.class.getName() );

    private final PDFont _pdFont;

    PdfFontMetrics( Font font )
    {
        super( font );

        _pdFont = PdfGfx.convertFont( font );
    }

    @Override
    public int getAscent()
    {
        return convert( _pdFont.getFontDescriptor().getAscent() );
    }

    @Override
    public int getDescent()
    {
        // Descent is negative in PDFBox.
        return convert( -_pdFont.getFontDescriptor().getDescent() );
    }

    @Override
    public int getLeading()
    {
        // TODO 20% leading. Check if the original leading is 0.0.
        // http://stackoverflow.com/questions/925147/incorrect-missing-font-metrics-in-java
        return Math.round( (getAscent() + getDescent()) * 0.2f );
    }

    @Override
    public int getMaxAdvance()
    {
        return convert( _pdFont.getFontDescriptor().getMaxWidth() );
    }

    @Override
    public int charWidth( char ch )
    {
        try
        {
            return convert( _pdFont.getWidth( ch ) );
        }
        catch ( IOException e )
        {
            LOG.log( Level.WARNING, "charWidth failed.", e );
            return 0;
        }
    }

    @Override
    public int charsWidth( char[] data, int off, int len )
    {
        try
        {
            return convert( _pdFont.getStringWidth( new String( data, off, len ) ) );
        }
        catch ( IOException e )
        {
            LOG.log( Level.WARNING, "charsWidth failed.", e );
            return 0;
        }
    }

    private int convert( float length )
    {
        // Assume that the pt-size of the awt font relates to the size of the pdfont.
        return Math.round(
                length / 1000.0f * getFont().getSize2D() );
    }

    /**
     * Generated for PdfFontMetrics.java.
     */
    private static final long serialVersionUID = 318525355158807769L;
}

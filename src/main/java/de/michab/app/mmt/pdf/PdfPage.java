/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

/**
 * A single page in a pdf document.
 *
 * @author Michael Binz
 */
public class PdfPage extends PdfElementContainer
{
    private final static Logger LOG =
            Logger.getLogger( PdfPage.class.getName() );

    private final PDPage _page;

    private final PdfDocument _parent;
    private final Orientation _orientation;
    private final Size _size;

    public enum Size
    {
        A4( PDRectangle.A4 );

        final PDRectangle value;

        private Size( PDRectangle sizee )
        {
            value = sizee;
        }
    };

    public enum Orientation
    {
        PORTRAIT( 0 ),
        LANDSCAPE( 90 );

        final int value;

        private Orientation( int orientation )
        {
            value = orientation;
        }
    };

    /**
     * Create an instance.
     *
     * @param parent The document the page is part of.
     * @param size The page size.
     * @param orientation The page orientation.
     */
    PdfPage( PdfDocument parent, Size size, Orientation orientation )
    {
        _parent = parent;

        _orientation = orientation;
        _size = size;

        _page = new PDPage();
        _page.setMediaBox( size.value );
        _page.setRotation( orientation.value );
    }

    public PdfDocument getDocument()
    {
        return _parent;
    }

    public float getWidth()
    {
        if ( Orientation.LANDSCAPE == _orientation )
            return _size.value.getHeight();

        return _size.value.getWidth();
    }

    public float getHeight()
    {
        if ( Orientation.LANDSCAPE == _orientation )
            return _size.value.getWidth();

        return _size.value.getHeight();
    }

    private PDPageContentStream getContentStream()
        throws IOException
    {
        PDPageContentStream result = new PDPageContentStream(
                _parent.getDelegate(),
                _page );

        // add the rotation using the current transformation matrix
        // including a translation of pageWidth to use the lower left corner as 0,0 reference
        // Pdf reference, chapter 4.2 coordinate systems
        // TODO(michab) This is compensated and used in PdfGfx. Check if we can move
        // it to this code.
        result.transform(
                new Matrix(
                        0,
                        1,
                        -1,
                        0,
                        getHeight(),
                        0 ) );

        return  result;
    }

    PDPage getDelegate()
    {
        return _page;
    }

    /**
     * Paint the page.
     */
    void paint()
    {
        List<PdfPageElement> toDraw = new ArrayList<>( _parent.get() );

        toDraw.addAll( get() );

        try ( PdfGfx gfx2 = new PdfGfx(
                this,
                getContentStream() )  )
        {
            // Propagate page settings.
            gfx2.setFont( getFont() );

            for ( PdfPageElement c : toDraw )
                c.paint( gfx2 );
        }
        catch ( Exception e )
        {
            LOG.log( Level.WARNING, "Paint failed.", e );
        }
    }

    private Font _font = PdfDocument.DEFAULT_FONT;

    /**
     * TODO if null then use again the default font.
     *
     * @param font
     * @return
     */
    public PdfPage setFont( Font font )
    {
        if ( _font == null )
            _font = PdfDocument.DEFAULT_FONT;
        else
            _font = font;

        return this;
    }
    public Font getFont()
    {
        return _font;
    }
}

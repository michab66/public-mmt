/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;

import de.michab.app.mmt.pdf.PdfPage.Orientation;
import de.michab.app.mmt.pdf.PdfPage.Size;

/**
 *
 * @author Michael Binz
 */
public class PdfDocument extends PdfElementContainer
{
    private final static Logger LOG =
            Logger.getLogger( PdfDocument.class.getName() );

    private final PDDocument _doc =
            new PDDocument();

    private final Size _size;

    private final Orientation _orientation;

    final static Font DEFAULT_FONT =
            new Font( "Helvetica", Font.PLAIN, 14 );

    /**
     * The pages in this document.
     */
    private final List<PdfPage> _pages =
            new ArrayList<>();

    /**
     *
     * @param size
     */
    public PdfDocument( Size size, Orientation orientation )
    {
        _size = size;
        _orientation = orientation;
    }

    public PdfPage createPage()
    {
        PdfPage result = new PdfPage( this, _size, _orientation );

        _doc.addPage( result.getDelegate() );
        _pages.add( result );

        return result;
    }

    public List<PdfPage> getPages()
    {
        return Collections.unmodifiableList( _pages );
    }

    public Size getSize()
    {
        return _size;
    }

    public Orientation getOrientation()
    {
        return _orientation;
    }

    public PDDocument getDelegate()
    {
        return _doc;
    }

    public void removePage( PdfPage page )
    {
        _doc.removePage( page.getDelegate() );

        _pages.remove( page );
    }

    public void close()
    {
        try
        {
            _doc.close();
        }
        catch ( IOException e )
        {
            LOG.log( Level.WARNING, "Document close failed.", e );
        }
    }

    /**
     *
     */
    public void paint()
    {
        for ( PdfPage c : _pages )
            c.paint();
    }
}

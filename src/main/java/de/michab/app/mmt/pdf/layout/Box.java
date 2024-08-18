/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.michab.app.mmt.pdf.PdfGfx;
import de.michab.app.mmt.pdf.PdfPageElement;

/**
 *
 *
 * @author Michael Binz
 */
public abstract class Box extends PdfPageElement
{
    private static final Logger LOG = Logger.getLogger( Box.class.getName() );

    private final List<PdfPageElement> _elements = new ArrayList<>();

    private PdfPageElement.Pos _anchor = Pos.CENTER;

    /**
     * The inter-component gap.
     */
    private float _gap;

    /**
     *
     * @param elements
     */
    protected Box( PdfPageElement ... elements )
    {
        this( 0, Pos.CENTER, elements );
    }

    protected Box( float gap )
    {
        this( gap, Pos.CENTER, (PdfPageElement[])null );
    }

    protected Box( float gap, Pos anchor, PdfPageElement ... elements  )
    {
        invalidate();

        _gap = gap;
        _anchor = anchor;

        if ( elements == null )
            return;

        for ( PdfPageElement c : elements )
        {
            if ( c != null )
                _elements.add( c );
            else
                LOG.warning( "Skipped null PageElement." );
        }
    }

    public Pos getAnchor()
    {
        return _anchor;
    }

    public void setAnchor( Pos anchor )
    {
        if ( _anchor == anchor )
            return;

        _anchor = anchor;

        invalidate();
    }

    protected List<PdfPageElement> getElements()
    {
        return _elements;
    }

    /**
     * add an element for display.
     * @param element
     *
     * @return The new element's index.
     */
    public void add( PdfPageElement element )
    {
        _elements.add( element );

        invalidate();
    }

    @Override
    public void paint( PdfGfx gfx2 ) throws Exception
    {
        if ( _elements.size() == 0 )
            return;

        try ( PdfGfx translated = (PdfGfx)gfx2.create(
                getX(),
                getY(),
                getWidth(),
                getHeight() ) )
        {
            for ( PdfPageElement c : _elements )
                c.paint( translated );
        }
    }

    /**
     * Has to set size and position of every component.
     * The passed elements list is never empty or null.
     */
    protected abstract void doLayout( List<PdfPageElement> elements );

    private void performLayout()
    {
        if ( _elements.size() == 0 )
        {
            setSize( 0, 0 );
            return;
        }

        doLayout( _elements );

        if ( super.getWidth() < 0 || super.getHeight() < 0 )
            throw new AssertionError( "doLayout() implementation error." );
    }

    private void invalidate()
    {
        setSize( -1, -1 );
    }

    @Override
    public float getWidth()
    {
        if ( _elements.size() == 0 )
            return 0.0f;

        float result = super.getWidth();

        if ( result >= 0 )
            return result;

        performLayout();

        return super.getWidth();
    }

    @Override
    public float getHeight()
    {
        if ( _elements.size() == 0 )
            return 0.0f;

        float result = super.getHeight();

        if ( result > 0 )
            return result;

        performLayout();

        return super.getHeight();
    }

    public float getGap()
    {
        return _gap;
    }

    public void setGap( float gap )
    {
        if ( gap == _gap )
            return;

        _gap = gap;

        invalidate();
    }

    /**
     * Creates an invisible filler of the requested size.
     *
     * @param w The filler width.
     * @param h The filler height.
     * @return A filler to be placed in a layout.
     */
    public static PdfPageElement createFiller( float w, float h )
    {
        if ( w <= 0 || h <= 0 )
            throw new IllegalArgumentException(
                    String.format( "Dimensions must be positive: w=%f, h=%f", w, h ) );

        return new PdfPageElement() {
            {
                setSize( w, h );
            }

            @Override
            public void paint( PdfGfx gfx2 ) throws Exception
            {
            }
        };
    }

    /**
     * Creates a square filler.
     * @param d The filler height and width.
     * @return A filler to be placed in a layout.
     */
    public static PdfPageElement createSquareFiller( float d )
    {
        return createFiller( d, d );
    }
}

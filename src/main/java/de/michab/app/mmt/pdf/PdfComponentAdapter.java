/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.util.logging.Logger;

import javax.swing.JComponent;


/**
 * Integrates a Swing component into a PDF page.
 *
 * @author Michael Binz
 */
public class PdfComponentAdapter extends PdfPageElement
{
    private static final Logger LOG =
            Logger.getLogger( PdfComponentAdapter.class.getName() );

    private final JComponent _component;

    /**
     * Create an instance.
     *
     * @param component The component to adapt.
     */
    public PdfComponentAdapter( JComponent component )
    {
        _component =
                component;
        setSize(
                component.getWidth(),
                component.getHeight() );
        setPosition(
                component.getX(),
                component.getY() );
    }

    @Override
    public void paint( PdfGfx gfx2 ) throws Exception
    {
        @SuppressWarnings("serial")
        JComponent parent = new JComponent()
        {
            @Override
            public java.awt.Graphics getGraphics() {
                return gfx2;
            };
        };

        parent.add( _component );

        if ( _component.getGraphics() != gfx2 )
            LOG.warning( "Grapics2D injection failed: " + _component.getGraphics() );

        try ( PdfGfx translatedGfx = (PdfGfx)gfx2.create(
                getX(),
                getY(),
                getWidth(),
                getHeight() ) )
        {
            _component.printAll( translatedGfx );
        }
        finally
        {
            parent.remove( _component );
        }
    }
}

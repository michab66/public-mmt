/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 *
 *
 * @author Michael Binz
 */
public class PdfImage extends PdfPageElement
{
    private final BufferedImage _icon;

    /**
     *
     * @param _document
     * @param icon
     */
    public PdfImage( PdfDocument _document, Image icon )
    {
        _icon = SwingFXUtils.fromFXImage( icon, null );

        try
        {
            PDImageXObject pm = LosslessFactory.createFromImage(
                    _document.getDelegate(),
                    _icon );

            setSize( pm.getWidth(), pm.getHeight() );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     * @param page
     * @param icon
     */
    public PdfImage( PdfPage page, Image icon )
    {
        this( page.getDocument(), icon );
    }

    @Override
    public final void paint( PdfGfx gfx2 ) throws Exception
    {
        gfx2.drawImage( _icon, (int)getX(), (int)getY(), null );
    }
}

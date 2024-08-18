/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.smack.fx.ActionFx;
import org.smack.util.JavaUtil;
import org.smack.util.TimeProbe;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * Show pdf-based help.
 *
 * @author Michael Binz
 */
public final class ScreenHelp extends ConfigScreen
{
    private static final Logger LOG =
            Logger.getLogger( ScreenHelp.class.getName() );

    /**
     * The page display component.
     */
    private final Label _pagePanel = new Label();

    /**
     * Holds the pages to display.
     */
    private final List<ImageView> _pages = new ArrayList<>();

    /**
     * The index of the currently displayed page.
     *
     *  @see #_pages
     */
    private int _currentPage;

    /**
     * TODO make smarter and better configurable. Works for now...
     */
    @Resource
    private String _resourceName;

    /**
     * Make an instance.
     *
     * @param previous
     * @param application
     */
    public ScreenHelp( Screen<Node> previous, Mmt application )
    {
        super( application, ColumnTension.TO_INNER, previous );
    }

    @Override
    protected Node initConfigScreen() throws Exception
    {
        PDDocument doc = null;

        try ( InputStream is = getClass().getResourceAsStream( _resourceName ) )
        {
            if ( is == null )
            {
                throw new AssertionError( "Not found: " + _resourceName );
            }

            doc =
            		Loader.loadPDF(is.readAllBytes());

            PDFRenderer pr =
                    new PDFRenderer( doc );

            for ( int i = 0 ; i < doc.getNumberOfPages() ; i++ )
            {
                TimeProbe tp = new TimeProbe( "Page " + i + " took: " ).start();

                BufferedImage bi = pr.renderImage( i );

                // We workaround a problem in OpenOffice pdf generation. A
                // pdf-page always has a white single pixel column to the
                // right and bottom. So we remove that here.
                bi = bi.getSubimage(
                        0,
                        0,
                        bi.getWidth()-1,
                        bi.getHeight()-1 );

                _pages.add( new ImageView( SwingFXUtils.toFXImage( bi, null ) ) );

                // ImageIO.write( bi, "png", new java.io.File("help"+i+".png") );

                LOG.info( tp.stop().toString() );
            }

            _currentPage =
                    0;
            _pagePanel.setGraphic( _pages.get( _currentPage ) );
        }
        finally
        {
        	JavaUtil.force( doc::close );
        }

        add(
                _pagePanel,
                Column.CENTER,
                Row.CENTER );

        return this;
    }

    @Override
    protected void actOk()
    {
        if ( _currentPage < (_pages.size()-1) )
        {
            _pagePanel.setGraphic( _pages.get( ++_currentPage ) );
            return;
        }

        super.actOk();
    }

    @Override
    protected void actCancel()
    {
        if ( _currentPage > 0 )
        {
            _pagePanel.setGraphic( _pages.get( --_currentPage ) );
            return;
        }

        super.actCancel();
    }

    @Override
    protected Node makeOkButton( ActionFx action )
    {
        return MmtButton.makeNextButton( action );
    }
}

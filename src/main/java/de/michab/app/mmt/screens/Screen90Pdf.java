/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.rendering.PDFRenderer;
import org.smack.fx.ActionFx;
import org.smack.util.JavaUtil;
import org.smack.util.TimeProbe;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.pdf.PdfDocument;
import de.michab.app.mmt.reports.MmtReport;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

/**
 * Do the PDF thing...
 *
 * @author Michael Binz
 */
public class Screen90Pdf extends BaseScreen<Mmt>
{
    private static final Logger LOG = Logger.getLogger( Screen90Pdf.class.getName() );

    private final Screen<Node> _previous;

    private Node _pagePositionDisplay;

    @Resource
    private String _pagePosition;

    public Screen90Pdf( Screen<Node> previous, Mmt application )
    {
        super( application, ColumnTension.TO_INNER );

        _previous = previous;
    }

    private final Label _pagePanel = new Label();
    private PdfDocument _inputPDF = null;
    private final List<BufferedImage> _pages = new ArrayList<>();
    private int _currentPage;

    @Override
    protected Node initScreen() throws Exception
    {
        add(
                MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );
        add(
                MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER,
                Row.BUTTON_NEXT );
        {
            _inputPDF =
                    MmtReport.generateReport( getApplication() );

            PDFRenderer pr = new PDFRenderer( _inputPDF.getDelegate() );

            for ( int i = 0 ; i < _inputPDF.getPages().size() ; i++ )
            {
                TimeProbe tp = new TimeProbe( "Page " + i + " took: " ).start();
                _pages.add( pr.renderImage( i ) );
                LOG.warning( tp.stop().toString() );
            }

            _currentPage =
                    0;

            _pagePanel.setGraphic( getPage( _currentPage ) );

            add( _pagePanel, Column.CENTER, Row.CENTER );
        }

        resetPagePositionDisplay();

        return this;
    }

    private ImageView getPage( int pageNumber ) throws IOException
    {
        BufferedImage image =
                _pages.get( pageNumber );
        WritableImage fxImage =
                SwingFXUtils.toFXImage( image, null );
        return new ImageView( fxImage );
    }

    private void actNext()
    {
        if ( _currentPage < (_pages.size()-1) )
        {
            try
            {
                _pagePanel.setGraphic(
                        getPage( ++_currentPage ) );
                resetPagePositionDisplay();

                return;
            }
            catch ( IOException e )
            {
                LOG.log( Level.WARNING, "Page display failed.", e );
            }
        }

        Mmt app = getApplication();
        app.setScreen(
                () -> new Screen91PdfFinish( this, app, _inputPDF.getDelegate() ) );
    }

    private void actPrevious()
    {
        if ( _currentPage > 0 )
        {
            try
            {
                _pagePanel.setGraphic(
                        getPage( --_currentPage ) );
                resetPagePositionDisplay();
                return;
            }
            catch ( IOException e )
            {
                LOG.log( Level.WARNING, "Page display failed.", e );
            }
        }

        JavaUtil.force( _inputPDF::close );

        getApplication().setScreen( _previous );
    }

    /**
     * Format the page position text. Something like 'Page 1 of 2'.
     */
    private String makePagePositionText()
    {
        return String.format(
                _pagePosition,
                1+ _currentPage,
                _pages.size() );
    }

    private void resetPagePositionDisplay()
    {
        if ( _pagePositionDisplay != null )
        {
            removeLayoutComponent( _pagePositionDisplay );
        }

        _pagePositionDisplay =
                makeHeaderRichText( makePagePositionText() );

        add(
                _pagePositionDisplay,
                Column.START,
                Row.HEAD );
    }
}

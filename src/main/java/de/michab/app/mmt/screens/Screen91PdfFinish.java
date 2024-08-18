/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.smack.fx.ActionFx;
import org.smack.util.JavaUtil;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Tenant;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.util.MmtUtils;
import javafx.scene.Node;

/**
 * Near the end.
 *
 * @author Michael Binz
 */
public class Screen91PdfFinish extends BaseScreen<Mmt>
{
    private static final Logger LOG =
            Logger.getLogger( Screen91PdfFinish.class.getName() );

    private final Screen<Node> _previous;

    private final PDDocument _pdf;

    private PrintService _defaultPrinter =
            PrintServiceLookup.lookupDefaultPrintService();

    @Resource
    private String _header;

    @Resource
    private String _headerLeft;
    @Resource
    private String _footerLeft;
    @Resource
    private String _followupLeft;

    @Resource
    private String _headerMiddle;
    @Resource
    private String _footerMiddle;
    @Resource
    private String _followupMiddle;

    @Resource
    private String _headerRight;
    @Resource
    private String _footerRight;


    /**
     *
     * @param previous
     * @param application
     * @param pdf
     */
    public Screen91PdfFinish( Screen<Node> previous, Mmt application, PDDocument pdf )
    {
        super( application );

        pdf.getClass();

        _previous = previous;
        _pdf = pdf;
    }

    @Override
    protected Node initScreen()
    {
        // Screen header.
        add( makeScreenHeader( _header ),
                Column.CENTER,
                Row.SCREEN_HEAD );

        // Left column.
        {
            Node c = MmtButton.makeButton(
                    MmtButton.DIMENSION_L,
                    new ActionFx( this::actSave ).inject( getClass(), "actSave" ),
                    true );

            add( makeHeader( _headerLeft ), Column.START, Row.HEAD );
            add( c, Column.START, Row.CENTER );
            add( makeFooter( _footerLeft ), Column.START, Row.FOOT );
            setFocusComponent( c );
        }

        // Center column.
        {
            Node c = MmtButton.makeButton(
                    MmtButton.DIMENSION_L,
                    ACT_PRINT,
                    true );

            add( makeHeader( _headerMiddle ), Column.CENTER, Row.HEAD );
            add( c, Column.CENTER, Row.CENTER );
            add( makeFooter( _footerMiddle ), Column.CENTER, Row.FOOT );

            // Printing is only enabled if we have a default printer.
            ACT_PRINT.enabledProperty.set( _defaultPrinter != null );
        }

        // Right column.
        {
            Node c = MmtButton.makeNextButton(
                    new ActionFx( this::actRight ) );
            add( makeHeader( _headerRight ), Column.END, Row.HEAD );
            add( c,
                    Column.END,
                    Row.CENTER );
            add( makeFooter( _footerRight ), Column.END, Row.FOOT );
        }
        // Common back button.
        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );

        return this;
    }

    @Override
    public void leaveScreen( Screen<Node> next )
    {
        // Do not release the pdf file if we go back.
        if ( next != _previous )
        {
            JavaUtil.force( _pdf::close );
        }

        super.leaveScreen( next );

        final Tenant tenant =
                getApplication().getTenant();

        final String statusMessage =
                MmtUtils.formatTenant( tenant );

        getApplication().showStatusMessage( statusMessage );
    }

    /**
     * Saves to pdf.
     */
    private void actSave()
    {
        File targetFile = MmtUtils.fileFromPatient(
                getApplication().getPatient(),
                "pdf" );

        try
        {
            _pdf.save( targetFile );

            LOG.info( "Wrote file: " + targetFile.getPath() );
        }
        catch ( IOException e )
        {
            // TODO goto error screen.
            LOG.log( Level.WARNING, "Writing file failed: " + targetFile.getPath(), e );
        }

        String message = String.format( _followupLeft, targetFile.getPath() );

        Toolkit.getDefaultToolkit().beep();

        getApplication().showStatusMessage( message );
    }

    private ActionFx ACT_PRINT =
            new ActionFx( this::actPrint ).inject( getClass(), "actPrint" );

    private void actPrint()
    {
        try
        {
            DocPrintJob pj = _defaultPrinter.createPrintJob();

            Doc dpj = new SimpleDoc(
                    new PDFPageable( _pdf ),
                    DocFlavor.SERVICE_FORMATTED.PAGEABLE,
                    null );

            pj.print( dpj, null );
        }
        catch ( Exception e )
        {
            // TODO goto error screen.
            LOG.log(
                    Level.WARNING,
                    "Printing failed: " + _defaultPrinter.getName(),
                    e );
        }

        getApplication().showStatusMessage(
                String.format(
                        _followupMiddle,
                        _defaultPrinter.getName() ) );
    }

    private void actRight()
    {
        getApplication().setPatient(
                null );
        getApplication().setScreen(
                () -> new Screen01Patient( getApplication() ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

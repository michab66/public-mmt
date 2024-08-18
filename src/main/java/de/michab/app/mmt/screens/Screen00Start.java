/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.logging.Logger;

import org.smack.fx.ActionFx;
import org.smack.util.TimeProbe;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Tenant;
import de.michab.app.mmt.pdf.PdfDocument;
import de.michab.app.mmt.pdf.PdfPage;
import de.michab.app.mmt.pdf.PdfText;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.util.MmtUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The welcome screen.
 *
 * @author Michael Binz
 */
public class Screen00Start extends BaseScreen<Mmt>
{
    private static final Logger LOG =
            Logger.getLogger( Screen00Start.class.getName() );

    private final ActionFx ACT_NEXT = new ActionFx( this::actNext );

    /**
     * Create an instance.
     *
     * @param application The application to use.
     */
    public Screen00Start( Mmt application )
    {
        super( application );
    }

    @Override
    protected final Node initScreen()
    {
        getApplication().setPatient( null );

        setStatusbarVisible( true );

        add(
                new ImageView( _image ),
                Column.CENTER,
                Row.CENTER );

        add(
                MmtButton.makeNextButton( ACT_NEXT ),
                Column.CENTER,
                Row.BUTTON_NEXT );

        add(
                makeFooter( " " ),
                Column.CENTER,
                Row.FOOT );

        // This is experimental support for multi-screen
        // window dragging.
//        this.setOnDragDetected(
//                this::consoleDragDetected );

        return this;
    }

//    private void consoleDragDetected( MouseEvent e )
//    {
//        System.out.println( "Drag detected2." );
//        setOnMouseReleased( this::consoleDragEnd );
//        e.consume();
//
//    }
//    private void consoleDragEnd( MouseEvent e )
//    {
//        System.out.println( "Drag end." );
//
//        for ( javafx.stage.Screen c :  javafx.stage.Screen.getScreens() )
//        {
//            Rectangle2D bounds = c.getBounds();
//            if ( ! bounds.contains( e.getScreenX(), e.getScreenY() ) )
//                continue;
//
//            Stage s = getApplication().getStage();
//            System.out.println( s.getX() + " " + s.getY()  );
//
//            System.out.println( "Bounds: " + bounds );
//            System.out.printf( "Stage x=%f y=%f\n", s.getX(), s.getY() );
//            System.out.println( "Contains: " +
//                    bounds.contains( s.getX(),s.getY() )  );
//
//            s.setFullScreen( false );
//            s.setX( bounds.getMinX() );
//            s.setY( bounds.getMinY() );
//            s.centerOnScreen();
//            s.setFullScreen( true );
//
//            break;
//        }
//
//        setOnMouseReleased( null );
//        e.consume();
//    }

    @Override
    public void enterScreen( Screen<Node> previous )
    {
        super.enterScreen( previous );

        getApplication().onFirstScreenProperty.set( true );

        final Tenant tenant =
                getApplication().getTenant();

        final String statusMessage =
                MmtUtils.formatTenant( tenant );

        getApplication().showStatusMessage( statusMessage );

        // Start a thread that touches (and thus loads) the PDF classes in the
        // background.  This results later, if we show the real PDF reports,
        // in much better user performance.
        {
            final Thread t = new Thread( () -> warmUpPdf() );
            t.setDaemon( true );
            t.setPriority( Thread.NORM_PRIORITY-1 );
            t.start();
        }
    }

    @Override
    public void leaveScreen( Screen<Node> next )
    {
        super.leaveScreen( next );

        getApplication().onFirstScreenProperty.set( false );
    }

    private void actNext( ActionEvent ae )
    {
        getApplication().setScreen(
                () -> new Screen01Patient( getApplication() ) );
    }

    private void setStatusbarVisible( boolean what )
    {
        getApplication().toolbarVisibleProperty.set( what );
    }

    @Resource(name="Screen00Start._image")
    private Image _image;

    private void warmUpPdf()
    {
        final TimeProbe tp = new TimeProbe( Thread.currentThread().getName() ).start();

        // We create a dummy document.
        final PdfDocument warmupDoc = new PdfDocument(
                PdfPage.Size.A4,
                PdfPage.Orientation.LANDSCAPE );

        final PdfPage page = warmupDoc.createPage();

        page.add( new PdfText( getApplication().getTitle() ) );

        warmupDoc.paint();

        warmupDoc.close();

        LOG.info( tp.stop().toString() );
    };
}

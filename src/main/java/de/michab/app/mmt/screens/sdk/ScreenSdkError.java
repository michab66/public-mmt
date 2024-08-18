/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.fx.ActionFx;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.screens.Screen00Start;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;

/**
 * Handle an error.
 *
 * @author Michael Binz
 */
public class ScreenSdkError extends BaseScreen<Mmt>
{
    private static final Logger LOG =
            Logger.getLogger( ScreenSdkError.class.getName() );

    private final Screen<Node> _previous;

    private final Throwable _exception;

    public ScreenSdkError( Screen<Node> previous, Mmt application, Throwable e )
    {
        super( application, ColumnTension.TO_INNER );

        _previous = previous;
        _exception = e;

        // Write the exception into the log file.
        LOG.log( Level.SEVERE, "Message:" + e.getMessage(), e );
    }

    @Override
    protected Node initScreen()
    {
//        add( makeScreenHeader( "Something bad has happened..." ),
//                Column.CENTER, Row.SCREEN_HEAD );

        {
            Text c = new Text( toString( _exception ) );
//            c.setLineWrap( true ) TODO important;
            //c.setForeground( ScreenUtils.getForeground() );

            ScrollPane sp = new ScrollPane(
                    c );
//                    ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
//                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

//            // TODO Would be handier to have something like 75% of height.
//            {
//                int size = Geometry.cmToPx(
//                        WheelModel.getWheelDiameterCm() );
//                Dimension d = new Dimension(
//                        size,
//                        Math.round(size * 0.75f) );
//                sp.setPreferredSize( d );
//            }
//
//            // http://stackoverflow.com/questions/11717577/java-how-to-make-containers-jscrollpane-background-not-opaque-ie-transparen
//            if ( sp.isOpaque() )
//                sp.setOpaque( false );
//            if ( sp.getViewport().isOpaque() )
//                sp.getViewport().setOpaque( false );
//            sp.setBorder( null );

            // Way cool: http://stackoverflow.com/questions/4298582/jscrollpane-scrolling-with-arrow-keys
            // Note the tool 'KeyBindings'.
//            InputMap im = sp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//            im.put(KeyStroke.getKeyStroke("DOWN"), "unitScrollDown");
//            im.put(KeyStroke.getKeyStroke("UP"), "unitScrollUp");

            add( sp,
                Column.CENTER, Row.CENTER );
        }

//        add( new JLabel( MmtButton.makeFatalIcon() ), Column.START, Row.CENTER );

        add( MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER, Row.BUTTON_NEXT );
        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER, Row.BUTTON_PREV );

        return this;
    }

    private void actNext()
    {
        getApplication().setScreen(
                () -> new Screen00Start( getApplication() ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen(
                () -> _previous );
    }

    /**
     * Convert an exception to its string representation.
     * TODO consider move to mack.
     *
     * @param e The original exception.
     * @return The converted exception.
     */
    private static String toString( Throwable e )
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

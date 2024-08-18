/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.Objects;

import org.smack.fx.ActionFx;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.MmtEyeBar;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.scene.Node;

/**
 * Test intro screen.
 *
 * @author Michael Binz
 */
public class Screen03 extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    private final Side _side;

    /**
     * Create an instance.
     *
     * @param previous The previous screen.
     * @param application The hosting application.
     * @param side The side to test.
     */
    public Screen03( Screen<Node> previous, Mmt application, Side side )
    {
        super( Objects.requireNonNull( application ) );

        _previous =
                Objects.requireNonNull( previous );
        _side =
                Objects.requireNonNull( side );
    }

    @Override
    protected Node initScreen()
    {
        add( makeScreenHeader( getHeader() ),
                Column.CENTER,
                Row.SCREEN_HEAD );
        add( new MmtEyeBar( _side ),
                Column.CENTER,
                Row.CENTER );
        add( MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER,
                Row.BUTTON_NEXT );
        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );

        return this;
    }

    private String getHeader()
    {
        switch ( _side )
        {
        case BOTH:
            return _headerRala;
        case LEFT:
            return _headerLa;
        case RIGHT:
            return _headerRa;
        default:
            throw new IllegalArgumentException( "_side is " + _side );
        }
    }

    @Resource
    private String _headerRa;
    @Resource
    private String _headerLa;
    @Resource
    private String _headerRala;

    private void actNext()
    {
        Mmt app = getApplication();

        app.setScreen(
                () -> new Screen05TestPreparation( this, app, _side ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

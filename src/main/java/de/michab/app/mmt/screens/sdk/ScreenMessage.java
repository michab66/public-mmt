/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import org.smack.fx.ActionFx;

import de.michab.app.mmt.Mmt;
import javafx.scene.Node;

/**
 * Settings screen.
 *
 * @author Michael Binz
 */
public class ScreenMessage extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    private final String _message;

    /**
     * Create an instance.
     *
     * @param previous The previous screen.
     * @param application The application
     * @param source The source action for management of the enabled state.
     */
    public ScreenMessage( Screen<Node> previous, Mmt application, String message )
    {
        super( application );

        _previous = previous;
        _message = message;
    }

    @Override
    protected Node initScreen() throws Exception
    {
        add( makeHeader( _message ),
                Column.CENTER,
                Row.HEAD );

        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );

        return this;
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

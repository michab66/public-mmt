/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import org.smack.fx.ActionFx;
import org.smack.fx.FxUtil;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.screens.ScreenUtils;
import javafx.scene.Node;

/**
 * An MMT configuration screen.
 *
 * @author Michael Binz
 */
public abstract class ConfigScreen extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    public ConfigScreen( Mmt application, ColumnTension columnAlignment, Screen<Node> previous )
    {
        super( application, columnAlignment );
        _previous = previous;
    }

    public ConfigScreen( Mmt application, Screen<Node> previous )
    {
        super( application );
        _previous = previous;
    }

    /**
     * Default init screen implementation. Subclasses have to
     * override {@link #initConfigScreen()}.
     */
    @Override
    protected final Node initScreen() throws Exception
    {
        setBackground( FxUtil.getBackground( ScreenUtils.getLowContrastFx() ) );

        add( makeOkButton( ACT_OK ),
                Column.CENTER, Row.BUTTON_NEXT );
        add( makeCancelButton( ACT_CANCEL ),
                Column.CENTER, Row.BUTTON_PREV );

        return initConfigScreen();
    }

    /**
     * Template method to configure the ok button.  The default implementation
     * uses {@link MmtButton#makeOkButtonFx(ActionFx)}.
     */
    protected Node makeOkButton( ActionFx action )
    {
        return MmtButton.makeOkButton( action );
    }

    /**
     * Template method to configure the cancel button.  The default
     * implementation
     * uses {@link MmtButton#makePreviousButton(ActionFx))}.
     */
    protected Node makeCancelButton( ActionFx action )
    {
        return MmtButton.makePreviousButton( action );
    }

    /**
     * The OK action. Subclasses may adjust the enabled state.
     */
    protected final ActionFx ACT_OK =
            new ActionFx( this::actOk );

    /**
     * Default OK action implementation. Leaves screen to previous.
     */
    protected void actOk()
    {
        getApplication().setScreen( _previous );
    }

    private final ActionFx ACT_CANCEL =
            new ActionFx( this::actCancel );

    /**
     * Default cancel action implementation. Leaves screen to previous.
     */
    protected void actCancel()
    {
        getApplication().setScreen( _previous );
    }

    /**
     * Perform screen-specific initialization.
     */
    abstract protected Node initConfigScreen() throws Exception;
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

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
 * Test scope screen.
 *
 * @author Michael Binz
 */
public class Screen02TestScope extends  BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    public Screen02TestScope( Screen<Node> previous, Mmt application )
    {
        super( application );
        _previous = previous;
    }

    @Override
    protected Node initScreen()
    {
        add( makeScreenHeader( _header ), Column.CENTER, Row.SCREEN_HEAD );

        add( makeHeader( _headerRa ),
                Column.START,
                Row.HEAD );

        add( new MmtEyeBar( Side.RIGHT ),
                Column.START,
                Row.CENTER );
        add( makeFooter( _footerRa ),
                Column.START,
                Row.FOOT );
        add( MmtButton.makeNextButton( new ActionFx( this::actRa ) ),
                Column.START,
                Row.BUTTON_NEXT );

        add( makeHeader( _headerRaLa ),
                Column.CENTER,
                Row.HEAD );

        add( new MmtEyeBar( Side.BOTH ),
                Column.CENTER,
                Row.CENTER );
        add( makeFooter( _footerRaLa ),
                Column.CENTER,
                Row.FOOT );
        add( MmtButton.makeNextButton( new ActionFx( this::actRaLa ) ),
                Column.CENTER,
                Row.BUTTON_NEXT );

        add( makeHeader( _headerLa ),
                Column.END,
                Row.HEAD );
        add( new MmtEyeBar( Side.LEFT ),
                Column.END,
                Row.CENTER );
        add( makeFooter( _footerLa ),
                Column.END,
                Row.FOOT );
        add( MmtButton.makeNextButton( new ActionFx( this::actLa ) ),
                Column.END,
                Row.BUTTON_NEXT );

        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );

        return this;
    }

    @Resource
    private String _header;

    @Resource
    private String _headerRa;
    @Resource
    private String _footerRa;

    private void actRa()
    {
        Mmt app = getApplication();
        app.setScreen( () -> new Screen03( this, app, Side.RIGHT ) );
    }

    @Resource
    private String _headerRaLa;
    @Resource
    private String _footerRaLa;

    private void actRaLa()
    {
        Mmt app = getApplication();
        app.setScreen( () -> new Screen03( this, app, Side.BOTH ) );
    }

    @Resource
    private String _headerLa;
    @Resource
    private String _footerLa;

    private void actLa()
    {
        Mmt app = getApplication();
        app.setScreen( () -> new Screen03( this, app, Side.LEFT ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

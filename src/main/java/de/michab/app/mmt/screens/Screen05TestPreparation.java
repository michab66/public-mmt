/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.ArrayList;
import java.util.List;

import org.smack.fx.ActionFx;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.dm.Patient;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.scene.Node;

/**
 * Test preparation screen.
 *
 * @author Michael Binz
 */
public class Screen05TestPreparation extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    private final Side _side;

    public Screen05TestPreparation( Screen<Node> previous, Mmt application, Side side )
    {
        super( application );

        _previous = previous;
        _side = side;
    }

    @Override
    protected Node initScreen()
    {
        add( makeScreenHeader( _header ),
                Column.CENTER, Row.SCREEN_HEAD );
        add( makeHeaderRichText( _body ),
                Column.CENTER, Row.CENTER );
        add( MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER, Row.BUTTON_NEXT );
        add( MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER, Row.BUTTON_PREV );

        return this;
    }

    @Resource
    private String _header;

    @Resource
    private String _body;

    private void actNext()
    {
        Patient patient =
                getApplication().getPatient();
        List<Experiment> toPerform =
                new ArrayList<>();

        if ( _side == Side.BOTH )
        {
            toPerform.add( new Experiment( patient, Side.RIGHT, Contrast.HI ) );
            toPerform.add( new Experiment( patient, Side.RIGHT, Contrast.LO ) );
            toPerform.add( new Experiment( patient, Side.LEFT, Contrast.HI ) );
            toPerform.add( new Experiment( patient, Side.LEFT, Contrast.LO ) );
        }
        else if ( _side == Side.LEFT )
        {
            toPerform.add( new Experiment( patient, Side.LEFT, Contrast.HI ) );
            toPerform.add( new Experiment( patient, Side.LEFT, Contrast.LO ) );
        }
        else if ( _side == Side.RIGHT )
        {
            toPerform.add( new Experiment( patient, Side.RIGHT, Contrast.HI ) );
            toPerform.add( new Experiment( patient, Side.RIGHT, Contrast.LO ) );
        }
        else
        {
            throw new AssertionError();
        }

        Mmt app = getApplication();
        app.setScreen(
                () -> new Screen06TestPreparationReally(
                        this,
                        app,
                        toPerform,
                        1 ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.List;

import org.smack.fx.ActionFx;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.MmtEyeBar;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.scene.Node;

/**
 * Test preparation screen.
 *
 * @author Michael Binz
 */
public class Screen06TestPreparationReally extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    private final List<Experiment> _tests;
    private final Experiment _currentTest;

    /**
     * The test number, this is one-based.
     */
    private final int _number;

    public Screen06TestPreparationReally(
            Screen<Node> previous,
            Mmt application,
            List<Experiment> tests,
            int testNumber )
    {
        super( application );

        _previous = previous;
        _tests = tests;
        _number = testNumber;
        _currentTest = _tests.get( testNumber-1 );
    }

    @Override
    protected final Node initScreen()
    {
        add( makeScreenHeader(
                String.format(
                        _header,
                        _whatTest[_number-1],
                        _ofHowMany[ _tests.size()-1] ) ),
                Column.CENTER, Row.SCREEN_HEAD );

        add( makeHeader( getBody() ),
                Column.CENTER, Row.HEAD );

        Side side = _currentTest.getEye();
        if ( _tests.size() == 4 )
        {
            side = Side.BOTH;
        }

        add( new MmtEyeBar( side ).mark( _number ),
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
    private String _bodyLa;
    @Resource
    private String _bodyRa;

    private String getBody()
    {
        switch ( _currentTest.getEye() )
        {
        case LEFT:
            return _bodyLa;
        case RIGHT:
            return _bodyRa;
        default:
            throw new AssertionError();
        }
    }

    @Resource
    private String[] _whatTest;
    @Resource
    private String[] _ofHowMany;

    private void actNext()
    {
        Mmt app = getApplication();
        app.setScreen(
                () -> new Screen08PreTest( this, app, _tests, _number ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

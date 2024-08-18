/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.List;
import java.util.function.Supplier;

import org.smack.fx.ActionFx;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.components.MmtLegend;
import de.michab.app.mmt.components.MmtTestResultFx;
import de.michab.app.mmt.components.ResultWheelFx;
import de.michab.app.mmt.components.WheelLayoutPane;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Probe;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.MmtEyeBar;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.screens.sdk.ScreenMessage;
import de.michab.app.mmt.util.MmtUtils;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.StackPane;

/**
 * After a test.
 *
 * @author Michael Binz
 */
public class Screen10TestResults extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;
    private final List<Experiment> _tests;
    private final Experiment _currentTest;

    private final int _number;

    @Resource
    private String _noValidTestFound;

    @Resource
    private String _score;

    public Screen10TestResults(
            Screen<Node> previous,
            Mmt application,
            List<Experiment> tests,
            int testNumber )
    {
        super( application, ColumnTension.TO_INNER );

        _previous = previous;
        _tests = tests;
        _number = testNumber;
        _currentTest = _tests.get( testNumber-1 );
    }

    @Override
    protected Node initScreen()
    {
            add(
                    makeHeaderRichText(
                            String.format(
                                    _score,
                                    MmtUtils.formatScore(
                                            _currentTest.getScore() ) ) ),
                    Column.START,
                    Row.HEAD );
            add(
                    MmtEyeBar.getIcon( _currentTest ),
                    Column.START,
                    Row.CENTER );

        ResultWheelFx resultWheel =
                new ResultWheelFx();
        WheelLayoutPane wheelLayoutPane =
                new WheelLayoutPane( resultWheel, true );

        // TODO twenty eight is arbitrary.  Can this be computed intelligently?
        double resultHeightPx = resultWheel.getHeight() / 28;

        for ( Probe c : _currentTest.getProbes() )
        {
            MmtTestResultFx testResult =
                    new MmtTestResultFx( c.getScore(), resultHeightPx );

            testResult.resizableProperty.set( false );

            wheelLayoutPane.addLayoutComponent(
                    testResult,
                    c.getPosition() );
        }

        StackPane stack = new StackPane(
                resultWheel,
                wheelLayoutPane );
        add(
                stack,
                Column.CENTER,
                Row.CENTER );
        add(
                MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER,
                Row.BUTTON_NEXT );

        add(
                new MmtLegend(),
                Column.END,
                Row.CENTER );

        {
            MmtButton node = MmtButton.makeTextButton(
                    new ActionFx( this::actPrevious ).inject(
                            getClass(), "actPrevious" ),
                    true );

            node.setAccelerator(
                    new KeyCodeCombination( KeyCode.BACK_SPACE ) );

            add(
                    node,
                    Column.CENTER,
                    Row.BUTTON_PREV );
        }

        return this;
    }

    private void actNext()
    {
        int testNumber = _number +1;

        final Mmt app = getApplication();

        Supplier<Screen<Node>> component = null;
        if ( testNumber >  _tests.size() )
        {
            int count = 0;
            for ( Experiment c : _tests )
            {
                if ( c.isTestComplete() )
                {
                    app.getPatient().addExperiment( c );
                    count++;
                }
            }

            if ( count > 0 )
            {
                // Exit to pdf generation.
                component = () -> new Screen90Pdf( this, app );
            }
            else
            {
                component = () -> new ScreenMessage( this, app, _noValidTestFound );
            }
        }
        else
        {
            component = () -> new Screen06TestPreparationReally( this, app, _tests, testNumber );
        }

        app.setScreen( component );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

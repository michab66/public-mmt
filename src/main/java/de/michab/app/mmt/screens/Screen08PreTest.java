/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.List;

import org.smack.fx.ActionFx;
import org.smack.fx.FxUtil;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.components.Wheel;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.MmtEyeBar;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * The last stop in front of a test.
 *
 * @author Michael Binz
 */
public class Screen08PreTest extends BaseScreen<Mmt>
{
    private final Screen<Node> _previous;

    private final List<Experiment> _tests;
    private final int _number;

    @Resource
    private String _headerRight;
    @Resource
    private String _centerRight;

    public Screen08PreTest(
            Screen<Node> previous,
            Mmt application,
            List<Experiment> tests,
            int testNumber )
    {
        super( application, ColumnTension.TO_INNER );

        _previous = previous;
        _tests = tests;
        _number = testNumber;
    }

    @Override
    protected Node initScreen()
    {
        Experiment experiment = _tests.get( _number-1 );

        add(
                MmtEyeBar.getIcon( experiment ),
                Column.START,
                Row.CENTER );

        add(
                MmtButton.makePreviousButton( new ActionFx( this::actPrevious ) ),
                Column.CENTER,
                Row.BUTTON_PREV );

        add(
                MmtButton.makeNextButton( new ActionFx( this::actNext ) ),
                Column.CENTER,
                Row.BUTTON_NEXT );

        {
            Wheel wheel = new Wheel(
                    FxUtil.to( ScreenUtils.getForeground() ) );
            wheel.setFocusTraversable( false );
            add( new StackPane( wheel ), Column.CENTER, Row.CENTER );
        }

        add(
                new VBox(
                        makeHeaderRichText( _headerRight ),
                        makeFooterRichText( _centerRight ) ),
                Column.END,
                Row.CENTER );

        return this;
    }

    private void actNext()
    {
        Mmt app = getApplication();

        app.setScreen( () -> new Screen09Test( this, app, _tests, _number ) );
    }

    private void actPrevious()
    {
        getApplication().setScreen( _previous );
    }
}

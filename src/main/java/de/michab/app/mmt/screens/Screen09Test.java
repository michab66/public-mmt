/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.awt.Color;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.smack.fx.FxUtil;
import org.smack.util.StringUtil;
import org.smack.util.TimeProbe;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.components.MmtProbe;
import de.michab.app.mmt.components.Wheel;
import de.michab.app.mmt.components.WheelLayout;
import de.michab.app.mmt.components.WheelLayoutPane;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Probe;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.util.ShapeSet;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 * The core test implementation.
 *
 * @author Michael Binz
 */
public class Screen09Test extends  BaseScreen<Mmt>
{
    private final static Logger LOG =
            Logger.getLogger( Screen09Test.class.getName() );

    private final List<Experiment> _tests;
    private final Experiment _currentTest;

    private final int _number;
    private final Screen<Node> _previous;

    private final Color _testColor;

    private ShapeSet _shapeSet;

    public Screen09Test(
            Screen<Node> previous,
            Mmt application,
            List<Experiment> tests,
            int testNumber )
    {
        super( application );

        _tests = tests;
        _number = testNumber;
        _currentTest = tests.get( testNumber-1 );

        _previous = previous;

        _testColor = ScreenUtils.getTestColor( _currentTest.getContrast() );

        _originalCursor = getCursor();
    }

    @Override
    protected Node initScreen()
    {
        _wheel.addEventFilter(
                javafx.scene.input.KeyEvent.ANY,
                _testStarter );
        StackPane stack = new StackPane(
                _wheel,
                _wheelLayout );
        add(
                stack,
                Column.CENTER,
                Row.CENTER );

        return this;
    }

    @Override
    public void enterScreen( Screen<Node> previous )
    {
        super.enterScreen( previous );
        // Make sure we set the shape set only once on entering.
        _shapeSet = getApplication().getShapeSet();

        setCursor( Cursor.NONE );

        getApplication().toolbarVisibleProperty.set( false );

        // Initialize the test sets (let the games begin).
        _probesRemaining =
                new ArrayList<>( CONSTRAINT_VALUES );
        Collections.shuffle(
                _probesRemaining );
        _probesTaken =
                new ArrayList<>( _probesRemaining.size() );
    }

    @Override
    public void leaveScreen( Screen<Node> previous )
    {
        getApplication().toolbarVisibleProperty.set( true );

        setCursor( _originalCursor );
    }

    private final Wheel _wheel =
            new Wheel( FxUtil.to( ScreenUtils.getForeground() ) );

    private final WheelLayoutPane _wheelLayout =
            new WheelLayoutPane( _wheel );

    /**
     * The set of possible positions.
     */
    private final static Set<String> CONSTRAINT_VALUES =
            Collections.unmodifiableSet( WheelLayout.constraints.getValues() );

    /**
     * The positions of the remaining tests.  This is equivalent to
     * the constraints.
     */
    private List<String> _probesRemaining = null;

    /**
     * The trials already performed.
     */
    private List<MmtProbe> _probesTaken = null;

    private void start()
    {
        _wheel.removeEventFilter(
                javafx.scene.input.KeyEvent.ANY,
                _testStarter );

        _wheel.addEventFilter(
                javafx.scene.input.KeyEvent.ANY,
                _finishTestStep );

        performTestStep();
    }

    /**
     * Terminates a test.
     * @param validTest If true this was a valid test.
     */
    private void nextScreen( boolean validTest )
    {
        toExperiment(
                validTest ?
                    _probesTaken :
                    null );

        final Mmt app = getApplication();

        app.setScreen( () -> new Screen10TestResults(
                _previous,
                app,
                _tests,
                _number ) );
    }

    /**
     * Starts the test by waiting for an initial key from the user and
     * starting the test. The escape key skips the test.
     */
    private final EventHandler<KeyEvent> _testStarter = this::testStarter;
    private void testStarter( KeyEvent e )
    {
        if ( e.getEventType() == KeyEvent.KEY_TYPED &&
                StringUtil.hasContent(e.getCharacter(), false ) )
        {
            // All key typed events start the test.
            start();
        }
        else if ( e.getEventType() == KeyEvent.KEY_RELEASED )
        {
            if ( KeyCode.ESCAPE.equals( e.getCode() ) )
            {
                e.consume();
                // Reaching this means the user has explicitly skipped this
                // test.
                nextScreen( false );
                return;
            }

            if ( e.getCode() != KeyCode.F12 )
            {
                return;
            }
            e.consume();

            debugFill();

            start();
        }
    }

    /**
     * Finalizes a test step by handling the answer typed by the user.
     */
    private final EventHandler<KeyEvent> _finishTestStep = this::finishTestStep;
    private void finishTestStep( KeyEvent e )
    {
    	if ( e.getEventType() == KeyEvent.KEY_RELEASED &&
    			KeyCode.ESCAPE.equals( e.getCode() ) )
    	{
    		// Remove the last test if canceling.
    		_probesTaken.remove( 0 );

    		nextScreen( true );
    		return;
    	}

        if ( e.getEventType() != KeyEvent.KEY_TYPED )
        {
            return;
        }

        String key =  e.getCharacter();
        if ( StringUtil.isEmpty(key, false) )
        {
            return;
        }

        char c = key.charAt( 0 );

        // Beep after entering the user response.
        Toolkit.getDefaultToolkit().beep();

        c = Character.toUpperCase( c );

        _probesTaken.get( 0 ).setResult( c );

        if ( _probesRemaining.isEmpty() )
        {
            _wheel.removeEventFilter(
                    KeyEvent.KEY_RELEASED,
                    _finishTestStep );

            nextScreen( true );
            return;
        }

        performTestStep();
    }

    /**
     * Performs a single test step.
     */
    private void performTestStep()
    {
        String pos = Objects.requireNonNull(
                _probesRemaining.remove( 0 ) );
        MmtProbe probe = new MmtProbe( pos, _shapeSet );
        _probesTaken.add( 0, probe );

        probe.setVisible( false );
        probe.setForeground( _testColor );

        LOG.info( "Adding at " + probe.getProbe().getPosition() );
        _wheelLayout.addLayoutComponent( probe, probe.getProbe().getPosition() );

        new ProbeTimer(
                INITIAL_DELAY_MS,
                DISPLAY_DURATION_MS,
                probe )
            .start();
    }

    /**
     *
     * @param from Null is allowed.
     */
    private void toExperiment( List<MmtProbe> from )
    {
        final Mmt app = getApplication();

        if ( from == null )
        {
            from = Collections.emptyList();
        }

        _currentTest.setShapeSet( app.getShapeSet() );

        List<Probe> probes = new ArrayList<>();

        for ( MmtProbe c : from )
        {
            probes.add( c.getProbe() );
        }

        _currentTest.setProbes( probes );
    }

    @Resource
    private int INITIAL_DELAY_MS;
    @Resource
    private int DISPLAY_DURATION_MS;

    private final Cursor _originalCursor;

    @Override
    public Node getFocusComponent()
    {
        return _wheel;
    }

    private class ProbeTimer extends AnimationTimer
    {
        boolean __isInitialised = false;
        long __delay;
        long __hold;
        final Node __node;

        public ProbeTimer( int delay, int hold, Node node )
        {
            __node =
                    node;
            __node.setVisible(
                    false );
            __delay =
                    TimeUnit.MILLISECONDS.toNanos( delay );

            __hold =
                    __delay +
                    TimeUnit.MILLISECONDS.toNanos( hold );
        }

        private final TimeProbe tp = new TimeProbe( "bah" );

        @Override
        public void handle( long now )
        {
            if ( !__isInitialised )
            {
                __isInitialised = true;
                __delay += now;
                __hold += now;
                return;
            }

            if ( now >= __hold )
            {
                __node.setVisible( false );
                tp.stop();
                // This condition will never again be taken.
                __hold = Long.MAX_VALUE;
                stop();

                LOG.info( tp.toString() );
            }
            else if ( now > __delay )
            {
                __node.setVisible( true );
                tp.start();
                // This condition will never again be taken.
                __delay = Long.MAX_VALUE;
            }
        }
    }

    private void debugFill()
    {
        while ( _probesRemaining.size() > 1 )
        {
            String pos = _probesRemaining.remove( 0 );
            assert pos != null;

            MmtProbe probe = new MmtProbe( pos, _shapeSet );
            _probesTaken.add( 0, probe );

            probe.setVisible( true );
            probe.setForeground( _testColor );

            LOG.info( "RecrtAdding at " + probe.getProbe().getPosition() );

            _wheelLayout.addLayoutComponent( probe, probe.getProbe().getPosition() );

            Probe dmProbe = probe.getProbe();
            probe.setResult( dmProbe.getExpected() );
        }
    }
}

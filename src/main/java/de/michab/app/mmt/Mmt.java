/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jdesktop.beans.PropertyLink;
import org.jdesktop.util.PlatformType;
import org.smack.application.ApplicationInfo;
import org.smack.application.ApplicationProperties;
import org.smack.fx.ActionFx;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager.Resource;
import org.smack.util.resource.ResourceUtil;

import de.michab.app.mmt.components.DateTimeLabel;
import de.michab.app.mmt.components.MmtStatusbar;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.dm.Patient;
import de.michab.app.mmt.dm.Tenant;
import de.michab.app.mmt.lab.SingleFrameApplication;
import de.michab.app.mmt.screens.Screen00Start;
import de.michab.app.mmt.screens.Screen04SelectGlyphSet;
import de.michab.app.mmt.screens.Screen98Tenant;
import de.michab.app.mmt.screens.Screen99Calibration;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.screens.sdk.ScreenHelp;
import de.michab.app.mmt.screens.sdk.ScreenSdkError;
import de.michab.app.mmt.util.MmtUtils;
import de.michab.app.mmt.util.ShapeSet;
import de.michab.app.mmt.util.ShapeSetLandolt;
import de.michab.app.mmt.util.ShapeSetSloan;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.converter.FloatStringConverter;

/**
 * Main entry point. Where the fun begins...
 *
 * @author Michael Binz
 */
public class Mmt extends SingleFrameApplication
{
    private final static Logger LOG =
            Logger.getLogger( Mmt.class.getName() );

    @Resource
    private String _platformNotSupported;

    /**
     * Central background used for the main window.
     */
    private SimpleObjectProperty<Background> _mainBackground =
            new SimpleObjectProperty<>(
                    this,
                    "background",
                    ScreenUtils.getBackgroundFx() );

    public Mmt()
    {
        super( true );
    }

    private final SimpleObjectProperty<Screen<Node>> _screen =
            new SimpleObjectProperty<>( this, "screen", null )
    {
        @Override
        public void set(Screen<Node> newValue)
        {
            Screen<Node> current = get();

            if ( current != null )
                current.leaveScreen( newValue );

            newValue.enterScreen( current );

            super.set( newValue );
        };
    };

    /**
     * Get the main component.
     *
     * @return The main component.
     */
    public Screen<Node> getScreen()
    {
        return _screen.get();
    }

    /**
     * Sets the current screen. Prefer this if switching to a new screen
     * to get a simple error handling.
     *
     * @param supplier A screen supplier.
     */
    private void setScreenImpl( Supplier<Screen<Node>> supplier )
    {
        // Set the cursor back to default state.  This is the
        // counterpart of setCursor in setScreen.
        getView().setCursor( Cursor.DEFAULT );

        Screen<Node> screen;

        try
        {
            screen = supplier.get();
        }
        catch ( Throwable e )
        {
            screen = new ScreenSdkError(
                    getScreen(),
                    this,
                    e );
        }

        setScreen( screen );
    }

    /**
     * Needed because of wait cursor handling.
     *
     * @param supplier A screen supplier.
     */
    private void setScreenIntermediate( Supplier<Screen<Node>> supplier )
    {
        Platform.runLater(
                () -> setScreenImpl( supplier ) );
    }

    /**
     * Sets the current screen. Prefer this if switching to a new screen
     * to get solid error handling and a wait cursor.
     *
     * @param supplier A screen supplier.
     */
    public void setScreen( Supplier<Screen<Node>> supplier )
    {
        getView().setCursor( Cursor.WAIT );

        Platform.runLater(
                () -> setScreenIntermediate( supplier ) );
    }

    /**
     * Sets the current screen.
     *
     * @param component The component to display.
     */
    public void setScreen( Screen<Node> screen )
    {
        Node component = null;

        try
        {
            component = screen.getComponent();
        }
        catch ( Throwable e )
        {
            // Error handling mode phase one: The screen
            // implementation failed. Install the special
            // error screen.
            // This is expected and handled gracefully.
            screen = new ScreenSdkError(
                    getScreen(),
                    this,
                    e );

            try
            {
                component = screen.getComponent();
            }
            catch ( Exception e1 )
            {
                // Error handling mode phase two: The error screen
                // implementation failed.
                // This is highly unexpected, so we shutdown.
                LOG.log(
                        Level.SEVERE,
                        "Fatal problem in ErrorHandler. Terminating..." );
                e1.printStackTrace();
                System.exit( 1 );
            }
        }

        // Set the focus if this is requested.
        {
            Node focusComponent = screen.getFocusComponent();
            if ( focusComponent != null )
            {
                Platform.runLater( () -> focusComponent.requestFocus() );
            }
        }

        // Install the new screen.
        {
            BorderPane v = getView();
            v.setCenter( component );

            if ( component instanceof Region )
            {
                Region region = (Region)component;
                _mainBackground.set( region.getBackground() );
            }
        }

        // Do logging.
        if ( StringUtil.hasContent( component.getId() ) )
        {
            LOG.info( component.getId() );
        }
        else
        {
            LOG.info( component.getClass().getSimpleName() );
        }

        // All went good. Set the property. This signals the new screen
        // to potential listeners.
        _screen.set( screen );
    }

    private final SimpleObjectProperty<ShapeSet> _shapeSet =
            new SimpleObjectProperty<>( this, "shapeSet", null );

    private ShapeSet[] _shapeSets = new ShapeSet[] {
            new ShapeSetSloan(),
            new ShapeSetLandolt()
    };

    /**
     * Get the available ShapeSets.
     *
     * @return The available ShapeSets.
     */
    public ShapeSet[] getShapeSets()
    {
        return _shapeSets.clone();
    }

    /**
     * Get the currently selected ShapeSet.
     *
     * @return The currently selected ShapeSet.
     */
    public ShapeSet getShapeSet()
    {
        ApplicationProperties aps =
                ServiceManager.getApplicationService( ApplicationProperties.class );

        String shapesetName =
                aps.get( getClass(), _shapeSet.getName(), _shapeSets[0].getClass().getName() );

        for ( ShapeSet c : _shapeSets )
        {
            if ( shapesetName.equals( c.getClass().getName() ) )
            {
                _shapeSet.set( c );
                break;
            }
        }

        return _shapeSet.get();
    }

    /**
     * Bound property. Null not allowed.
     *
     * @param shapeSet The new shapeset to set.
     */
    public void setShapeSet( ShapeSet shapeSet )
    {
        _shapeSet.set(
                Objects.requireNonNull( shapeSet ) );
        ApplicationProperties aps =
                ServiceManager.getApplicationService( ApplicationProperties.class );

        aps.put( getClass(), _shapeSet.getName(), shapeSet.getClass().getName() );
    }

    private final Label _patientLabel = new Label();

    @Override
    protected void startup()
    {
        if ( ! PlatformType.is( PlatformType.WINDOWS ) &&
                ! PlatformType.is( PlatformType.OS_X ) )
        {
            String message = String.format(
                    _platformNotSupported,
                    PlatformType.getPlatform() );

            org.smack.fx.FxUtil.handleFatalError( message );
        }

        if ( _shapeSets == null || _shapeSets.length == 0 )
        {
            throw new RuntimeException( "No shapeset configured." );
        }
        _shapeSet.set( _shapeSets[0] );

        ACT_HOME =
                new ActionFx( this::actHome );
        ACT_HELP =
                new ActionFx( this::actHelp ).inject( getClass(), "actHelp" );
        ACT_CALIBRATE =
                new ActionFx( this::actCalibrate ).inject( getClass(), "actCalibrate" );
        ACT_TENANT =
                new ActionFx( this::actTenant ).inject( getClass(), "actTenant" );
        ACT_GLYPHSET =
                new ActionFx( this::actGlyphset ).inject( getClass(), "actGlyphset" );

        {
            Stage mainFrame = getStage();
            mainFrame.setFullScreenExitKeyCombination(
                    KeyCombination.NO_MATCH );
            mainFrame.setFullScreen(
                    true );
            mainFrame.getIcons().add(
                    getIcon() );
        }

        BorderPane v = getView();

        v.backgroundProperty().bind( _mainBackground );

        v.setTop(
                makeToolbar() );
        v.setBottom(
                makeStatusbar() );

        actHome();
    }

    private final SimpleObjectProperty<Patient> _patient =
            new SimpleObjectProperty<>( this, "patient",null );

    /**
     * Set the current patient instance and displays it in the top bar.
     *
     * @param newValue The new patient. {@code null} is allowed.
     */
    public void setPatient( Patient newValue )
    {
        Platform.runLater( ( ) -> setPatientImpl( newValue ) );
    }
    private void setPatientImpl( Patient newValue )
    {
        _patient.set( newValue );

        String patientInHeader = StringUtil.EMPTY_STRING;

        if ( newValue != null )
        {
            patientInHeader = String.format( "%s %s, %s",
                    newValue.getFirstname(),
                    newValue.getLastname(),
                    MmtUtils.formatDate( newValue.getBirthdate() ) );
        }

        _patientLabel.setText( patientInHeader );
    }

    /**
     * Get the current patient.
     *
     * @return The current patient.
     */
    public Patient getPatient()
    {
        return _patient.get();
    }

    private final ObjectProperty<Tenant> _tenant =
            PropertyLink.persist(
                    new SimpleObjectProperty<>(
                            this,
                            "tenant",
                            null ),
                    Tenant.TENANT_CONVERTER );

    /**
     * Set the tenant and displays it in the status bar.
     *
     * @param newValue The new patient.
     */
    public void setTenant( Tenant newValue )
    {
        _tenant.set( newValue );

        // Update the status bar.
        showStatusMessage( MmtUtils.formatTenant( newValue ) );
    }

    /**
     * Get the current patient.
     *
     * @return The current patient.
     */
    public Tenant getTenant()
    {
        return _tenant.get();
    }

    /**
     * Get the requested test.
     *
     * @return The test or null.
     */
    public Experiment getTest( Side side, Contrast contrast )
    {
        return _patient.get().getExperiment( side, contrast );
    }

    /**
     * Set the current test result.
     *
     * @param test The test result. This collection is copied.
     */
    public void setTest( Experiment test )
    {
        _patient.get().addExperiment( test );
    }

    /**
     * The dpi property.
     */
    private final SimpleObjectProperty<Float> _dpi = PropertyLink.persist( new SimpleObjectProperty<>(
            this,
            "dpi",
            Float.NaN ),
            org.smack.util.ServiceManager.getApplicationService( FloatStringConverter.class ) );
//          ServiceManager.getApplicationService( FloatStringConverter.class ) );

    /**
     * Offers the application-wide dpi setting.
     *
     * @return The application-wide dpi setting.
     */
    public float getDpi()
    {
        float result = _dpi.get();

        // If already initialized...
        if ( ! Float.isNaN( result ) )
        {
            return result;
        }

        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    /**
     * Set the application wide dpi value.
     *
     * @param dpi The value to set. This is a bound property.
     */
    public void setDpi( float dpi )
    {
        _dpi.set( dpi );
    }

    /**
     * @return True if the screen resolution is calibrated.
     */
    private boolean isCalibrated()
    {
        return ! Float.isNaN( _dpi.get() );
    }

    private void actExit()
    {
        exit();
    }

    private ActionFx ACT_HELP;

    private void actHelp()
    {
        setScreen( () -> {
            return new ScreenHelp( getScreen(), this );
        } );
    }

    private ActionFx ACT_HOME;

    public void actHome()
    {
        Screen<Node> startScreen = new Screen00Start( this );

        setScreen(() -> {
            if ( ! isCalibrated() )
            {
                return new Screen99Calibration( startScreen, this );
            }
            else
            {
                return startScreen;
            }
        });
    }

    private ActionFx ACT_CALIBRATE;

    private void actCalibrate()
    {
        setScreen( () -> {
            return new Screen99Calibration( getScreen(), this );
        });
    }

    private ActionFx ACT_TENANT;

    private void actTenant()
    {
        setScreen( () -> {
            return new Screen98Tenant( getScreen(), this );
        });
    }

    private ActionFx ACT_GLYPHSET;

    private void actGlyphset()
    {
        setScreen( () -> {
            return new Screen04SelectGlyphSet( getScreen(), this );
        });
    }

    /**
     * Creates the toolbar at main window top.
     */
    private ToolBar makeToolbar()
    {
        ToolBar result = new ToolBar();

        result.backgroundProperty().bind( _mainBackground );

        {
            Label title = new Label(
                    getTitle(),
                    new ImageView( getIcon() ) );
            title.setTextFill(
                    ScreenUtils.getForegroundFx() );

            title.setFont( ScreenUtils.getFontSmall() );
            result.getItems().add( title );
        }

        result.getItems().add( ScreenUtils.rigidAreaSqareCm( 1.0 ) );

        result.getItems().addAll( makeHideableToolbar() );

        result.getItems().add( ScreenUtils.rigidAreaSqareCm( 1.0 ) );

        {
            Label c = _patientLabel;
            c.setTextFill( ScreenUtils.getForegroundFx() );
            c.setFont( ScreenUtils.getFontSmall() );
            result.getItems().add( c );
        }

        result.getItems().add( org.smack.fx.FxUtil.createHorizontalGlue() );

        {
            Label c = new DateTimeLabel();
            c.setTextFill( ScreenUtils.getForegroundFx() );
            c.setFont( ScreenUtils.getFontSmall() );
            result.getItems().add( c );
        }

        result.getItems().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            Node c = MmtButton.makeHomeButton(
                    ACT_HOME );
            ACT_HOME.enabledProperty.bind(
                    Bindings.not( onFirstScreenProperty ) );
            result.getItems().add( c );
        }

        result.getItems().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            ActionFx exitAction = new ActionFx( this::actExit );
            Node c = MmtButton.makeExitButton(
                    exitAction );
            result.getItems().add( c );
        }

        result.visibleProperty().bind( toolbarVisibleProperty );

        return result;
    }

    /**
     * Flags whether the bottom toolbar is visible.  This is a central
     * point for controlling the enabled-state of most of the main
     * window widgets.
     */
    public final SimpleBooleanProperty toolbarVisibleProperty =
            new SimpleBooleanProperty( this, "toolbarVisible", true );

    public final SimpleBooleanProperty onFirstScreenProperty =
            new SimpleBooleanProperty( this, "onFirstScreen", true );

    /**
     * Creates the menu buttons in the main window top toolbar.
     */
    private List<Node> makeHideableToolbar()
    {
        double GAP = 0.25;

        List<Node> result = new ArrayList<>();

        {
            Node c = MmtButton.makeTextButton(
                    MmtButton.DIMENSION_S,
                    ACT_HELP, true );
            ACT_HELP.enabledProperty.bind( onFirstScreenProperty );
            result.add( c );
        }

        result.add( ScreenUtils.rigidAreaSqareCm( GAP ) );

        {
            Node c = MmtButton.makeTextButton(
                    MmtButton.DIMENSION_S ,
                    ACT_GLYPHSET, true );
            ACT_GLYPHSET.enabledProperty.bind( onFirstScreenProperty );
            result.add( c );
        }

        result.add( ScreenUtils.rigidAreaSqareCm( GAP ) );

        {
            Node c = MmtButton.makeTextButton(
                    MmtButton.DIMENSION_S ,
                    ACT_TENANT, true );
            ACT_TENANT.enabledProperty.bind( onFirstScreenProperty );
            result.add( c );
        }

        result.add( ScreenUtils.rigidAreaSqareCm( GAP ) );

        {
            Node c = MmtButton.makeTextButton(
                    MmtButton.DIMENSION_S,
                    ACT_CALIBRATE, true );
            ACT_CALIBRATE.enabledProperty.bind( onFirstScreenProperty );
            result.add( c );
        }

        return result;
    }

    /**
     * Creates the status bar.
     */
    private MmtStatusbar makeStatusbar()
    {
        MmtStatusbar result = new MmtStatusbar();

        result.backgroundProperty().bind( _mainBackground );
        result.setFont( ScreenUtils.getFontSmall() );

        Label version = new Label(
                ServiceManager.getApplicationService(
                        ApplicationInfo.class ).getVersion() );

        version.setFont( ScreenUtils.getFontTiny() );

        result.addRight( version );

        {
            // The build process has to generate a file 'build.number' into
            // our resource directory.  This file has to contain a single
            // line representing the build number.
            String buildNumber;
            try
            {
                buildNumber = new String(
                        ResourceUtil.loadResource( getClass(), "build.number" ) );
            }
            // If the file doesn't exist we handle that.
            catch ( Exception e )
            {
                buildNumber = "000";
            }
            result.addRight( new Label( "." + buildNumber ) );
        }

        result.visibleProperty().bind( toolbarVisibleProperty );

        return result;
    }

    /**
     * Set a message in the status bar.
     */
    public void showStatusMessage( String message )
    {
        MmtStatusbar statusbar = getStatusbar();

        statusbar.setMessage( message );
    }

    public MmtStatusbar getStatusbar()
    {
        return (MmtStatusbar)getView().getBottom();
    }

    public Node getToolbar()
    {
        return getView().getTop();
    }

    /**
     * Main entry point.  May the power be with you.
     *
     * @param argv The command line arguments.
     */
    public static void main( String[] argv ) throws Exception
    {
        String logFile = System.getProperty("java.util.logging.config.file");
        if(logFile == null)
        {
            try {
                LogManager.getLogManager().readConfiguration(
                        Mmt.class.getClassLoader().getResourceAsStream("logging.properties"));
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        PlatformType pt = PlatformType.getPlatform();

        LOG.info( String.format(
                "Platform=%s('%s')",
                pt,
                pt.resourceSuffix ) );
        LOG.info( String.format(
                "Locale is '%s'",
                Locale.getDefault() ) );
        LOG.info( String.format(
                "Date pattern is '%s'",
                MmtUtils.getDatePattern() ) );
        LOG.info( String.format(
                "java.home= '%s'",
                System.getProperty( "java.home" ) ) );

        ServiceManager.initApplicationService(
                new org.smack.application.ApplicationInfo( Mmt.class ) );

        launch( Mmt.class, argv );
    }
}

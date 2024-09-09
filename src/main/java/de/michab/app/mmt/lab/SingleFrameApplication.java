/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.logging.Logger;

import org.jdesktop.util.PlatformType;
import org.smack.application.ApplicationInfo;
import org.smack.fx.ImageUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * A JavaFX-based application implementation.
 *
 * @version $Revision: 2120 $
 * @author Michael Binz
 */
public abstract class SingleFrameApplication extends Application
{
    private static final Logger LOG =
            Logger.getLogger( SingleFrameApplication.class.getName() );

    private final boolean _openOnActiveScreen;

    protected abstract void startup();

    /**
     * Start the application.
     * @param openOnActiveScreen Used in multi screen environments.  If true
     * the application window is opened on the screen where the mouse pointer
     * is located. If false it is opened on the primary screen.
     */
    protected SingleFrameApplication( boolean openOnActiveScreen )
    {
        _openOnActiveScreen =
                openOnActiveScreen;

        ServiceManager.initApplicationService(
                Application.class,
                this );

        // Inject resource-defined fields on the application instance.
        // This ensures that these are set before the object is
        // accessible to the user.
        ServiceManager.getApplicationService(
                ResourceManager.class ).injectResources( this );
    }

    /**
     * Start the application on the primary screen.
     */
    protected SingleFrameApplication()
    {
        this( false );
    }

    private final BorderPane _node =
            new BorderPane();

    public BorderPane getView()
    {
        return _node;
    }

    public Stage getStage()
    {
        return _mainStage;
    }

    private void sleep( long ms )
    {
        try
        {
            Thread.sleep( ms );
        }
        catch ( InterruptedException e )
        {
        }
    }

    protected void exit()
    {
        LOG.warning( "Exiting ..." );

        Platform.exit();

        // On Mac we need to directly terminate, otherwise
        // we get a SEGV.
        if ( PlatformType.is(PlatformType.OS_X) ) {
            LOG.warning( "Mac exit." );
            System.exit(0);
        }
    }

    public String getTitle()
    {
        return  ServiceManager.getApplicationService( ApplicationInfo.class )
                .getTitle();
    }

    public Image getIcon()
    {
        return ImageUtil.toFxImage( ServiceManager.getApplicationService( ApplicationInfo.class )
                .getIcon() );
    }

    private Stage _mainStage;

    @Override
    public void start( Stage stage )
    {
        _mainStage = stage;

        // Open the window on the active screen, i.e. the screen where the mouse
        // pointer is.
        if ( _openOnActiveScreen )
        {
            Point pos = MouseInfo.getPointerInfo().getLocation();
            _mainStage.setX( pos.getX() );
            _mainStage.setY( pos.getY() );
            _mainStage.centerOnScreen();
        }

        startup();

        stage.setTitle(
                getTitle() );
        // BLUE should never be visible.
        stage.setScene(
                new Scene( _node, Color.BLUE ));
        stage.show();
    }
}

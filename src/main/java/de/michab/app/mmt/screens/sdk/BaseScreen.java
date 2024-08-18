/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.util.Objects;
import java.util.logging.Logger;

import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;

import de.michab.app.mmt.components.MmtTextFlow;
import de.michab.app.mmt.lab.SingleFrameApplication;
import de.michab.app.mmt.screens.ScreenUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * A base class for concrete screen implementations.
 *
 * @author Michael Binz
 */
public abstract class BaseScreen<T extends SingleFrameApplication>
    extends
       MmtLayoutPane
    implements
        Screen<Node>
{
    private static final Logger LOG =
            Logger.getLogger( BaseScreen.class.getName() );

    private final T _app;

    /**
     * Create an instance with a CENTER {@link ColumnTension}.
     *
     * @param application The hosting application.
     */
    public BaseScreen( T application )
    {
        this( application, ColumnTension.CENTER );
    }

    /**
     * Create an instance.
     *
     * @param application The hosting application.
     */
    public BaseScreen( T application, ColumnTension columnAlignment )
    {
        super( columnAlignment );

        _app = Objects.requireNonNull(
                application );

        setId(
                getClass().getSimpleName() );

        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( this );

        setBackground( ScreenUtils.getBackgroundFx() );
    }

    private Node _ui;

    @Override
    public Node getComponent() throws Exception
    {
        if ( _ui == null )
        {
            _ui = initScreen();
        }

        return _ui;
    }

    @Override
    public String getName()
    {
        return getId();
    }

    /**
     * To be implemented by subclasses.  Initialize screen content.
     *
     * @return The initialized screen content.
     * @throws Exception
     */
    protected abstract Node initScreen() throws Exception;

    /**
     * Called if a screen is entered.  This default implementation
     * only prints a log message.
     *
     * @param The screen we replace.
     */

    @Override
    public void enterScreen( Screen<Node> previous )
    {
        LOG.info( "Entering screen " + getName() );
    }

    @Override
    public void leaveScreen( Screen<Node> next )
    {
        LOG.info( "Leaving screen " + getName() );
    }

    /**
     *
     * @param component
     * @param column
     * @param row
     */
    protected final void add( Node component, Column column, Row row )
    {
        addLayoutComponent(
            component,
            column,
            row );
    }

    /**
     * @return The screen's application.
     */
    protected final T getApplication()
    {
        return _app;
    }

    /**
     * The component to gain focus on first display of the screen.
     */
    private Node _focusComponent;

    /**
     * @param c The component to gain focus on first display of this screen.
     */
    protected void setFocusComponent( Node c )
    {
        _focusComponent = c;
    }

    /**
     * @return The component to gain focus on screen display.  If none was set,
     * a default component is computed and returned.
     */
    @Override
    public Node getFocusComponent()
    {
        if ( _focusComponent != null )
        {
            // Was explicitly set.
            return _focusComponent;
        }

        // Compute default...
        for ( final Column c : new Column[]{ Column.CENTER, Column.END, Column.START } )
        {
            final Node result = getComponentAt( c, Row.BUTTON_NEXT );
            if ( result != null )
            {
                return result;
            }
        }

        for ( final Column c : new Column[]{ Column.CENTER, Column.END, Column.START } )
        {
            final Node result = getComponentAt( c, Row.BUTTON_PREV );
            if ( result != null )
            {
                return result;
            }
        }

        return null;
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Node makeScreenHeader( String text )
    {
        return MmtTextFlow.create( text );
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Node makeHeader( String text )
    {
        final Label result = new Label( text );

        result.setFont( ScreenUtils.getFontMediumFx() );
        result.setTextFill( ScreenUtils.getForegroundFx() );

        return result;
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Node makeHeaderRichText( String text )
    {
        MmtTextFlow tf = MmtTextFlow.create( text );

        // For now we re-package the nodes that were added in fxml.
        MmtTextFlow result = new MmtTextFlow(
                ScreenUtils.getFontMediumFx(),
                ScreenUtils.getForegroundFx() );

        result.setTextAlignment( tf.getTextAlignment() );

        result.getChildren().addAll( tf.getChildren() );

        return result;
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Label makeFooter( String text )
    {
        final Label result = new Label( text );

        result.setFont( ScreenUtils.getFontSmall() );
        result.setTextFill( ScreenUtils.getForegroundFx() );

        return result;
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Node makeFooterRichText( String text )
    {
        MmtTextFlow tf = MmtTextFlow.create( text );

        // For now we re-package the nodes that were added in fxml.
        MmtTextFlow result = new MmtTextFlow(
                ScreenUtils.getFontSmall(),
                ScreenUtils.getForegroundFx() );

        result.setTextAlignment( tf.getTextAlignment() );

        result.getChildren().addAll( tf.getChildren() );

        return result;
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Label makeLabel( String text )
    {
        return makeLabel( text, ScreenUtils.getForegroundFx() );
    }

    /**
     * Create a node adjusted for our application.
     */
    protected Label makeLabel( String text, Color color )
    {
        final Label result = new Label( text );

        result.setFont( ScreenUtils.getFontSmall() );
        result.setTextFill( color );

        result.setMaxWidth( Double.MAX_VALUE );
        result.setAlignment( Pos.CENTER );

        return result;
    }
}

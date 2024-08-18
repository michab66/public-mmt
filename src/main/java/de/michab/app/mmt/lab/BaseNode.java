/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.util.logging.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A base class for implementing resizable nodes.  The resizable
 * property allows to lock the size.
 *
 * @author Michael Binz
 */
public class BaseNode extends Canvas
{
    private static final Logger LOG =
            Logger.getLogger( BaseNode.class.getName() );

    private static final int DEFAULT_W =
            20;
    private static final int DEFAULT_H =
            20;
    private static final Color DEFAULT_STROKE =
            Color.WHITE;
    private static final Color DEFAULT_FILL =
            Color.GREEN;

    public final SimpleObjectProperty<Paint> fillProperty =
            new SimpleObjectProperty<>(
                    this,
                    "fill",
                    DEFAULT_FILL );

    public final SimpleObjectProperty<Paint> strokeProperty =
            new SimpleObjectProperty<>(
                    this,
                    "stroke",
                    DEFAULT_STROKE );

    public final SimpleBooleanProperty resizableProperty =
            new SimpleBooleanProperty(
                    this,
                    "resizable",
                    true );

    public BaseNode( double w, double h )
    {
        super(
                w,
                h );
    }

    public BaseNode()
    {
        this( DEFAULT_W, DEFAULT_H );
    }

    /**
     * To be called from the subclass constructor to trigger the
     * initial repaint.
     */
    public void repaint()
    {
        prepareRepaint( getGraphicsContext2D() );
    }

    @Override
    public boolean isResizable()
    {
        return resizableProperty.get();
    }

    @Override
    public double minWidth( double height )
    {
        if ( isResizable() )
            return 0;

        return prefWidth( height );
    }

    @Override
    public double prefWidth( double height )
    {
        return widthProperty().get();
    }

    @Override
    public double maxWidth( double height )
    {
        if ( isResizable() )
            return Double.MAX_VALUE;

        return prefWidth( height );
    }
    @Override
    public double minHeight( double width )
    {
        if ( isResizable() )
            return 0;

        return prefHeight( width );
    }

    @Override
    public double prefHeight( double width )
    {
        return heightProperty().get();
    }

    @Override
    public double maxHeight( double width )
    {
        if ( isResizable() )
            return Double.MAX_VALUE;

        return prefHeight( width );
    }

    @Override
    public void resize( double width, double height )
    {
        widthProperty().set( width );
        heightProperty().set( height );

        prepareRepaint( getGraphicsContext2D() );
    }

    /**
     * Called on resize events.
     *
     * @param g The node's graphics context.
     */
    private void prepareRepaint(GraphicsContext gc)
    {
        LOG.info( "repaint( w=" + getWidth() + ", h=" + getHeight() );

        gc.save();

        gc.clearRect(
                0,
                0,
                getWidth(),
                getHeight());

        gc.setFill(
                fillProperty.get() );
        gc.setStroke(
                strokeProperty.get() );

        try
        {
            repaint( gc );
        }
        finally
        {
            gc.restore();
        }
    }

    protected void repaint( GraphicsContext gc )
    {
        if ( fillProperty.get() != null )
        {
            gc.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight() );
        }

        if ( strokeProperty.get() != null  )
        {
            gc.strokeLine(
                    0,
                    0,
                    getWidth(),
                    getHeight());
            gc.strokeLine(
                    getWidth(),
                    0,
                    0,
                    getHeight());
        }
    }

    /**
     * Snap a double dimension into the JavaFX coordinate system
     * so that lines are drawn crisp.
     *
     * @param v The dimension to snap.
     * @return The snapped value.
     * @see http://dlsc.com/2014/04/10/javafx-tip-2-sharp-drawing-with-canvas-api/
     */
    protected final double snap( double v )
    {
        return ((int) v) + .5;
    }
}

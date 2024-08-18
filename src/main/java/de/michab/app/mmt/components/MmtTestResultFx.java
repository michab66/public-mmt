/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.util.Objects;
import java.util.logging.Logger;

import de.michab.app.mmt.dm.Probe.Score;
import de.michab.app.mmt.lab.BaseNode;
import de.michab.app.mmt.screens.ScreenUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

/**
 * A component that represents a test result in MMT. This is square black
 * and white or split color.
 *
 * @author Michael Binz
 */
public class MmtTestResultFx extends BaseNode
{
    private final static Logger LOG =
            Logger.getLogger( MmtTestResultFx.class.getName() );

    private final Score _mode;

    /**
     * The border color property.
     */
    public final SimpleObjectProperty<Paint> borderColorProperty =
            new SimpleObjectProperty<>(
                    this,
                    "borderColor",
                    null );

    /**
     * Create an instance for the passed score.
     *
     * @param score The score to display.
     */
    public MmtTestResultFx( Score score, double edgeLength )
    {
        super(
                edgeLength,
                edgeLength );

        _mode =
                Objects.requireNonNull( score );
        fillProperty.set(
                ScreenUtils.getForegroundFx() );
        strokeProperty.set(
                ScreenUtils.getBackgroundColorFx() );

        repaint();
    }

    /**
     * If we have a border color explicitly set, return this.
     * Otherwise return background color.
     */
    private Paint computeBorderColor()
    {
        Paint result = borderColorProperty.get();

        if ( result == null )
            result = getForeground();

        return result;
    }

    @Override
    protected void repaint( GraphicsContext g )
    {
        javafx.geometry.Rectangle2D box;
        {
            double w = getWidth();
            double h = getHeight();
            double squareEdgeLength = Math.min( w, h );

            box = new javafx.geometry.Rectangle2D(
                    (w - squareEdgeLength) * 0.5 ,
                    (h - squareEdgeLength) * 0.5 ,
                    squareEdgeLength-1,
                    squareEdgeLength-1 );
        }

        if ( _mode == Score.UNNOTICED )
        {
            g.setFill( getForeground() );
            fill( g, box );
        }
        else if ( _mode == Score.RECOGNIZED )
        {
            g.setFill( getBackground() );
            fill( g, box );
        }
        else if ( _mode == Score.NOTICED )
        {
            g.setFill( getBackground() );
            fill( g, box );
            g.setFill( getForeground() );

            g.beginPath();
            g.moveTo( box.getMinX(), box.getMinY() );
            g.lineTo( box.getWidth(), box.getHeight() );
            g.lineTo( box.getMinX(), box.getHeight() );
            g.closePath();
            g.fill();
        }
        else
            LOG.severe( "Unexpected mode: " + _mode );

        // Draw a frame in border color.
        g.setStroke(
                computeBorderColor() );
        stroke(
                g,
                box );
    }

    private Paint getBackground()
    {
        return fillProperty.get();
    }

    private Paint getForeground()
    {
        return strokeProperty.get();
    }

    private void fill( GraphicsContext g, Rectangle2D box )
    {
        g.rect(
                snap( box.getMinX() ),
                snap( box.getMinY() ),
                 box.getWidth() ,
                 box.getHeight() ) ;
        g.fill();
    }

    private void stroke( GraphicsContext g, Rectangle2D box )
    {
        g.rect(
                snap( box.getMinX() ),
                snap( box.getMinY() ),
                 box.getWidth() ,
                 box.getHeight() ) ;
        g.stroke();
    }
}

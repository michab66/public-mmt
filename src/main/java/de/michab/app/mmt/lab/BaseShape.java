/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.smack.util.collections.CollectionUtil;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;

/**
 * A Basic shape.
 *
 * @author Michael Binz
 */
public class BaseShape extends Path
{
    private static final Logger LOG =
            Logger.getLogger( BaseShape.class.getName() );
    private static final int DEFAULT_W =
            20;
    private static final int DEFAULT_H =
            20;
    private static final Color DEFAULT_STROKE =
            Color.WHITE;
    private static final Color DEFAULT_FILL =
            Color.GREEN;

    protected final SimpleDoubleProperty widthProperty =
            new SimpleDoubleProperty( this, "width", DEFAULT_W );

    public DoubleProperty widthProperty()
    {
        return widthProperty;
    }

    protected final SimpleDoubleProperty heightProperty =
            new SimpleDoubleProperty( this, "height", DEFAULT_H );

    public DoubleProperty heightProperty()
    {
        return heightProperty;
    }

    public BaseShape()
    {
        this(
                DEFAULT_W,
                DEFAULT_H );
    }

    /**
     * Create an instance with a given size.  Implementers of
     * subclasses have to call repaint after this constructor.
     * @param w The component width.
     * @param h The component height.
     */
    public BaseShape( double w, double h )
    {
        setFill(
                DEFAULT_FILL );
        setStroke(
                DEFAULT_STROKE );
        setFillRule(
                FillRule.EVEN_ODD );
    }

    public SimpleBooleanProperty resizableProperty =
            new SimpleBooleanProperty(
                    this,
                    "resizable",
                    true );

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

    protected void createPath( List<PathElement> pathElements )
    {
        moveTo(
                pathElements,
                0,
                0 );
        lineTo(
                pathElements,
                widthProperty.get(),
                heightProperty.get());
        moveTo( pathElements,
                0,
                heightProperty.get() );

        lineTo(
                pathElements,
                widthProperty.get(),
                0 );
    }


    private List<PathElement> makePath()
    {
        List<PathElement> result =
                new ArrayList<>();

        createPath( result );

        if ( result.isEmpty() )
            throw new IllegalArgumentException( "Path empty." );

        if ( CollectionUtil.lastElement( result ).get() instanceof ClosePath )
            ;
        else
            closePath( result );

        return result;
    }

    protected final void arcTo(
            List<PathElement> path,
            double radiusX,
            double radiusY,
            double xAxisRotation,
            double x,
            double y,
            boolean largeArcFlag,
            boolean sweepFlag  )
    {
        ArcTo arc =
                new ArcTo(
                        radiusX,
                        radiusY,
                        xAxisRotation,
                        x,
                        y,
                        largeArcFlag,
                        sweepFlag );

        path.add( arc );
    }

    protected final void arcTo(
            List<PathElement> path,
            double radiusX,
            double radiusY,
            double xAxisRotation,
            Point2D point,
            boolean largeArcFlag,
            boolean sweepFlag  )
    {
        arcTo(
                path,
                radiusX,
                radiusY,
                xAxisRotation,
                point.getX(),
                point.getY(),
                largeArcFlag,
                sweepFlag );
    }

    protected final static void vLineTo( List<PathElement> path, double y )
    {
        path.add( new VLineTo( y ) );
    }

    protected final static void hLineTo( List<PathElement> path, double x )
    {
        path.add( new HLineTo( x ) );
    }

    protected final static void lineTo( List<PathElement> path, javafx.geometry.Point2D point )
    {
        lineTo( path, point.getX(), point.getY() );
    }

    protected final static void lineTo( List<PathElement> path, double x, double y )
    {
        path.add( new LineTo( x, y ) );
    }

    protected final static void moveTo( List<PathElement> path, javafx.geometry.Point2D point )
    {
        moveTo( path, point.getX(), point.getY() );
    }

    protected final static void moveTo( List<PathElement> path, double x, double y )
    {
        path.add( new MoveTo( x, y ) );
    }

    protected final static void cubicCurveTo(
            List<PathElement> path,
            double c1x,
            double c1y,
            double c2x,
            double c2y,
            double x,
            double y )
    {
        path.add( new CubicCurveTo( c1x, c1y, c2x, c2y, x, y ) );
    }

    protected final static void quadCurveTo(
            List<PathElement> path,
            double cx,
            double cy,
            double x,
            double y )
    {
        path.add( new QuadCurveTo( cx, cy, x, y ) );
    }

    protected static void closePath( List<PathElement> path )
    {
        path.add( new ClosePath() );
    }

    protected final void repaint()
    {
        getElements().clear();

        getElements().addAll( makePath() );
    }

    @Override
    public void resize( double width, double height )
    {
        if ( ! isResizable() )
        {
            LOG.warning( "Not resizable." );
            return;
        }

        widthProperty.set( width );
        heightProperty.set( height );

        repaint();
    }

    /**
     * Convert an AWT Shape to a list of JavaFx path elements.
     *
     * @param shape The shape to convert.
     * @return The path elements.
     */
    public static void add( List<PathElement> result, Shape shape )
    {
        float[] coords =
                new float[6];

        for ( PathIterator pi = shape.getPathIterator(null) ;
                !pi.isDone() ;
                pi.next())
        {
            int type = pi.currentSegment(coords);

            switch ( type )
            {
            case PathIterator.SEG_LINETO:
                lineTo(
                        result,
                        coords[0],
                        coords[1] );
                break;
            case PathIterator.SEG_CLOSE:
                closePath( result );
                break;
            case PathIterator.SEG_CUBICTO:
                cubicCurveTo(
                        result,
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3],
                        coords[4],
                        coords[5] );
                break;
            case PathIterator.SEG_QUADTO:
                quadCurveTo(
                        result,
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3] );
                break;
            case PathIterator.SEG_MOVETO:
                moveTo(
                        result,
                        coords[0],
                        coords[1] );
                break;
            default:
                throw new InternalError( "Unexpected type: " + type );
            }
        }
    }
}

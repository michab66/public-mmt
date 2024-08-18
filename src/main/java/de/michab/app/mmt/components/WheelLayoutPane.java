/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.smack.util.MathUtil;
import org.smack.util.StringUtil;
import org.smack.util.collections.MultiMap;

import de.michab.app.mmt.lab.BasePane;
import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * A layout manager for wheel components.
 *
 * @author Michael Binz
 */
public class WheelLayoutPane extends BasePane<String>
{
    @SuppressWarnings("unused")
    private final static Logger LOG =
            Logger.getLogger( WheelLayoutPane.class.getName() );

    private final Node _parent;

    /**
     * Special formatting of the four center components.
     * If false all overlap, otherwise layout tiled.
     */
    private final boolean _specialCenter;

    /**
     * Contains validated constraints as points, where x is beam, y is circle.
     */
    private final Map<Node, Point> _componentToConstraint =
            new HashMap<>();

    private final List<Float> _rayAngles =
            WheelModel.getHandAngles();

    /**
     * Create a new wheel layout for the passed Wheel.
     *
     * @param parent The parent wheel.
     * @param specialCenter True if the center components should be layout
     * tiled.
     */
    public WheelLayoutPane( Node parent, boolean specialCenter )
    {
        _specialCenter =
                specialCenter;
        _parent =
                parent;
    }

    /**
     * Create a new wheel layout for the passed Wheel. Center is layout
     * overlapping.
     *
     * @param parent The parent wheel.
     */
    public WheelLayoutPane( Node parent )
    {
        this( parent, false );
    }

    @Override
    public void addLayoutComponent( Node comp, String constraints )
    {
        addLayoutComponent( comp, constraints, comp.isResizable() );
    }

    private final Map<Node,Boolean> _resizableMap = new HashMap<>();

    public void addLayoutComponent( Node comp, String constraints, boolean resize )
    {
//        LOG.info( "visible " + comp.isVisible() );
        int combinedConstraint =
                Integer.parseInt( constraints );
        int circle =
                combinedConstraint % 10;
        int beam =
                combinedConstraint / 10;

        if ( beam > _rayAngles.size() )
            throw new IllegalArgumentException( "MAX_BEAM is " + _rayAngles.size() );
        if ( circle > WheelModel.MAX_CIRCLE )
            throw new IllegalArgumentException( "MAX_CIRCLE is " + WheelModel.MAX_CIRCLE );
        if ( beam < 0 )
            throw new IllegalArgumentException( "beam negative" );
        if ( circle < 0 )
            throw new IllegalArgumentException( "circle negative" );

        Point p =
                new Point( beam, circle );

        _componentToConstraint.put(
                comp,
                p );
        _resizableMap.put(
                comp,
                resize );

        // Note: Do not access the size operations on the component
        // here. Only in the layout operation.
        getChildren().add( comp );
    }

    @Override
    protected void positionChildren()
    {
        Point2D center = new Point2D(
                getWidth() * 0.5,
                getHeight() * 0.5 );

        for ( Node c : _componentToConstraint.keySet() )
        {
            Point2D position =
                    callWithDecomposedConstraints( c, this::getPosition );
            Dimension2D dimension =
                    callWithDecomposedConstraints( c, this::computeSize );

//            LOG.info( "In layout: " + dimension );
            final double w =
                    dimension.getWidth();
            final double h =
                    dimension.getHeight();
            final double x =
                    (position.getX() - (w / 2)) +
                    center.getX();

            final double y =
                    (position.getY() - (h / 2)) +
                    center.getY();

//            LOG.info( String.format(
//                    "x=%.5f,y=%.5f,w=%.5f,h=%.5f",
//                    x,
//                    y,
//                    w,
//                    h ) );
            setBounds(
                    c,
                    MathUtil.round( x ),
                    MathUtil.round( y ),
                    MathUtil.round( w ),
                    MathUtil.round( h ) );
        }
    }

    private Dimension2D computeSize( Node node, int beam, int circle )
    {
        Dimension2D result = computeSizeImpl( node, beam, circle );

//        LOG.info( "Circle= " + circle + " Size is " + result );

        return result;
    }

    private Dimension2D computeSizeImpl( Node node, int beam, int circle )
    {
        if ( ! _resizableMap.get( node ) )
        {
            return new Dimension2D(
                    node.prefWidth( -1 ),
                    node.prefHeight( -1 ) );
        }

        double compDim =
                WheelModel.getGlyphHeightInPx( circle, beam );

        return new Dimension2D( compDim, compDim );
    }

    /**
     *
     * @param beam
     * @param circle
     * @return
     */
    private Point2D computePosition( double width, int beam, int circle )
    {
        if ( circle == 0 )
            return new Point2D(0,0);

        return Geometry.pointWithDistanceFromA(
                _rayAngles.get( beam ),
                WheelModel.getEccentricityRadius( width, circle-1 ) );
    }

    /**
     *
     * @param csize
     * @param combinedConstraint
     * @return
     */
    private Point2D computeSpecialPosition( Node wc, int beam, int circle )
    {
        if ( circle != 0 )
            throw new IllegalArgumentException(
                    "circle not 0: " + circle );

        double resultX;
        double resultY;

        Dimension2D csize = new Dimension2D(
                wc.prefWidth( -1 ), wc.prefHeight( -1 ) );

        switch ( beam )
        {
        case 1:
            resultX = csize.getWidth() * 0.5f;
            resultY = csize.getHeight() * 0.5f;
            resultX++;
            resultY++;
            break;

        case 3:
            resultX = -csize.getWidth() * 0.5f;
            resultY = csize.getHeight() * 0.5f;
            resultX--;
            resultY++;
            break;

        case 5:
            resultX = -csize.getWidth() * 0.5f;
            resultY = -csize.getHeight() * 0.5f;
            resultX--;
            resultY--;
            break;

        case 7:
            resultX = csize.getWidth() * 0.5f;
            resultY = -csize.getHeight() * 0.5f;
            resultX++;
            resultY--;
            break;

        // The following constraints do not exist.
        case 0:
        case 2:
        case 4:
        case 6:
            throw new IllegalArgumentException(
                    "Bad beam: " + beam );

        default:
            return computePosition( peerDiameter(), beam, circle );
        }

        return new Point2D( resultX, resultY );
    }

    private interface Dcc<T>
    {
        T call( Node n, int circle, int beam );
    }

    private <T> T callWithDecomposedConstraints( Node node, Dcc<T> func )
    {
        Point constraint =
                _componentToConstraint.get( node );

        return func.call( node, constraint.x, constraint.y );
    }

    public final static MultiMap<Integer, Integer, String> constraints =
            new MultiMap<>();

    static {
        for ( int i = 0 ; i < WheelModel.NUM_HANDS ; i++ )
            for ( int j = 0 ; j < WheelModel.MAX_CIRCLE+1 ; j++ )
            {
                constraints.put( i, j, StringUtil.EMPTY_STRING + i + j );
            }

        constraints.remove( 0, 0 );
        constraints.remove( 2, 0 );
        constraints.remove( 4, 0 );
        constraints.remove( 6, 0 );
    }

    private Point2D getPosition( Node component, int beam, int circle )
    {
        Point2D result;

        if ( ! specialCenter( beam, circle ) )
            result = computePosition( peerDiameter(), beam, circle );
        else
            result = computeSpecialPosition(
                    component,
                    beam,
                    circle );

        return result;
    }

    private boolean specialCenter( int beam, int circle )
    {
        if ( ! _specialCenter )
            return false;

        if ( circle != 0 )
            return false;

        switch ( beam )
        {
        case 1:
        case 3:
        case 5:
        case 7:
            return true;

        default:
            throw new IllegalArgumentException(
                    "Bad beam: " + beam );
        }
    }

    public void removeLayoutComponent( Node comp )
    {
        _componentToConstraint.remove( comp );
    }

    private double peerDiameter()
    {
        Bounds peerBounds =
                _parent.getLayoutBounds();

//        LOG.info( "Peer bounds: " + peerBounds );

        return Math.max(
                peerBounds.getWidth(),
                peerBounds.getHeight() );
    }
}

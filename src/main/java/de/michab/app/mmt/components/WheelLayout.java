/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.geom.Dimension2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.smack.util.MathUtil;
import org.smack.util.StringUtil;
import org.smack.util.collections.MultiMap;

import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Point2D;

/**
 * A layout manager for wheel components.
 *
 * @author Michael Binz
 */
public class WheelLayout implements LayoutManager2
{
    private final WheelBase _parent;

    private final Map<Component, String> _componentToConstraint =
            new HashMap<>();

    private final List<Float> _rayAngles =
            WheelModel.getHandAngles();

    /**
     * Create a new wheel layout for the passed Wheel.
     *
     * @param parent The parent wheel.
     */
    WheelLayout( WheelBase parent )
    {
        _parent = parent;
    }

    @Override
    public void addLayoutComponent( Component comp, Object constraints )
    {
        if ( ! (constraints instanceof String ) )
            throw new IllegalArgumentException( "invalid constraint" );

        // Note: Do not access the size operations on the component
        // here. Only in the layout operation.

        _componentToConstraint.put(
                comp,
                constraints.toString() );
    }

    @Override
    public void layoutContainer( Container parent )
    {
        if ( _parent != parent )
          throw new IllegalArgumentException( "Bad parent" );

        synchronized ( parent.getTreeLock() )
        {
            layoutContainerImpl( parent );
        }
    }

    private void layoutContainerImpl( Container parent )
    {
        Point2D center = new Point2D(
                parent.getWidth() * 0.5,
                parent.getHeight() * 0.5 );

        for ( Component c : _componentToConstraint.keySet() )
        {
            Point2D position = getPosition( c, _componentToConstraint.get( c ) );

            Dimension2D size = c.getPreferredSize();

            double x =
                    (position.getX() - (size.getWidth() / 2)) +
                    center.getX();
            double y =
                    (position.getY() - (size.getHeight() / 2)) +
                    center.getY();
            double w =
                    size.getWidth();
            double h =
                    size.getHeight();

            c.setBounds(
                    MathUtil.round( x ),
                    MathUtil.round( y ),
                    MathUtil.round( w ),
                    MathUtil.round( h ) );
        }
    }

    @Override
    public float getLayoutAlignmentX( Container target )
    {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY( Container target )
    {
        return 0.5f;
    }

    @Override
    public void invalidateLayout( Container target )
    {
    }

    /**
     *
     * @param beam
     * @param circle
     * @return
     */
    private Point2D computePosition( int width, int beam, int circle )
    {
        if ( beam > _rayAngles.size() )
            throw new IllegalArgumentException( "MAX_BEAM is " + _rayAngles.size() );
        if ( circle > WheelModel.MAX_CIRCLE )
            throw new IllegalArgumentException( "MAX_CIRCLE is " + WheelModel.MAX_CIRCLE );
        if ( beam < 0 )
            throw new IllegalArgumentException( "beam negative" );
        if ( circle < 0 )
            throw new IllegalArgumentException( "circle negative" );

        if ( circle == 0 )
            return new Point2D( 0, 0 );

        return Geometry.pointWithDistanceFromA(
                _rayAngles.get( beam ),
                WheelModel.getEccentricityRadius( width, circle-1 ) );
    }

    private Point2D computePosition( int width, String combinedConstraint )
    {
        try
        {
            return computePosition( width, Integer.parseInt( combinedConstraint ) );
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( "Bad constraint: " + combinedConstraint );
        }
    }

    /**
     *
     * @param csize
     * @param combinedConstraint
     * @return
     */
    private Point2D computeSpecialPosition( Component wc, String combinedConstraint )
    {
        float resultX;
        float resultY;

        Dimension csize = wc.getPreferredSize();

        switch ( combinedConstraint )
        {
        case "10":
            resultX = csize.width * 0.5f;
            resultY = csize.height * 0.5f;
            resultX++;
            resultY++;
            break;

        case "30":
            resultX = -csize.width * 0.5f;
            resultY = csize.height * 0.5f;
            resultX--;
            resultY++;
            break;

        case "50":
            resultX = -csize.width * 0.5f;
            resultY = -csize.height * 0.5f;
            resultX--;
            resultY--;
            break;

        case "70":
            resultX = csize.width * 0.5f;
            resultY = -csize.height * 0.5f;
            resultX++;
            resultY--;
            break;

        // The following constraints do not exist.
        case "00":
        case "20":
        case "40":
        case "60":
            throw new IllegalArgumentException(
                    "Bad special constraint: " + combinedConstraint );

        default:
            return computePosition( _parent.getDiameter(), combinedConstraint );
        }

        return new Point2D( resultX, resultY );
    }

    /**
     *
     * @param beam
     * @param circle
     * @return
     */
    private Point2D computePosition( int wheelSize, int combinedConstraint )
    {
        int circle = combinedConstraint % 10;
        int beam = combinedConstraint / 10;

        if ( beam > _rayAngles.size() )
            throw new IllegalArgumentException( "MAX_BEAM is " + _rayAngles.size() );
        if ( circle > WheelModel.MAX_CIRCLE )
            throw new IllegalArgumentException( "MAX_CIRCLE is " + WheelModel.MAX_CIRCLE );

        return computePosition( wheelSize, beam, circle );
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

    private Point2D getPosition( Component component, String constraints )
    {
        Point2D result;

        if ( ! specialCenter( component ) )
            result = computePosition( _parent.getDiameter(), constraints );
        else
            result = computeSpecialPosition(
                    component,
                    constraints.toString() );

        return result;
    }

    private boolean specialCenter( Component c )
    {
        if ( ! (c instanceof JComponent) )
            return false;

        return Boolean.TRUE == ((JComponent)c).getClientProperty( WheelLayout.class );
    }

    /**
     * Configure a component so that special formatting for the center positions takes place.
     *
     * @param c The target component.
     * @param what true want special formatting, false no special formatting.
     */
    public static void wantSpecialCenterlayout( JComponent c, boolean what )
    {
        c.putClientProperty( WheelLayout.class, what );
    }

    @Override
    public void removeLayoutComponent( Component comp )
    {
        _componentToConstraint.remove( comp );
    }

    @Override
    public void addLayoutComponent( String name, Component comp )
    {
        throw new AssertionError();
    }

    @Override
    public Dimension preferredLayoutSize( Container parent )
    {
        throw new AssertionError();
    }

    @Override
    public Dimension minimumLayoutSize( Container parent )
    {
        throw new AssertionError();
    }

    @Override
    public Dimension maximumLayoutSize( Container target )
    {
        throw new AssertionError();
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Point2D;

/**
 * The wheel used in the Macular Mapping test. This represents the raw wheel
 * drawn as a shape without any special behavior.
 *
 * @see Wheel
 * @see ResultWheel
 *
 * @author Michael Binz
 */
public abstract class WheelBase extends ShapeComponent
{
    private final int DIAMETER_PX =
            Geometry.cmToPx( WheelModel.getWheelDiameterCm() );
    private final int DIAMETER_INNER_PX =
            DIAMETER_PX / 4 ;

    private static final int ARC_COUNT = WheelModel.NUM_HANDS;

    /**
     * The outer gap of the hands in degrees.
     */
    private static final int ARC_GAP_DEG = 6;

    private static final int ARC_DEG = (360 / ARC_COUNT);

    /**
     *
     */
    public WheelBase()
    {
        Dimension d =
                new Dimension( DIAMETER_PX, DIAMETER_PX );
        setPreferredSize(
                d );
        setSize(
                d );
        setLayout(
                new WheelLayout( this ) );
        setFocusable(
                true );
        setForeground(
                Color.WHITE );
        setOpaque(
                false );
    }

    private final Shape _wheelShape = createWheelPath();

    /**
     * Used in initialization.
     */
    private Shape createWheelPath()
    {
        Rectangle2D arcBounds = new Rectangle( 0, 0, DIAMETER_PX, DIAMETER_PX );

        int halfGap = ARC_GAP_DEG / 2;

        Path2D gp = new Path2D.Float();

        for ( int i = 0 ; i < ARC_COUNT ; i++ )
        {
            float startAngle = (i * ARC_DEG) + (ARC_DEG / 2.0f);

            gp.append( new Arc2D.Float( arcBounds,
                    startAngle + halfGap,
                    ARC_DEG - ARC_GAP_DEG,
                    Arc2D.OPEN ), true );

            float endAngle = startAngle + ARC_DEG;

            Point2D p = Geometry.pointWithDistanceFromA(
                    endAngle,
                    DIAMETER_INNER_PX/2 );

            gp.lineTo(
                    p.getX() + arcBounds.getCenterX(),
                    -p.getY() + arcBounds.getCenterY() );
        }

        gp.closePath();

        return gp;
    }

    /**
     * Created for WheelBase.java
     */
    private static final long serialVersionUID = -1710492345128587897L;

    @Override
    protected Shape createShape()
    {
        return _wheelShape;
    }

    /**
     * Get the diameter of the displayed wheel.
     *
     * @return The diameter of the displayed wheel.
     */
    public int getDiameter()
    {
        return Math.min(
                getHeight(),
                getWidth() );
    }
}

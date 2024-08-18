/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.util.List;

import de.michab.app.mmt.lab.BaseShape;
import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.PathElement;

/**
 * The wheel used in the Macular Mapping test. This represents the raw wheel
 * drawn as a shape without any special behavior.
 *
 * @author Michael Binz
 */
public class Wheel extends BaseShape
{
    private static final int DIAMETER_PX =
            Geometry.cmToPx( WheelModel.getWheelDiameterCm() );
    private static final int ARC_COUNT =
            WheelModel.NUM_HANDS;
    /**
     * The outer gap of the hands in degrees.
     */
    private static final int ARC_GAP_DEG =
            6;
    private static final int ARC_DEG =
            (360 / ARC_COUNT);

    /**
     * Create an instance.
     */
    public Wheel( Color color )
    {
        setFill(color);
        setStroke(null);
        setFillRule(FillRule.EVEN_ODD);
        resize( DIAMETER_PX, DIAMETER_PX );
        resizableProperty.set( false );
    }

    @Override
    protected void createPath( List<PathElement> result )
    {
        final double innerDiameter =
                DIAMETER_PX / 4;

        final double halfGap =
                ARC_GAP_DEG / 2.0;
        final double halfArc =
                ARC_DEG / 2.0;
        final double radius =
                DIAMETER_PX / 2.0f;
        final double innerRadius =
                innerDiameter / 2.0f;

        moveTo(
                result,
                Geometry.pointWithDistanceFromA(
                        halfArc,
                        innerRadius ) );

        for ( int i = 0 ; i < ARC_COUNT ; i++ )
        {
            float startAngle =
                    i * ARC_DEG;

            Point2D outerA = Geometry.pointWithDistanceFromA(
                    startAngle + halfGap + halfArc,
                    radius );

            lineTo( result, outerA );

            Point2D outerB = Geometry.pointWithDistanceFromA(
                    startAngle + ARC_DEG - ARC_GAP_DEG + halfArc,
                    radius );

            arcTo(
                    result,
                    radius,
                    radius,
                    0,
                    outerB.getX(),
                    outerB.getY(),
                    true,
                    false
                    );

            Point2D innerB = Geometry.pointWithDistanceFromA(
                    startAngle + ARC_DEG + halfArc,
                    innerRadius );

            lineTo( result,
                    innerB );
        }
    }

    /**
     * Get the diameter of the displayed wheel.
     *
     * @return The diameter of the displayed wheel.
     */
    public int getDiameter()
    {
        return DIAMETER_PX;
    }
}

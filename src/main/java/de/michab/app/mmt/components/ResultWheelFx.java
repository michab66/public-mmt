/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.util.List;

import org.smack.fx.FxUtil;
import org.smack.util.MathUtil;

import de.michab.app.mmt.lab.BaseNode;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/**
 * The wheel used to display the results of the Macular Mapping test.
 *
 * @author Michael Binz
 */
public class ResultWheelFx extends BaseNode
{
    private String _eccentricityStringFormat = "%.1f°";

    /**
     * The ray defining the number positions.
     */
    private double _numberRayAngle = 346;
    /**
     * TODO currently not used because of display differences between
     * pdf and in-application.
     */
    private Font _defaultFont = Font.getDefault();

    /**
     * Create an instance.
     */
    public ResultWheelFx()
    {
        super(
                DIAMETER_PX(),
                DIAMETER_PX() );

        resizableProperty.set( false );

        fillProperty.set(
                FxUtil.to( ScreenUtils.getForeground() ) );
        strokeProperty.set(
                FxUtil.to( java.awt.Color.LIGHT_GRAY ) );
        repaint();
    }

    @Override
    protected void repaint( GraphicsContext g )
    {
        double centerX =
                getWidth() / 2;
        double centerY =
                getHeight() / 2;
        double diameter =
                getDiameter();
        double radius =
                diameter / 2;

        g.fillOval(
                centerX - radius,
                centerY - radius,
                diameter,
                diameter );

        g.setFont( _defaultFont );

        for ( int i = 0 ; i < WheelModel.MAX_CIRCLE ; i++ )
        {
            radius =
                    WheelModel.getEccentricityRadius( getDiameter(), i );
            diameter =
                    2 * radius;

            g.strokeOval(
                    centerX - radius,
                    centerY - radius,
                    diameter,
                    diameter );

            Point2D p =
                    Geometry.pointWithDistanceFromA( _numberRayAngle, radius );

            // Translate.
            p = new Point2D(
                    p.getX() + centerX,
                    p.getY() + centerY );

            g.strokeText(
                    makeEccentricityLabel( WheelModel.getEccentricityDeg( i ) ),
                    MathUtil.round( p.getX() ),
                    MathUtil.round( p.getY() ) );
        }

        List<Float> handAngles =
                WheelModel.getHandAngles();
        double wheelRadius =
                getDiameter() / 2;
        double theta =
                360.0 / handAngles.size();


        for ( int i = 0 ; i < handAngles.size() / 2 ; i++ )
        {
            g.strokeLine(
                    centerX-wheelRadius,
                    centerY,
                    centerX+wheelRadius,
                    centerY );

            // I'm lazy, let the context do the math.
            g.transform(
                    new Affine(
                            new Rotate(
                                    theta, centerX, centerY) ) );
        }
    }

    /**
     * @return The diameter of the shown circle in pixels.
     */
    public double getDiameter()
    {
        return Math.min( getWidth(), getHeight() );
    }

    private String makeEccentricityLabel( float ecc )
    {
        return String.format( _eccentricityStringFormat, ecc );
    }

    private static int DIAMETER_PX()
    {
        return Geometry.cmToPx( WheelModel.getWheelDiameterCm() );
    }
}

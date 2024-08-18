/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.util;

import org.smack.util.ServiceManager;

import de.michab.app.mmt.Mmt;
import javafx.geometry.Point2D;

/**
 * Geometry support.
 *
 * @author Michael Binz
 */
public class Geometry
{

    private static int sector( double angle )
    {
        if ( angle > 3*90 )
        {
            return 3;
        }
        if ( angle > 2*90 )
        {
            return 2;
        }
        if ( angle > 1*90 )
        {
            return 1;
        }

        return 0;
    }

    /**
     * Computes a point that has a given distance from P(0,0) and is on a line
     * with a given angle from P.
     *
     * @param angle The angle of the ray.
     * @param distance The distance of the target point.
     * @return The target point.
     */
    public static Point2D pointWithDistanceFromA( double angle, double distance )
    {
        // Ensure that we stay in the 360 degree range.
        angle %= 360.0f;

        return pointWithDistanceFromA(
                sector( angle ),
                Math.tan( Math.toRadians( angle )),
                distance );
    }

    private static Point2D pointWithDistanceFromA( int sector, double m, double distance )
    {
        double resultX;
        double resultY;

        // Check this!
        if ( m != 0.0 )
        {
            double intersection = intersectLineAndCircle( m, distance );
            resultY = intersection;
            resultX = Math.abs( intersection / m );
        }
        else
        {
            resultX = distance;
            resultY = 0;
        }

        switch ( sector )
        {
            case 0:
                break;
            case 1:
                resultX = -resultX;
                break;
            case 2:
                resultX = -resultX;
                resultY = -resultY;
                break;
            case 3:
                resultY = -resultY;
                break;
        }

        return new Point2D( resultX, resultY );
    }

    /**
     *
     * @param lineSlope
     * @param circleRadius
     * @return
     */
    private static double intersectLineAndCircle( double lineSlope, double circleRadius )
    {
        double radiusSquare =
                circleRadius * circleRadius;
        double slopeSquare =
                lineSlope * lineSlope;

        double squared =
                radiusSquare / (1.0d + slopeSquare);

        return Math.abs( lineSlope * Math.sqrt( squared ) );
    }

    enum Unit {
        CM, INCH
    }

    public static float getDpi()
    {
        return ServiceManager.getApplicationService( Mmt.class ).getDpi();
    }

    /**
     * Converts a centimeter dimension to a calibrated pixel value.
     *
     * @param cm The number of centimeters to convert.
     * @return The number of pixels representing the passed length.
     */
    static public int cmToPx( float cm )
    {
        return inchToPx( toInch( cm ) );
    }

    /**
     * Convert the passed pixel length to centimeters.
     *
     * @param pix A pixel length.
     * @return The result in centimeters.
     */
    static public float pxToCm( float pix )
    {
        return toCm( pxToInch( pix ) );
    }

    /**
     * Converts an inch dimension to a calibrated pixel value.
     *
     * @param inch The number of inches to convert.
     * @return The number of pixels representing the passed length.
     */
    public static int inchToPx( float inch )
    {
        return Math.round( inch * getDpi() );
    }

    /**
     * Converts the passed pixel length to inch.
     *
     * @param pix A pixel length.
     * @return The length in inch.
     */
    public static float pxToInch( float pix )
    {
        return pix / getDpi();
    }

    /**
     * Conversion factor cm to inch.
     */
    private static final float _2_54 = 2.54f;

    /**
     * Converts the passed centimeter length to inch.
     *
     * @param cm A centimeter length.
     * @return The length in inch.
     */
    public static float toInch( float cm )
    {
        return cm / _2_54;
    }

    /**
     * Converts the passed inch length to centimeters.
     *
     * @param inch An inch length.
     * @return The length in centimeters.
     */
    public static float toCm( float inch )
    {
        return inch * _2_54;
    }

    /**
     * Hide constructor.
     */
    private Geometry()
    {
        throw new AssertionError();
    }
}

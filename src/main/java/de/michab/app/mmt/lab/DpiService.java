/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.awt.Toolkit;

import org.jdesktop.beans.PropertyLink;

import de.michab.app.mmt.Mmt;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.converter.FloatStringConverter;

/**
 * Geometry support.
 *
 * @author Michael Binz
 */
public class DpiService
{
    private ObjectProperty<Float> _dpi =
            PropertyLink.persist(
                    new SimpleObjectProperty<>(
                            this,
                            "dpi",
                            Float.NaN ),
                    new FloatStringConverter() );

    /**
     * Offers the application-wide dpi setting.
     *
     * @return The application-wide dpi setting.
     */
    public float getDpi()
    {
        float result = _dpi.get();

        // If already initialized...
        if ( ! Float.isNaN( result ) )
            return result;

        return Toolkit.getDefaultToolkit().getScreenResolution();
    }

    /**
     * Set the application wide dpi value.
     *
     * @param dpi The value to set. This is a bound property.
     * @see Mmt#PN_DPI
     */
    public void setDpi( float dpi )
    {
        _dpi.set( dpi );
    }

    /**
     * @return True if the screen resolution is calibrated.
     */
    public boolean isCalibrated()
    {
        return ! Float.isNaN( _dpi.get() );
    }

    /**
     * Converts a centimeter dimension to a calibrated pixel value.
     *
     * @param cm The number of centimeters to convert.
     * @return The number of pixels representing the passed length.
     */
    public int cmToPx( float cm )
    {
        return inchToPx( toInch( cm ) );
    }

    /**
     * Convert the passed pixel length to centimeters.
     *
     * @param pix A pixel length.
     * @return The result in centimeters.
     */
    public float pxToCm( float pix )
    {
        return toCm( pxToInch( pix ) );
    }

    /**
     * Converts an inch dimension to a calibrated pixel value.
     *
     * @param inch The number of inches to convert.
     * @return The number of pixels representing the passed length.
     */
    public int inchToPx( float inch )
    {
        return Math.round( inch * getDpi() );
    }

    /**
     * Converts the passed pixel length to inch.
     *
     * @param pix A pixel length.
     * @return The length in inch.
     */
    public float pxToInch( float pix )
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
    public float toInch( float cm )
    {
        return cm / _2_54;
    }

    /**
     * Converts the passed inch length to centimeters.
     *
     * @param inch An inch length.
     * @return The length in centimeters.
     */
    public  float toCm( float inch )
    {
        return inch * _2_54;
    }

    /**
     * Hide constructor.
     */
    public DpiService()
    {
    }
}

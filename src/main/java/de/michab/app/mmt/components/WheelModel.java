/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.smack.util.MathUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.util.Geometry;

/**
 * A model for wheel components.
 *
 * @author Michael Binz
 */
public class WheelModel
{
//    private static final Logger LOG =
//            Logger.getLogger( WheelModel.class.getName() );

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( WheelModel.class );
    }

    /**
     * The circle eccentricities from inner to outer.
     */
    @Resource
    private static float[] _eccentricitiesDeg; /* =
        { 2.5f, 5.0f, 7.5f, 10.0f, 11.5f }; */

    private static final float MAX_ECCENTRICITY =
            _eccentricitiesDeg[ _eccentricitiesDeg.length-1 ];

    private static final float[] _eccentricityFactors = initFactors();

    private static final float[] initFactors()
    {
        // Step 1: Compute b for a of 1.0.  The resulting b is arbitrary.
        // The goal is to get the scaling factors for different as (eccentricities),
        // starting with 1.0 for the largest eccentricity.
        double b = 1.0 / Math.tan( Math.toRadians( MAX_ECCENTRICITY ) );

        // Step 2: For each eccentricity in degrees compute the scaling
        // factor. The last entry in the row is 1.0.
        float[] result = new float[ _eccentricitiesDeg.length ];

        int idx = 0;
        for ( float c : _eccentricitiesDeg )
        {
            result[idx++] = (float)
                    (b * Math.tan( Math.toRadians( c ) ) );
        }

        return result;
    }

    /**
     * @param wheelDiameter The wheel diameter.
     * @param eccentricityIdx The eccentricity index.
     * @return The respective radius.
     */
    public static double getEccentricityRadius(
            double wheelDiameter,
            int eccentricityIdx )
    {
        return MathUtil.round(
                wheelDiameter * 0.5 * _eccentricityFactors[eccentricityIdx] );

    }

    /**
     * Get the eccentricity in degrees for the passed eccentricity.
     *
     * @param eccentricityIdx
     * @return
     */
    public static float getEccentricityDeg( int eccentricityIdx )
    {
        return _eccentricitiesDeg[ eccentricityIdx ];
    }

    public static final int NUM_HANDS = 8;
    public static final int MAX_CIRCLE = _eccentricitiesDeg.length -1;

    /**
     * Create an instance.
     */
    private WheelModel()
    {
        throw new AssertionError();
    }

    /**
     * Get the viewing distance so that eccentricity requirements are met.
     *
     * @return The expected viewing distance.
     */
    private static float getViewingDistanceCm()
    {
        return 40.0f;
    }

    /**
     *
     * @return
     */
    public static float getWheelDiameterCm()
    {
        float radius = (float)
            (getViewingDistanceCm() * Math.tan( Math.toRadians( MAX_ECCENTRICITY ) ));

        return 2.0f * radius;
    }

    /**
     * Get the angles of the hands. The first angle is zero.
     *
     * @return The hand's number and angles.
     */
    public static List<Float> getHandAngles()
    {
        List<Float> result = new ArrayList<>( NUM_HANDS );

        float delta = 360.0f / NUM_HANDS;

        for ( int i = 0 ; i < NUM_HANDS ; i++ )
        {
            result.add( i * delta );
        }

        return Collections.unmodifiableList( result );
    }

    @Resource
    private static float[] _glyphHeightsCm; /* =
        { .49f, .57f, .65f, .74f }; */

    /**
     * Get the height of the glyph at the passed position.
     *
     * @param eIdx The excentricity index.
     * @param handIdx The hand index.
     * @return A glyph height.
     */
    public static float getGlyphHeightInCm( int eIdx, int handIdx )
    {
        float height;

        if ( eIdx == 0 )
        {
            height = _glyphHeightsCm[ handIdx / 2 ];
        }
        else
        {
            height = _glyphHeightsCm[ eIdx-1 ];
        }

        return height;
    }

    public static float getGlyphHeightInPx( int eIdx, int handIdx )
    {
        float inCm = getGlyphHeightInCm(
                eIdx,
                handIdx );

        return Geometry.cmToPx( inCm );
    }
 }

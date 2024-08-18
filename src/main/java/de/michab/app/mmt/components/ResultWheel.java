/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.smack.util.MathUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Probe;
import de.michab.app.mmt.util.Geometry;
import de.michab.app.mmt.util.MmtUtils;
import javafx.geometry.Point2D;

/**
 * The wheel used to display the results of the Macular Mapping test.
 *
 * @author Michael Binz
 */
public class ResultWheel extends WheelBase
{
    @Resource
    private String _eccentricityStringFormat;
    @Resource
    private Color _decorationColor;
    @Resource
    private float _numberRayAngle;
    /**
     * TODO currently not used because of display differences between
     * pdf and in-application.
     */
    //@Resource
    //private Font _defaultFont;

    /**
     * Create an instance.
     */
    public ResultWheel()
    {
        init();
    }

    /**
     * Create a wheel and initialize it using the passed experiment.
     *
     * @param experiment The experiment to display, null is allowed.
     */
    public ResultWheel( Experiment experiment )
    {
        if ( experiment != null )
        {
            // Add the result components.
            for ( Probe c : experiment.getProbes() )
                add( new MmtTestResult( c.getScore() ), c.getPosition() );
        }

        init();
    }

    private void init()
    {
        ServiceManager.getApplicationService( ResourceManager.class ).
            injectResources( this );

        setFocusable( false );
    }

    @Override
    protected Shape createShape()
    {
        return new Ellipse2D.Float( 0, 0, 10, 10 );
    }

    private void paintComponentImpl( Graphics2D g )
    {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g.setFont( getFont() );

        for ( int i = 0 ; i < WheelModel.MAX_CIRCLE ; i++ )
        {
            int radius =
                    (int)WheelModel.getEccentricityRadius( getDiameter(), i );
            int diameter =
                    2 * radius;

            g.setColor(
                    _decorationColor );
            g.drawOval(
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

            g.setColor(
                    getFill() ?
                            getBackground() :
                            getForeground() );

            g.drawString(
                    makeEccentricityLabel( WheelModel.getEccentricityDeg( i ) ),
                    MathUtil.round( p.getX() ),
                    MathUtil.round( p.getY() ) );
        }

        g.setColor( _decorationColor );

        List<Float> handAngles =
                WheelModel.getHandAngles();
        int wheelRadius =
                getDiameter() / 2;
        double theta =
                Math.toRadians( 360.0 / handAngles.size() );

        for ( int i = 0 ; i < handAngles.size() / 2 ; i++ )
        {
            g.drawLine(
                    centerX-wheelRadius,
                    centerY,
                    centerX+wheelRadius,
                    centerY );

            // I'm lazy, let the context do the math.
            g.rotate(
                    theta,
                    centerX,
                    centerY );
        }
    }

    @Override
    protected void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        Graphics2D g2 = MmtUtils.cloneAntialiased( g );

        try
        {
            paintComponentImpl( g2 );
        }
        finally
        {
            g2.dispose();
        }
    }

    private String makeEccentricityLabel( float ecc )
    {
        return String.format( _eccentricityStringFormat, ecc );
    }

    /**
     * Generated for ResultWheel.java.
     */
    private static final long serialVersionUID = 8438602778762464591L;
}

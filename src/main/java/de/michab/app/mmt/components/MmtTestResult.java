/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.jdesktop.beans.JavaBeanProperty;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Probe.Score;
import de.michab.app.mmt.util.MmtUtils;

/**
 * A component that represents a test result in MMT. This is square black
 * and white or split color.
 *
 * @author Michael Binz
 */
@SuppressWarnings("serial")
public class MmtTestResult extends JComponent
{
    private final static Logger LOG =
            Logger.getLogger( MmtTestResult.class.getName() );

    private final Score _mode;

    @Resource
    private float SIZE_RELATIVE_TO_PARENT;
    @Resource
    private Color DFLT_BACKGROUND;
    @Resource
    private Color DFLT_FOREGROUND;

    /**
     * The border color property.
     */
    public final JavaBeanProperty<Color, MmtTestResult> borderColor =
            new JavaBeanProperty<>( this, null, "borderColor" );

    /**
     * Create an instance for the passed score.
     *
     * @param score The score to display.
     */
    public MmtTestResult( Score score )
    {
        ServiceManager.getApplicationService( ResourceManager.class ).
            injectResources( this );

        _mode = score;

        setBackground( DFLT_BACKGROUND );
        setForeground( DFLT_FOREGROUND );

        setOpaque( true );

        WheelLayout.wantSpecialCenterlayout( this, true );
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension parentSize = getParent().getSize();

        int relevantSize = Math.min(
                parentSize.width,
                parentSize.height );

        int edgeLength = Math.round(
                SIZE_RELATIVE_TO_PARENT * relevantSize );

        return new Dimension( edgeLength, edgeLength );
    }

    /**
     * Get the border color. May return null if no border color is set.
     */
    public Color getBorderColor()
    {
        return borderColor.get();
    }

    /**
     * Set the border color.
     *
     * @param newValue The new border color. Null allowed.
     */
    public void setBorderColor( Color newValue )
    {
        borderColor.set( newValue );
    }

    /**
     * If we have a border color explicitly set, return this.
     * Otherwise return background color.
     */
    private Color computeBorderColor()
    {
        Color result = getBorderColor();

        if ( result == null )
            result = getForeground();

        return result;
    }

    private void paintComponentImpl( Graphics2D g )
    {
        Rectangle2D box;
        {
            int w = getWidth();
            int h = getHeight();
            int squareEdgeLength = Math.min( w, h );

            box = new Rectangle2D.Float(
                    Math.round( (w - squareEdgeLength) * 0.5f ),
                    Math.round( (h - squareEdgeLength) * 0.5f ),
                    squareEdgeLength-1,
                    squareEdgeLength-1 );
        }

        if ( _mode == Score.UNNOTICED )
        {
            g.setColor( getForeground() );
            g.fill( box );
        }
        else if ( _mode == Score.RECOGNIZED )
        {
            g.setColor( getBackground() );
            g.fill( box );
        }
        else if ( _mode == Score.NOTICED )
        {
            g.setColor( getBackground() );
            g.fill( box );
            g.setColor( getForeground() );

            Path2D gp = new Path2D.Float();
            gp.moveTo( box.getX(), box.getY() );
            gp.lineTo( box.getWidth(), box.getHeight() );
            gp.lineTo( box.getX(), box.getHeight() );
            gp.closePath();
            g.fill( gp );
        }
        else
            LOG.severe( "Unexpected mode: " + _mode );

        // Draw a frame in border color.
        g.setColor( computeBorderColor() );
        g.draw( box );
    }

    @Override
    public void paintComponent( Graphics g )
    {
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
}

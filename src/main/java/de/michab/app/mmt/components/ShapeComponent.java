/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;

import javax.swing.JComponent;

import org.jdesktop.beans.JavaBeanProperty;

import de.michab.app.mmt.util.ImageUtil;
import de.michab.app.mmt.util.MmtUtils;


/**
 * A shape-based component.
 *
 * @author Michael Binz
 */
public abstract class ShapeComponent extends JComponent
{
    /**
     * The cached shape. Only initialized once.
     */
    private Shape _shapeCached = null;

    /**
     * Create an instance.
     */
    public ShapeComponent()
    {
        setOpaque( false );
    }

    /**
     * Draws the shape scaled, but keeps x,y relation.
     *
     * @param g The graphics context to draw on. It is
     * not needed to dispose this context.
     */
    private void paintComponentImpl( Graphics2D g )
    {
        // Obey the border settings.
        Insets border = getInsets();

        Dimension dimension = new Dimension(
                getWidth()
                    -border.left
                    -border.right,
                getHeight()
                    -border.top
                    -border.bottom );

        if ( isOpaque() )
        {
            g.setColor( getBackground() );
            g.fillRect(
                    border.left,
                    border.top,
                    dimension.width,
                    dimension.height );
        }

        g.setColor( getForeground() );

        g.translate(
                border.left,
                border.top );

        ImageUtil.drawShapeImage(
                dimension,
                g,
                getShape(),
                getFill() );
    }

    @Override
    protected void paintComponent( Graphics graphics )
    {
        Graphics2D g = MmtUtils.cloneAntialiased( graphics );

        try
        {
            paintComponentImpl( g );
        }
        finally
        {
            g.dispose();
        }
    }

    private final JavaBeanProperty<Boolean, ShapeComponent> _fill =
            new JavaBeanProperty<Boolean, ShapeComponent>( this, Boolean.TRUE, "fill" );

    /**
     * Get the current fill property setting.
     *
     * @return The current fill property setting.
     */
    public boolean getFill()
    {
        return _fill.get();
    }

    /**
     * Sets the fill property.
     *
     * @param newValue  On true, the component is filled, on false
     * only an outline is drawn.
     */
    public void setFill( boolean newValue )
    {
        _fill.set( newValue );
    }

    @Override
    protected void printComponent( Graphics g )
    {
        synchronized ( getTreeLock() )
        {
            validateTree();
        }

        super.printComponent( g );
    }

    private Shape getShape()
    {
        if ( _shapeCached == null )
            _shapeCached = createShape();

        return _shapeCached;
    }
    protected abstract Shape createShape();

    /**
     * Generated for {@link ShapeComponent};
     */
    private static final long serialVersionUID = -5270380670055689410L;
}

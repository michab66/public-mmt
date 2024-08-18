/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

/**
 * A visible object that can be placed on a pdf page.  Note that an
 * implementation has full control and responsibility on its own size.
 * The position of the {@link PdfPageElement} is controlled from the
 * surrounding layout engine and cannot be controlled.
 *
 * Paint your component a the position that is defined by {@link #getX()} and
 * {@link #getY()}.
 *
 * @author Michael Binz
 */
public abstract class PdfPageElement
{
    public enum Pos { TOP, BOTTOM, CENTER, START, END };

    private float _x;
    private float _y;
    private float _width;
    private float _height;
    private Color _foreground = Color.BLACK;
    private Font _font;

    public abstract void paint( PdfGfx gfx2 )
        throws Exception;

    /**
     * Allows to store a component implementation to store its current size.
     *
     * @param w The width.
     * @param h The height.
     */
    protected void setSize( float w, float h )
    {
        _width = w;
        _height = h;
    }

    /**
     * Returns the point in the coordinate system that has the greatest distance
     * from the coordinate system's origin.  Simply said, this is the point to
     * the bottom left of the coordinate system.
     *
     * @return The point marking the size of the coordinate system.
     */
    public Point2D getDimension()
    {
        return new Point2D.Float( _width, _height );
    }

    public PdfPageElement setPosition( float x, float y )
    {
        _x = x;
        _y = y;

        return this;
    }

    /**
     * Get the width.
     *
     * @return The width.
     * @see #setSize(float, float)
     */
    public float getWidth()
    {
        return _width;
    }

    /**
     * Get the height.
     *
     * @return The height.
     * @see #setSize(float, float)
     */
    public float getHeight()
    {
        return _height;
    }

    public float getX()
    {
        return _x;
    }

    public float getY()
    {
        return _y;
    }

    public PdfPageElement setForeground( Color color )
    {
        _foreground = color;

        return this;
    }

    public Color getForeground()
    {
        return _foreground;
    }

    public PdfPageElement setFont( Font font )
    {
        _font = font;
        return this;
    }

    public Font getFont()
    {
        return _font;
    }
}

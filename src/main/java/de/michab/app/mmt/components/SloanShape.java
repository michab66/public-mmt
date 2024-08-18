/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.smack.util.StringUtil;

import de.michab.app.mmt.lab.BaseShape;
import de.michab.app.mmt.util.ImageUtil;
import javafx.scene.shape.PathElement;

/**
 * Sloan Shapes.
 *
 * @see https://en.wikipedia.org/wiki/Sloan_letters
 * @author Michael Binz
 */
public class SloanShape extends BaseShape
{
    private static char[] _characters = "CDHKNORSVZ".toCharArray();

    public enum Glyph {
        C ('C'),
        D ('D'),
        H ('H'),
        K ('K'),
        N ('N'),
        O ('O'),
        R ('R'),
        S ('S'),
        V ('V'),
        Z ('Z');

        public final char _char;

        private Glyph( char character )
        {
            _char = character;
        }
    };

    private final Glyph _glyph;

    public SloanShape( Glyph glyph )
    {
        setStroke( null );

        _glyph = glyph;

        repaint();
    }

    public char getDemoChar()
    {
        return _characters[4];
    }

    /**
     * The base font used to create the shape.  The size is arbitrary,
     * i.e. the resulting text is scaled into the shape's available
     * bounding box.
     */
    private final Font _font = java.awt.Font.decode(
            "SansSerif-bold-72" );

    @Override
    protected void createPath( List<PathElement> pathElements )
    {
        // We're called from the ctor, _glyph may not be
        // initialized yet.
        if ( _glyph == null )
            throw new AssertionError();

        Shape awtShape =
                createShapeFor( _glyph._char );
        add(
                pathElements,
                awtShape );
    }

    private Shape createShapeFor( char character )
    {
        //
        // Compute the shape using awt mechanics.
        //
        String expectedString = StringUtil.EMPTY_STRING + character;

        GlyphVector gv = _font.createGlyphVector(
                getFontMetrics( _font ).getFontRenderContext(),
                expectedString );

        Shape result = gv.getOutline();

        //
        // Scale the shape into our available bounds.
        //
        Rectangle2D r =
                result.getBounds2D();
        AffineTransform transform =
                new AffineTransform();
        transform.scale(
                widthProperty().get() / r.getWidth(),
                heightProperty().get() / r.getHeight() );
        return transform.createTransformedShape(
                result );
    }

    private static FontMetrics getFontMetrics( Font font )
    {
        Graphics2D g = ImageUtil.makeImage( 1, 1 ).createGraphics();

        try
        {
            g.setFont( font );
            return g.getFontMetrics();
        }
        finally
        {
            g.dispose();
        }
    }
}

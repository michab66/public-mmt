/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

import javax.swing.Icon;

import org.smack.fx.FxUtil;
import org.smack.util.MathUtil;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

/**
 * Image helper operations.
 *
 * @author Michael Binz
 */
public final class ImageUtil
{
    /**
     * Tries to narrow the bounds of the passed shape.
     *
     * @param shape The shape whose bounds are tried to be narrowed.
     * @return The narrowed bounds.
     */
    private static Rectangle2D.Float narrowBounds( Shape shape )
    {
        Rectangle2D.Float result = new Rectangle2D.Float();

        float[] coords = new float[6];

        for ( PathIterator i = shape.getPathIterator(null, 1) ; ! i.isDone() ; i.next() )
        {
            if ( i.currentSegment(coords) != PathIterator.SEG_CLOSE )
                result.add(coords[0], coords[1]);
        }

        return result;
    }

    /**
     * Scales and centers a shape to fit into the passed graphics context
     * with the passed size. Keeps the x/y relation.
     *
     * @param size
     * @param g
     * @param shape
     * @param drawFilled
     */
    public static void drawShapeImage(
            Dimension size,
            Graphics2D g,
            Shape shape,
            boolean drawFilled )
    {
        Rectangle2D bounds = narrowBounds( shape );

        double glyphWidth =
                bounds.getWidth();
        double glyphHeight =
                bounds.getHeight();

        double scx = size.getWidth() / glyphWidth;
        double scy = size.getHeight() / glyphHeight;
        double scm = Math.min( scx, scy );

        double glyphWidthScaled = glyphWidth * scm;
        double glyphHeightScaled = glyphHeight * scm;

        // Center the result.
        {
            double btlx = 0.0;
            double btly = 0.0;
            if ( glyphHeightScaled < size.getHeight() )
                btly = (size.getHeight() - glyphHeightScaled) / 2.0;
            if ( glyphWidthScaled < size.getWidth() )
                btlx = (size.getWidth() - glyphWidthScaled) / 2.0;
            g.translate( btlx, btly );
        }

        // Set the scaling.
        g.scale( scm, scm );

        // Move to the right position in the scaled coordinates.
        double tlx =
                bounds.getX();
        double tly =
                bounds.getY();
        g.translate( -tlx, -tly );

        if ( drawFilled )
            g.fill( shape );
        else
            g.draw( shape );
    }

    /**
     * Make an image with absolute size from the passed text.
     *
     * @param size
     * @param color
     * @param font
     * @param text
     * @return
     */
    public static BufferedImage makeGlyphImage( int size, Color color, Font font, String text )
    {
        BufferedImage result = makeImage( new Dimension( size, size ) );

        Graphics2D g = result.createGraphics();

        try
        {
            GlyphVector gv = font.createGlyphVector(
                    g.getFontRenderContext(),
                    text );

            Rectangle2D r = narrowBounds( gv.getOutline() );

            double width;

            if ( r.getHeight() >= r.getWidth() )
                width = size;
            else
            {
                width =
                        (size / r.getHeight()) * r.getWidth();
            }

            Dimension d = new Dimension(
                    (int)Math.round( width ),
                    size );

            return makeGlyphImage( d, color, font, text );
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Make an image with absolute size from the passed text.
     *
     * @param size
     * @param color
     * @param font
     * @param text
     * @return
     */
    public static BufferedImage makeGlyphImage( Dimension size, Color color, Font font, String text )
    {
        BufferedImage result = makeImage( size );

        Graphics2D g = antialiasedGfx(
                result );
        GlyphVector gv = font.createGlyphVector(
                g.getFontRenderContext(),
                text.toCharArray() );

        Shape shape = gv.getOutline();

        g.setColor( color );
        drawShapeImage( size, g, shape, true );
        g.dispose();

        return result;
    }

    /**
     *
     * @param size
     * @param color
     * @param shape
     * @return
     */
    public static BufferedImage makeShapeImage( Dimension size, Color color, Shape shape, boolean fill )
    {
        BufferedImage result =
                makeImage( size );
        Graphics2D g =
                antialiasedGfx( result );
        try
        {
            g.setColor( color );
            drawShapeImage( size, g, shape, fill );
        }
        finally
        {
            g.dispose();
        }

        return result;
    }

    /**
     * Make an image from the passed text. The size corresponds to the
     * given font.
     *
     * @param color The color to use to draw the text.
     * @param font The font to use.
     * @param text The text to draw.
     * @return An image that shows the passed text.
     */
    public static BufferedImage makeTextImage( Color color, Font font, String text )
    {
        BufferedImage result = makeImage( new Dimension( 1, 1 ) );

        Graphics2D g = result.createGraphics();

        try
        {
            // Compute the size of the actual image to return.
            GlyphVector gv = font.createGlyphVector(
                    g.getFontRenderContext(),
                    text );
            int height =
                    g.getFontMetrics( font ).getHeight();
            int baseline =
                    g.getFontMetrics( font ).getAscent();
            Rectangle2D bounds =
                    narrowBounds( gv.getOutline() );

            // Switch from the dummy result to the actual result.
            g.dispose();
            result = makeImage((int)bounds.getWidth(), height );
            g = antialiasedGfx( result );
            g.setColor( color );
            g.setFont( font );
            g.drawString( text, 0, baseline );

            return result;
        }
        finally
        {
            g.dispose();
        }
    }

    /**
     * Create a transparent image of the specified size.
     *
     * @param size The target size of the image.
     * @return A newly allocated image of the requested size.
     */
    public static BufferedImage makeImage( Dimension size )
    {
        return makeImage(
                size.width,
                size.height );
    }

    /**
     * Create a transparent image of the specified size.
     *
     * @param width The width of the returned image.
     * @param height The height of the returned image.
     * @return A newly allocated image of the requested size.
     */
    public static BufferedImage makeImage( int width, int height )
    {
        return new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB );
    }

    /**
     * Sandwich a number of images.
     *
     * @param xAlignment Alignment in x direction [0.0..1.0].
     * @param yAlignment Alignment in y direction [0.0..1.0].
     * @param images The images to sandwich, processed left-to-right, i.e.
     * the last image is the topmost drawn.
     * @return The sandwich image. Dimensions are the maxima of the passed
     * image's x/y dimensions.
     */
    public static BufferedImage stackImages(
            float xAlignment,
            float yAlignment,
            Image ... images )
    {
        final BufferedImage result;
        // Find the maximum dimension and create the respective result image.
        {
            int maxWidth = 0;
            int maxHeight = 0;

            for ( Image c : images )
            {
                maxWidth = Math.max( maxWidth, c.getWidth( null ) );
                maxHeight = Math.max( maxHeight, c.getHeight( null ) );
            }

            result = makeImage( maxWidth, maxHeight );
        }

        // Paint the images in the passed order into the result image.
        // Center in x and y direction.

        Graphics2D gfx = result.createGraphics();

        try
        {
            for ( Image c : images )
            {
                float x = (result.getWidth() - c.getWidth( null )) * xAlignment;
                float y = (result.getHeight() - c.getHeight( null )) * yAlignment;

                gfx.drawImage(
                        c,
                        Math.round( x ),
                        Math.round( y ),
                        null );
            }
        }
        finally
        {
            gfx.dispose();
        }

        return result;
    }

    /**
     * Sandwich a number of images.
     *
     * @param images The images to sandwich, processed left-to-right, i.e.
     * the last image is the topmost drawn.
     * @return The sandwich image. Dimensions are the maxima of the passed
     * image's x/y dimensions.
     */
    public static BufferedImage stackImages(
            Image ... images )
    {
        return stackImages( 0.5f,  0.5f, images );
    }

    /**
     * Converts a given Image into a BufferedImage.
     *
     * @param image The Image to be converted.
     * @return If the passed image was
     * already a BufferedImage it is simply returned, otherwise
     * a newly allocated image is returned.
     */
    public static BufferedImage toBufferedImage( Image image )
    {
        try
        {
            return (BufferedImage)image;
        }
        catch ( ClassCastException e )
        {
            return cloneImage( image );
        }
    }

    /**
     * Converts an icon to a BufferedImage.
     *
     * @param img The Image to be converted.
     * @return A newly allocated BufferedImage instance.
     */
    public static BufferedImage toBufferedImage( Icon img )
    {
        BufferedImage result = makeImage(
                img.getIconWidth(),
                img.getIconHeight() );

        Graphics2D bGr = result.createGraphics();

        try
        {
            img.paintIcon( null, bGr, 0, 0 );
        }
        finally
        {
            bGr.dispose();
        }

        // Return the buffered image
        return result;
    }

    public static BufferedImage cloneImage( Image image )
    {
        BufferedImage result = makeImage(
                image.getWidth( null ),
                image.getHeight( null ) );

        Graphics2D g = result.createGraphics();
        try
        {
            g.drawImage( image, 0, 0, null );
        }
        finally
        {
            g.dispose();
        }

        return result;
    }

    public static WritableImage cloneImage( javafx.scene.image.Image image )
    {
        return subImage(
                image,
                0,
                0,
                MathUtil.round( image.getWidth() ),
                MathUtil.round( image.getHeight() ) );
    }

    public static WritableImage subImage( javafx.scene.image.Image image, int x, int y, int w, int h )
    {
        PixelReader pr = image.getPixelReader();

        return new WritableImage(
                pr,
                x,
                y,
                w,
                h );
    }

    /**
     *
     * @param image
     * @param color
     * @return
     */
    public static BufferedImage stainImage( Image image, Color color )
    {
        // This implementation is working, but is pretty inefficient.
        // TODO: Rework.
        Image result = Toolkit.getDefaultToolkit().createImage(
                new FilteredImageSource(
                        image.getSource(),
                        new DropColour( color ) ) );

        return toBufferedImage( result );
    }

    public static javafx.scene.image.Image stainImage(
            javafx.scene.image.Image image,
            javafx.scene.paint.Color color )
    {
        BufferedImage bi = stainImage(
                SwingFXUtils.fromFXImage( image, null ),
                FxUtil.to( color ) );

        return SwingFXUtils.toFXImage( bi, null );
    }

    public static Dimension dimensionOf( Image image )
    {
        return new Dimension(
                image.getWidth( null ),
                image.getHeight( null ) );
    }

    public static Dimension dimensionOf( Icon icon )
    {
        return new Dimension(
                icon.getIconWidth(),
                icon.getIconHeight() );
    }

    private static class DropColour extends RGBImageFilter
    {
        private final Color __color;

        DropColour( Color color )
        {
            canFilterIndexColorModel = true;

            __color = color;
        }

        private static final float F = 1.0f / 0xff;

        @Override
        public int filterRGB( int x, int y, int rgb )
        {
            Color c = new Color( rgb, true );

            float r = F * c.getRed();
            float g = F * c.getGreen();
            float b = F * c.getBlue();

            Color result = new Color(
                    Math.round(r * __color.getRed()),
                    Math.round(g * __color.getGreen()),
                    Math.round(b * __color.getBlue()),
                    c.getAlpha() );

            return result.getRGB();
        }
    }

    /**
     * Activate antialiasing for the passed graphics context.
     *
     * @param gfx A graphic context.
     * @return The passed graphics context with antialiasing switched on.
     */
    public static Graphics2D addAntialiasing( Graphics gfx )
    {
        Graphics2D g2 = (Graphics2D)gfx;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return g2;
    }

    /**
     * @param img An image.
     * @return The graphics context of the image with antialiasing
     * switched on.
     */
    public static Graphics2D antialiasedGfx( BufferedImage img )
    {
        return addAntialiasing(
                img.createGraphics() );
    }

    /**
     * Convert a BufferedImage to an FX image.
     *
     * @param image The Image to convert.
     * @return A newly allocated fx image.
     */
    public static WritableImage toFxImage( Image image )
    {
        BufferedImage clone =
                cloneImage( image );

        return SwingFXUtils.toFXImage( clone, null );
    }

    /**
     * Hide constructor.
     */
    private ImageUtil()
    {
        throw new AssertionError();
    }
}

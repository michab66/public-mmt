/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import de.michab.app.mmt.util.ImageUtil;

/**
 * A PDF-based graphics context. May take longer to completely implement.
 *
 * @author Michael Binz
 */
public class PdfGfx extends Graphics2D implements AutoCloseable
{
    private final static Logger LOG =
            Logger.getLogger( PdfGfx.class.getName() );

    /**
     * The PDF page this context is attached to.
     */
    private final PdfPage _page;

    /**
     * The PDF content stream used to draw.
     */
    private final PDPageContentStream _contentStream;

    /**
     * The current transform of this graphics context.
     */
    private final AffineTransform _currentTransform;

    /**
     * Used to control whether the embedded content stream needs disposal.
     * Note that we must not close the content stream twice or bad things
     * happen. And NOTE: We inherit a finalizer from {@link Graphics2D}
     * so we get always called more than once.
     */
    private boolean _needsDispose;

    /**
     * Create an instance.
     *
     * @param cs The encapsulated content stream.
     */
    public PdfGfx(
            PdfPage page,
            PDPageContentStream cs )
    {
        _needsDispose =
                true;
        _page =
                page;
       _contentStream =
               cs;
       _currentTransform =
               new AffineTransform();
       // Adapt to the page coordinate system.
       _currentTransform.translate( 0, _page.getHeight() );
       _currentTransform.scale( 1.0, -1.0 );

       Objects.requireNonNull( page.getFont() );

       setFont( page.getFont() );
    }

    private PdfGfx( PdfGfx parent )
    {
        _needsDispose =
                false;
        _page =
                parent._page;
        _contentStream =
                parent._contentStream;
        _clipBounds =
                parent.getClipBounds();
        _color =
                parent.getColor();
        _font =
                parent.getFont();
        _pdFont =
                parent._pdFont;
        _stroke =
                parent._stroke;
        _currentTransform =
                new AffineTransform( parent._currentTransform );
    }

    @Override
    public void draw( Shape s )
    {
        try
        {
            drawShapeImpl( s );

            _contentStream.stroke();
        }
        catch ( IOException e )
        {
            handleException( e );
        }
    }

    @Override
    public boolean drawImage( Image img, AffineTransform xform,
            ImageObserver obs )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawImage( BufferedImage img, BufferedImageOp op, int x, int y )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawRenderedImage( RenderedImage img, AffineTransform xform )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawRenderableImage( RenderableImage img, AffineTransform xform )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawString( String str, int x, int y )
    {
        drawString( str, (float)x, (float)y );
    }

    @Override
    public void drawString( String str, float x, float y )
    {
        try
        {
            applyTextState( _contentStream );

            float[] coordinate = new float[]{ x, y };

            translateToPdf( coordinate, 0, coordinate, 0, 1 );

            x = coordinate[0];
            y = coordinate[1];

            _contentStream.beginText();
            _contentStream.newLineAtOffset( x, y );
            _contentStream.showText( str );
            _contentStream.endText();
        }
        catch ( Exception e )
        {
            handleException( e );
        }
    }

    @Override
    public void drawString( AttributedCharacterIterator iterator, int x, int y )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawString( AttributedCharacterIterator iterator, float x, float y )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawGlyphVector( GlyphVector g, float x, float y )
    {
        AffineTransform originalTransform = getTransform();
        translate( x, y );
        fill( g.getOutline() );
        setTransform( originalTransform );
    }

    @Override
    public void fill( Shape s )
    {
        try
        {
            drawShapeImpl( s );

            _contentStream.fill();
        }
        catch ( IOException e )
        {
            handleException( e );
        }
    }

    public void drawShapeImpl( Shape s ) throws IOException
    {
        applyGfxState( _contentStream );

        // Slots 6 and 7 contain the most recent MOVE_TO coordinates.
        float[] coords = new float[8];

        for (PathIterator pi = s.getPathIterator(null); !pi.isDone(); pi.next())
        {
            int type = pi.currentSegment(coords);

            switch ( type )
            {
            case PathIterator.SEG_LINETO:
                translateToPdf( coords, 0, coords, 0, 1 );
                _contentStream.lineTo(
                        coords[0],
                        coords[1] );
                break;
            case PathIterator.SEG_CLOSE:
                _contentStream.lineTo(
                        coords[6],
                        coords[7] );
                break;
            case PathIterator.SEG_CUBICTO:
                translateToPdf( coords, 0, coords, 0, 3 );
                _contentStream.curveTo(
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3],
                        coords[4],
                        coords[5] );
                break;
            case PathIterator.SEG_QUADTO:
                translateToPdf( coords, 0, coords, 0, 2 );
                _contentStream.curveTo1(
                        coords[0],
                        coords[1],
                        coords[2],
                        coords[3] );
                break;
            case PathIterator.SEG_MOVETO:
                translateToPdf( coords, 0, coords, 0, 1 );
                _contentStream.moveTo(
                        coords[6] = coords[0],
                        coords[7] = coords[1] );
                break;
            default:
                throw new InternalError( "" + type );
            }
        }
    }

    @Override
    public boolean hit( Rectangle rect, Shape s, boolean onStroke )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void setComposite( Composite comp )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void setPaint( Paint paint )
    {
        throw new InternalError( "Not implemented." );
    }

    private BasicStroke _stroke = new BasicStroke();

    @Override
    public void setStroke( Stroke s )
    {
        if ( s == null )
            s = new BasicStroke();

        try
        {
            _stroke = (BasicStroke)s;
        }
        catch ( ClassCastException c)
        {
            throw new InvalidParameterException( "Expected BasicStroke" );
        }
    }

    private final Map<Key, Object> _renderingHints = new HashMap<>();

    @Override
    public void setRenderingHint( Key hintKey, Object hintValue )
    {
        _renderingHints.put( hintKey, hintValue );
    }

    @Override
    public Object getRenderingHint( Key hintKey )
    {
        return _renderingHints.get( hintKey );
    }

    @Override
    public void setRenderingHints( Map<?, ?> hints )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void addRenderingHints( Map<?, ?> hints )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public RenderingHints getRenderingHints()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void translate( int x, int y )
    {
        translate( (double)x, (double)y );
    }

    @Override
    public void translate( double tx, double ty )
    {
        _currentTransform.translate( tx, ty );
    }

    @Override
    public void rotate( double theta )
    {
        _currentTransform.rotate( theta );
    }

    @Override
    public void rotate( double theta, double x, double y )
    {
        _currentTransform.rotate( theta, x, y );
    }

    @Override
    public void scale( double sx, double sy )
    {
        _currentTransform.scale( sx, sy );
    }

    @Override
    public void shear( double shx, double shy )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void transform( AffineTransform Tx )
    {
        _currentTransform.concatenate( Tx );
    }

    @Override
    public void setTransform( AffineTransform Tx )
    {
        _currentTransform.setTransform( Tx );
    }

    @Override
    public AffineTransform getTransform()
    {
        return new AffineTransform( _currentTransform );
    }

    @Override
    public Paint getPaint()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public Composite getComposite()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void setBackground( Color color )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public Color getBackground()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public Stroke getStroke()
    {
        return _stroke;
    }

    @Override
    public void clip( Shape s )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public FontRenderContext getFontRenderContext()
    {
        return new FontRenderContext( null, true, true );
    }

    @Override
    public Graphics create()
    {
        return new PdfGfx( this );
    }

    /**
     * Create a newly allocated Graphics context using high precision
     * coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param width The width of the clipping rectangle.
     * @param height The height of the clipping rectangle.
     * @return The new graphics context.
     * @see Graphics#create(int, int, int, int)
     */
    public Graphics create( float x, float y, float width, float height )
    {
        PdfGfx result = (PdfGfx)create();

        result.translate(
                x,
                y );
        result.clipRect(
                0,
                0,
                Math.round( width ),
                Math.round( height ) );

        return result;
    }

    private static final Color DEFAULT_COLOR = Color.BLACK;

    private Color _color = DEFAULT_COLOR;

    @Override
    public Color getColor()
    {
        return _color;
    }

    @Override
    public void setColor( Color c )
    {
        if ( c == null )
            c = DEFAULT_COLOR;

        _color = c;
    }

    @Override
    public void setPaintMode()
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void setXORMode( Color c1 )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public Font getFont()
    {
        return _font;
    }

    private Font _font;
    private PDFont _pdFont;

    @Override
    public void setFont( Font font )
    {
        // Documentation of our base class says 'a null argument
        // is silently ignored'.
        if ( font == null )
            return;

        _font = font;

        _pdFont = convertFont(
                _font );
    }

    private final static PDFont HELVETICA =
    		new PDType1Font( Standard14Fonts.FontName.HELVETICA );
    private final static PDFont HELVETICA_BOLD_OBLIQUE =
    		new PDType1Font( Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE );
    private final static PDFont HELVETICA_BOLD =
    		new PDType1Font( Standard14Fonts.FontName.HELVETICA_BOLD );
    private final static PDFont HELVETICA_OBLIQUE =
    		new PDType1Font( Standard14Fonts.FontName.HELVETICA_OBLIQUE );

    static PDFont convertFont( Font font )
    {
        if ( ! "SansSerif".equals( font.getFamily() ) )
        {
            LOG.warning( "Default font for: " + font );
            return HELVETICA;
        }

        if ( font.isBold() && font.isItalic() )
            return HELVETICA_BOLD_OBLIQUE;
        else if ( font.isBold() )
            return HELVETICA_BOLD;
        else if ( font.isItalic() )
            return HELVETICA_OBLIQUE;

        return HELVETICA;
    }

    @Override
    public FontMetrics getFontMetrics( Font f )
    {
        return createFontMetrics( f );
    }

    private Rectangle _clipBounds = null;

    @Override
    public Rectangle getClipBounds()
    {
        return _clipBounds;
    }

    @Override
    public void clipRect( int x, int y, int width, int height )
    {
        Rectangle parameters = new Rectangle( x, y, width, height );

        if ( _clipBounds == null )
            _clipBounds = parameters;
        else
            _clipBounds = _clipBounds.intersection( parameters );
    }

    @Override
    public void setClip( int x, int y, int width, int height )
    {
        _clipBounds = new Rectangle( x, y, width, height );
    }

    private Shape _clipShape = null;
    @Override
    public Shape getClip()
    {
        return _clipShape;
    }

    @Override
    public void setClip( Shape clip )
    {
        _clipShape =  clip;
    }

    @Override
    public void copyArea( int x, int y, int width, int height, int dx, int dy )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawLine( int x1, int y1, int x2, int y2 )
    {
        try
        {
            applyGfxState( _contentStream );

            float[] twoCoo = new float[]{ x1, y1, x2, y2 };

            translateToPdf( twoCoo, 0, twoCoo, 0, 2 );

            _contentStream.moveTo(
                    twoCoo[0],
                    twoCoo[1] );
            _contentStream.lineTo(
                    twoCoo[2],
                    twoCoo[3] );
            _contentStream.stroke();
        }
        catch ( IOException e )
        {
            handleException( e );
        }
    }

    @Override
    public void fillRect( int x, int y, int width, int height )
    {
        fill( new Rectangle2D.Float( x, y, width, height ) );
    }

    @Override
    public void clearRect( int x, int y, int width, int height )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawRoundRect( int x, int y, int width, int height,
            int arcWidth, int arcHeight )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void fillRoundRect( int x, int y, int width, int height,
            int arcWidth, int arcHeight )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawOval( int x, int y, int width, int height )
    {
        draw( new Ellipse2D.Float( x, y, width, height ) );
    }

    @Override
    public void fillOval( int x, int y, int width, int height )
    {
        fill( new Ellipse2D.Float( x, y, width, height ) );
    }

    @Override
    public void drawArc( int x, int y, int width, int height, int startAngle,
            int arcAngle )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void fillArc( int x, int y, int width, int height, int startAngle,
            int arcAngle )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawPolyline( int[] xPoints, int[] yPoints, int nPoints )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void drawPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void fillPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public boolean drawImage( Image img, int x, int y, ImageObserver observer )
    {
        try
        {
            PDImageXObject pm = LosslessFactory.createFromImage(
                    _page.getDocument().getDelegate(),
                    ImageUtil.toBufferedImage( img ) );

            float[] coo = new float[]{ x, y };

            translateToPdf( coo, 0, coo, 0, 1 );

            _contentStream.drawImage(
                    pm,
                    coo[0],
                    coo[1] - pm.getHeight() );
        }
        catch ( IOException e )
        {
            handleException( e );
        }

        return true;
    }

    @Override
    public boolean drawImage( Image img, int x, int y, int width, int height,
            ImageObserver observer )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public boolean drawImage( Image img, int x, int y, Color bgcolor,
            ImageObserver observer )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public boolean drawImage( Image img, int x, int y, int width, int height,
            Color bgcolor, ImageObserver observer )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, ImageObserver observer )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2, Color bgcolor,
            ImageObserver observer )
    {
        throw new InternalError( "Not implemented." );
    }

    @Override
    public void dispose()
    {
        if ( !_needsDispose )
            return;

        _needsDispose = false;

        try
        {
            _contentStream.close();
        }
        catch ( Exception e )
        {
            LOG.log( Level.WARNING, "Dispose failed.", e );
        }
    }

    /**
     *
     * @param e
     */
    private void handleException( Exception e )
    {
        LOG.log( Level.WARNING, "PdfGfx failure", e );
    }

    @Override
    public void close() throws Exception
    {
        dispose();
    }

    private void applyGfxState( PDPageContentStream cs )
        throws IOException
    {
        // Color
        //
        _contentStream.setStrokingColor( _color );
        _contentStream.setNonStrokingColor( _color );

        // Stroke
        //
        _contentStream.setLineWidth( _stroke.getLineWidth() );

        switch ( _stroke.getEndCap() )
        {
        case BasicStroke.CAP_BUTT:
            _contentStream.setLineCapStyle( 0 );
            break;
        case BasicStroke.CAP_ROUND:
            _contentStream.setLineCapStyle( 1 );
            break;
        case BasicStroke.CAP_SQUARE:
            _contentStream.setLineCapStyle( 2 );
            break;
        default:
            throw new AssertionError();
        }

        switch ( _stroke.getLineJoin() )
        {
        case BasicStroke.JOIN_BEVEL:
            _contentStream.setLineJoinStyle( 2 );
            break;
        case BasicStroke.JOIN_MITER:
            _contentStream.setLineJoinStyle( 0 );
            break;
        case BasicStroke.JOIN_ROUND:
            _contentStream.setLineJoinStyle( 1 );
            break;
        default:
            throw new AssertionError();
        }
    }

    private void applyTextState( PDPageContentStream cs ) throws IOException
    {
        applyGfxState( cs );

        PDFontDescriptor fd = _pdFont.getFontDescriptor();

        _contentStream.setFont(
                _pdFont,
                _font.getSize2D() );

        _contentStream.setLeading( fd.getLeading() );
    }

    private void translateToPdf( float[] src, int srcOff, float[] dst, int dstOff, int count )
    {
        _currentTransform.transform(
                dst, dstOff, dst, dstOff, count );
    }

    public static FontMetrics createFontMetrics( Font f )
    {
        return new PdfFontMetrics( f );
    }
}

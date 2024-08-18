/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.util.Objects;

import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.util.ImageUtil;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A component displaying the eye icons. Allows to mark one of the
 * icons.
 *
 * @author Michael Binz
 */
public class MmtEyeBar extends Label
{
    // TODO first draw markers and circles, then the icons
    // on top of it.  This would fill the currently visible gaps.
    private final Side _side;

    private final int _tileCount;

    private final int _tileWidth;

    private int _marked = 0;

    private final Canvas _canvas;

    private MmtEyeBar( Side side, int marked )
    {
        int borderPx =
                _borderPx;
        _side =
                Objects.requireNonNull( side );
        _canvas =
                cvt( getIconImpl( side ) );
        setGraphic(
                _canvas );

        switch ( _side )
        {
        case BOTH:
            _tileCount = 4;
            break;
        case LEFT:
        case RIGHT:
            _tileCount = 2;
            break;
        default:
            throw new IllegalArgumentException( "" + _side );
        }

        _tileWidth =
                (int)(_canvas.getHeight() - (2*borderPx));

        setMarked( marked );

        // Validate icon size.
        {
            int requiredIconWidth =
                    _tileCount * _tileWidth;
            requiredIconWidth +=
                    (_tileCount+1) * borderPx;

            if ( _canvas.getWidth() != requiredIconWidth )
            {
                throw new AssertionError(
                        String.format( "Expected %d, got %d.",
                                requiredIconWidth, _canvas.getWidth() ) );
            }
        }
    }

    public MmtEyeBar( Side side )
    {
        this( side, 0 );
    }

    public int getIconCount()
    {
        return _tileCount;
    }

    /**
     * @param idx Sets the mark. Zero means 'no mark'.
     * @return The object for chaining.
     */
    public MmtEyeBar mark( int idx )
    {
        setMarked( idx );

        return this;
    }

    /**
     * @param idx Sets the mark. Zero means 'no mark'.
     */
    public void setMarked( int idx )
    {
        if ( idx > _tileCount || idx < 0 )
        {
            throw new ArrayIndexOutOfBoundsException( idx );
        }

        _marked = idx;

        paintComponent( _canvas.getGraphicsContext2D() );
    }

    public int getMarked()
    {
        return _marked;
    }

    public static Node getIcon( Side side )
    {
        return new ImageView( getIconImpl( side ) );

    }

    private static Image getIconImpl( Side side )
    {
        switch ( side )
        {
        case BOTH:
            return _iconRala;
        case LEFT:
            return _iconLa;
        case RIGHT:
            return _iconRa;
        default:
            throw new IllegalArgumentException( "Side is " + side );
        }
    }

    private void paintComponentImpl( GraphicsContext gfx )
    {
        gfx.setLineWidth( _borderPx );

        for ( int i = 0 ; i < _tileCount ; i++ )
        {
            drawMarker( gfx, i, ScreenUtils.getLowContrastFx() );
        }
        if ( _marked > 0 )
        {
            drawMarker( gfx, _marked-1, ScreenUtils.getHeaderFx() );
        }
    }

    private void drawMarker( GraphicsContext gfx, int idx, Paint color )
    {
        int borderPx =
                _borderPx;

        gfx.setStroke( color );

        int x =
                1;
        int y =
                0;

        x +=
                idx * (borderPx + _tileWidth);

        x +=
                borderPx/2;
        y +=
                borderPx/2;
        int wh =
                (int)(_canvas.getHeight() - borderPx);

        gfx.strokeOval(
                x-1,
                y,
                wh,
                wh );
    }

    /**
     *
     */
    final private void paintComponent( GraphicsContext graphics )
    {
        graphics.save();

        try
        {
            paintComponentImpl( graphics );
        }
        finally
        {
            graphics.restore();
        }
    }

    /**
     * Used for initialization of static constants.
     */
    private static Canvas makeIcon( int idx )
    {
        if ( idx > 3 || idx < 0 )
        {
            throw new IllegalArgumentException();
        }

        WritableImage icon = ImageUtil.cloneImage( _iconBar );

        int fullWidth =
                (int)_iconBar.getHeight();
        int innerWidth =
                fullWidth - (2*_borderPx);
        int idxDelta =
                innerWidth + _borderPx;

        WritableImage result = ImageUtil.subImage(
                icon,
                idx * idxDelta,
                0,
                fullWidth,
                fullWidth );

        return drawLabels(
                result,
                idx );
    }

    private static Image getIcon( Side side, Contrast contrast )
    {
        if ( side == Side.LEFT )
        {
            if ( contrast == Contrast.LO )
            {
                return _iconLa10;
            }
            else
            {
                return _iconLa100;
            }
        }
        else if ( side == Side.RIGHT )
        {
            if ( contrast == Contrast.LO )
            {
                return _iconRa10;
            }
            else
            {
                return _iconRa100;
            }
        }

        throw new IllegalArgumentException( StringUtil.EMPTY_STRING + side );
    }

    public static Node getIcon( Experiment experiment )
    {
        return new ImageView(
                getImage( experiment ) );
    }

    public static Image getImage( Experiment experiment )
    {
        return getIcon(
                experiment.getEye(),
                experiment.getContrast() );
    }

    // https://stackoverflow.com/questions/14882806/center-text-on-canvas
    private static void drawLabel(
            Canvas image,
            GraphicsContext gfx,
            String text,
            int offsetY )
    {
        double x =
                image.getWidth() / 2.0;

        int textWidthPx;

        {
            Text t = new Text( text );
            t.setFont( gfx.getFont() );
            textWidthPx = (int)t.getLayoutBounds().getWidth();
        }

        x -=
                textWidthPx / 2.0;

        gfx.fillText(
                text,
                Math.round( x ),
                offsetY );
    }

    private static Canvas cvt( Image image )
    {
        Canvas result = new Canvas(
                image.getWidth(),
                image.getHeight() );
        GraphicsContext gc =
                result.getGraphicsContext2D();

        gc.drawImage( image, 0, 0 );

        return result;
    }

    private static Canvas drawLabels(
            WritableImage image,
            int idx )
    {
        Canvas canvas = cvt( image );

        GraphicsContext gfx =
                canvas.getGraphicsContext2D(); //.antialiasedGfx( image );

        gfx.setFill(
                ScreenUtils.getLowContrastFx() );

        // Derive a bold font ...
        Font f = Font.font(
                ScreenUtils.getFontTiny().getFamily(),
                FontWeight.BOLD,
                FontPosture.REGULAR,
                ScreenUtils.getFontTiny().getSize() );

        gfx.setFont( f );

        // Side ...
        drawLabel(
                canvas,
                gfx,
                _sideLabels[ (idx>>1) & 1 ],
                _sideLabelsOffsetY );
        // Contrast ...
        drawLabel(
                canvas,
                gfx,
                _contrastLabels[ idx & 1 ],
                _contrastLabelsOffsetY );

        return canvas;
    }

    @Resource
    private static Image _iconBar;
    @Resource
    private static int _borderPx;
    @Resource
    private static String[] _sideLabels;
    @Resource
    private static int _sideLabelsOffsetY;
    @Resource
    private static String[] _contrastLabels;
    @Resource
    private static int _contrastLabelsOffsetY;

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( MmtEyeBar.class );
    }

    // Keep the initializations below the static initializer.

    private static final Image _iconRa100 =
            toImage( makeIcon( 0 ) );
    private static final Image _iconRa10 =
            toImage( makeIcon( 1 ) );
    private static final Image _iconLa100 =
            toImage( makeIcon( 2 ) );
    private static final Image _iconLa10 =
            toImage( makeIcon( 3 ) );

    private static final Image _iconRa =
            toImage( mergeIcons(
                    makeIcon( 0 ),
                    makeIcon( 1 ) ) );
    private static final Image _iconLa =
            toImage( mergeIcons(
                    makeIcon( 2 ),
                    makeIcon( 3 ) ) );
    private static final Image _iconRala =
                    toImage( mergeIcons(
                            mergeIcons(
                                    makeIcon( 0 ),
                                    makeIcon( 1 ) ),
                            mergeIcons(
                                    makeIcon( 2 ),
                                    makeIcon( 3 ) ) ) );

    /**
     * Merge two eyebar images.  This is not general purpose since
     * it ensures that only a single circle border is between the
     * images.
     *
     * @param left The left image.
     * @param right The right image.
     * @return The merged image.
     */
    private static Canvas mergeIcons(
            Canvas left,
            Canvas right )
    {
        if ( left.getHeight() != right.getHeight() )
        {
            throw new IllegalArgumentException();
        }
        if ( left.getWidth() != right.getWidth() )
        {
            throw new IllegalArgumentException();
        }

        Canvas result =
                new Canvas(
                        left.getWidth() - _borderPx + right.getWidth(),
                        right.getHeight() );

        GraphicsContext gfx = result.getGraphicsContext2D();

        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill( Color.TRANSPARENT );

        gfx.drawImage(
                left.snapshot( sp, null ),
                0,
                0,
                left.getWidth(),
                left.getHeight()
                );
        gfx.drawImage(
                right.snapshot( sp, null ),
                left.getWidth() - _borderPx,
                0,
                right.getWidth(), right.getHeight()
                );

        return result;
    }

    private static Image toImage( Canvas canvas )
    {
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill( Color.TRANSPARENT );

        return canvas.snapshot( sp, null );
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.awt.Color;
import java.awt.Font;

import org.smack.fx.FxUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.util.Geometry;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

/**
 * Helper operations for screen implementations.
 *
 * @author Michael Binz
 */
public final class ScreenUtils
{
    public static Node rigidAreaSqareCm( double cm )
    {
        int wPx = Geometry.cmToPx( (float)cm );

        Region result = new Region();
        result.setPrefSize( wPx, wPx );

        return result;
    }

    @Resource
    private static Color _background;

    public static Color getBackground()
    {
        return _background;
    }
    public static javafx.scene.paint.Color getBackgroundColorFx()
    {
        return FxUtil.to( getBackground() );
    }
    public static Background getBackgroundFx()
    {
        return FxUtil.getBackground(
                getBackgroundColorFx() );
    }

    @Resource
    private static Color _foreground;

    public static Color getForeground()
    {
        return _foreground;
    }
    public static javafx.scene.paint.Color getForegroundFx()
    {
        return FxUtil.to( _foreground );
    }

    @Resource
    private static javafx.scene.paint.Color _lowContrast;

    public static javafx.scene.paint.Color getLowContrastFx()
    {
        return _lowContrast;
    }

    @Resource
    private static Color _header;

    /**
     * Get the header color.  Currently a touch of blue...
     *
     * @return The header color.
     */
    public static Color getHeader()
    {
        return _header;
    }
    public static javafx.scene.paint.Color getHeaderFx()
    {
        return FxUtil.to( _header );
    }

    @Resource
    private static Font _font;

    public static Font getFontMedium()
    {
        return _font;
    }
    public static javafx.scene.text.Font getFontMediumFx()
    {
        return FxUtil.to( ScreenUtils.getFontMedium() );
    }

    @Resource
    private static javafx.scene.text.Font _fontLarge;

    public static javafx.scene.text.Font getFontLarge()
    {
        return _fontLarge;
    }

    @Resource
    private static javafx.scene.text.Font _fontSmall;

    public static javafx.scene.text.Font getFontSmall()
    {
        return _fontSmall;
    }

    @Resource
    private static javafx.scene.text.Font _fontTiny;

    public static javafx.scene.text.Font getFontTiny()
    {
        return _fontTiny;
    }

    @Resource
    private static Color _color100;
    @Resource
    private static Color _color10;

    /**
     * Get the color to use in the wheel test. Differs between 100 and
     * 10 percent contrast.
     *
     * @param contrast The experiment's contrast.
     * @return The color to use in the test.
     */
    public static Color getTestColor( Contrast contrast )
    {
        if ( contrast == Contrast.LO )
        {
            return _color10;
        }
        else
        {
            return _color100;
        }
    }

    /**
     * Allows to override the default test contrast values.
     *
     * @param contrast The contrast to override.
     * @param color The color for the contrast.
     */
    public static void setTestColor( Contrast contrast, Color color )
    {
        if ( contrast == Contrast.HI )
        {
            _color100 = color;
        }
        else
        {
            _color10 = color;
        }
    }

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( ScreenUtils.class );
    }

    /**
     * Adjusts a textfield to MMT standard.
     *
     * @param c The textfield to adjust.
     * @param columns The number of columns of the resulting text field.
     * @return The adjusted textfield.
     */
    public static <T extends TextField> T adjustTextfield( T c, int columns )
    {
        c.setBorder( null );

        c.setAlignment( Pos.CENTER );

        c.setBackground( FxUtil.getBackground( getLowContrastFx() ) );
        c.setStyle( String.format( "-fx-text-fill: %s;" , toWeb( ScreenUtils.getForegroundFx() ) ) );

        c.setFont( getFontSmall() );
        c.setPrefColumnCount( columns );

        // Select all content on focus.
        c.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                c.selectAll();
            } } );

        return c;
    }

    public static String toWeb( javafx.scene.paint.Color c )
    {
        int r =
                (int)Math.round(c.getRed()*255);
        int g =
                (int)Math.round(c.getGreen()*255);
        int b =
                (int)Math.round(c.getBlue()*255);
        int alpha =
                (int)Math.round(c.getOpacity()*255);

        return String.format( "#%2x%2x%2x%2x" ,
                r, g, b, alpha );
    }

    private ScreenUtils()
    {
        throw new AssertionError();
    }
}

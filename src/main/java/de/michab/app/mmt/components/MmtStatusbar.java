/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import org.smack.fx.FxUtil;

import de.michab.app.mmt.screens.ScreenUtils;
import javafx.animation.FillTransition;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToolBar;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Statusbar used in MMT.
 *
 * @author Michael Binz
 */
public class MmtStatusbar extends ToolBar
{
    private final Label _label = new Label();

    /**
     * A rectangle whose sole purpose is to be
     * (a) linked against the animation effect and
     * (b) whose fill property is bound to the label's
     * text fill.
     */
    private final Rectangle _fillIndirection =
            new Rectangle();

    /**
     * Create an instance.
     */
    public MmtStatusbar()
    {
        _label.textFillProperty().bind(
                _fillIndirection.fillProperty() );

        getItems().addAll(
                _label,
                FxUtil.createHorizontalGlue() );
    }

    /**
     * Set a message. The message is animated to draw attention from the user.
     *
     * @param message The message to set.
     */
    public void setMessage( String message )
    {
        _label.setText( message );

        FillTransition ft =
                new FillTransition(
                        Duration.millis(3000),
                        _fillIndirection,
                        ScreenUtils.getForegroundFx(),
                        ScreenUtils.getLowContrastFx() );

        ft.play();
    }

    public void addRight( Labeled node )
    {
        getItems().add( node );
    }

    public void removeRight( Labeled node )
    {
        getItems().remove( node );
    }

    public void setFont( Font font )
    {
        _label.setFont( font );
    }
}

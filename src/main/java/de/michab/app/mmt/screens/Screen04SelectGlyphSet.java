/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.util.Objects;

import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.screens.sdk.ConfigScreen;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.util.Geometry;
import de.michab.app.mmt.util.ShapeSet;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Configures the glyph set.
 *
 * @author Michael Binz
 */
public final class Screen04SelectGlyphSet extends ConfigScreen
{
    private ShapeSet _selectedShapeSet;
    private Circle _selectedCircle;

    public Screen04SelectGlyphSet( Screen<Node> previous, Mmt application )
    {
        super( application, previous );

        _selectedShapeSet =
                Objects.requireNonNull( application.getShapeSet() );
    }

    @Override
    protected Node initConfigScreen() throws Exception
    {
        add( makeScreenHeader( _header ),
                Column.CENTER,
                Row.SCREEN_HEAD );

        {
            HBox hb = new HBox( Geometry.cmToPx( 1.0f ) );
            int twoCm = Geometry.cmToPx( 2.0f );
            double scale = twoCm * 0.59;

            for ( ShapeSet crt : getApplication().getShapeSets() )
            {
                Shape tc = crt.createShapeFor( crt.getDemoChar() );
                tc.setId( crt.getName() );
                tc.setFill( ScreenUtils.getForegroundFx() );

                StackPane box = new StackPane( tc );
                box.setPrefSize( scale, scale );
                box.setMinSize( scale, scale );
                box.setMaxSize( scale, scale );

                Circle circle = new Circle( twoCm / 2.0 );

                if ( crt == _selectedShapeSet)
                {
                    circle.setFill( ScreenUtils.getHeaderFx() );
                    _selectedCircle = circle;
                }
                else
                {
                    circle.setFill( Color.TRANSPARENT );
                }

                StackPane cp = new StackPane();

                cp.getChildren().addAll(
                        circle,
                        box );

                hb.getChildren().add( cp );

                cp.setOnMouseClicked( (s) -> mouseClicked(crt,circle) );
            }

            add( hb, Column.CENTER, Row.CENTER );
        }

        return this;
    }

    @Resource
    private String _header;

    @Override
    protected void actOk()
    {
        getApplication().setShapeSet( _selectedShapeSet );

        super.actOk();
    }

    /**
     * Handles test component selection.
     */
    private void mouseClicked( ShapeSet shapeSet, Circle circle )
    {
        if ( _selectedShapeSet == shapeSet )
        {
            return;
        }

        // Clear color of current selection.
        _selectedCircle.setFill( Color.TRANSPARENT );
        // Set color of new selection.
        circle.setFill( ScreenUtils.getHeaderFx() );
        // Make the current selection from the new selection.
        _selectedCircle = circle;
        _selectedShapeSet = shapeSet;
    }
}

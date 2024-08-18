/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.collections.MapWithProducer;

import de.michab.app.mmt.util.ImageUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Michael Binz
 */
public class FillableImage extends ImageView
{
    static final Logger LOG =
            Logger.getLogger( FillableImage.class.getName() );

    private final Image _originalImage;

    public FillableImage( Image image )
    {
        _originalImage =
                image;
        setImage(
                _originalImage );
        fillProperty.addListener(
                (x,o,n) -> fillListener(n) );
    }

    public final SimpleObjectProperty<Paint> fillProperty =
            new SimpleObjectProperty<>( Color.WHITE );

    private MapWithProducer<Color, Image> _colorToImage =
            new MapWithProducer<>( this::createImage );

    private void fillListener( Paint newColor )
    {
        try
        {
            setImage( _colorToImage.get( (Color)newColor ) );
        }
        catch ( ClassCastException e )
        {
            LOG.log(
                    Level.WARNING,
                    "Paint != Color not supported.",
                    e );
        }
    }

    private Image createImage( Color color )
    {
        return ImageUtil.stainImage( _originalImage, color );
    }
}

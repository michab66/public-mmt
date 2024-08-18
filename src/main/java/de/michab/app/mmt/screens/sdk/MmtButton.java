/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.util.Objects;

import javax.swing.Action;

import org.smack.fx.ActionFx;
import org.smack.util.JavaUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.lab.FillableImage;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.util.ImageUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * MMT button powerhouse.
 *
 * @author Michael Binz
 */
public final class MmtButton extends StackPane
{
    public static final String GLYPH_EXIT =
            "\u2716"; //;
    public static final String GLYPH_NEXT =
            "\u25ba"; //, 0.6f );
    public static final String GLYPH_PREV =
            "\u25c4"; //, 0.4f );
    public static final String GLYPH_UP =
            "\u25b2"; //, 0.5f );
    public static final String GLYPH_DOWN =
            "\u25bc";
    public static final String GLYPH_OK =
            "\u2713";
    public static final String GLYPH_HOME =
            "\u2302";
    public static final String GLYPH_POISON =
            "\ue429";

    /**
     * The small button size. Used for back buttons.
     */
    public static final Dimension2D DIMENSION_S =
            new Dimension2D( 35, 35 );
    /**
     * The large button size.  Used for next buttons.
     */
    public static final Dimension2D DIMENSION_L =
            new Dimension2D( 60, 60 );

    @Resource
    private static Image _okIcon;

    private static Node makeGlyphButton( String glyph, ActionFx action )
    {
        MmtButton result =
                new MmtButton( DIMENSION_L );
        result.setText(
                glyph,
                ScreenUtils.getFontLarge(),
                0.3,
                0 );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable( true );

        return result;
    }

    public static Node makeOkButton( ActionFx action )
    {
        MmtButton result = makeImageButton(
                DIMENSION_L,
                _okIcon,
                true );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable( true );

        return result;
    }

    public static Node makeNextButton( ActionFx action )
    {
        return makeGlyphButton( GLYPH_NEXT, action );
    }

    public static Node makePreviousButton( ActionFx action )
    {
        MmtButton result =
                new MmtButton( DIMENSION_S );
        result.setText(
                GLYPH_PREV,
                ScreenUtils.getFontMediumFx(),
                -0.6,
                0 );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable ( true );

        result.setAccelerator( new KeyCodeCombination( KeyCode.BACK_SPACE ) );

        Rectangle rectangle = new Rectangle(
                DIMENSION_L.getWidth(),
                DIMENSION_L.getHeight(),
                javafx.scene.paint.Color.TRANSPARENT );

        StackPane positioner2 = new StackPane( rectangle, result );

        return positioner2;

//        // TODO(michab) This is not the best place...
//        // Check if we can configure the key stroke...
////        JXTools.addKeyBinding(
////                button,
////                KeyStroke.getKeyStroke( (char)KeyEvent.VK_BACK_SPACE ),
////                JComponent.WHEN_IN_FOCUSED_WINDOW,
////                a );
    }

    public static Node makeExitButton( ActionFx action )
    {
        Font f =
                Font.font( "SansSerif", 29 );
        MmtButton result =
                new MmtButton( DIMENSION_S );
        result.setText(
                GLYPH_EXIT,
                f,
                0,
                0 );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable( false );

        return result;
    }

    @Resource
    private static Image _homeIcon;

    /**
     * Creates a non-focusable home button.
     *
     * @see MmtButton#makeHomeButton(Action, boolean)
     */
    public static Node makeHomeButton( ActionFx a )
    {
        return makeHomeButton( a, false );
    }

    /**
     * Creates a home button.
     *
     * @param a The action to install.
     * @param focusable True if the button should be focusable.
     * @return The home button.
     */
    public static Node makeHomeButton( ActionFx action, boolean focusable )
    {
        MmtButton result = makeImageButton(
                DIMENSION_S,
                _homeIcon,
                focusable );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable( focusable );

        return result;
    }

    public static MmtButton makeTextButton( ActionFx a )
    {
        return makeTextButton( a, false );
    }

    public static MmtButton makeTextButton( ActionFx action, boolean focus )
    {
        return makeTextButton( DIMENSION_S, action, focus );
    }

    public static MmtButton makeTextButton(
            Dimension2D dimension,
            ActionFx action,
            boolean focus )
    {
        Text text =
                new Text( action.getText() );
        text.setFont(
                ScreenUtils.getFontSmall() );
        double w =
                Math.max(
                        text.prefWidth( -1 ),
                        dimension.getWidth() );

        dimension =
                new Dimension2D(
                        w*1.15,
                        dimension.getHeight() );
        MmtButton result =
                new MmtButton( dimension );
        result.setText(
                text,
                0,
                0 );
        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable(
                focus );

        if ( action.getAccelerator() != null )
        {
            result.setAccelerator(
                    action.getAccelerator() );
        }

        return result;
    }

    private static MmtButton makeImageButton(
            Dimension2D dimension,
            Image image,
            boolean focus )
    {
        MmtButton result =
                new MmtButton( dimension );

        FillableImage fi =
                new FillableImage( ImageUtil.cloneImage( image ) );
        result.setNode( fi, fi.fillProperty, 0, 0 );

        return result;
    }

    public static MmtButton makeButton(
            Dimension2D dimension,
            ActionFx action,
            boolean focus )
    {
        MmtButton result;

        if ( action.getImage() != null )
        {
            result = makeImageButton(
                    dimension, action.getImage(),
                    focus );
        }
        else if ( action.getText() != null )
        {
            result = makeTextButton(
                    dimension,
                    // TODO text
                    action,
                    focus );
        }
        else
        {
            throw new IllegalArgumentException( "No text/image set." );
        }

        result.setOnAction(
                (s) -> action.actionPerformed( null ) );
        result.disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        result.setFocusTraversable( focus );

        return result;
    }

    static private Rectangle makeClip( Dimension2D dimension )
    {
        Rectangle result = new Rectangle(
                dimension.getWidth(),
                dimension.getHeight() );
        result.setArcHeight( 14 );
        result.setArcWidth( 14 );
        result.setFill( ScreenUtils.getHeaderFx() );

        return result;
    }

    SimpleObjectProperty<Paint> stroke = new SimpleObjectProperty<>(
            this,
            "stroke",
            ScreenUtils.getBackgroundColorFx() );

    private void listenDisabled(
            ObservableValue<? extends Boolean> arg0,
            Boolean from,
            Boolean to )
    {
        fill.set( to ?
                ScreenUtils.getLowContrastFx() :
                ScreenUtils.getHeaderFx() );
        stroke.set(
                ScreenUtils.getBackgroundColorFx() );
    }

    private void listenFocus(
            ObservableValue<? extends Boolean> arg0,
            Boolean from,
            Boolean to )
    {
        stroke.set( to ?
                ScreenUtils.getForegroundFx() :
                ScreenUtils.getBackgroundColorFx() );
    }

    private static final double _mouseScale = 1.04;

    private void mouseEntered()
    {
        if ( isDisabled() )
        {
            return;
        }
        setScaleX( _mouseScale );
        setScaleY( _mouseScale );
    }
    private void mouseExited()
    {
        if ( isDisabled() )
        {
            return;
        }
        setScaleX( 1.00 );
        setScaleY( 1.00 );
    }

    private void evtKey( KeyEvent event )
    {
        KeyCode keyCode =
                event.getCode();

        if (
                keyCode.equals( KeyCode.SPACE ) ||
                keyCode.equals( KeyCode.ENTER ) )
        {
            fire();
        }
    }

    private void setText(
            String text,
            Font font,
            double xWeight,
            double yWeight )
    {
        Text t =
                new Text( text );
        t.setFont(
                font );
        setText( t, xWeight, yWeight );
    }

    private void setText(
            Text t,
            double xWeight,
            double yWeight )
    {
        setNode(
                t,
                t.fillProperty(),
                xWeight,
                yWeight );
    }

    private void setNode(
            Node node,
            ObjectProperty<Paint> fill,
            double xWeight,
            double yWeight )
    {
        node.setMouseTransparent(
                true );
        fill.bind(
                stroke );
        addLayoutComponent(
                node,
                xWeight,
                yWeight );
    }

    private void addLayoutComponent( Node t, double xWeight, double yWeight )
    {
        JavaUtil.Assert( Math.abs( xWeight ) <= 1 );
        JavaUtil.Assert( Math.abs( yWeight ) <= 1 );

        if ( xWeight != 0.0 )
        {
            double deltaX =
                    _innerRect.getWidth() -
                    t.prefWidth( -1 );
            deltaX /= 2;

            t.setTranslateX( deltaX*xWeight );
        }
        if ( yWeight != 0.0 )
        {
            double deltaY =
                    _innerRect.getHeight() -
                    t.prefHeight( -1 );
            deltaY /= 2;

            t.setTranslateY( deltaY*yWeight );
        }

        getChildren().add( t );
    }

    private void setOnAction(EventHandler<ActionEvent> value)
    {
        addEventHandler( ActionEvent.ACTION, value );
    }

    private void fire()
    {
        if ( isDisabled() )
        {
            return;
        }
        fireEvent(
                new ActionEvent( this, null ) );
    }

    private void sceneAcceleratorListener(
            Scene from,
            Scene to  )
    {
        if ( from != null && _accelerator != null )
        {
            from.getAccelerators().remove( _accelerator.getKey() );
        }

        if ( to != null && _accelerator != null )
        {
            to.getAccelerators().put(
                    _accelerator.getKey(),
                    _accelerator.getValue() );
        }
    }

    private Pair<KeyCombination,Runnable> _accelerator;
    public final ObjectProperty<Paint> fill;

    /**
     * Set an accelerator.
     * @param kc The accelerator's key code.
     * @see https://stackoverflow.com/questions/12710468/using-javafx-2-2-mnemonic-and-accelerators
     */
    public void setAccelerator( KeyCombination kc )
    {
        if ( _accelerator != null )
        {
            throw new IllegalArgumentException( "Already set." );
        }
        _accelerator = new Pair<>(
                Objects.requireNonNull( kc ), () -> fire() );

        sceneProperty().addListener(
                (m,o,n) ->
                    sceneAcceleratorListener( o, n ) );
    }

    private final Rectangle _innerRect;

    private MmtButton( Dimension2D dimension )
    {
        _innerRect =
                makeClip( dimension );
        fill =
                _innerRect.fillProperty();
        getChildren().add( _innerRect );

        fill.set(
                ScreenUtils.getHeaderFx() );
        stroke.set(
                ScreenUtils.getBackgroundColorFx() );
        disabledProperty().addListener(
                this::listenDisabled );
        focusedProperty().addListener(
                this::listenFocus );
        _innerRect.setOnMouseEntered(
                (s) -> mouseEntered() );
        _innerRect.setOnMouseExited(
                (s) -> mouseExited() );
        _innerRect.setOnMouseClicked(
                (s) -> fire() );
        setOnKeyPressed(
                (s) -> evtKey( s ) );
    }

    private MmtButton(
            Dimension2D dimension,
            ActionFx action,
            boolean isFocusable )
    {
        _innerRect =
                makeClip( dimension );
        fill =
                _innerRect.fillProperty();
        getChildren().add( _innerRect );

        fill.set(
                ScreenUtils.getHeaderFx() );
        stroke.set(
                ScreenUtils.getBackgroundColorFx() );
        disabledProperty().addListener(
                this::listenDisabled );
        focusedProperty().addListener(
                this::listenFocus );
        _innerRect.setOnMouseEntered(
                (s) -> mouseEntered() );
        _innerRect.setOnMouseExited(
                (s) -> mouseExited() );
        _innerRect.setOnMouseClicked(
                (s) -> fire() );
        setOnKeyPressed(
                (s) -> evtKey( s ) );

        setOnAction(
                (s) -> action.actionPerformed( s ) );
        disableProperty().bind(
                Bindings.not( action.enabledProperty ) );
        setFocusTraversable(
                isFocusable );
    }

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( MmtButton.class );
    }
}

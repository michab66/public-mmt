/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.components.ResultWheelFx;
import de.michab.app.mmt.components.WheelLayoutPane;
import de.michab.app.mmt.screens.sdk.ConfigScreen;
import de.michab.app.mmt.screens.sdk.Screen;
import de.michab.app.mmt.util.Geometry;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * DPI calibration screen.
 *
 * DPI is on the development system 100.84.
 *
 * @author Michael Binz
 */
public class Screen99Calibration extends ConfigScreen
{
    private final static Logger LOG =
            Logger.getLogger( Screen99Calibration.class.getName() );

    private static final NumberFormat numberFmt =
            NumberFormat.getInstance();

    private final TextField _width = new TextField()
    {
        @Override
        public boolean isResizable()
        {
            return false;
        }
    };

    @Resource
    private String _headerLeft;
    @Resource
    private String _headerRight;

    private final ResultWheelFx _wheel = new ResultWheelFx();

    /**
     * Create an instance.
     *
     * @param previous Our anchor screen.
     * @param application The host application.
     */
    public Screen99Calibration( Screen<Node> previous, Mmt application )
    {
        super( application, ColumnTension.TO_INNER, previous );
    }

    @Override
    protected Node initConfigScreen() throws Exception
    {
        // TODO no good.
        //_width.setPrefSize( 60, 40 );
        _width.resize( 60, 40 );

        setFocusComponent( _width );

        _wheel.setFocusTraversable( false );
        WheelLayoutPane wheelLayoutPane =
                new WheelLayoutPane( _wheel );

        wheelLayoutPane.addLayoutComponent(
                _width,
                "00" );

        wheelLayoutPane.addLayoutComponent(
                makeScreenHeader( _headerLeft ),
                "62",
                false );
        wheelLayoutPane.addLayoutComponent(
                makeScreenHeader( _headerRight ),
                "22",
                false );

        _width.setText( new DecimalFormat( "0.00" ).format(
                        Geometry.pxToCm( (float)_wheel.getDiameter() ) ) );

        ScreenUtils.adjustTextfield(
                _width,
                _width.getText().length()+2 );

        _width.setOnAction( this::handleTextfieldOk );

        add(
                new StackPane( _wheel, wheelLayoutPane ),
                Column.CENTER,
                Row.CENTER );

        ACT_OK.enabledProperty.bind(
                Bindings.and(
                        Bindings.isNotEmpty( _width.textProperty() ),
                        Bindings.createBooleanBinding(
                                this::isValid,
                                _width.textProperty() ) ) );

        return this;
    }

    /**
     * Represents the OK gesture.
     */
    @Override
    protected void actOk()
    {
        double dotWidth = _wheel.getDiameter();

        try
        {
            float sizeCm =
                    parse( _width.getText() );
            float sizeInch =
                    Geometry.toInch( sizeCm );
            double newDpi =
                    dotWidth / sizeInch;

            LOG.info( String.format( "New dpi is %.2f", newDpi ) );

            getApplication().setDpi( (float)newDpi );
        }
        catch ( Exception e )
        {
            LOG.log( Level.WARNING, "Unexpected exception.", e );
        }

        super.actOk();
    }

    private float parse( String toParse ) throws Exception
    {
        if ( numberFmt instanceof DecimalFormat )
        {
            DecimalFormat df = (DecimalFormat)numberFmt;

            char groupingSeparator =
                    df.getDecimalFormatSymbols().getGroupingSeparator();

            if ( toParse.indexOf( groupingSeparator ) > -1 )
            {
                throw new Exception( "Grouping separator not allowed." );
            }
        }

        float result = numberFmt.parse( toParse ).floatValue();

        if ( result < 0 )
        {
            throw new Exception( "Negative value not allowed." );
        }

        // TODO could add code here to prevent the user from setting
        // fatal values leading to gigantic or microscopic wheels.
        // No good idea on algorithm, so do it later.

        return result;
    }

    private boolean isValid()
    {
        try
        {
            parse( _width.getText() );
            return true;
        }
        catch ( Exception ignore )
        {
            return false;
        }
    }

    private void handleTextfieldOk( ActionEvent e )
    {
        if ( ! ACT_OK.enabledProperty.get() )
        {
            return;
        }

        ACT_OK.actionPerformed( e );
    }
}

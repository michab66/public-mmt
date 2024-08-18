/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Tenant;
import de.michab.app.mmt.screens.sdk.ConfigScreen;
import de.michab.app.mmt.screens.sdk.Screen;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * UI to populate patient data.
 *
 * @author Michael Binz
 */
public final class Screen98Tenant extends ConfigScreen
{
    private TextField _company;
    private TextField _address1;
    private TextField _address2;

    @Resource
    private int _intTextWidth;
    @Resource
    private String _textHeading;
    @Resource
    private String _textCompany;
    @Resource
    private String _textAddress1;
    @Resource
    private String _textAddress2;

    public Screen98Tenant( Screen<Node> previous, Mmt application )
    {
        super( application, previous );
    }

    public Tenant getTenant()
    {
        Tenant result = new Tenant();

        result.setName( _company.getText().trim() );
        result.setAddress1( _address1.getText().trim() );
        result.setAddress2( _address2.getText().trim() );

        return result;
    }

    public void setTenant( Tenant newValue )
    {
        _company.setText(
                newValue.getName()  );
        _address1.setText(
                newValue.getAddress1()  );
        _address2.setText(
                newValue.getAddress2() );
    }

    @Override
    protected Node initConfigScreen() throws Exception
    {
        add( makeScreenHeader( _textHeading ), Column.CENTER, Row.SCREEN_HEAD );

        VBox box = new VBox();
        {
            box.getChildren().add(
                    makeLabel( _textCompany, ScreenUtils.getHeaderFx() ) );
        }

        {
            _company = ScreenUtils.adjustTextfield( new TextField(), _intTextWidth );
            _company.setBackground( ScreenUtils.getBackgroundFx() );
            _company.setOnAction( this::handleTextfieldOk );
            box.getChildren().add( _company );

            setFocusComponent( _company );
        }

        box.getChildren().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            box.getChildren().add(
                    makeLabel( _textAddress1, ScreenUtils.getHeaderFx() ) );
        }

        {
            _address1 = ScreenUtils.adjustTextfield( new TextField(), _intTextWidth );
            _address1.setBackground( ScreenUtils.getBackgroundFx() );
            _address1.setOnAction( this::handleTextfieldOk );
            box.getChildren().add( _address1 );
        }

        box.getChildren().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            box.getChildren().add(
                    makeLabel( _textAddress2, ScreenUtils.getHeaderFx() ) );
        }

        {
            _address2 = ScreenUtils.adjustTextfield( new TextField(), _intTextWidth );
            _address2.setBackground( ScreenUtils.getBackgroundFx() );
            _address2.setOnAction( this::handleTextfieldOk );
            box.getChildren().add( _address2 );
        }

        add( box, Column.CENTER, Row.CENTER );

        Tenant tenant =
                getApplication().getTenant();
        if ( tenant != null )
        {
            setTenant( tenant );
        }

        return this;
    }

    @Override
    protected void actOk()
    {
        getApplication().setTenant( getTenant() );

        super.actOk();
    }

    private void handleTextfieldOk(javafx.event.ActionEvent e )
    {
        if ( ACT_OK.enabledProperty.get() )
        {
            // Content is entered and valid.  Go to the next screen.
            ACT_OK.actionPerformed( e );
            return;
        }

        // Pass on the focus in the upper text fields.
        if ( e.getSource() == _company )
        {
            _address1.requestFocus();
        }
        else if ( e.getSource() == _address1 )
        {
            _address2.requestFocus();
        }
    };
}

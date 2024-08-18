/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens;

import java.text.ParseException;

import org.smack.fx.ActionFx;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Patient;
import de.michab.app.mmt.screens.sdk.BaseScreen;
import de.michab.app.mmt.screens.sdk.MmtButton;
import de.michab.app.mmt.util.MmtUtils;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * UI to populate patient data.
 *
 * @author Michael Binz
 */
public final class Screen01Patient extends BaseScreen<Mmt>
{
    private TextField _firstName;
    private TextField _lastName;
    private TextField _birthdate;

    @Resource( name="Screen01Patient._intTextWidth")
    private int _intTextWidth;
    @Resource
    private String _textHeading;
    @Resource
    private String _textFirstName;
    @Resource
    private String _textLastName;
    @Resource
    private String _textBirthdate;
    @Resource
    private String _testFirstName;
    @Resource
    private String _testLastName;
    @Resource
    private String _testBirthdate;

    public Screen01Patient( Mmt application )
    {
        super( application );
    }

    public Patient getPatient()
    {
        try
        {
            return new Patient(
                    _firstName.getText().trim(),
                    _lastName.getText().trim() ,
                    MmtUtils.parseDate( _birthdate.getText() ));
        }
        catch ( final ParseException e )
        {
            throw new RuntimeException( e );
        }
    }

    public void setPatient( Patient newValue )
    {
        _firstName.setText(
                newValue.getFirstname()  );
        _lastName.setText(
                newValue.getLastname()  );
        _birthdate.setText(
                MmtUtils.formatDate( newValue.getBirthdate() ) );
    }

    @Override
    protected Node initScreen()
    {
        // F12 performs auto fill.
        addEventFilter(
                KeyEvent.KEY_RELEASED,
                s -> {
                    if ( s.getCode() != KeyCode.F12 )
                    {
                        return;
                    }
                    s.consume();
                    debugFill();
                });

        add(
                makeScreenHeader( _textHeading.toString() ),
                Column.CENTER,
                Row.SCREEN_HEAD );

        final VBox box = new VBox();
        {
            box.getChildren().add(
                    makeLabel( _textFirstName, ScreenUtils.getHeaderFx() ) );
        }

        {
            _firstName = ScreenUtils.adjustTextfield( new TextField(), _intTextWidth );
            _firstName.setOnAction( this::handleTextfieldOk );
            box.getChildren().add( _firstName );

            setFocusComponent( _firstName );
        }

        box.getChildren().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            box.getChildren().add(
                    makeLabel( _textLastName, ScreenUtils.getHeaderFx() ) );
        }

        {
            _lastName = ScreenUtils.adjustTextfield( new TextField(), _intTextWidth );
            _lastName.setOnAction( this::handleTextfieldOk );
            box.getChildren().add( _lastName );
        }

        box.getChildren().add( ScreenUtils.rigidAreaSqareCm( 0.5 ) );

        {
            box.getChildren().add(
                    makeLabel( _textBirthdate, ScreenUtils.getHeaderFx() ) );
        }

        {
            _birthdate = ScreenUtils.adjustTextfield(
                    new TextField(), _intTextWidth );
            _birthdate.setPromptText( MmtUtils.getDatePattern() );
            _birthdate.setOnAction( this::handleTextfieldOk );

            box.getChildren().add( _birthdate );
        }

        add( box, Column.CENTER, Row.CENTER );

        add(
                MmtButton.makeOkButton( ACT_NEXT ),
                Column.CENTER,
                Row.BUTTON_NEXT );

        ACT_NEXT.enabledProperty.bind(
                Bindings.and(
                        Bindings.and(
                                Bindings.isNotEmpty( _firstName.textProperty() ),
                                Bindings.isNotEmpty( _lastName.textProperty() ) ),
                        Bindings.createBooleanBinding(
                                this::isDateValid,
                                _birthdate.textProperty() ) ) );

        return this;
    }

    private void actNext()
    {
        final Mmt app = getApplication();

        app.setPatient(
                getPatient() );

        app.setScreen(
                () -> new Screen02TestScope( this, app ) );
    }

    private final ActionFx ACT_NEXT = new ActionFx( this::actNext );

    /**
     * Handles textfield onAction messages.
     *
     * @param event
     */
    private void handleTextfieldOk(javafx.event.ActionEvent e )
    {
        if ( ACT_NEXT.enabledProperty.get() )
        {
            // Content is entered and valid.  Go to the next screen.
            ACT_NEXT.actionPerformed( e );
            return;
        }

        // Pass on the focus in the upper text fields.
        if ( e.getSource() == _firstName )
        {
            _lastName.requestFocus();
        }
        else if ( e.getSource() == _lastName )
        {
            _birthdate.requestFocus();
        }
    }

    private boolean isDateValid(  )
    {
        try
        {
            return null !=
                    MmtUtils.parseDate( _birthdate.textProperty().get() );
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private void debugFill()
    {
        _firstName.setText(
                _testFirstName );
        _lastName.setText(
                _testLastName );
        _birthdate.setText(
                _testBirthdate );
    }
}

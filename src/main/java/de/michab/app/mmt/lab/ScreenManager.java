/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * M model type.
 * C Screen UI library component type.
 *
 * @author Michael Binz
 */
public class ScreenManager<M,C>
{
    protected final SimpleObjectProperty<M> _model = new SimpleObjectProperty<>(
                this, "model", null );

    public final SimpleObjectProperty<Screen<M,C>> currentScreen =
            new SimpleObjectProperty<>( this, "currentScreen", null );

    public final Screen<M,C> _startScreen;

    /**
     * The screen history in backwards order: The current screen
     * is at position zero, the initial or oldest screen is the
     * last in the list.
     */
    private final ObservableList<Screen<M,C>> _screenHistory =
            FXCollections.observableArrayList();

    /**
     *
     * @param model
     * @param initialScreen
     */
    public ScreenManager(
            M model,
            Screen<M,C> initialScreen )
    {
        _model.set(
                model );
        _startScreen =
                Objects.requireNonNull( initialScreen );
        setScreen(
                _startScreen );

        currentScreen.addListener( (s,o,n) -> Objects.requireNonNull( n ) );
    }

    /**
    *
    * @param model
    * @param initialScreen
    */
   public ScreenManager(
           Screen<M,C> initialScreen )
   {
       this( null, initialScreen );
   }

    /**
     *
     * @param screen
     */
    public void setScreen( Supplier<Screen<M,C>> supplier )
    {
        Screen<M,C> screen;

        try
        {
            screen = supplier.get();
        }
        catch ( Throwable e )
        {
            // TODO switch to error screen.
            screen = null;
        }

        setScreen( screen );
    }

    public void setScreen( Screen<M,C> screen )
    {
        Screen<M,C> previous =
                getScreen();
        _screenHistory.add(
                0,
                screen );

        if ( previous != null )
        {
            previous.leaveScreen(
                    _model.get(),
                    this,
                    screen );
        }

        screen.enterScreen(
                _model.get(),
                this,
                previous );
    }

    /**
     *
     * @return
     */
    public Screen<M,C> getScreen()
    {
        if ( _screenHistory.size() > 0 )
            return _screenHistory.get( 0 );

        // We only return a null if called from the constructor.
        return null;
    }

    public void showError( Throwable e )
    {

    }

    public boolean isBackAllowed()
    {
        return _screenHistory.size() > 1;
    }

    public void goBack()
    {
        if ( ! isBackAllowed() )
            throw new IllegalStateException();

        Screen<M,C> toLeave =
                getScreen();
        _screenHistory.remove(
                0 );
        switchScreen(
                toLeave,
                getScreen() );
    }

    private void switchScreen(
            Screen<M,C> from,
            Screen<M,C> to )
    {
        from.leaveScreen(
                _model.get(),
                this,
                to );
        to.enterScreen(
                _model.get(),
                this,
                from );
    }

    /**
     * An interface to be implemented by screen components.
     */
    public interface Screen<M,C>
    {
        /**
         * Return the name of the Screen.
         */
        String getName();

        /**
         * Should trigger screen initialization.  Constructor should be
         * kept lightweight.
         *
         * @return The component to display.
         * @throws Exception
         */
        C getComponent() throws Exception;

        /**
         * Return the component that should get the focus on display.
         *
         * @return The component that should receive the focus on display.
         */
        C getFocusComponent();

        /**
         * Called when this screen is displayed.
         * @param previous The screen displayed before this screen.
         */
        void enterScreen(
            M model,
            ScreenManager<M,C> manager,
            Screen<M,C> previous );

        /**
         * Called when this screen is left.
         * @param next The next screen that will be shown.
         */
        void leaveScreen(
            M model,
            ScreenManager<M,C> manager,
            Screen<M,C> next );
    }

    @SuppressWarnings("unused")
    private static final Logger LOG =
            Logger.getLogger( ScreenManager.class.getName() );
}

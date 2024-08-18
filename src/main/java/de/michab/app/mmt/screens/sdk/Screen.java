/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

/**
 * An interface to be implemented by screen components.
 *
 * @author Michael Binz
 */
public interface Screen<CT>
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
    CT getComponent() throws Exception;

    /**
     * Return the component that should get the focus on display.
     *
     * @return The component that should receive the focus on display.
     */
    CT getFocusComponent();

    /**
     * Called when this screen is displayed.
     * @param previous The screen displayed before this screen.
     */
    void enterScreen( Screen<CT> previous );

    /**
     * Called when this screen is left.
     * @param next The next screen that will be shown.
     */
    void leaveScreen( Screen<CT> next );
}

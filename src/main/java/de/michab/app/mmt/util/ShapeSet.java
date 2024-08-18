/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.util;

import javafx.scene.shape.Shape;

/**
 * Represents a set of test glyphs.
 *
 * @author Michael Binz
 */
public abstract class ShapeSet
{
    public abstract Shape createShapeFor( char character );

    /**
     * Get a random char from this shape set.
     *
     * @return A random character from this shape set.
     */
    public abstract char getRandomChar();

    /**
     * Get a demonstration char from this shape set.
     *
     * @return A random character from this shape set.
     */
    public abstract char getDemoChar();

    /**
     * Get the test set name for end user display in the UI.
     *
     * @return The test set name.
     */
    public String getName()
    {
        return getClass().getSimpleName();
    }

    @Override
    public String toString()
    {
        return getName();
    }

    public double getSizeCorrectionFactor()
    {
        return 1;
    }
}

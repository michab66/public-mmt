/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smack.util.MathUtil;

import de.michab.app.mmt.components.SloanShape;
import de.michab.app.mmt.components.SloanShape.Glyph;
import javafx.scene.shape.Shape;

/**
 * Sloan Shapes.
 *
 * @author Michael Binz
 */
public class ShapeSetSloan extends ShapeSet
{
    private static final Map<Character, Glyph> _charDirectionMap =
            getCharacterMap();

    private static List<Character> _characters =
            Collections.unmodifiableList(
                    new ArrayList<>(
                            _charDirectionMap.keySet() ) );

    public ShapeSetSloan()
    {
    }

    private static Map<Character, Glyph> getCharacterMap()
    {
        Map<Character, Glyph> result = new HashMap<>();

        for ( Glyph c : Glyph.values() )
            result.put( c._char, c );

        return Collections.unmodifiableMap( result );
    }

    @Override
    public Shape createShapeFor( char character )
    {
        Glyph direction = _charDirectionMap.get( character );

        if ( direction == null )
            throw new IllegalArgumentException( "Invalid character: " + character );

        return new SloanShape( direction );
    }

    @Override
    public char getDemoChar()
    {
        return Glyph.H._char;
    }

    @Override
    public char getRandomChar()
    {
        return _characters.get(
                MathUtil.randomBetween( 0, _charDirectionMap.size()-1 ) );
    }
}

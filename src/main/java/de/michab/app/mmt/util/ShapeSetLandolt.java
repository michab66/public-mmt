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

import de.michab.app.mmt.components.LandoltShape;
import de.michab.app.mmt.components.LandoltShape.Direction;
import javafx.scene.shape.Shape;

/**
 * Landolt Shapes.  A Landolt C, also known as a Japanese Vision Test, Landolt
 * ring or Landolt broken ring, is an optotype, i.e. a standardized symbol used
 * for testing vision. It was developed by the Swiss-born ophthalmologist
 * Edmund Landolt.
 *
 * @author Michael Binz
 */
public class ShapeSetLandolt extends ShapeSet
{
    private static final Map<Character, Direction> _charDirectionMap =
            getCharacterToDirectionMap();

    private static List<Character> _characters =
            Collections.unmodifiableList(
                    new ArrayList<>(
                            _charDirectionMap.keySet() ) );

    public ShapeSetLandolt()
    {
    }

    private static Map<Character, Direction> getCharacterToDirectionMap()
    {
        Map<Character, Direction> result = new HashMap<>();

        result.put( '6', Direction.E );
        result.put( '3', Direction.SE );
        result.put( '2', Direction.S );
        result.put( '1', Direction.SW );
        result.put( '4', Direction.W );
        result.put( '7', Direction.NW );
        result.put( '8', Direction.N );
        result.put( '9', Direction.NE );

        return Collections.unmodifiableMap( result );
    }

    @Override
    public Shape createShapeFor( char character )
    {
        Direction direction = _charDirectionMap.get( character );

        if ( direction == null )
            throw new IllegalArgumentException( "Invalid character: " + character );

        return new LandoltShape( direction );
    }

    @Override
    public char getDemoChar()
    {
        return '4';
    }

    @Override
    public char getRandomChar()
    {
        return _characters.get(
                MathUtil.randomBetween( 0, _charDirectionMap.size()-1 ) );
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.util.List;

import org.smack.util.MathUtil;

import de.michab.app.mmt.lab.BaseShape;
import javafx.scene.shape.PathElement;

/**
 * Landolt Shapes.  A Landolt C, also known as a Japanese Vision Test, Landolt
 * ring or Landolt broken ring, is an optotype, i.e. a standardized symbol used
 * for testing vision. It was developed by the Swiss-born ophthalmologist
 * Edmund Landolt.
 *
 * @author Michael Binz
 */
public class LandoltShape extends BaseShape
{
    public enum Direction {
        N (6 * 45),
        NE(7 * 45),
        E (0 * 45),
        SE(1 * 45),
        S (2 * 45),
        SW(3 * 45),
        W (4 * 45),
        NW(5 * 45);

        final int _angle;

        private Direction(int direction)
        {
            _angle = direction;
        }
    };

    private final Direction _direction;

    private static char[] _characters = new char[]{
        '6', // E
        '3', // SE
        '2', // S
        '1', // SW
        '4', // W
        '7', // NW
        '8', // N
        '9'  // NE
    };

//    private static final Map<Character, Direction> _charDirectionMap = getCharacterToDirectionMap();

    public LandoltShape( Direction direction )
    {
        _direction = direction;

        setStroke(
                null );
        rotateProperty().set(
                _direction._angle );

        repaint();
    }

//    private static Map<Character, Direction> getCharacterToDirectionMap()
//    {
//        Map<Character, Direction> result = new HashMap<>();
//
//        result.put( '6', Direction.E );
//        result.put( '3', Direction.SE );
//        result.put( '2', Direction.S );
//        result.put( '1', Direction.SW );
//        result.put( '4', Direction.W );
//        result.put( '7', Direction.NW );
//        result.put( '8', Direction.N );
//        result.put( '9', Direction.NE );
//
//        return Collections.unmodifiableMap( result );
//    }

    @Override
    protected void createPath( List<PathElement> result )
    {
        double dPx = Math.min(
                widthProperty.get(),
                heightProperty.get() ) / 5;

        // Compute the necessary points.
        double topY =
                0.5 * dPx;
        double botY =
                -topY;
        double radiusOuter =
                2.5 * dPx;
        double radiusInner =
                1.5 * dPx;
        double xOuter =
                MathUtil.pythagorasD(
                        radiusOuter,
                        topY );
        double xInner =
                MathUtil.pythagorasD(
                        radiusInner,
                        topY );

        // Position to the first point.
        moveTo( result, xOuter, topY );

        // Draw the outer circle.
        arcTo(
                result,
                radiusOuter,
                radiusOuter,
                0,
                xOuter,
                botY,
                true,
                true );
        // Line to inner.
        lineTo( result, xInner, botY );

        // Draw the inner circle.
        arcTo(
                result,
                radiusInner,
                radiusInner,
                0,
                xInner,
                topY,
                true,
                false );
    }

    public char getDemoChar()
    {
        return _characters[4];
    }
}

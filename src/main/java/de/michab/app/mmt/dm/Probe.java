/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.dm;

/**
 * A single MMT probe.
 *
 * @author Michael
 */
public class Probe
    implements
        Comparable<Probe>
{
    public enum Score
    {
        NOTICED(0.5f), UNNOTICED(0.0f), RECOGNIZED(1.0f);

        public final float score;

        Score( float score )
        {
            this.score = score;
        }
    };

    private final static char NOT_NOTICED = ' ';

    private String _position;
    private char _expected;
    private char _perceived;

    public Probe( String position )
    {
        _position = position;
    }

    @Override
    public Probe clone()
    {
        Probe result = new Probe( _position );
        result.setExpected( _expected );
        result.setPerceived( _perceived );
        return result;
    }

    public int getEccentricityIdx()
    {
        return Integer.parseInt( _position.substring( 1,2 ) );
    }

    public int getHandAngleIdx()
    {
        return Integer.parseInt( _position.substring( 0,1 ) );
    }

    public char getExpected()
    {
        return _expected;
    }

    public void setExpected( char _expected )
    {
        this._expected = _expected;
    }

    public char getPerceived()
    {
        return _perceived;
    }

    public void setPerceived( char perceived )
    {
        _perceived = perceived;
    }

    public Score getScore()
    {
        if ( NOT_NOTICED == _perceived )
            return Score.UNNOTICED;
        else if ( _expected == _perceived )
            return Score.RECOGNIZED;

        return Score.NOTICED;
    }

    public String getPosition()
    {
        return _position;
    }

    @Override
    public int compareTo( Probe o )
    {
        // Note that what we actually compare below are indices and
        // not absolute values. In this case this is ok, since in all
        // cases a larger index also means a larger number.

        int idx1 = getEccentricityIdx();
        int idx2 = o.getEccentricityIdx();

        if ( idx1 < idx2 )
            return -1;
        else if ( idx1 > idx2 )
            return 1;

        // Eccentricity was equal, so we continue comparing
        // the hand angle.

        idx1 = getHandAngleIdx();
        idx2 = o.getHandAngleIdx();

        if ( idx1 < idx2 )
            return -1;
        else if ( idx1 > idx2 )
            return 1;

        throw new AssertionError( "eccentricity and and hand angle equal." );
    }
}

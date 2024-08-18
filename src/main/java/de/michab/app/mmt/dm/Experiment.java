/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.dm;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.smack.util.StringUtil;

import de.michab.app.mmt.util.ShapeSet;

/**
 * Represents a single experiment for a single eye. A complete MMT test consists of
 * four experiments L100, L10, R100, R10.
 *
 * @author Michael Binz
 */
public class Experiment
{
    /**
     * The maximum number of probes.
     */
    public final static int MAX_PROBE_COUNT = 36;

    public enum Side {
        LEFT("la"),
        RIGHT("ra"),
        BOTH("rala");

        public final String _resourceName;

        private Side( String resourceName )
        {
            _resourceName = resourceName;
        }
    };

    public enum Contrast {
        HI( 100 ),
        LO( 10 );

        public final int percent;

        private Contrast( int p )
        {
            percent = p;
        }
    };

    private final Contrast _contrast;

    private final Side _eye;

    private LocalDate _testDate =
            LocalDate.now();

    private ShapeSet _shapeSet;

    private List<Probe> _probes =
            Collections.emptyList();

    private final Patient _patient;

    /**
     * Create an instance. The test date is initialized to the current date.
     *
     * @param side The side of this experiment. Null and Side.BOTH not allowed.
     * @param contrast The experiment's contrast. Null not allowed.
     * @throws NullPointerException on a {@code null} argument.
     */
    public Experiment( Patient patient, Side side, Contrast contrast )
    {
        // Null check.
        patient.getClass();
        side.getClass();
        contrast.getClass();

        if ( side == Side.BOTH )
            throw new IllegalArgumentException( StringUtil.EMPTY_STRING + side );

        _patient =
                patient;
        _eye =
                side;
        _contrast =
                contrast;
    }

    public Contrast getContrast()
    {
        return _contrast;
    }

    public Side getEye()
    {
        return _eye;
    }

    public LocalDate getTestDate()
    {
        return _testDate;
    }

    public void setTestDate( LocalDate testDate )
    {
        _testDate = testDate;
    }

    public ShapeSet getShapeSet()
    {
        return _shapeSet;
    }

    public void setShapeSet( ShapeSet shapeSet )
    {
        _shapeSet = shapeSet;
    }

    public List<Probe> getProbes()
    {
        return _probes;
    }

    /**
     * Set the experiment's probes. The length of the passed list
     * must  not exceed {@link #MAX_PROBE_COUNT} elements.
     *
     * @param probes The experiment's probes.
     */
    public void setProbes( List<Probe> probes )
    {
        if ( probes == null )
            probes = Collections.emptyList();

        if ( probes.size() > MAX_PROBE_COUNT )
            throw new IllegalArgumentException( "Length > max" );

        _probes = probes;
    }

    /**
     * Get the experiment's overall MMT score.
     *
     * @return The MMT score.
     */
    public float getScore()
    {
        float result = 0.0f;

        for ( Probe c : _probes )
            result += c.getScore().score;

        return result;
    }

    /**
     * Does the test consist of the full number of probes?
     *
     * @return {@code true} if the test has the full number of probes.
     */
    public boolean isTestComplete()
    {
        return _probes.size() == MAX_PROBE_COUNT;
    }

    public Patient getPatient()
    {
        return _patient;
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.dm;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.smack.util.StringUtil;
import org.smack.util.collections.MultiMap;

import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;

/**
 * The patient. The one being sick.
 *
 * @author Michael Binz
 */
public class Patient
{
    private final String _firstName;
    private final String _lastName;
    private final LocalDate _birthdate;

    private final MultiMap<Side, Contrast, Experiment> _experiments =
            new MultiMap<>();

    /**
     * Create an instance.
     */
    public Patient( String firstname, String lastname, LocalDate birthdate )
    {
        _firstName = firstname;
        _lastName = lastname;
        _birthdate = birthdate;
    }

    public String getFirstname()
    {
        return _firstName;
    }

    public String getLastname()
    {
        return _lastName;
    }

    public LocalDate getBirthdate()
    {
        return _birthdate;
    }

    /**
     * Get the patient's age in years.
     *
     * @return The patient's age in years.
     */
    public int getAge()
    {
        return (int)ChronoUnit.YEARS.between( _birthdate, LocalDate.now() );
    }

    /**
     * Add an Experiment.
     *
     * @param experiment The experiment to add.  No null allowed.
     */
    public void addExperiment( Experiment experiment )
    {
        if ( experiment.getEye() == Side.BOTH )
            throw new AssertionError( StringUtil.EMPTY_STRING + Side.BOTH );

        Side side =
                experiment.getEye();
        Contrast contrast =
                experiment.getContrast();

        _experiments.put( side, contrast, experiment );
    }

    /**
     * Get an experiment.
     *
     * @param side The side.  No null allowed.
     * @param contrast The contrast.  No null allowed.
     * @return The experiment.  Null if the experiment is not available.
     */
    public Experiment getExperiment( Side side, Contrast contrast )
    {
        return _experiments.get(
                Objects.requireNonNull( side ),
                Objects.requireNonNull( contrast ) );
    }
}

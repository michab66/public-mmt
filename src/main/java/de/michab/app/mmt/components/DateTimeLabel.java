/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A label displaying the current time.
 *
 * @author Michael Binz
 */
public class DateTimeLabel extends Label
{
    /**
     * Creates an instance.
     */
    public DateTimeLabel()
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( this );

        {
            String DATE_PATTERN =
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                        FormatStyle.MEDIUM,
                        null,
                        IsoChronology.INSTANCE,
                        Locale.getDefault() );
            String TIME_PATTERN =
                DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                        null,
                        FormatStyle.SHORT,
                        IsoChronology.INSTANCE,
                        Locale.getDefault() );

            String combinedFormat =
                    DATE_PATTERN +
                    " " +
                    _dateTimeDelimiter +
                    " " +
                    TIME_PATTERN;

            _dateFormat = new SimpleDateFormat( combinedFormat );
        }

        setText( getCurrentDateTimeFormatted() );

        Timeline timer = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        s -> setText( getCurrentDateTimeFormatted() )),
                new KeyFrame(
                        Duration.minutes( 1 )));
        // We want to get notified 1 second past the new minute.
        timer.setDelay(
                Duration.seconds( 61 - LocalTime.now().getSecond() ) );
        timer.setCycleCount(
                Timeline.INDEFINITE );
        timer.play();
    }

    /**
     * A delimiter string put between date and time.
     */
    @Resource
    private String _dateTimeDelimiter;

    private final SimpleDateFormat _dateFormat;

    private String getCurrentDateTimeFormatted()
    {
        return _dateFormat.format( new Date() );
    }
}

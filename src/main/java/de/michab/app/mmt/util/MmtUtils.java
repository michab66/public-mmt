/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.dm.Patient;
import de.michab.app.mmt.dm.Tenant;
import javafx.util.StringConverter;

/**
 * General MMT utility operations.
 *
 * @author Michael Binz
 */
public class MmtUtils
{
    private static final Logger LOG =
            Logger.getLogger( MmtUtils.class.getName() );

    /**
     * The application-wide local time pattern.  Use this for formatting.
     * Note that we define this ourselves since the locale-dependent APIs
     * do not work in a jlink-generated application image using OpenJdk 11.
     */
    @Resource
    private static String DATE_PATTERN;

    private static final DateFormat DATE_FORMAT;

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( MmtUtils.class );
        DATE_FORMAT =
                new SimpleDateFormat(
                        Objects.requireNonNull( DATE_PATTERN ) );
        DATE_FORMAT.setLenient(
                false );
    }

    /**
     * @return The application-wide date pattern.
     */
    public static String getDatePattern()
    {
        return DATE_PATTERN;
    }

    /**
     * Format a date to a string.
     *
     * @param date The date to format.
     * @return A formatted date.
     */
    public static String formatDate( LocalDate date )
    {
        return date.format( DateTimeFormatter.ofPattern( DATE_PATTERN ) );
    }

    /**
     * Parse a string to a date.  Allows two-digit years and maps this
     * properly into the century (according to the rules described in
     * the java api docs for simple date format 'yy' patterns).
     *
     * @param date The date in string form.
     * @return The parsed date.
     * @throws ParseException If the passed string was not valid.
     */
    public static LocalDate parseDate( String date ) throws ParseException
    {
        Date input = DATE_FORMAT.parse( date );

        return input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Formats a score to score/maxScore, e.g. 18.5/36.
     *
     * @param score The raw score.
     * @return The formatted score.
     */
    public static String formatScore( float score )
    {
        return String.format( "%.01f/%d",
                score,
                Experiment.MAX_PROBE_COUNT );
    }

    public static void main( String[] args )
    {
        System.out.println( getDesktopDirectory() );

        System.out.println( formatScore( 3.0f ) );
        System.out.println( formatScore( 3.105f ) );
        System.out.println( formatScore( 3.115f ) );
        System.out.println( formatScore( 3.125f ) );
        System.out.println( formatScore( 3.135f ) );
        System.out.println( formatScore( 3.145f ) );
        System.out.println( formatScore( 3.156f ) );
        System.out.println( formatScore( 3.16f ) );
    }

    /**
     * Formats a GFS score.
     *
     * @param _100
     * @param _10
     * @return
     */
    public static String formatGfs( float _100, float _10 )
    {
        float score = 0.0f;

        // Only compute score if we have a non-zero divisor.
        // Otherwise we return zero.
        if ( _100 > 0 )
        {
            score = _10 / _100;
        }

        return String.format( "%.01f", score );
    }

    /**
     * Format the tenant.
     *
     * @param tenant The tenant object to format.
     * @return The formatted tenant object.
     */
    public static String formatTenant( Tenant tenant )
    {
        if ( tenant == null )
        {
            return StringUtil.EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder(
                tenant.getName() );
        sb.append( ", " );
        sb.append(
                tenant.getAddress1() );
        String address2 = tenant.getAddress2();
        if ( StringUtil.hasContent( address2 ) )
        {
            sb.append( ", " );
            sb.append( address2 );
        }

        return sb.toString();
    }

    /**
     * Makes a filename from a Patient object.
     *
     * @param patient The patient, null not allowed.
     * @param suffix The file suffix.
     * @return
     */
    public static File fileFromPatient( Patient patient, String suffix )
    {
        if ( ! suffix.startsWith( "." ) )
        {
            suffix = "." + suffix;
        }

        File homeDir =
                getDesktopDirectory();

        StringBuilder name = new StringBuilder( "mmt_" );

        name.append( patient.getLastname() );
        name.append( '_' );
        name.append( patient.getFirstname() );
        name.append( '_' );
        name.append( formatDate( LocalDate.now() ) );
        name.append( '_' );

        if (
                null != patient.getExperiment( Side.RIGHT, Contrast.HI ) ||
                null != patient.getExperiment( Side.RIGHT, Contrast.LO ) )
        {
            name.append( "RA" );
        }
        if (
                null != patient.getExperiment( Side.LEFT, Contrast.HI ) ||
                null != patient.getExperiment( Side.LEFT, Contrast.LO ) )
        {
            name.append( "LA" );
        }

        File result = new File(
                homeDir,
                name.toString() + suffix );

        // If the file exists already add an integer suffix until
        // the new file does not exist.
        if ( ! result.exists() )
        {
            return result;
        }

        int count = 0;

        while ( true )
        {
            count++;

            result = new File(
                    homeDir,
                    name.toString() + "_" + count + suffix );

            if ( ! result.exists() )
            {
                return result;
            }
        }
    }

    private static final StringConverter<byte[]> _arrayConverter = new StringConverter<>()
    {
        @Override
        public String toString( byte[] array )
        {
            StringBuilder result =
                    new StringBuilder( array.length * 2 );

            for ( Byte c : array )
            {
                result.append( String.format( "%02x", c ) );
            }

            return result.toString();
        }

        private byte[] fromStringImpl( String string )
        {
            byte[] result = new byte[ string.length() / 2 ];

            if ( (result.length * 2) != string.length() )
            {
                throw new IllegalArgumentException( string );
            }

            for ( int i = 0 ; i < result.length ; i++ )
            {
                int stringIndex =
                        i * 2;
                String pair =
                        string.substring( stringIndex, stringIndex+2 );
                result[i] = (byte)
                        Short.parseShort( pair, 16 );
            }

            return result;
        }

        @Override
        public byte[] fromString( String string )
        {
            try
            {
                return fromStringImpl( string );
            }
            catch( Exception e )
            {
                return null;
            }
        }
    };

    public static String toHex( byte[] array )
    {
        return _arrayConverter.toString( array );
    }


    public static byte[] fromHex( String signature )
    {
        return _arrayConverter.fromString( signature );
    }

    /**
     * @return The user's desktop directory.
     */
    public static File getDesktopDirectory()
    {
        File home = new File(
                System.getProperty("user.home") );
        File result =
                new File( home, "Desktop" );
        if ( result.exists() )
        {
            return result;
        }

        return home;
    }

    /**
     * Clone a gfx context and set the antialiasing hints. The caller
     * has to dispose the returned clone if done with it.
     *
     * @param g The gfx context to clone.
     * @return A cloned and cast gfx context.
     */
    public static Graphics2D cloneAntialiased( Graphics g )
    {
        return ImageUtil.addAntialiasing(
                g.create() );
    }

    private static URL makeIndirectionUrlImpl( URL src ) throws IOException
    {
        Path tempFile = Files.createTempFile(
                MmtUtils.class.getSimpleName(),
                ".tmp" );

        // Prevent to leave spam in the temp directory.
        tempFile.toFile().deleteOnExit();

        LOG.info( "Created temporary file: " + tempFile );

        try ( InputStream is = src.openStream() )
        {
            Files.copy( is, tempFile, StandardCopyOption.REPLACE_EXISTING );
        }

        URL result = tempFile.toUri().toURL();

        LOG.info( "Returning URL: " + result.toExternalForm() );

        return result;
    }

    /**
     * Converts the passed url into an url pointing into the local file
     * system.  This is done by copying the content of the input url.
     * @param src The source URL.
     * @return A local file-system URL in external form.
     */
    public static String makeIndirectionUrl( URL src )
    {
        try
        {
            return makeIndirectionUrlImpl( src ).toExternalForm();
        }
        catch ( Exception e )
        {
            throw new AssertionError( e.getMessage() , e );
        }
    }

    /**
     * Hide constructor.
     */
    private MmtUtils()
    {
        throw new AssertionError();
    }
}

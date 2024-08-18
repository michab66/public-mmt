/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Contrast;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.pdf.PdfDocument;
import de.michab.app.mmt.pdf.PdfPage;
import de.michab.app.mmt.pdf.PdfPage.Orientation;
import de.michab.app.mmt.pdf.PdfPage.Size;

/**
 * Creates the MMT report as a pdf.
 *
 * @author Michael Binz
 */
public final class MmtReport
{
    static final Logger LOG = Logger.getLogger( MmtReport.class.getName() );

    /**
     * Create the report.
     */
    public static PdfDocument generateReport( Mmt app ) throws IOException
    {
        PdfDocument doc = new PdfDocument( Size.A4, Orientation.LANDSCAPE );

        // Add elements common for all pages.
        doc.add( ReportUtils.makeHeadline( app, doc ) );
        doc.add( ReportUtils.makeInfoColumnCommon( app ) );
        doc.add( ReportUtils.makeFootline() );
        doc.add( ReportUtils.makeLegend() );

        List<Experiment> left =
                new ArrayList<>();
        List<Experiment> right =
                new ArrayList<>();

        Experiment left100 = validateTest(
                app.getTest( Side.LEFT, Contrast.HI ) );
        if ( left100 != null )
            left.add( left100 );
        Experiment left10 = validateTest(
                app.getTest( Side.LEFT, Contrast.LO ) );
        if ( left10 != null )
            left.add( left10 );

        Experiment right100 = validateTest(
                app.getTest( Side.RIGHT, Contrast.HI ) );
        if ( right100 != null )
            right.add( right100 );
        Experiment right10 = validateTest(
                app.getTest( Side.RIGHT, Contrast.LO ) );
        if ( right10 != null )
            right.add( right10 );

        if ( right.size() == 2 )
        {
            // Add full report page right.
            PdfPage page = doc.createPage();

            generatePageFull(
                    page,
                    right100, right10 );
        }

        if ( left.size() == 2 )
        {
            // Add full report page left.
            PdfPage page = doc.createPage();

            generatePageFull(
                    page,
                    left100, left10 );
        }

        if ( right.size() == 1 )
        {
            PdfPage page = doc.createPage();

            generatePageSimple(
                    page,
                    right.get( 0 ) );
        }

        if ( left.size() == 1 )
        {
            PdfPage page = doc.createPage();

            generatePageSimple(
                    page,
                    left.get( 0 ) );
        }

        if ( doc.getPages().size() == 0 )
        {
            LOG.warning( "No pages generated." );
            // Add error report.
        }

        doc.paint();

        return doc;
    }

    private static void generatePageFull(
            PdfPage page,
            Experiment _100, Experiment _10 )
    {
        Objects.requireNonNull( _100 );
        Objects.requireNonNull( _10 );

        if ( _100.getEye() != _10.getEye() )
            throw new IllegalArgumentException();

        // Column 1
        page.add( ReportUtils.makeInfoColumnForTest( _100, _10 ) );
        // Column 2
        page.add( ReportUtils.makeWheelColumn( page, _100, _10 ) );
    }

    /**
     *
     * @param page
     * @param _100
     */
    private static void generatePageSimple(
            PdfPage page,
            Experiment _100 )
    {
        // Validate input.
        Objects.requireNonNull( _100 );

        // Column 1
        page.add( ReportUtils.makeInfoColumnForTest( _100 ) );
        // Column 2
        page.add( ReportUtils.makeWheelColumn( page, _100 ) );
    }

    private static Experiment validateTest( Experiment experiment )
    {
        if ( experiment == null )
            return null;

        if ( ! experiment.isTestComplete() )
            return null;

        return experiment;
    }

    /**
     * Hide constructor.
     */
    private MmtReport()
    {
        throw new AssertionError();
    }
}

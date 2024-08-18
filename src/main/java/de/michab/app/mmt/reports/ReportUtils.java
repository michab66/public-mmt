/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.reports;

import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Objects;

import org.smack.application.ApplicationInfo;
import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.Mmt;
import de.michab.app.mmt.components.MmtTestResult;
import de.michab.app.mmt.components.ResultWheel;
import de.michab.app.mmt.dm.Experiment;
import de.michab.app.mmt.dm.Experiment.Side;
import de.michab.app.mmt.dm.Patient;
import de.michab.app.mmt.dm.Probe.Score;
import de.michab.app.mmt.dm.Tenant;
import de.michab.app.mmt.pdf.PdfComponentAdapter;
import de.michab.app.mmt.pdf.PdfDocument;
import de.michab.app.mmt.pdf.PdfImage;
import de.michab.app.mmt.pdf.PdfPage;
import de.michab.app.mmt.pdf.PdfPageElement;
import de.michab.app.mmt.pdf.PdfPageElement.Pos;
import de.michab.app.mmt.pdf.PdfText;
import de.michab.app.mmt.pdf.PdfUtil;
import de.michab.app.mmt.pdf.layout.Box;
import de.michab.app.mmt.pdf.layout.HorizontalBox;
import de.michab.app.mmt.pdf.layout.VerticalBox;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.screens.sdk.MmtEyeBar;
import de.michab.app.mmt.util.MmtUtils;

/**
 * Helper operations for report implementations.
 *
 * @author Michael Binz
 */
public final class ReportUtils
{
    /**
     * Inter-component gap.
     */
    static final float GAP = 10;

    /**
     * Headline
     */
    private static final float TOP_LINE_Y = 9;

    /**
     * Footer.
     */
    private static final float BOT_LINE_Y = 579;

    /**
     * Left column, common part.
     */
    static final float MID_LINE_Y_A = 55;

    /**
     * Left column, page specific part.
     */
    static final float MID_LINE_Y_B = 210;

    /**
     * Left bound.
     */
    static final float A_LINE_X = 20;
    /**
     * Wheel column.
     */
    static final float B_LINE_X = 330;

    /**
     *
     * @param app
     * @return
     */
    public static PdfPageElement makeInfoColumnCommon( Mmt app )
    {
        VerticalBox vb = new VerticalBox();
        vb.setPosition( A_LINE_X, MID_LINE_Y_A );
        vb.setGap( GAP );
        vb.setAnchor( Pos.START );

        vb.add( Box.createFiller( 1, PdfUtil.cmToPt( 0.1f ) ) );
        vb.add( new PdfText(
                _headline ).setFont( getFontLarge() ) );

        vb.add( makePatient( app ) );

        return vb;
    }

    /**
     * This is the leftmost column.
     *
     * @param _100 The high contrast test.
     * @param _10 The low contrast test.
     * @return The layed-out column.
     */
    public static PdfPageElement makeInfoColumnForTest( Experiment _100, Experiment _10 )
    {
        Objects.requireNonNull( _100 );

        Side side = _100.getEye();

        VerticalBox vb = new VerticalBox();
        vb.setAnchor( Pos.START );
        vb.setGap( GAP );
        vb.setPosition( A_LINE_X, MID_LINE_Y_B + PdfUtil.cmToPt( 2.5f ) );

        String text = ( side == Side.LEFT ) ?
            _leftEye :
            _rightEye;

        vb.add( new PdfText( text )
            .setFont( ReportUtils.getFontLarge() )
            .setForeground( getHeader() ) );

        if ( _10 == null )
            return vb ;

        vb.add( makeGfsRelation( _100, _10 ) );

        return vb ;
    }
    public static PdfPageElement makeInfoColumnForTest( Experiment _100 )
    {
        return makeInfoColumnForTest( _100, null );
    }

    /**
     * Create a full wheel column.
     *
     * @param page
     * @param _100
     * @param _10
     * @return
     */
    public static PdfPageElement makeWheelColumn( PdfPage page, Experiment _100, Experiment _10 )
    {
        VerticalBox vb = new VerticalBox();
        vb.setPosition( ReportUtils.B_LINE_X, ReportUtils.MID_LINE_Y_A );
        vb.setAnchor( Pos.START );
        vb.setGap( ReportUtils.GAP * 2 );

        vb.add( ReportUtils.makeWheelRow(
                page.getDocument(),
                _100  ) );

        vb.add( ReportUtils.makeWheelRow(
                page.getDocument(),
                _10  ) );

        return vb;
    }

    /**
     * Create a half wheel column.
     *
     * @param page
     * @param _100
     * @return
     */
    public static PdfPageElement makeWheelColumn( PdfPage page, Experiment _100 )
    {
        VerticalBox vb = new VerticalBox();

        vb.setAnchor( Pos.CENTER );
        vb.setGap( GAP );

        PdfPageElement wheelRow = ReportUtils.makeWheelRow(
                page.getDocument(),
                _100 );

        vb.add( wheelRow );

        vb.setPosition(
                ReportUtils.B_LINE_X,
                (page.getHeight() - wheelRow.getHeight()) / 2 );

        return vb;
    }

    /**
     * The wheel size square.
     */
    private final static int WS = 240;

    public static PdfPageElement makeWheelRow( PdfDocument doc, Experiment experiment )
    {
        HorizontalBox hb = new HorizontalBox();

        hb.add( new PdfImage(
                doc,
                MmtEyeBar.getImage( experiment ) ) );
        ResultWheel c =
                new ResultWheel( experiment );

        c.setBackground( getBackground() );
        c.setForeground( getForeground() );
        c.setFill( false );
        c.setSize( WS, WS );
        c.setFont( getFontTiny() );
        hb.add( new PdfComponentAdapter( c ) );

        ////
        hb.add( Box.createSquareFiller( GAP ) );

        // Add info block.
        {
            VerticalBox scoreBox = new VerticalBox();
            scoreBox.setAnchor( Pos.START );
            scoreBox.setGap( GAP );

            String fmt = StringUtil.concatenate( "\n", _resultAtContrast );

            String infoTop = String.format(
                    fmt, experiment.getContrast().percent );
            scoreBox.add( new PdfText( infoTop ).setFont( getFontSmall() ) );

            String infoBot = String.format(
                    _resultScore, MmtUtils.formatScore( experiment.getScore() ) );
            scoreBox.add( new PdfText( infoBot ).setFont( getFontMedium() ).setForeground( getHeader() ) );

            hb.add( scoreBox );
        }

        return hb;
    }

    /**
     * @return Returns the footline. Currently this contains only the MMT
     * version number.
     */
    public static PdfPageElement makeFootline()
    {
        String version = ServiceManager.getApplicationService(
                ApplicationInfo.class ).getVersion();
        PdfText result = new PdfText(
                version );
        result.setFont( getFontTiny() );
        result.setPosition(
                // Alignment right.
                PdfUtil.cmToPt( 28.5f ) - result.getWidth(),
                BOT_LINE_Y );

        return result;
    }

    /**
     * Creates the headline layout.
     *
     * @param document
     * @return
     */
    public static PdfPageElement makeHeadline( Mmt app, PdfDocument document )
    {
        HorizontalBox result =
                new HorizontalBox();
        result.setPosition( A_LINE_X, TOP_LINE_Y );
        result.setGap( GAP );

        result.add( new PdfImage(
                document,
                app.getIcon() ) );

        result.add( new PdfText(
                app.getTitle() ) );

        Tenant tenant = app.getTenant();

        if ( tenant == null )
                    return result;

        PdfPageElement tenantElement =
                makeTenant( tenant ).setFont( ReportUtils.getFontMedium() );

        float tenantWidth =
                tenantElement.getWidth();
        final float CM_IN_PT_23 =
                PdfUtil.cmToPt( 23 );
        if ( tenantWidth < CM_IN_PT_23 )
        {
            result.add( Box.createFiller(
                    // Alignment right.
                    CM_IN_PT_23 - tenantWidth,
                    1 ) );
        }

        result.add( tenantElement );

        return result;
    }

    static PdfPageElement makeLegend()
    {
        VerticalBox vb = new VerticalBox(
                makeLegendLine( _unnoticed, Score.UNNOTICED ),
                makeLegendLine( _noticed, Score.NOTICED ),
                makeLegendLine( _recognized, Score.RECOGNIZED ),
                new PdfText( _gfsLegend ).setFont( getFontSmall() ));

        vb.setAnchor( Pos.START );
        vb.setGap( GAP / 2 );
        vb.setPosition( A_LINE_X, BOT_LINE_Y -
                vb.getHeight() -
                GAP -
                PdfUtil.cmToPt( 1 ) );

        return vb;
    }

    /**
     *
     * @param text
     * @param score
     * @return
     */
    private static PdfPageElement makeLegendLine( String text, Score score )
    {
        PdfText textBox = new PdfText( text );
        textBox.setFont( getFontSmall() );

        MmtTestResult res1 = new MmtTestResult( score );
        res1.setSize( (int)textBox.getHeight(), (int)textBox.getHeight() );

        HorizontalBox result = new HorizontalBox(
                new PdfComponentAdapter( res1 ),
                textBox );

        result.setAnchor( Pos.BOTTOM );
        result.setGap( GAP );

        return result;
    }

    /**
     *
     * @param _100
     * @param _10
     * @return
     */
    private static PdfPageElement makeGfsRelation( Experiment _100, Experiment _10 )
    {
        String message =
                _diagGfsRelation +
                " " +
                MmtUtils.formatGfs( _100.getScore(), _10.getScore() );

        PdfText result = new PdfText( message );
        result.setFont( getFontMedium() );
        result.setForeground( getHeader() );

        return result;
    }

    /**
     * Format the tenant.
     *
     * @param tenant The tenant object to format.
     * @return The formatted tenant object.
     */
    private static PdfPageElement makeTenant( Tenant tenant )
    {
        return new PdfText(
                MmtUtils.formatTenant( tenant ) ).setFont( ReportUtils.getFontSmall() );
    }

    private static PdfPageElement makePatient( Mmt app )
    {
        Patient patient = Objects.requireNonNull(
                app.getPatient() );

        PdfText result = new PdfText(
                String.format(
                        _formatName,
                        patient.getFirstname(),
                        patient.getLastname(),
                        MmtUtils.formatDate( patient.getBirthdate() ),
                        patient.getAge() ),
                String.format(
                        _formatExperiment,
                        MmtUtils.formatDate( LocalDate.now() ) ) );

        result.setForeground( getHeader() );
        result.setFont( getFontMedium() );

        return result;
    }

    @Resource
    private static Color _background;

    public static Color getBackground()
    {
        return _background;
    }

    @Resource
    private static Color _foreground;

    public static Color getForeground()
    {
        return _foreground;
    }

    /**
     * Get the header color.  Currently a touch of blue...
     *
     * @return The header color.
     */
    public static Color getHeader()
    {
        return ScreenUtils.getHeader();
    }

    @Resource
    private static Font _fontMedium;

    public static Font getFontMedium()
    {
        return _fontMedium;
    }

    @Resource
    private static Font _fontLarge;

    public static Font getFontLarge()
    {
        return _fontLarge;
    }

    @Resource
    private static Font _fontSmall;

    public static Font getFontSmall()
    {
        return _fontSmall;
    }

    @Resource
    private static Font _fontTiny;

    private static Font getFontTiny()
    {
        return _fontTiny;
    }

    /**
     * Optotypen-Erkennung im zentralen ...
     */
    @Resource
    private static String[] _headline;

    @Resource
    private static String _formatName;
    @Resource
    private static String _formatExperiment;

    @Resource
    private static String _leftEye;
    @Resource
    private static String _rightEye;

    /**
     * The value gets appended.
     *
     * GFS-Verhältnis =
     */
    @Resource
    private static String _diagGfsRelation;
    @Resource
    private static String _unnoticed;
    @Resource
    private static String _noticed;
    @Resource
    private static String _recognized;
    @Resource
    private static String[] _resultAtContrast;
    @Resource
    private static String _resultScore;
    @Resource
    private static String[] _gfsLegend;

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( ReportUtils.class );
    }

    private ReportUtils()
    {
        throw new AssertionError();
    }
}

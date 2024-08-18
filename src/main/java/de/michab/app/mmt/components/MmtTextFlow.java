/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.smack.util.ServiceManager;
import org.smack.util.StringUtil;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;
import org.smack.util.resource.ResourceUtil;
import org.smack.util.xml.XmlUtil;

import de.michab.app.mmt.screens.ScreenUtils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * An extended text component.  Note that this could malfunction in case
 * Nvidia drivers are installed.
 *
 * @author Michael Binz
 */
public class MmtTextFlow extends TextFlow
{
    private static final Logger LOG =
            Logger.getLogger( MmtTextFlow.class.getName() );

    private static final byte[] CONVERTER =
            ResourceUtil.loadResource(
                    MmtTextFlow.class,
                    "xhtmlToTextFlow.xsl" );

    @Resource
    private static StringBuilder _test;
    @Resource
    private static StringBuilder _test2;

    private final Font _defaultFont;
    private final Color _defaultColor;

    /**
     * Create an instance. Default font and color are adjusted for
     * screen headers.  This is historical.
     */
    public MmtTextFlow()
    {
        this(
                ScreenUtils.getFontMediumFx(),
                ScreenUtils.getHeaderFx() );
    }

    public MmtTextFlow( Font defaultFont, Color defaultColor )
    {
        _defaultFont = Objects.requireNonNull(
                defaultFont );
        _defaultColor = Objects.requireNonNull(
                defaultColor );

        getChildren().addListener( y );
    }

    private final ListChangeListener<Node> y = new ListChangeListener<>()
    {

        @Override
        public void onChanged(
                javafx.collections.ListChangeListener.Change<? extends Node> c )
        {
            while ( c.next() )
            {
                if ( ! c.wasAdded() )
                    return;

                updateChildren(c.getList().subList( c.getFrom(), c.getTo() ));
            }
        }
    };

    private void updateChildren( List<? extends Node> nodes )
    {
        for ( Node c : nodes )
            try
            {
                MmtText mmtText = (MmtText)c;

                Color color = mmtText.getMmtColor();
                if ( color == null )
                    color = _defaultColor;
                mmtText.setFill( color );

                Font font = mmtText.getMmtFont();
                if ( font == null )
                    font = _defaultFont;
                mmtText.setFont( font );
            }
            catch ( ClassCastException ignore )
            {
                // We skip if the component is not a MmtText node.
            }
    }

    /**
     * Process simplified html as formerly used in Swing to create
     * a MmtTextFlow instance.  If the passed test does not start
     * with a prefix of "<html>", then the text is placed in the
     * result without any modification.
     *
     * @param resourceText
     * @return An MmtTextFlow instance.
     */
    public static MmtTextFlow create( String resourceText )
    {
        if ( ! resourceText.startsWith( "<html>" ) )
        {
            MmtText text =
                    new MmtText( resourceText );
            MmtTextFlow result =
                    new MmtTextFlow();
            result.getChildren().add( text );
            result.setTextAlignment( TextAlignment.CENTER );

            return result;
        }

        String fxml = null;
        try
        {
            fxml = XmlUtil.transform(
                    new ByteArrayInputStream( CONVERTER ),
                    new ByteArrayInputStream( resourceText.getBytes() ) );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }

        if ( StringUtil.isEmpty( fxml ) )
            throw new AssertionError( "Empty fxml." );

        try
        {
            return new FXMLLoader().load(
                    new ByteArrayInputStream( fxml.getBytes() ) );
        }
        catch ( Exception e )
        {
            LOG.log( Level.SEVERE, fxml, e );

            if ( e instanceof RuntimeException )
                throw (RuntimeException)e;
            throw new RuntimeException( e );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        ResourceManager rm =
                ServiceManager.getApplicationService( ResourceManager.class );

        rm.injectResources( MmtTextFlow.class );

        MmtTextFlow textflow = create( _test.toString() );

        System.err.println( "Success: " + textflow );
    }
}

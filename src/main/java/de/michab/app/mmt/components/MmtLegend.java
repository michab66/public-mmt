/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2016-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import org.smack.fx.FxUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Probe.Score;
import de.michab.app.mmt.screens.ScreenUtils;
import de.michab.app.mmt.screens.sdk.MmtButton;
import javafx.geometry.Dimension2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * The test results legend box.
 *
 * @author Michael Binz
 */
public class MmtLegend extends VBox
{
    @Resource
    private String _unnoticed;
    @Resource
    private String _noticed;
    @Resource
    private String _recognized;

    private final Label c10;
    private final Label c11;
    private final Label c12;

    private final MmtTestResultFx t00;
    private final MmtTestResultFx t01;
    private final MmtTestResultFx t02;

    public MmtLegend()
    {
        this( MmtButton.DIMENSION_S );
    }

    /**
     * Create an instance.
     */
    public MmtLegend( Dimension2D d )
    {
        // Set the spacing.
        super( 5 );

        ServiceManager.getApplicationService( ResourceManager.class ).
            injectResources( this );

        t00 = new MmtTestResultFx( Score.UNNOTICED, d.getHeight() );
        t01 = new MmtTestResultFx( Score.NOTICED, d.getHeight()  );
        t02 = new MmtTestResultFx( Score.RECOGNIZED, d.getHeight()  );

        adjust( t00, t01, t02 );

        c10 = new Label( _unnoticed );
        c11 = new Label( _noticed );
        c12 = new Label( _recognized );

        c10.setGraphic( t00 );
        c11.setGraphic( t01 );
        c12.setGraphic( t02 );

        adjust( c10, c11, c12 );

        getChildren().addAll( c10, c11, c12 );
    }

    private void adjust( Label... components )
    {
        for ( Label c : components )
        {
            c.setTextFill( ScreenUtils.getForegroundFx() );
            c.setBackground( FxUtil.getBackground( ScreenUtils.getBackgroundColorFx() ) );
            c.setFont( ScreenUtils.getFontSmall() );
        }
    }

    private void adjust( MmtTestResultFx... trs  )
    {
        for ( MmtTestResultFx c : trs )
        {
            c.borderColorProperty.set( c.fillProperty.get() );
        }
    }
}

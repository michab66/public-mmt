/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.components;

import java.awt.Color;

import org.smack.fx.FxUtil;
import org.smack.util.ServiceManager;
import org.smack.util.resource.ResourceManager;
import org.smack.util.resource.ResourceManager.Resource;

import de.michab.app.mmt.dm.Probe;
import de.michab.app.mmt.util.ShapeSet;
import javafx.scene.Parent;
import javafx.scene.shape.Shape;

/**
 * A testpoint used in MMT. Responsible for the display of a test glyph.
 *
 * @author Michael Binz
 */
public class MmtProbe
    extends
        Parent
    implements Comparable<MmtProbe>
{
    private final Probe _dmProbe;

    @Resource
    private Color DFLT_FOREGROUND;

    private final Shape _shape;

    private final char _key;

    /**
     * Only for test purposes.
     */
    public MmtProbe( String position, ShapeSet ss )
    {
        _key = ss.getRandomChar();

        _dmProbe = new Probe(position);
        _dmProbe.setExpected( _key );
        _shape = ss.createShapeFor( _key );

        getChildren().add( _shape );

        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( this );

        _shape.setFill( FxUtil.to( DFLT_FOREGROUND ) );
        _shape.setStroke( null );

        focusTraversableProperty().set( false );
    }

    @Override
    public void resize( double width, double height )
    {
        _shape.resize( width, height );
    }

    @Override
    public double minWidth( double height )
    {
        return _shape.minWidth( height );
    }
    @Override
    public double minHeight( double width )
    {
        return _shape.minHeight( width );
    }
    @Override
    public double maxWidth( double height )
    {
        return _shape.maxWidth( height );
    }
    @Override
    public double maxHeight( double width )
    {
        return _shape.maxHeight( width );
    }

    @Override
    public boolean isResizable()
    {
        return _shape.isResizable();
    }

    /**
     * Set the probe's result.
     *
     * @param result The character seen by the test person.
     */
    public void setResult( char result )
    {
        _dmProbe.setPerceived( result );
    }

    @Override
    public int compareTo( MmtProbe o )
    {
        return _dmProbe.compareTo( o._dmProbe );
    }

    public Probe getProbe()
    {
        return _dmProbe.clone( );
    }

    public void setForeground( Color testColor )
    {
        // TODO check if this can be set by the constructor.
        _shape.setFill( FxUtil.to( testColor ) );
    }
}

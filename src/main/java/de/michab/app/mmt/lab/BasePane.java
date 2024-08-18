/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.lab;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * A base class for JavaFX layout implementations.
 *
 * @author Michael Binz
 */
public abstract class BasePane<T> extends Pane
{
    @SuppressWarnings("unused")
    private final static Logger LOG =
            Logger.getLogger( BasePane.class.getName() );

    /**
	 * Position all children.
	 */
    protected abstract void positionChildren();

    protected abstract void addLayoutComponent(
    		Node node,
    		T constraint );

    /**
     * Called in the layout phase.
     */
    @Override
    protected void layoutChildren()
    {
        positionChildren();

        for ( Node c : getChildren() )
        {
            Rectangle2D b = getBounds( c );

//            String msg = String.format(
//                    "BasePane: x=%f,y=%f,w=%f,h=%f",
//                    b.getMinX(),
//                    b.getMinY(),
//                    b.getWidth(),
//                    b.getHeight() ) + "resizable=" +c.isResizable() + " min=" + c.minWidth( -1 );
//
//            LOG.info( msg );
            HPos hAlignment =
                    getAlignment( c );

            if ( c.isResizable() )
            {
//                System.err.println( "doLayout: " + c );
                layoutInArea(
                        c,
                        b.getMinX(),
                        b.getMinY(),
                        b.getWidth(),
                        b.getHeight(),
                        0,
                        Insets.EMPTY,
                        hAlignment,
                        VPos.CENTER );
            }
            else
            {
//                System.err.println( "doPosition: " + c );
                positionInArea(
                        c,
                        b.getMinX(),
                        b.getMinY(),
                        b.getWidth(),
                        b.getHeight(),
                        0,
                        Insets.EMPTY,
                        hAlignment,
                        VPos.CENTER,
                        true );
            }
        }
    }


    private final Map<Node,Rectangle2D> _boundsMap =
            new HashMap<>();

    protected void setBounds( Node b, double x, double y, double w, double h )
    {
        setBounds(
                b,
                new Rectangle2D( x, y, w, h ) );
    }
    protected void setBounds( Node n, Rectangle2D r )
    {
        _boundsMap.put(
                n,
                r );
    }
    protected Rectangle2D getBounds( Node n )
    {
        return Objects.requireNonNull(
                _boundsMap.get( n ),
                "Bounds not set." );
    }

    protected final double getX( Node n )
    {
        return getBounds( n ).getMinX();
    }

    protected final double getY( Node n )
    {
        return getBounds( n ).getMinY();
    }

    protected final double getHeight( Node n )
    {
        return getBounds( n ).getHeight();
    }

    protected final double getWidth( Node n )
    {
        return getBounds( n ).getWidth();
    }

    /**
     * Sets the alignment for the child when contained by a border pane.
     * If set, will override the border pane's default alignment for the child's position.
     * Setting the value to null will remove the constraint.
     * @param child the child node of a border pane
     * @param value the alignment position for the child
     */
    protected void setAlignment( Node child, HPos value )
    {
        setProperty( child, HPos.class, value );
    }

    /**
     * Get the passed node's alignment.  If none was set, then
     * {@link HPos#CENTER} alignment is returned.
     * @param child The node.
     * @return The node's alignment.
     */
    protected HPos getAlignment( Node child )
    {
        HPos result = (HPos)
                getProperty( child, HPos.class );
        if ( result != null )
            return result;

        return HPos.CENTER;
    }

    protected final void setProperty(Node node, Object key, Object value)
    {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    protected final Object getProperty(Node node, Object key)
    {
        if (node.hasProperties()) {
            return node.getProperties().get(key);
        }
        return null;
    }
}

/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.screens.sdk;

import java.util.Objects;
import java.util.Optional;

import org.smack.util.EnumArray;
import org.smack.util.collections.MultiMap;

import de.michab.app.mmt.lab.BasePane;
import de.michab.app.mmt.util.Geometry;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.util.Pair;

/**
 * A layout pane used for all application screens.
 *
 * @author Michael Binz
 */
public class MmtLayoutPane extends
    BasePane<Pair<MmtLayoutPane.Column,MmtLayoutPane.Row>>
{
    private final int ONE_CM =
            Geometry.cmToPx( 1.0f );
    private final MultiMap<Row, Column, Node> _comps =
            new MultiMap<>();
    private final ColumnTension _columnAlignment;

    /**
     * The allowed component rows.
     */
    public enum Row {
        /**
         * The screen header.  This is placed on screen top using all allowed
         * width. Only a single SCREEN_HEAD component is allowed at the
         * CENTER column.
         */
        SCREEN_HEAD, HEAD, CENTER, FOOT, BUTTON_NEXT, BUTTON_PREV };
    public enum Column { START, CENTER, END };
    /**
     * The column tension.
     */
    public enum ColumnTension {
        /**
         * Center the components in their column.
         */
        CENTER,
        /**
         * Format the components with a tension towards the
         * center component.
         */
        TO_INNER };

    /**
     * The row heights of the center column.
     */
    private final EnumArray<Row, Double> rowHeightsCenter =
            new EnumArray<>( Row.class, 0.0 );
    /**
     * The row heights for both outer columns.
     */
    private final EnumArray<Row, Double> rowHeightsOuter =
            new EnumArray<>( Row.class, 0.0 );

    /**
     * The respective column's width.
     */
    private final EnumArray<Column, Double> colWidths =
            new EnumArray<>( Column.class, 0.0 )
    {
        @Override
        public void set( Column c, Double width )
        {
            if ( width < 0 )
                width = 0.;
            super.set( c, width );
        }
    };
    /**
     * The respective column's x position.
     */
    private final EnumArray<Column, Double> colX =
            new EnumArray<>( Column.class, 0.0 );

    /**
     * Create an instance.
     */
    public MmtLayoutPane( ColumnTension columnAlignment )
    {
        _columnAlignment = columnAlignment;
    }

    @Override
    protected void positionChildren()
    {
        adjustDimensions();

        adjustPositions();

        for ( Row row : Row.values() )
            for ( Column column : Column.values() )
                getComponentOptional( column, row ).ifPresent(
                        (s) -> setBounds( s, _cellSizes.get( row, column ) ) );
    }

    public Node getComponentAt( Column col, Row row )
    {
        return _comps.get( row, col );
    }

    /**
     * Returns a component for the passed layout coordinates.
     *
     * @param col Layout x coordinate.
     * @param row The layout y coordinate.
     * @return An optional.
     */
    private Optional<Node> getComponentOptional( Column col, Row row )
    {
        return Optional.ofNullable( _comps.get( row, col ) );
    }

    /**
     * @param row The row to process.
     * @param columns If not passed, iterate across all columns.
     * @return The maximum preferred height of all nodes in a row.
     */
    private double computeRowHeight( Row row, Column ... columns )
    {
        if ( columns.length == 0 )
            columns = Column.values();

        double result = 0;

        for ( Column c : columns )
        {
            Node comp = _comps.get( row, c );
            if ( comp == null )
                continue;

            result = Math.max(
                    comp.prefHeight( -1 ),
                    result );
        }

        return result;
    }


    /**
     * Initializes the column and row widths and heights.
     */
    private void adjustDimensions()
    {
        // Compute the center width maximum.
        for ( Row r : new Row[]{Row.HEAD,Row.CENTER,Row.FOOT,Row.BUTTON_NEXT,Row.BUTTON_PREV} )
        {
            Node comp = getComponentAt( Column.CENTER, r );
            if ( comp == null )
                continue;

            double newWidth = Math.max(
                    colWidths.get( Column.CENTER ),
                    comp.prefWidth( -1 ) );

            colWidths.set( Column.CENTER, newWidth );
        }

        // Limit the center column width.
        double limit = (getWidth() * (3.0/5.0));
        if ( colWidths.get( Column.CENTER ) > limit )
            colWidths.set( Column.CENTER, limit );

        // Get the available width from the parent.
        double remainingWidth =
                getWidth() -
                colWidths.get( Column.CENTER );

        colWidths.set( Column.START, remainingWidth/2 );
        colWidths.set( Column.END, remainingWidth/2 );

        // Make a gap of one cm between the columns, using space from
        // the outer columns.
        colWidths.set(
                Column.START,
                colWidths.get( Column.START ) - ONE_CM );
        colWidths.set(
                Column.END,
                colWidths.get( Column.END ) - ONE_CM );

        // Compute the column x-positions.
        colX.set( Column.START,
                0.0 );
        colX.set( Column.CENTER,
                (getWidth() - colWidths.get( Column.CENTER ))
                / 2 );
        colX.set( Column.END,
                getWidth() -
                colWidths.get( Column.END ) );

        // Heights.
        for ( Row r : Row.values() )
        {
            double rowHeight = computeRowHeight( r );
            rowHeightsCenter.set( r, rowHeight );
            rowHeightsOuter.set( r, rowHeight );
        }

        // Limit the center row height.
        double centerRowHeightLimit = getHeight();
        if ( centerRowHeightLimit < rowHeightsCenter.get( Row.CENTER ) )
            rowHeightsCenter.set( Row.CENTER, centerRowHeightLimit );

        // Whaddah?
        rowHeightsOuter.set( Row.CENTER,
                computeRowHeight( Row.CENTER, Column.START, Column.END ) );
    }

    private final MultiMap<Row, Column, Rectangle2D> _cellSizes =
            new MultiMap<>();

    /**
     * Adjusts the component positions.
     */
    private void adjustPositions()
    {
        // Baselines.
        double headBaseline = getPercentY( 0.33f );
        double centerBaseline = getPercentY( 0.50f );
        double buttonBaseline = getPercentY( 0.80f );

        for ( Column c : new Column[]{Column.START, Column.END, Column.CENTER} )
        {
            final double x =
                    colX.get( c );
            final double w =
                    colWidths.get( c );

            {
                double h = getRowHeight( Row.CENTER, c );
                double y = centerBaseline - Math.round( h / 2.0f );
                _cellSizes.put( Row.CENTER, c, new Rectangle2D( x, y, w, h ) );
            }

            {
                double h = getRowHeight( Row.HEAD, c );
                double y = _cellSizes.get( Row.CENTER, c ).getMinY() - h;
                _cellSizes.put( Row.HEAD, c, new Rectangle2D( x, y, w, h ) );
            }

            // Special handling for SCREEN_HEAD.  This is placed across the
            // full line.  Note that we ensure that only a single SCREEN_HEAD
            // component was added.
            {
                double h =
                        getRowHeight( Row.SCREEN_HEAD, c );
                double y =
                        Math.min( _cellSizes.get( Row.HEAD, c ).getMinY(), headBaseline ) -
                        h;

                _cellSizes.put( Row.SCREEN_HEAD, c, new Rectangle2D(
                        0,
                        y,
                        getWidth(),
                        h ) );
            }

            {
                double h = getRowHeight( Row.FOOT, c );
                double y =
                        _cellSizes.get( Row.CENTER, c ).getMinY() +
                        _cellSizes.get( Row.CENTER, c ).getHeight();
                _cellSizes.put( Row.FOOT, c, new Rectangle2D( x, y, w, h ) );
            }

            {
                double h = getRowHeight( Row.BUTTON_NEXT, c );
                double y = Math.max(
                        _cellSizes.get( Row.FOOT, c ).getMinY() + _cellSizes.get( Row.FOOT, c ).getHeight(),
                        buttonBaseline );
                _cellSizes.put( Row.BUTTON_NEXT, c, new Rectangle2D( x, y, w, h ) );

            }
            {
                double h = getRowHeight( Row.BUTTON_PREV, c );
                double y = _cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() + _cellSizes.get( Row.BUTTON_NEXT, c ).getHeight();
                _cellSizes.put( Row.BUTTON_PREV, c, new Rectangle2D( x, y, w, h ) );
            }

            // Button special handling. If the buttons in the center column
            // are below the button border, then these get moved to the
            // start (previous) and end (next) column.

            if ( c != Column.CENTER )
                continue;

            assert _cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() >= buttonBaseline;

            if ( _cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() == buttonBaseline )
                continue;

            // Check if we can move up the whole show.
            // We had a defect in the test screen,moving the wheel to top of screen,
            // thus we perform this if more than one component is contained.
            // TODO: Maybe the whole move-up can be removed?
            if ( _comps.getValues().size() > 1 && (_cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() - buttonBaseline) < _cellSizes.get( Row.SCREEN_HEAD, c ).getMinY() )
            {
                moveColumnUp( c, _cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() - buttonBaseline );
                assert _cellSizes.get( Row.BUTTON_NEXT, c ).getMinY() == buttonBaseline;
            }
            else // ( nextComp.getY() > buttonY )
            {
                // Do NOT move the component in the component
                // raster.  Instead compute a temporary position.
                moveComponent(
                        c,
                        Row.BUTTON_NEXT,
                        Column.END,
                        Row.BUTTON_NEXT );
                moveComponent(
                        c,
                        Row.BUTTON_PREV,
                        Column.START,
                        Row.BUTTON_NEXT );
            }
        }

        for ( Row row : Row.values() )
            for ( Column column : Column.values() )
                getComponentOptional( column, row ).ifPresent(
                        (s) -> setBounds( s, _cellSizes.get( row, column ) ) );
    }

    private double getRowHeight( Row r, Column c )
    {
        if ( Column.CENTER == c )
            return rowHeightsCenter.get( r );

        return rowHeightsOuter.get( r );
    }

    /**
     * Get an y value based on a percentual division of the root pane.
     *
     * @param parent The parent container used to access the root pane
     * and the parent component.
     * @param percent A percent value. 1.0f means the bottom end of the
     * container, 0.0f means delta above the container.
     * @return An integer y value, maybe less than zero.
     */
    private double getPercentY( float percent )
    {
        double rootHeight =
                getHeight();
        double result =
                percent * rootHeight;

        return result;
    }

    /**
     * Move a component.
     *
     * @param fromCol
     * @param fromRow
     * @param toCol
     * @param toRow
     */
    private void moveComponent(
            Column fromCol,
            Row fromRow,
            Column toCol,
            Row toRow )
    {
        // Get the target rectangle.
        Rectangle2D r = Objects.requireNonNull(
                _cellSizes.get( toRow, toCol ) );
        {
            Node component = _comps.get( fromRow, fromCol );
            if ( component != null )
            {
                if ( _columnAlignment == ColumnTension.TO_INNER )
                {
                    if ( toCol == Column.START  )
                        setAlignment( component, HPos.RIGHT );
                    else if ( toCol == Column.END  )
                        setAlignment( component, HPos.LEFT );
                    else
                        setAlignment( component, HPos.CENTER );
                }
            }
        }

        _cellSizes.put( fromRow, fromCol, r );
    }

    private double moveColumnUp( Column c, double distance )
    {
        Rectangle2D rect = null;

        for ( Row r : Row.values() )
        {
            rect = _cellSizes.get( r, c );

            rect = new Rectangle2D(
                    rect.getMinX(),
                    rect.getMinY() - distance,
                    rect.getWidth(),
                    rect.getHeight() );
            _cellSizes.put( r, c, rect );
        }

        return distance;
    }

    /**
     * Add a component.
     *
     * @param component The component.
     * @param c The components column.
     * @param r The component's row.
     */
    public void addLayoutComponent(
            Node component,
            Column c, Row r )
    {
        {
            HPos alignment = HPos.CENTER;
            if ( _columnAlignment == ColumnTension.TO_INNER )
                if ( c == Column.START )
                    alignment = HPos.RIGHT;
                else if ( c == Column.END )
                    alignment = HPos.LEFT;

            setAlignment(
                    component,
                    alignment );
        }

        // CENTER_HEAD components must only be placed in the CENTER column.
        if ( r == Row.SCREEN_HEAD && c != Column.CENTER )
            throw new IllegalArgumentException();

        if ( _comps.get( r, c ) != null )
            throw new IllegalArgumentException( "Cell already used." );

        _comps.put(
                r,
                c,
                component );
        getChildren().add(
                component );
    }
    @Override
    public void addLayoutComponent(
            Node component,
            Pair<Column,Row> c )
    {
        addLayoutComponent( component, c.getKey(), c.getValue() );
    }

    public void removeLayoutComponent( Node comp )
    {
        getChildren().remove( comp );

        for ( Column x : Column.values() ) for ( Row y : Row.values() )
        {
            if ( comp == _comps.get( y, x ) )
            {
                _comps.remove( y, x );
                return;
            }
        }
    }
}

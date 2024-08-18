/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container for pdf elements.
 *
 * @author Michael Binz
 */
abstract class PdfElementContainer
{
    /**
     * The list of page elements that are part of all pages, e.g headers and
     * footers.
     */
    private final List<PdfPageElement> _commonElements =
            new ArrayList<>();

    /**
     * Add a common element that is to be shown on all pages.
     *
     * @param element The element to add.
     */
    public void add( PdfPageElement element )
    {
        _commonElements.add( element );
    }

    /**
     * Remove a common page element.
     *
     * @param element The element to remove.
     */
    public void remove( PdfPageElement element )
    {
        _commonElements.remove( element );
    }

    /**
     * Get the common page elements.
     *
     * @return The common page elements in order of addition.
     */
    public List<PdfPageElement> get()
    {
        return Collections.unmodifiableList( _commonElements );
    }
}

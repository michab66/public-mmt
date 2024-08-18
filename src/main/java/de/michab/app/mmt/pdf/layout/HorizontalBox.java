/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf.layout;

import java.util.List;

import de.michab.app.mmt.pdf.PdfPageElement;

/**
 *
 *
 * @author Michael Binz
 */
public class HorizontalBox extends Box
{

    public HorizontalBox( PdfPageElement.Pos anchor, PdfPageElement ... elements )
    {
        super( elements );
        setAnchor( anchor );
    }

    public HorizontalBox( PdfPageElement ... elements )
    {
        super( elements );
    }

    @Override
    protected void doLayout( List<PdfPageElement> elements )
    {
        // Pass 1: Calculate the component size.
        {
            float newWidth =
                    0.0f;
            float newHeight =
                    0.0f;

            for ( PdfPageElement c : elements )
            {
                newWidth +=
                    c.getWidth() ;
                newHeight =
                    Math.max( newHeight, c.getHeight() );
            }

            newWidth +=
                    (elements.size()-1) * getGap();

            setSize(
                    newWidth,
                    newHeight );
        }

        // Pass 2: Layout the components.
        {
            float currentX = 0;

            for ( PdfPageElement c : elements )
            {
                float currentY;

                switch ( getAnchor() )
                {
                case TOP:
                    currentY = 0;
                    break;
                case BOTTOM:
                    currentY = getHeight() - c.getHeight();
                    break;
                case CENTER:
                    currentY = (getHeight() - c.getHeight()) / 2.0f;
                    break;
                default:
                    throw new AssertionError( getAnchor() );
                }

                c.setPosition( currentX, currentY );

                currentX += c.getWidth() + getGap();
            }
        }
    }
}

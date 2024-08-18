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
public class VerticalBox extends Box
{
    public VerticalBox( PdfPageElement.Pos anchor, PdfPageElement ... elements )
    {
        super( elements );
        setAnchor( anchor );
    }

    public VerticalBox( PdfPageElement ... elements )
    {
        super( elements );
    }

//    public VerticalBox( float aLineX, float topLineY, float gap)
//    {
//        super( gap );
//        setPosition( aLineX, topLineY );
//    }

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
                newWidth =
                    Math.max( newWidth, c.getWidth() );
                newHeight +=
                    c.getHeight();
            }

            newHeight +=
                    (elements.size()-1) * getGap();

            setSize(
                    newWidth,
                    newHeight );
        }

        // Pass 2: Layout the components.
        {
            float currentY = 0;

            for ( PdfPageElement c : elements )
            {
                float currentX;

                switch ( getAnchor() )
                {
                case START:
                    currentX = 0;
                    break;
                case END:
                    currentX = getWidth() - c.getWidth();
                    break;
                case CENTER:
                    currentX = (getWidth() - c.getWidth()) / 2.0f;
                    break;
                default:
                    throw new AssertionError( getAnchor() );
                }

                c.setPosition( currentX, currentY );

                currentY += c.getHeight() + getGap();
            }
        }
    }
}

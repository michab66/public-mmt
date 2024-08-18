/* sehsystem/mmt2
 *
 * Unpublished work.
 * Copyright © 2015-2024 Michael G. Binz
 */
package de.michab.app.mmt.pdf;

/**
 * Pdf utility operations.
 *
 * @author Michael Binz
 */
public final class PdfUtil
{
    /**
     * Convert cm to pt (1/72in).
     *
     * @param cm Centimeter.
     * @return pt.
     */
    public static float cmToPt( float cm )
    {
         return cm / 2.54f * 72.0f;
    }

    private PdfUtil()
    {
        throw new AssertionError();
    }
}

package com.aluxian.drizzle.recycler.adapters;

import android.support.v7.graphics.Palette;

/**
 * Interface implemented by ViewHolders that wish to be notified of style changes.
 */
public interface MultiTypeStyleable {

    /**
     * Called when the style changes. The item should apply the new colours to its views.
     *
     * @param swatch The new Swatch of colours.
     */
    public void setColors(Palette.Swatch swatch);

}

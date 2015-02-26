package com.aluxian.drizzle.adapters;

import android.support.v7.graphics.Palette;

public interface AdapterHeaderListener {

    /**
     * Called after the header view is loaded.
     *
     * @param swatch The generated colour swatch for the shot preview.
     * @param height The height of the entire header view.
     */
    public void onHeaderLoaded(Palette.Swatch swatch, int height);

}

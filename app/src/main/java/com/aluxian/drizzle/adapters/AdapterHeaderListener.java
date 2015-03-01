package com.aluxian.drizzle.adapters;

import com.aluxian.drizzle.utils.UberSwatch;

public interface AdapterHeaderListener {

    /**
     * Called after the header view is loaded.
     *
     * @param swatch The generated colours for the shot preview.
     * @param height The height of the entire header view.
     */
    void onHeaderLoaded(UberSwatch swatch, int height);

}

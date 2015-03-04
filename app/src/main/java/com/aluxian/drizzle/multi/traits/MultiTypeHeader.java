package com.aluxian.drizzle.multi.traits;

import com.aluxian.drizzle.utils.UberSwatch;

public interface MultiTypeHeader {

    /**
     * Listener for the state of the header item in adapters.
     */
    interface StateListener {

        /**
         * Called after the header view is loaded.
         *
         * @param swatch The generated colours for the shot preview.
         * @param height The height of the entire header view.
         */
        void onHeaderLoaded(UberSwatch swatch, int height);

    }

}

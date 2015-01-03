package com.aluxian.drizzle.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aluxian.drizzle.R;

public class IntroFragment extends Fragment {

    /** The callbacks instance (the Activity). */
    private Callbacks mCallbacks;

    // TODO: Start downloading items so they load instantly when the user exits the intro screen
    // TODO: Preload sign in webview

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        for (int id : new int[]{R.id.btn_sign_in, R.id.btn_sign_up, R.id.btn_skip}) {
            view.findViewById(id).setOnClickListener(v -> mCallbacks.onIntroButtonClicked(id));
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallbacks = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement IntroFragment.Callbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface Callbacks {

        /**
         * Called when a button is clicked.
         *
         * @param id The id of the clicked button.
         */
        void onIntroButtonClicked(int id);

    }

}

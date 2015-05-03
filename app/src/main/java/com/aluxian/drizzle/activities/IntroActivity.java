package com.aluxian.drizzle.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.aluxian.drizzle.Keys;
import com.aluxian.drizzle.R;

public class IntroActivity extends Activity {

    // TODO: Start downloading items so they load instantly when the user exits the intro screen
    // TODO: Preload sign in webview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        findViewById(R.id.btn_sign_in).setOnClickListener((v) -> {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });

        findViewById(R.id.btn_skip).setOnClickListener((v) -> finish());
        findViewById(R.id.btn_sign_up).setOnClickListener((v) ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://dribbble.com/signup"))));

        // Don't show this activity again when the app starts
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(Keys.INTRO_SHOWN, true).apply();
    }

}

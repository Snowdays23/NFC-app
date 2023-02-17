package it.snowdays.snowdays23.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.util.platform.Prefs;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.exists(this, SDApp.Constants.Prefs.SESSION)) {
            SDRestApi.setAccessToken(Prefs.get(this, SDApp.Constants.Prefs.SESSION));
            SDRestApi.setRefreshToken(Prefs.get(this, SDApp.Constants.Prefs.SESSION_REFRESH));
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
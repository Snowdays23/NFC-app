package it.snowdays.snowdays23.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Event;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.service.response.RestResponse;
import it.snowdays.snowdays23.util.platform.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity implements Callback<Event> {

    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.toolbar_create_event_activity_done) {
                startLoading();
                getSnowdaysTrackService().createEvent(new Event(
                        ((EditText) findViewById(R.id.activity_create_event_name)).getText().toString(),
                        ((EditText) findViewById(R.id.activity_create_event_description)).getText().toString(),
                        String.valueOf(getResources().getIdentifier("ic_flag", "drawable", getPackageName())),
                        ((SwitchMaterial) findViewById(R.id.activity_create_event_only_participants)).isChecked()
                )).enqueue(this);
            }
            return true;
        });
    }

    private void startLoading() {
        loadingDialog = buildLoadingDialog();
        loadingDialog.show();
    }

    private void stopLoading() {
        loadingDialog.dismiss();
        loadingDialog = null;
    }

    private AlertDialog buildLoadingDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login_loading)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private static SDTrackService getSnowdaysTrackService() {
        return SDRestApi.getSDTrackingService();
    }

    @Override
    public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
        stopLoading();
        if (response.isSuccessful()) {
            Toast.makeText(this, R.string.activity_create_event_success, Toast.LENGTH_SHORT).show();
            finish();
        } else if (response.code() == 401) {
            SDRestApi.refreshTokenAndReplicate(accessToken -> {
                Prefs.put(this, SDApp.Constants.Prefs.SESSION, accessToken);
                SDRestApi.setAccessToken(accessToken);
                call.clone().enqueue(this);
            }, exception -> startActivity(new Intent(this, LoginActivity.class)));
        } else {
            Toast.makeText(this,
                    R.string.error_part_by_uid_bad_request,
                        Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
        stopLoading();
        Toast.makeText(this,
                R.string.error_part_by_uid_bad_request,
                    Toast.LENGTH_SHORT).show();
    }
}
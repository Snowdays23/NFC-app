package it.snowdays.snowdays23.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.service.SDLoginService;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.request.LoginRequest;
import it.snowdays.snowdays23.service.response.LoginResponse;
import it.snowdays.snowdays23.util.platform.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameView = findViewById(R.id.activity_login_username);
        final EditText passwordView = findViewById(R.id.activity_login_password);
        final MaterialButton submitButton = findViewById(R.id.activity_login_submit);
        submitButton.setOnClickListener(v -> {
            if (usernameView.getText().length() > 0 || passwordView.getText().length() > 0) {
                startLoading();
                getSDLoginService().login(new LoginRequest(
                        usernameView.getText().toString(),
                        passwordView.getText().toString()
                )).enqueue(this);
            }
        });
    }

    @Override
    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        stopLoading();

        final LoginResponse loginResponse = response.body();
        if (response.isSuccessful()) {
            Objects.requireNonNull(loginResponse);
            final String accessToken = loginResponse.getAccessToken();
            final String refreshToken = loginResponse.getRefreshToken();
            if (accessToken == null || refreshToken == null) {
                Toast.makeText(this, R.string.activity_login_invalid_response, Toast.LENGTH_SHORT).show();
                return;
            }
            Prefs.put(this, SDApp.Constants.Prefs.SESSION, accessToken);
            Prefs.put(this, SDApp.Constants.Prefs.SESSION_REFRESH, refreshToken);
            SDRestApi.setAccessToken(accessToken);
            SDRestApi.setRefreshToken(refreshToken);
            Toast.makeText(this,
                    R.string.activity_login_success, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } else if (response.code() == 401) {
            Toast.makeText(this,
                    R.string.activity_login_wrong_credentials, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    R.string.activity_login_request_failure, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        stopLoading();
        Toast.makeText(this, R.string.activity_login_request_failure, Toast.LENGTH_SHORT).show();
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

    private static SDLoginService getSDLoginService() {
        return SDRestApi.getSDLoginService();
    }
}
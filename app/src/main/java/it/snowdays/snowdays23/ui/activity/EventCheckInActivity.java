package it.snowdays.snowdays23.ui.activity;

import android.animation.Animator;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Event;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.service.response.RestResponse;
import it.snowdays.snowdays23.util.platform.NfcUtils;
import it.snowdays.snowdays23.util.platform.Prefs;
import it.snowdays.snowdays23.util.ui.Snacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventCheckInActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TextView nameView;
    private TextView descriptionView;
    private ImageView iconView;
    private TextView checkedInView;
    private TextView leftView;
    private TextView expectedView;

    private SwipeRefreshLayout swipeView;
    private View infoContainer;
    private View statusContainer;
    private TextView statusView;
    private LottieAnimationView statusAnimationView;

    private AlertDialog loadingDialog;
    private NfcAdapter nfcAdapter;
    private Event selectedEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_check_in);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        selectedEvent = getIntent().getParcelableExtra("event");
        if (selectedEvent == null) {
            Toast.makeText(this, R.string.activity_event_check_in_no_event, Toast.LENGTH_SHORT).show();
            finish();
        }

        nameView =  findViewById(R.id.activity_event_check_in_name);
        descriptionView = findViewById(R.id.activity_event_check_in_description);
        iconView = findViewById(R.id.activity_event_check_in_icon);
        checkedInView = findViewById(R.id.activity_event_check_in_checked_in);
        leftView = findViewById(R.id.activity_event_check_in_left);
        expectedView = findViewById(R.id.activity_event_check_in_expected);

        swipeView = findViewById(R.id.refresh);
        swipeView.setOnRefreshListener(this);
        infoContainer = findViewById(R.id.activity_event_check_in_info_wrapper);
        statusContainer = findViewById(R.id.activity_event_check_in_status_wrapper);
        statusView = findViewById(R.id.activity_event_check_in_status);
        statusAnimationView = findViewById(R.id.activity_event_check_in_status_animation);

        updateEvent();
    }

    @Override
    public void onRefresh() {
        getSnowdaysTrackService().getEvent(selectedEvent.getSlug())
                .enqueue(new ReloadEventResponseCallback());
    }

    private void updateEvent() {
        nameView.setText(selectedEvent.getName());
        descriptionView.setText(selectedEvent.getDescription());
        iconView.setImageDrawable(ResourcesCompat.getDrawable(
                getResources(), Integer.parseInt(selectedEvent.getIcon()), null));
        checkedInView.setText(String.valueOf(selectedEvent.getCheckedIn()));
        leftView.setText(String.valueOf(selectedEvent.getLeft()));
        expectedView.setText(String.valueOf(selectedEvent.getExpected()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        final PendingIntent onRead = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        ensureNfcAdapter().enableForegroundDispatch(this, onRead, new IntentFilter[] {
                IntentFilter.create(NfcAdapter.ACTION_TAG_DISCOVERED, "*/*"),
                IntentFilter.create(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*"),
                IntentFilter.create(NfcAdapter.ACTION_NDEF_DISCOVERED, "*/*")
        }, new String[][] {
                new String[] { MifareUltralight.class.getName() }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            if (id != null) {
                String idString = NfcUtils.toHexString(id);
                Log.d("onTagScanned", "id: " + idString);
                getSnowdaysTrackService().checkIn(
                        selectedEvent.getSlug(), idString)
                            .enqueue(new CheckInResponseCallback());
            }
        }
    }

    private NfcAdapter ensureNfcAdapter() {
        if (nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return nfcAdapter;
    }

    private static SDTrackService getSnowdaysTrackService() {
        return SDRestApi.getSDTrackingService();
    }

    private void startLoading() {
        loadingDialog = buildLoadingDialog();
        loadingDialog.show();
    }

    private void stopLoading() {
        swipeView.setRefreshing(false);
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }

    private AlertDialog buildLoadingDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login_loading)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void animateCheckInOutcome(String animationAsset) {
        infoContainer.animate().cancel();
        statusAnimationView.cancelAnimation();
        statusContainer.animate().cancel();
        infoContainer.animate().alpha(0.f)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {
                    infoContainer.setVisibility(View.GONE);
                    infoContainer.setAlpha(1.f);

                    statusContainer.setVisibility(View.VISIBLE);
                    statusAnimationView.setAnimation(animationAsset);
                    statusAnimationView.setSpeed(1.5f);
                    statusAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            statusContainer.animate().alpha(0.f)
                                    .setStartDelay(500L)
                                    .setInterpolator(new LinearInterpolator())
                                    .withEndAction(() -> {
                                        statusContainer.setVisibility(View.GONE);
                                        statusContainer.setAlpha(1.f);
                                        infoContainer.animate().alpha(1.f)
                                                .setInterpolator(new LinearInterpolator())
                                                .withStartAction(() -> {
                                                    infoContainer.setAlpha(0.f);
                                                    infoContainer.setVisibility(View.VISIBLE);
                                                }).start();
                                    }).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    statusAnimationView.playAnimation();
                }).start();
    }

    private void animateCheckInSuccess() {
        statusView.setText(R.string.activity_event_check_in_success);
        animateCheckInOutcome("tick.json");
    }

    private void animateCheckInFailure(String message) {
        statusView.setText(message);
        animateCheckInOutcome("cross.json");
    }

    private class CheckInResponseCallback implements Callback<RestResponse> {
        @Override
        public void onResponse(@NonNull Call<RestResponse> call, @NonNull Response<RestResponse> response) {
            final RestResponse body = response.body();
            if (response.isSuccessful()) {
                animateCheckInSuccess();
            } else if (response.errorBody() != null) {
                try {
                    RestResponse rr = SDRestApi.gson.fromJson(
                            response.errorBody().string(), RestResponse.class);
                    if (rr != null && rr.getStatus() != null) {
                        animateCheckInFailure(rr.getStatus());
                    } else {
                        animateCheckInFailure(getString(R.string.activity_event_check_in_invalid_response));
                    }
                } catch (IOException e) {
                    animateCheckInFailure(getString(R.string.activity_event_check_in_invalid_response));
                }
            }
            onRefresh();
        }

        @Override
        public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
            animateCheckInFailure(getString(R.string.activity_event_check_in_failed_request));
        }
    }

    private class ReloadEventResponseCallback implements Callback<Event> {
        @Override
        public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
            stopLoading();

            final Event event = response.body();
            if (response.isSuccessful()) {
                selectedEvent = event;
                updateEvent();
            } else if (response.code() == 401) {
                SDRestApi.refreshTokenAndReplicate(accessToken -> {
                    Prefs.put(EventCheckInActivity.this, SDApp.Constants.Prefs.SESSION, accessToken);
                    SDRestApi.setAccessToken(accessToken);
                    call.clone().enqueue(this);
                }, exception -> startActivity(new Intent(EventCheckInActivity.this, LoginActivity.class)));
            } else {
                Snacks.normal(findViewById(R.id.activity_event_check_in),
                        getString(R.string.activity_event_check_in_update_failed),
                            getString(R.string.activity_event_check_in_update_retry),
                            v -> {
                                startLoading();
                                getSnowdaysTrackService().getEvent(selectedEvent.getSlug())
                                        .enqueue(new ReloadEventResponseCallback());
                            });
            }
        }

        @Override
        public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
            stopLoading();
            Snacks.normal(findViewById(R.id.activity_event_check_in),
                    getString(R.string.activity_event_check_in_update_failed),
                        getString(R.string.activity_event_check_in_update_retry),
                            v -> {
                                startLoading();
                                getSnowdaysTrackService().getEvent(selectedEvent.getSlug())
                                        .enqueue(new ReloadEventResponseCallback());
                            });
        }
    }

}

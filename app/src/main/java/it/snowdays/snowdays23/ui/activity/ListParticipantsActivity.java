package it.snowdays.snowdays23.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.SDApiService;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.service.response.RestResponse;
import it.snowdays.snowdays23.ui.adapter.ParticipantsListAdapter;
import it.snowdays.snowdays23.util.platform.NfcUtils;
import it.snowdays.snowdays23.util.platform.Prefs;
import it.snowdays.snowdays23.util.ui.Snacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListParticipantsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeView;
    private View mLoadingView;
    private RecyclerView mListView;
    private View mSnackbarContainer;

    private NfcAdapter mNfcAdapter;
    private ParticipantsListAdapter adapter;

    private Participant assignee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_participants);

        mLoadingView = findViewById(R.id.dim);
        mListView = findViewById(R.id.list);
        mSnackbarContainer = findViewById(R.id.activity_list_participants);
        mSwipeView = findViewById(R.id.activity_list_participants_inner);
        mSwipeView.setOnRefreshListener(this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if ("assign".equals(getIntent().getStringExtra("mode"))) {
            toolbar.setTitle(R.string.activity_list_participants_assign_title);
        }

        final SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.setSearchQuery(newText);
                }
                return true;
            }
        });

        onRefresh();
    }

    @Override
    public void onRefresh() {
        startLoading();
        getSnowdaysApiService().getAllParticipants()
                .enqueue(new AllParticipantsListResponseCallback());
    }

    private void startLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        mSwipeView.setRefreshing(false);
        mLoadingView.animate().alpha(0.f)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {
                    mLoadingView.setVisibility(View.GONE);
                    mLoadingView.setAlpha(1.f);
                }).start();
    }

    private AlertDialog buildBraceletConfirmationDialog(final Participant participant,
                                                        final String uid) {
        this.assignee = participant;
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_bracelet_confirmation)
                .create();
        dialog.setOnShowListener(dialog1 -> {
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
        });
        dialog.setOnDismissListener(dialog1 -> ensureNfcAdapter().disableForegroundDispatch(this));
        return dialog;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            if (id != null && assignee != null) {
                String idString = NfcUtils.toHexString(id);
                Log.d("onTagScanned", "id: " + idString);
                startLoading();
                getSnowdaysTrackingService().assignBraceletToParticipant(
                        assignee.getId(), idString
                ).enqueue(new AssignBraceletResponseCallback());
            }
        }
    }

    private NfcAdapter ensureNfcAdapter() {
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return mNfcAdapter;
    }

    private static SDApiService getSnowdaysApiService() {
        return SDRestApi.getSdApiService();
    }

    private static SDTrackService getSnowdaysTrackingService() {
        return SDRestApi.getSDTrackingService();
    }

    private class AllParticipantsListResponseCallback implements Callback<List<Participant>> {
        @Override
        public void onResponse(@NonNull Call<List<Participant>> call, @NonNull Response<List<Participant>> response) {
            stopLoading();
            if (response.isSuccessful()) {
                mListView.setLayoutManager(new LinearLayoutManager(
                        ListParticipantsActivity.this,
                            LinearLayoutManager.VERTICAL, false));
                adapter = new ParticipantsListAdapter(response.body());
                adapter.setSelectedListener(participant -> {
                    if ("assign".equals(getIntent().getStringExtra("mode"))) {
                        buildBraceletConfirmationDialog(participant,
                                getIntent().getStringExtra("uid")).show();
                    } else {
                        startActivity(new Intent(ListParticipantsActivity.this,
                                ParticipantDetailActivity.class)
                                    .putExtra("participant", participant));
                    }
                });
                mListView.setAdapter(adapter);

            } else if (response.code() == 401) {
                SDRestApi.refreshTokenAndReplicate(accessToken -> {
                    Prefs.put(ListParticipantsActivity.this, SDApp.Constants.Prefs.SESSION, accessToken);
                    SDRestApi.setAccessToken(accessToken);
                    call.clone().enqueue(this);
                }, exception -> startActivity(new Intent(ListParticipantsActivity.this, LoginActivity.class)));
            } else {
                Snacks.normal(mSnackbarContainer, getString(R.string.error_failed_loading),
                        getString(R.string.error_failed_loading_retry), v -> onRefresh());
            }
        }

        @Override
        public void onFailure(@NonNull Call<List<Participant>> call, @NonNull Throwable t) {
            Snacks.normal(mSnackbarContainer, getString(R.string.error_failed_loading),
                    getString(R.string.error_failed_loading_retry), v -> onRefresh());
        }
    }

    private class AssignBraceletResponseCallback implements Callback<RestResponse> {
        @Override
        public void onResponse(@NonNull Call<RestResponse> call, @NonNull Response<RestResponse> response) {
            stopLoading();
            Log.d("Assign", "onResponse: status code " + response.code());
            if (response.body() == null) {
                Toast.makeText(ListParticipantsActivity.this,
                        R.string.activity_list_participants_assign_error,
                            Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(ListParticipantsActivity.this,
                    response.body().getStatus(), Toast.LENGTH_SHORT).show();
            if (response.isSuccessful()) {
                finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
            Log.d("Assign", "onFailure", t);
            stopLoading();
            Toast.makeText(ListParticipantsActivity.this,
                    R.string.activity_list_participants_assign_error,
                        Toast.LENGTH_SHORT).show();
        }
    }
}
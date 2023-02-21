package it.snowdays.snowdays23.ui.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import it.snowdays.snowdays23.ui.activity.ListPeopleActivity;
import it.snowdays.snowdays23.ui.activity.LoginActivity;
import it.snowdays.snowdays23.ui.activity.ParticipantDetailActivity;
import it.snowdays.snowdays23.ui.adapter.ParticipantsListAdapter;
import it.snowdays.snowdays23.util.platform.Prefs;
import it.snowdays.snowdays23.util.ui.Snacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListParticipantsFragment extends NfcAwareFragment
        implements SwipeRefreshLayout.OnRefreshListener, SearchAwareFragment  {

    private SwipeRefreshLayout mSwipeView;
    private View mLoadingView;
    private RecyclerView mListView;
    private View mSnackbarContainer;

    private ParticipantsListAdapter adapter;
    private Participant assignee;
    private String targetId;

    private NfcAdapter mNfcAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_participants, container, false);

        mLoadingView = rootView.findViewById(R.id.dim);
        mListView = rootView.findViewById(R.id.list);
        mSnackbarContainer = rootView.findViewById(R.id.fragment_list_participants);
        mSwipeView = rootView.findViewById(R.id.activity_list_participants_inner);
        mSwipeView.setOnRefreshListener(this);

        final Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        if ("assign".equals(requireActivity().getIntent().getStringExtra("mode"))) {
            toolbar.setTitle(R.string.activity_list_participants_assign_title);
        }

        final SearchView searchView = requireActivity().findViewById(R.id.search);
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        onRefresh();
    }

    @Override
    public void onNfcTagScanned(String id) {
        if (assignee != null) {
            if (targetId != null && !targetId.equals(id)) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.dialog_bracelet_uid_mismatch_title)
                        .setMessage(R.string.dialog_bracelet_uid_mismatch_message)
                        .setPositiveButton(R.string.OK, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                return;
            }
            startLoading();
            getSnowdaysTrackingService().assignBraceletToParticipant(
                    assignee.getId(), id
            ).enqueue(new AssignBraceletResponseCallback());
        }
    }

    @Override
    public boolean isScanning() {
        return isVisible() && this.assignee != null;
    }

    @Override
    public boolean isSearching() {
        return isVisible() && this.adapter != null;
    }

    @Override
    public void onRefresh() {
        startLoading();
        getSnowdaysApiService().getAllParticipants()
                .enqueue(new AllParticipantsListResponseCallback());
    }

    @Override
    public ParticipantsListAdapter getAdapter() {
        return adapter;
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

    private NfcAdapter ensureNfcAdapter() {
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(requireContext());
        }
        return mNfcAdapter;
    }

    private AlertDialog buildBraceletConfirmationDialog(final Participant participant,
                                                        final String uid) {
        this.assignee = participant;
        this.targetId = uid;
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_bracelet_confirmation)
                .create();
        dialog.setOnShowListener(dialog1 -> {
            final PendingIntent onRead = PendingIntent.getActivity(
                    requireContext(), 0, new Intent(requireContext(), ListPeopleActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            ensureNfcAdapter().enableForegroundDispatch(requireActivity(), onRead, new IntentFilter[] {
                    IntentFilter.create(NfcAdapter.ACTION_TAG_DISCOVERED, "*/*"),
                    IntentFilter.create(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*"),
                    IntentFilter.create(NfcAdapter.ACTION_NDEF_DISCOVERED, "*/*")
            }, new String[][] {
                    new String[] { MifareUltralight.class.getName() }
            });
        });
        dialog.setOnDismissListener(dialog1 -> {
            ensureNfcAdapter().disableForegroundDispatch(requireActivity());
            ListParticipantsFragment.this.assignee = null;
        });
        return dialog;
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
                        requireContext(),
                        LinearLayoutManager.VERTICAL, false));
                adapter = new ParticipantsListAdapter(response.body());
                adapter.setSelectedListener(participant -> {
                    if ("assign".equals(requireActivity().getIntent().getStringExtra("mode"))) {
                        buildBraceletConfirmationDialog(participant,
                                requireActivity().getIntent().getStringExtra("uid")).show();
                    } else {
                        startActivity(new Intent(requireContext(),
                                ParticipantDetailActivity.class)
                                    .putExtra("participant", participant));
                    }
                });
                mListView.setAdapter(adapter);

            } else if (response.code() == 401) {
                SDRestApi.refreshTokenAndReplicate(accessToken -> {
                    Prefs.put(requireContext(), SDApp.Constants.Prefs.SESSION, accessToken);
                    SDRestApi.setAccessToken(accessToken);
                    call.clone().enqueue(this);
                }, exception -> startActivity(new Intent(requireContext(), LoginActivity.class)));
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
                Toast.makeText(requireContext(),
                        R.string.activity_list_participants_assign_error,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(),
                    response.body().getStatus(), Toast.LENGTH_SHORT).show();
            if (response.isSuccessful()) {
                requireActivity().finish();
            }
        }

        @Override
        public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
            Log.d("Assign", "onFailure", t);
            stopLoading();
            Toast.makeText(requireContext(),
                    R.string.activity_list_participants_assign_error,
                    Toast.LENGTH_SHORT).show();
        }
    }
}

package it.snowdays.snowdays23.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.SDLoginService;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.ui.activity.ListParticipantsActivity;
import it.snowdays.snowdays23.ui.activity.LoginActivity;
import it.snowdays.snowdays23.ui.activity.ParticipantDetailActivity;
import it.snowdays.snowdays23.util.platform.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanParticipantFragment extends NfcAwareFragment {

    private View mLoadingView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_scan_participant, container, false);

        mLoadingView = rootView.findViewById(R.id.dim);

        final Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.toolbar_main_activity_all_participants) {
                startActivity(new Intent(requireContext(), ListParticipantsActivity.class));
            }
            return true;
        });

        return rootView;
    }

    public void onNfcTagScanned(String id) {
        startLoading();
        getSnowdaysTrackService().getParticipantByBraceletId(id)
                .enqueue(new GetParticipantByUidResponseCallback());
    }

    @Override
    public boolean isScanning() {
        return isVisible();
    }

    private void startLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        mLoadingView.animate().alpha(0.f)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {
                    mLoadingView.setVisibility(View.GONE);
                    mLoadingView.setAlpha(1.f);
                }).start();
    }

    private class GetParticipantByUidResponseCallback implements Callback<Participant> {
        @Override
        public void onResponse(@NonNull Call<Participant> call, @NonNull Response<Participant> response) {
            stopLoading();
            if (response.isSuccessful()) {
                startActivity(new Intent(requireContext(), ParticipantDetailActivity.class)
                        .putExtra("participant", response.body()));
            } else if (response.code() == 404) {
                final List<String> path = call.request().url().pathSegments();
                startActivity(new Intent(requireContext(), ListParticipantsActivity.class)
                        .putExtra("mode", "assign")
                        .putExtra("uid", path.get(path.size() - 1)));
                Toast.makeText(requireContext(),
                        R.string.info_part_by_uid_not_found,
                        Toast.LENGTH_SHORT).show();
            } else if (response.code() == 401) {
                SDRestApi.refreshTokenAndReplicate(accessToken -> {
                    Prefs.put(requireContext(), SDApp.Constants.Prefs.SESSION, accessToken);
                    SDRestApi.setAccessToken(accessToken);
                    call.clone().enqueue(this);
                }, exception -> startActivity(new Intent(requireContext(), LoginActivity.class)));
            } else {
                Toast.makeText(requireContext(),
                        R.string.error_part_by_uid_bad_request,
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<Participant> call, @NonNull Throwable t) {
            stopLoading();
            Toast.makeText(requireContext(),
                    R.string.error_part_by_uid_bad_request,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static SDTrackService getSnowdaysTrackService() {
        return SDRestApi.getSDTrackingService();
    }

}

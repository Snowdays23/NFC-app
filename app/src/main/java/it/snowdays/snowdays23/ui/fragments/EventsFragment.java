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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Event;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.ui.activity.CreateEventActivity;
import it.snowdays.snowdays23.ui.activity.ListParticipantsActivity;
import it.snowdays.snowdays23.ui.activity.LoginActivity;
import it.snowdays.snowdays23.ui.activity.ParticipantDetailActivity;
import it.snowdays.snowdays23.ui.adapter.EventsListAdapter;
import it.snowdays.snowdays23.util.platform.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment implements Callback<List<Event>>, SwipeRefreshLayout.OnRefreshListener {

    private View loadingView;
    private SwipeRefreshLayout swipeView;
    private RecyclerView eventsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        final FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), CreateEventActivity.class)));

        eventsList = rootView.findViewById(R.id.events);
        eventsList.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.VERTICAL, false));
        eventsList.setAdapter(new EventsListAdapter(new ArrayList<>()));

        loadingView = rootView.findViewById(R.id.dim);
        swipeView = rootView.findViewById(R.id.refresh);
        swipeView.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        startLoading();
        getSnowdaysTrackService().getEvents().enqueue(this);
    }

    private void startLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        swipeView.setRefreshing(false);
        loadingView.animate().alpha(0.f)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {
                    loadingView.setVisibility(View.GONE);
                    loadingView.setAlpha(1.f);
                }).start();
    }

    private static SDTrackService getSnowdaysTrackService() {
        return SDRestApi.getSDTrackingService();
    }

    @Override
    public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
        stopLoading();

        if (response.isSuccessful()) {
            eventsList.setAdapter(new EventsListAdapter(response.body()));
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
    public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
        stopLoading();
        Toast.makeText(requireContext(),
                R.string.error_part_by_uid_bad_request,
                Toast.LENGTH_SHORT).show();
    }
}

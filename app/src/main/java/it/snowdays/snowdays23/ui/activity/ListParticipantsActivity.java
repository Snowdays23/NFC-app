package it.snowdays.snowdays23.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SnowdaysService;
import it.snowdays.snowdays23.ui.adapter.ParticipantsListAdapter;
import it.snowdays.snowdays23.util.Snacks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListParticipantsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeView;
    private View mLoadingView;
    private RecyclerView mListView;
    private View mSnackbarContainer;

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

        onRefresh();
    }

    @Override
    public void onRefresh() {
        startLoading();
        getSnowdaysService().getAllParticipants()
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

    private static SnowdaysService getSnowdaysService() {
        return SDRestApi.getSnowdaysService();
    }

    private class AllParticipantsListResponseCallback implements Callback<List<Participant>> {
        @Override
        public void onResponse(@NonNull Call<List<Participant>> call, @NonNull Response<List<Participant>> response) {
            stopLoading();
            if (response.isSuccessful()) {
                mListView.setLayoutManager(new LinearLayoutManager(
                        ListParticipantsActivity.this,
                            LinearLayoutManager.VERTICAL, false));
                mListView.setAdapter(new ParticipantsListAdapter(response.body()));
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
}
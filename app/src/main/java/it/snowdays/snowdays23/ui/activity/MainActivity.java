package it.snowdays.snowdays23.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SnowdaysService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mLoadingView;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingView = findViewById(R.id.dim);
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
    protected void onPause() {
        super.onPause();
        ensureNfcAdapter().disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            if (id != null) {
                String idString = toHexString(id);
                Log.d("onTagScanned", "id: " + idString);
                startLoading();
                getSnowdaysService().getParticipantByBraceletId(idString)
                        .enqueue(new GetParticipantByUidResponseCallback());
            }
        }
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

    private NfcAdapter ensureNfcAdapter() {
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return mNfcAdapter;
    }

    private static SnowdaysService getSnowdaysService() {
        return SDRestApi.getSnowdaysService();
    }

    private static String toHexString(byte[] arr) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : arr) {
            builder.append(String.format("%02x", b)).append(":");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString().replace(":", "");
    }

    private class GetParticipantByUidResponseCallback implements Callback<Participant> {
        @Override
        public void onResponse(@NonNull Call<Participant> call, @NonNull Response<Participant> response) {
            stopLoading();
            if (response.isSuccessful()) {
                startActivity(new Intent(MainActivity.this, ParticipantDetailActivity.class)
                        .putExtra("participant", response.body()));
            } else if (response.code() == 404) {
                Toast.makeText(MainActivity.this,
                        R.string.info_part_by_uid_not_found,
                            Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(MainActivity.this,
                        R.string.error_part_by_uid_bad_request,
                            Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(@NonNull Call<Participant> call, @NonNull Throwable t) {
            stopLoading();
            Toast.makeText(MainActivity.this,
                    R.string.error_part_by_uid_bad_request,
                        Toast.LENGTH_SHORT).show();
        }
    }
}
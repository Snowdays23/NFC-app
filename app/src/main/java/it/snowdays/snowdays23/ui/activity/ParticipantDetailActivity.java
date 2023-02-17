package it.snowdays.snowdays23.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.SDApp;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.service.SDRestApi;
import it.snowdays.snowdays23.service.SDTrackService;
import it.snowdays.snowdays23.service.response.RestResponse;
import it.snowdays.snowdays23.util.platform.NfcUtils;
import it.snowdays.snowdays23.util.platform.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipantDetailActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private Participant mParticipant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_detail);

        final TextView firstNameView = findViewById(R.id.activity_participant_detail_first_name);
        final TextView lastNameView = findViewById(R.id.activity_participant_detail_last_name);
        final TextView dobView = findViewById(R.id.activity_participant_detail_dob);
        final TextView universityView = findViewById(R.id.activity_participant_detail_university);
        final TextView braceletIdView = findViewById(R.id.activity_participant_detail_bracelet_id);
        final ImageView glutenFreeView = findViewById(R.id.activity_participant_detail_gluten_free);
        final ImageView vegetarianView = findViewById(R.id.activity_participant_detail_vegetarian);
        final ImageView veganView = findViewById(R.id.activity_participant_detail_vegan);
        final ImageView lactoseFreeView = findViewById(R.id.activity_participant_detail_lactose_free);

        mParticipant = getIntent().getParcelableExtra("participant");

        firstNameView.setText(mParticipant.getFirstName());
        lastNameView.setText(mParticipant.getLastName());
        dobView.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(mParticipant.getDateOfBirth()));
        universityView.setText(mParticipant.getUniversity().getName());
        braceletIdView.setText(mParticipant.getBraceletId());
        if (mParticipant.getEatingHabits().isGlutenFree()) {
            glutenFreeView.setVisibility(View.VISIBLE);
        }
        if (mParticipant.getEatingHabits().isVegan()) {
            veganView.setVisibility(View.VISIBLE);
        } else if (mParticipant.getEatingHabits().isVegetarian()) {
            vegetarianView.setVisibility(View.VISIBLE);
        }
        if (mParticipant.getEatingHabits().isLactoseFree()) {
            lactoseFreeView.setVisibility(View.VISIBLE);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> buildBraceletConfirmationDialog(null, null).show());
    }

    private AlertDialog buildBraceletConfirmationDialog(final Participant participant,
                                                        final String uid) {
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
            if (id != null) {
                String idString = NfcUtils.toHexString(id);
                Log.d("onTagScanned", "id: " + idString);
                getSnowdaysService().assignBraceletToParticipant(mParticipant.getId(), idString)
                        .enqueue(new AssignBraceletResponseCallback());
            }
        }
    }

    private NfcAdapter ensureNfcAdapter() {
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return mNfcAdapter;
    }

    private static SDTrackService getSnowdaysService() {
        return SDRestApi.getSDTrackingService();
    }

    private class AssignBraceletResponseCallback implements Callback<RestResponse> {
        @Override
        public void onResponse(@NonNull Call<RestResponse> call, @NonNull Response<RestResponse> response) {
            if (response.errorBody() != null) {
                try {
                    RestResponse rr = SDRestApi.gson.fromJson(
                            response.errorBody().string(), RestResponse.class);
                    if (rr != null && rr.getStatus() != null) {
                        Toast.makeText(ParticipantDetailActivity.this,
                                rr.getStatus(), Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (IOException e) {
                    /* fallback to generic error */
                }
                Toast.makeText(ParticipantDetailActivity.this,
                        R.string.activity_list_participants_assign_error,
                            Toast.LENGTH_SHORT).show();
                return;
            }
            if (response.isSuccessful()) {
                finish();
            } else if (response.code() == 401) {
                SDRestApi.refreshTokenAndReplicate(accessToken -> {
                    Prefs.put(ParticipantDetailActivity.this, SDApp.Constants.Prefs.SESSION, accessToken);
                    SDRestApi.setAccessToken(accessToken);
                    call.clone().enqueue(this);
                }, exception -> startActivity(new Intent(ParticipantDetailActivity.this, LoginActivity.class)));
            }
        }

        @Override
        public void onFailure(@NonNull Call<RestResponse> call, @NonNull Throwable t) {
            Toast.makeText(ParticipantDetailActivity.this,
                    R.string.activity_list_participants_assign_error,
                        Toast.LENGTH_SHORT).show();
        }
    }
}
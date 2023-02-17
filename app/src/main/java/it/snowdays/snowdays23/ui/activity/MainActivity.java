package it.snowdays.snowdays23.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.ui.fragments.EventsFragment;
import it.snowdays.snowdays23.ui.fragments.NfcAwareFragment;
import it.snowdays.snowdays23.ui.fragments.ScanParticipantFragment;
import it.snowdays.snowdays23.util.platform.NfcUtils;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mLoadingView;
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_participants) {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frame, new ScanParticipantFragment())
                        .commit();
            } else if (item.getItemId() == R.id.navigation_events) {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frame, new EventsFragment())
                        .commit();
            }
            return true;
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new ScanParticipantFragment())
                .commit();
    }

    private NfcAwareFragment findScanningFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof NfcAwareFragment && ((NfcAwareFragment) fragment).isScanning()) {
                return (NfcAwareFragment) fragment;
            }
        }
        return null;
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
                String idString = NfcUtils.toHexString(id);
                Log.d("onTagScanned", "id: " + idString);
                final NfcAwareFragment scanning = findScanningFragment();
                if (scanning != null) {
                    scanning.onNfcTagScanned(idString);
                }
            }
        }
    }

    private NfcAdapter ensureNfcAdapter() {
        if (mNfcAdapter == null) {
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return mNfcAdapter;
    }
}
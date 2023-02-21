package it.snowdays.snowdays23.ui.activity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.ui.fragments.ListParticipantsFragment;
import it.snowdays.snowdays23.ui.fragments.ListPartyBeastsFragment;
import it.snowdays.snowdays23.ui.fragments.NfcAwareFragment;
import it.snowdays.snowdays23.ui.fragments.SearchAwareFragment;
import it.snowdays.snowdays23.util.platform.NfcUtils;

public class ListPeopleActivity extends AppCompatActivity {

    private ViewPager2 mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_people);

        final SearchView searchView = findViewById(R.id.search);
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1:
                        return new ListPartyBeastsFragment();
                    default:
                    case 0:
                        return new ListParticipantsFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        final SearchAwareFragment fragment = findSearchingFragment();
                        if (fragment != null) {
                            fragment.getAdapter().setSearchQuery(newText);
                        }
                        return true;
                    }
                });
                searchView.setQuery("", false);
            }
        });
        final TabLayout tabLayout = findViewById(R.id.tabs);
        final TabLayoutMediator tlm = new TabLayoutMediator(tabLayout, mPager, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText(R.string.activity_list_people_tab_party_beasts);
                    break;
                default:
                case 0:
                    tab.setText(R.string.activity_list_people_tab_participants);
                    break;
            }
        });
        tlm.attach();
    }

    private SearchAwareFragment findSearchingFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof SearchAwareFragment && ((SearchAwareFragment) fragment).isSearching()) {
                return (SearchAwareFragment) fragment;
            }
        }
        return null;
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


}
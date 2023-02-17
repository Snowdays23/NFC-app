package it.snowdays.snowdays23.ui.fragments;

import androidx.fragment.app.Fragment;

public abstract class NfcAwareFragment extends Fragment {
    public abstract void onNfcTagScanned(String id);
    public abstract boolean isScanning();
}

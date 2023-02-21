package it.snowdays.snowdays23.ui.fragments;

import it.snowdays.snowdays23.ui.adapter.SearchAwareAdapter;

public interface SearchAwareFragment {
    boolean isSearching();
    SearchAwareAdapter getAdapter();
}

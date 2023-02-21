package it.snowdays.snowdays23.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.model.PartyBeast;
import it.snowdays.snowdays23.util.ui.ViewUtils;

public class PartyBeastsListAdapter extends RecyclerView.Adapter<PartyBeastsListAdapter.ViewHolder> implements SearchAwareAdapter {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mFullNameView;
        final TextView mUniversityView;
        final ImageView mTagAssignationView;
        final ImageView mIconView;

        private ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams()
                    .matchParentInWidth().wrapContentInHeight().get());

            mFullNameView = v.findViewById(R.id.item_participant_full_name);
            mUniversityView = v.findViewById(R.id.item_participant_university);
            mTagAssignationView = v.findViewById(R.id.item_participant_bracelet_assigned);
            mIconView = v.findViewById(R.id.item_participant_icon);
        }
    }

    private final List<PartyBeast> partyBeasts;
    private List<PartyBeast> filteredPartyBeasts;
    private Context context;

    private OnPartyBeastSelectedListener mSelectedListener;

    public PartyBeastsListAdapter(final List<PartyBeast> partyBeasts) {
        this.partyBeasts = new ArrayList<>(partyBeasts);
        this.filteredPartyBeasts = new ArrayList<>(partyBeasts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(View.inflate(context, R.layout.item_participant, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PartyBeast partyBeast = filteredPartyBeasts.get(position);
        holder.mFullNameView.setText(String.format("%s %s",
                partyBeast.getFirstName(), partyBeast.getLastName()));
        holder.mUniversityView.setText(partyBeast.getEmail());
        holder.mIconView.setImageResource(R.drawable.ic_party);
        if (partyBeast.getBraceletId() != null && !partyBeast.getBraceletId().isEmpty()) {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_assigned);
        } else {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_not_assigned);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mSelectedListener != null) {
                mSelectedListener.onSelect(partyBeast);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredPartyBeasts.size();
    }

    public void setSelectedListener(OnPartyBeastSelectedListener listener) {
        mSelectedListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSearchQuery(String query) {
        this.filteredPartyBeasts = new ArrayList<>();
        query = query.trim().toLowerCase(Locale.ROOT);
        for (final PartyBeast partyBeast : this.partyBeasts) {
            if (partyBeast.getFirstName().toLowerCase(Locale.ROOT).contains(query) ||
                    partyBeast.getLastName().toLowerCase(Locale.ROOT).contains(query) ||
                    partyBeast.getEmail().toLowerCase(Locale.ROOT).contains(query)) {
                this.filteredPartyBeasts.add(partyBeast);
            }
        }
        notifyDataSetChanged();
    }

    public interface OnPartyBeastSelectedListener {
        void onSelect(final PartyBeast partyBeast);
    }
}

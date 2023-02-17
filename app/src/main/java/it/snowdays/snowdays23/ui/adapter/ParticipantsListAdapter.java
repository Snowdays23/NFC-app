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
import it.snowdays.snowdays23.util.ui.ViewUtils;

public class ParticipantsListAdapter extends RecyclerView.Adapter<ParticipantsListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mFullNameView;
        final TextView mUniversityView;
        final ImageView mTagAssignationView;

        private ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams()
                    .matchParentInWidth().wrapContentInHeight().get());

            mFullNameView = v.findViewById(R.id.item_participant_full_name);
            mUniversityView = v.findViewById(R.id.item_participant_university);
            mTagAssignationView = v.findViewById(R.id.item_participant_bracelet_assigned);
        }
    }

    private List<Participant> participants;
    private List<Participant> filteredParticipants;
    private Context context;

    private OnParticipantSelectedListener mSelectedListener;

    public ParticipantsListAdapter(final List<Participant> participants) {
        this.participants = new ArrayList<>(participants);
        this.filteredParticipants = new ArrayList<>(participants);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(View.inflate(context, R.layout.item_participant, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Participant participant = filteredParticipants.get(position);
        holder.mFullNameView.setText(String.format("%s %s",
                participant.getFirstName(), participant.getLastName()));
        holder.mUniversityView.setText(participant.getUniversity().getName());
        if (participant.getBraceletId() != null && !participant.getBraceletId().isEmpty()) {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_assigned);
        } else {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_not_assigned);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mSelectedListener != null) {
                mSelectedListener.onSelect(participant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredParticipants.size();
    }

    public void setSelectedListener(OnParticipantSelectedListener listener) {
        mSelectedListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSearchQuery(String query) {
        this.filteredParticipants = new ArrayList<>();
        query = query.trim().toLowerCase(Locale.ROOT);
        for (final Participant participant : this.participants) {
            if (participant.getFirstName().toLowerCase(Locale.ROOT).contains(query) ||
                    participant.getLastName().toLowerCase(Locale.ROOT).contains(query) ||
                        participant.getUniversity().getName().toLowerCase(Locale.ROOT).contains(query) ||
                            participant.getEmail().toLowerCase(Locale.ROOT).contains(query)) {
                this.filteredParticipants.add(participant);
            }
        }
        notifyDataSetChanged();
    }

    public interface OnParticipantSelectedListener {
        void onSelect(final Participant participant);
    }
}

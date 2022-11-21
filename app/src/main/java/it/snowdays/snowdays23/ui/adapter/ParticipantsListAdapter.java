package it.snowdays.snowdays23.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Participant;
import it.snowdays.snowdays23.ui.activity.ParticipantDetailActivity;
import it.snowdays.snowdays23.util.ViewUtils;

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

    private final List<Participant> mParticipants;
    private Context mContext;

    public ParticipantsListAdapter(final List<Participant> participants) {
        mParticipants = new ArrayList<>(participants);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(View.inflate(mContext, R.layout.item_participant, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Participant participant = mParticipants.get(position);
        holder.mFullNameView.setText(String.format("%s %s",
                participant.getFirstName(), participant.getLastName()));
        holder.mUniversityView.setText(participant.getUniversity().getName());
        if (participant.getBraceletId() != null && !participant.getBraceletId().isEmpty()) {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_assigned);
        } else {
            holder.mTagAssignationView.setImageResource(R.drawable.ic_tag_not_assigned);
        }
        holder.itemView.setOnClickListener(v -> mContext.startActivity(
                new Intent(mContext, ParticipantDetailActivity.class)
                        .putExtra("participant", participant)));
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }
}

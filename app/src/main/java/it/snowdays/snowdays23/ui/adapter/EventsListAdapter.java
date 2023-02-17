package it.snowdays.snowdays23.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Event;
import it.snowdays.snowdays23.ui.activity.EventCheckInActivity;
import it.snowdays.snowdays23.util.ui.ViewUtils;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconView;
        final TextView nameView;
        final TextView descriptionView;
        final TextView counterView;

        ViewHolder(View v) {
            super(v);

            v.setLayoutParams(ViewUtils.newLayoutParams()
                    .matchParentInWidth().wrapContentInHeight().get());

            iconView = v.findViewById(R.id.item_event_icon);
            nameView = v.findViewById(R.id.item_event_name);
            descriptionView = v.findViewById(R.id.item_event_description);
            counterView = v.findViewById(R.id.item_event_counter);
        }
    }

    private final List<Event> events;
    private Context context;

    public EventsListAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context = parent.getContext(), R.layout.item_event, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = this.events.get(position);
        holder.nameView.setText(event.getName());
        holder.descriptionView.setText(event.getDescription());
        holder.iconView.setImageDrawable(ResourcesCompat.getDrawable(
                context.getResources(), Integer.parseInt(event.getIcon()), null));
        holder.counterView.setText(buildCounterString(event.getCheckedIn(), event.getExpected()));
        holder.itemView.setOnClickListener(v -> context.startActivity(
                new Intent(context, EventCheckInActivity.class)
                        .putExtra("event", event)));
    }

    private String buildCounterString(int checkedIn, int expected) {
        int percentageIn = (int) Math.round((double) checkedIn / expected * 100);
        return percentageIn + "% " +
                "(" + checkedIn + "/" + expected + ")";
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }
}

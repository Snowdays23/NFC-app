package it.snowdays.snowdays23.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import it.snowdays.snowdays23.R;
import it.snowdays.snowdays23.model.Participant;

public class ParticipantDetailActivity extends AppCompatActivity {

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

        final Participant participant = getIntent().getParcelableExtra("participant");
        firstNameView.setText(participant.getFirstName());
        lastNameView.setText(participant.getLastName());
        dobView.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(participant.getDateOfBirth()));
        universityView.setText(participant.getUniversity().getName());
        braceletIdView.setText(participant.getBraceletId());
        if (participant.getEatingHabits().isGlutenFree()) {
            glutenFreeView.setVisibility(View.VISIBLE);
        }
        if (participant.getEatingHabits().isVegan()) {
            veganView.setVisibility(View.VISIBLE);
        } else if (participant.getEatingHabits().isVegetarian()) {
            vegetarianView.setVisibility(View.VISIBLE);
        }
        if (participant.getEatingHabits().isLactoseFree()) {
            lactoseFreeView.setVisibility(View.VISIBLE);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
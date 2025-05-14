package edu.deakin.s600152989.sit305.a71p;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddItemActivity extends AppCompatActivity {

    private EditText titleEditText, dateEditText, descriptionEditText, locationEditText, contactEditText;
    private RadioGroup typeRadioGroup;
    private LostFoundViewModel lostFoundViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Initialize the ViewModel
        lostFoundViewModel = new ViewModelProvider(this).get(LostFoundViewModel.class);

        // Reference the EditText views
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        contactEditText = findViewById(R.id.contactEditText);
        typeRadioGroup = findViewById(R.id.typeRadioGroup);

        // Set click listener on the Save button
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String location = locationEditText.getText().toString();
            String contact = contactEditText.getText().toString();

            // Validate input fields and show a Toast message for empty fields
            if (title.isEmpty() || date.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(AddItemActivity.this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine item type from RadioGroup
            int selectedTypeId = typeRadioGroup.getCheckedRadioButtonId();
            String type = "Lost"; // default
            if (selectedTypeId == R.id.radioFound) {
                type = "Found";
            }

            // Create a new LostFoundItem object with the input data
            LostFoundItem item = new LostFoundItem(title, description, date, location, contact, type);

            // Use the ViewModel to insert the item into the database
            lostFoundViewModel.insert(item);

            // Close the activity
            finish();
        });

        // Set up the "Back" button logic
        findViewById(R.id.backButton).setOnClickListener(v -> {
            // Simply finish the activity to go back
            finish();

        });
    }
}

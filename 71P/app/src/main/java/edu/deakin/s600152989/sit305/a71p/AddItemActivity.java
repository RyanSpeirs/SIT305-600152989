package edu.deakin.s600152989.sit305.a71p;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            // Save the item into the database
            LostFoundItem item = new LostFoundItem(title, description, "2022-03-12", "Deakin", "contact@domain.com", "Lost");
            // Use ViewModel to insert data into Room Database
            lostFoundViewModel.insert(item);
            finish();
        });
    }
}
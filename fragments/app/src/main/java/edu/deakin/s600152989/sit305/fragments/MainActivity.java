package edu.deakin.s600152989.sit305.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    Button btnburwood, btngeelong;
    View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void selectFragment(View view) {
        Fragment fragment;

        if(view.getId() == R.id.btnburwood) {
            fragment = new BurwoodFragment();
        } else if (view.getId()== R.id.btngeelong){
            fragment = new GeelongFragment();

        } else {
            throw new RuntimeException("wrong thing happened");
        }
    }
}





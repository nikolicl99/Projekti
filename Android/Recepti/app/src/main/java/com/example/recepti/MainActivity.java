package com.example.recepti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button showList;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showList = (Button) findViewById(R.id.showList);
        exit = (Button) findViewById(R.id.exit);

//        CleanDatabases.cleanAllDatabases(this);

        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openList();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        // Dodaj dugme za Firebase test
        Button btnFirebaseTest = findViewById(R.id.btnFirebaseTest); // Kreiraj u layout
        btnFirebaseTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, FirebaseTestActivity.class);
            startActivity(intent);
        });
    }

    public void openList() {
        Intent intent = new Intent(this, RecipeList.class);
        startActivity(intent);
    }
}
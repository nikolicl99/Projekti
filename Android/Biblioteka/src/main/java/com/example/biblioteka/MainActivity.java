package com.example.biblioteka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private Button oNama, Knjige, Korpa, Exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oNama = (Button) findViewById(R.id.btnoNama);

        oNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openONama();
            }
        });

        Knjige = (Button) findViewById(R.id.btnKnjige);

        Knjige.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKnjige();
            }
        });

        Korpa = (Button) findViewById(R.id.btnKorpa);

        Korpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openKorpa();
            }
        });

        copyJsonFromAssets();

        Exit = (Button) findViewById(R.id.btnExit);

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExitDialog();
            }
        });
    }
    public void openONama() {
        Intent intent = new Intent(this, ONama.class);
        startActivity(intent);
    }

    public void openKnjige() {
        Intent intent = new Intent(this, Prikaz.class);
        startActivity(intent);
    }

    public void openKorpa() {
        Intent intent = new Intent(this, Korpa.class);
        startActivity(intent);
    }

    private void copyJsonFromAssets() {
        try {
            InputStream is = getAssets().open("db.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("db.json", Context.MODE_PRIVATE));
            writer.write(new String(buffer, StandardCharsets.UTF_8));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Da li ste sigurni da želite da izađete")
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Korisnik zeli da nastavi da koristi aplikaciju
                    }
                });
        builder.create().show();
    }
}
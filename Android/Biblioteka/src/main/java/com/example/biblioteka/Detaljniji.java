package com.example.biblioteka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Detaljniji extends AppCompatActivity {
    private TextView naslov, autor, zanr, godina;
    private Button naruceno;

    private MyData selectedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaljniji);

        naslov = findViewById(R.id.naslov);
        autor = findViewById(R.id.autor);
        zanr = findViewById(R.id.zanr);
        godina = findViewById(R.id.godina);
        naruceno = findViewById(R.id.naruceno);

        Intent intent = getIntent();
        int bookId = intent.getIntExtra("bookID", -1);

        if (bookId != -1) {
            selectedBook = loadBookDetails(bookId);
            if (selectedBook != null) {
                naslov.setText(selectedBook.getNaslov());
                autor.setText(selectedBook.getAutor());
                zanr.setText(selectedBook.getZanr());
                godina.setText(String.valueOf(selectedBook.getGodinaIzdavanja()));

                updateNarucenoButtonText();

                naruceno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleNaruceno();
                    }
                });
            } else {
                Toast.makeText(this, "Knjiga nije pronadjena", Toast.LENGTH_LONG).show();
            }
        }
    }

    private MyData loadBookDetails(int bookID) {
        List<MyData> dataList = loadJSON();

        for (MyData book : dataList) {
            if (book.getId() == bookID) {
                return book;
            }
        }
        return null;
    }

    private List<MyData> loadJSON() {
        try {
            InputStream is = openFileInput("db.json");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyData>>() {}.getType();
            return gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateNarucenoButtonText() {
        naruceno.setText(selectedBook.getNaruceno() == 0 ? "Iznajmi knjigu" : "Knjiga je već iznajmljena");
    }

    private void toggleNaruceno() {
        selectedBook.setNaruceno(selectedBook.getNaruceno() == 0 ? 1 : 0);
        updateNarucenoButtonText();
        saveDataToJSON();
        setResult(RESULT_OK);
    }

    private void saveDataToJSON() {
        List<MyData> dataList = loadJSON();
        for (MyData book : dataList) {
            if (book.getId() == selectedBook.getId()) {
                book.setNaruceno(selectedBook.getNaruceno());
                break;
            }
        }
        saveListToJSON(dataList);
    }

    private void saveListToJSON(List<MyData> dataList) {
        Gson gson = new Gson();
        String json = gson.toJson(dataList);
        try (OutputStreamWriter writer = new OutputStreamWriter(openFileOutput("db.json", Context.MODE_PRIVATE))) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

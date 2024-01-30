package com.example.biblioteka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Prikaz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prikaz);

        List<MyData> dataList = loadJSON();

        ArrayAdapter<MyData> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            MyData selectedBook = dataList.get(position);
            int bookID = selectedBook.getId();

            Intent intent = new Intent(Prikaz.this, Detaljniji.class);
            intent.putExtra("bookID", bookID);
            startActivity(intent);
        });
    }

    private List<MyData> loadJSON() {
        List<MyData> dataList = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(openFileInput("db.json"), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyData>>() {}.getType();
            dataList = gson.fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}

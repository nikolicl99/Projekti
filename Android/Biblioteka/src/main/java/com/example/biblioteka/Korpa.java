package com.example.biblioteka;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.biblioteka.Detaljniji;
import com.example.biblioteka.MyData;
import com.example.biblioteka.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Korpa extends AppCompatActivity {

    private static final int DETALJNIJE_REQUEST = 1;
    private ArrayAdapter<MyData> adapter;
    private List<MyData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_korpa);

        dataList = loadJSON();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

        ListView listView = findViewById(R.id.listViewKorpa);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyData selectedBook = dataList.get(position);
                int bookID = selectedBook.getId();

                Intent intent = new Intent(Korpa.this, Detaljniji.class);
                intent.putExtra("bookID", bookID);
                startActivityForResult(intent, DETALJNIJE_REQUEST);
            }
        });
    }

    private List<MyData> loadJSON() {
        try {
            InputStream is = openFileInput("db.json");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MyData>>() {}.getType();
            List<MyData> allBooks = gson.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), listType);
            List<MyData> filteredBooks = new ArrayList<>();
            for (MyData book : allBooks) {
                if (book.getNaruceno() == 1) {
                    filteredBooks.add(book);
                }
            }
            return filteredBooks;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETALJNIJE_REQUEST) {
            if (resultCode == RESULT_OK) {
                dataList.clear();
                dataList.addAll(loadJSON());
                adapter.notifyDataSetChanged();
            }
        }
    }
}

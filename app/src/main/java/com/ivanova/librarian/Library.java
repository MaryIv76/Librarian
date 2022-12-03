package com.ivanova.librarian;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ivanova.librarian.Models.Book;
import com.ivanova.librarian.Models.Books_info;
import com.ivanova.librarian.ViewModels.RecycleViewAdapter;

import java.util.ArrayList;

public class Library extends AppCompatActivity {

    private BottomNavigationView menu;
    private Spinner spinner;
    //private ListView booksListView;

    private ArrayList<Book> books;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        // ---------------------- Spinner -----------------------------
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(spinnerAdapter);

        // ---------------------- Menu -----------------------------
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(2).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(Library.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        return true;
                    }
                    case R.id.heartItem: {
                        return true;
                    }
                    case R.id.userItem: {
                        return true;
                    }
                }
                return true;
            }
        });

        // ---------------------- Books List View -----------------------------
        books = Books_info.getBooks();
        //booksListView = findViewById(R.id.booksListView);

        recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecycleViewAdapter(books, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        /*String[] arr = new String[]{"aaaaa", "bbbbb", "c","asd","ytre","adjkg"};
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(this, R.layout.library_book_item, R.id.authorInfo, arr);
        booksListView.setAdapter(listViewAdapter);*/
    }
}

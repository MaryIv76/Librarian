package com.ivanova.librarian.Views;

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
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.Models.BooksInfo;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.RecycleViewAdapter;
import com.ivanova.librarian.ViewModels.RecyclerViewInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ArrayList<BookModel> books;

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
                        Intent intent = new Intent(Library.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(Library.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });

        // ---------------------- Books List View -----------------------------
        books = BooksInfo.getRecommendedBooks();
        books.addAll(BooksInfo.getBestBooks());
        books.addAll(BooksInfo.getPopularBooks());
        books.addAll(BooksInfo.getNewBooks());

        recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecycleViewAdapter(books, this, Library.this);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onBookItemClick(int position) {
        Intent intent = new Intent(Library.this, Book.class);

        intent.putExtra("AUTHOR", books.get(position).getAuthor());
        intent.putExtra("BOOK_NAME", books.get(position).getBookName());
        intent.putExtra("YEAR", books.get(position).getYear());
        intent.putExtra("GENRE", books.get(position).getGenre());
        intent.putExtra("ANNOTATION", books.get(position).getAnnotation());
        intent.putExtra("IMAGE", String.valueOf(books.get(position).getImage()));
        intent.putExtra("RATING", String.valueOf(books.get(position).getRating()));

        startActivity(intent);
    }
}

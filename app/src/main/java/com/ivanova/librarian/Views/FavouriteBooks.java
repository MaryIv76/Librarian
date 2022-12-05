package com.ivanova.librarian.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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

public class FavouriteBooks extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView menu;

    private ArrayList<BookModel> books;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_books);

        // ---------------------- Menu -----------------------------
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(3).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        Intent intent = new Intent(FavouriteBooks.this, HomePage.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(FavouriteBooks.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(FavouriteBooks.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });

        // ---------------------- Books List View -----------------------------
        books = BooksInfo.getNewBooks();

        recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new RecycleViewAdapter(books, this, FavouriteBooks.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(int position) {
        Intent intent = new Intent(FavouriteBooks.this, Book.class);

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

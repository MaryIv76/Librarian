package com.ivanova.librarian.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ivanova.librarian.Models.BookModel;
import com.ivanova.librarian.Models.BooksInfo;
import com.ivanova.librarian.R;
import com.ivanova.librarian.ViewModels.HomePageRecycleViewAdapter;
import com.ivanova.librarian.ViewModels.HomePageRecyclerViewInterface;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity implements HomePageRecyclerViewInterface {

    private BottomNavigationView menu;
    private Spinner spinner;

    private ImageButton searchBtn;
    private boolean searchIsEnabled;
    private ImageButton filtersBtn;
    private RelativeLayout searchEditTextLayout;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        // ---------------------- Spinner -----------------------------
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner.setAdapter(adapter);


        // ---------------------- Books Horizontal List -----------------------------
        createHorizontalRecyclerView(R.id.firstBooksRecyclerHorizontalView, BooksInfo.getRecommendedBooks());
        createHorizontalRecyclerView(R.id.secondBooksRecyclerHorizontalView, BooksInfo.getBestBooks());
        createHorizontalRecyclerView(R.id.thirdBooksRecyclerHorizontalView, BooksInfo.getPopularBooks());
        createHorizontalRecyclerView(R.id.fourthBooksRecyclerHorizontalView, BooksInfo.getNewBooks());

        // ---------------------- Menu -----------------------------
        menu = findViewById(R.id.bottomNavigation);
        menu.getMenu().getItem(0).setChecked(true);
        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem: {
                        return true;
                    }
                    case R.id.searchItem: {
                        return true;
                    }
                    case R.id.bookItem: {
                        Intent intent = new Intent(HomePage.this, Library.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.heartItem: {
                        Intent intent = new Intent(HomePage.this, FavouriteBooks.class);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.userItem: {
                        Intent intent = new Intent(HomePage.this, UserAccount.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return true;
            }
        });


        // ---------------------- Search Button -----------------------------
        searchEditTextLayout = findViewById(R.id.et_layout_search);
        searchEditTextLayout.setEnabled(false);
        filtersBtn = findViewById(R.id.settingsButton);
        searchIsEnabled = false;

        searchBtn = findViewById(R.id.searchButton);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchIsEnabled) {
                    startSearching();

                } else {
                    endSearching();
                }
            }
        });

        searchEditText = findViewById(R.id.et_search);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    endSearching();
                    return true;
                }
                return false;
            }
        });
    }

    private void createHorizontalRecyclerView(int idBooksRecyclerHorizontalView, ArrayList<BookModel> books) {
        RecyclerView recyclerView = findViewById(idBooksRecyclerHorizontalView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter recyclerViewAdapter = new HomePageRecycleViewAdapter(books, this, HomePage.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onBookItemClick(ArrayList<BookModel> books, int position) {
        Intent intent = new Intent(HomePage.this, Book.class);

        intent.putExtra("AUTHOR", books.get(position).getAuthor());
        intent.putExtra("BOOK_NAME", books.get(position).getBookName());
        intent.putExtra("YEAR", books.get(position).getYear());
        intent.putExtra("GENRE", books.get(position).getGenre());
        intent.putExtra("ANNOTATION", books.get(position).getAnnotation());
        intent.putExtra("IMAGE", String.valueOf(books.get(position).getImage()));
        intent.putExtra("RATING", String.valueOf(books.get(position).getRating()));

        startActivity(intent);
    }

    private void startSearching() {
        searchIsEnabled = true;
        filtersBtn.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        filtersBtn.setEnabled(false);
        spinner.setEnabled(false);
        searchEditTextLayout.setVisibility(View.VISIBLE);
        searchEditTextLayout.setEnabled(true);

        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
    }

    private void endSearching() {
        searchIsEnabled = false;
        filtersBtn.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        filtersBtn.setEnabled(true);
        spinner.setEnabled(true);
        searchEditTextLayout.setVisibility(View.INVISIBLE);
        searchEditTextLayout.setEnabled(false);
        searchEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }
}
